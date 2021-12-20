package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedPacketDataSerializer;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInCustomPayload extends NMSObject {

    public WrappedInCustomPayload(Object object) {
        super(object);
    }

    public WrappedInCustomPayload(Object object, Player player) {
        super(object, player);
    }

    public WrappedInCustomPayload(PacketReceiveEvent event) {
        super(event);
    }

    public WrappedInCustomPayload(PacketSendEvent event) {
        super(event);
    }

    private static WrappedClass wrapped = Reflections.getNMSClass(Client.CUSTOM_PAYLOAD);

    private static WrappedField tagField;

    //1.7.10
    private static WrappedField lengthField;
    private static WrappedField dataField;

    //1.8
    private static WrappedClass wrappedPDS;
    private static WrappedField dataSerializer;

    //1.13+
    private static WrappedClass minecraftKeyWrapper;
    private static WrappedField keyOne, keyTwo;
    private static WrappedField mkField;

    private String tag;
    private int length;
    private byte[] data;

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            tag = tagField.get(getObject());
            length = lengthField.get(getObject());
            data = dataField.get(getObject());
        } else {
            Object packetData = dataSerializer.get(getObject());

            if(packetData != null) {
                WrappedPacketDataSerializer wpds = new WrappedPacketDataSerializer((Object) dataSerializer.get(getObject()));

                if(wpds.getRefCount() > 0) {
                    wpds.copy();
                    data = wpds.getData();
                } else data = new byte[0];
            } else data = new byte[0];
            length = data.length;

            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) tag = tagField.get(getObject());
            else {
                Object mk = mkField.get(getObject());
                tag = keyOne.get(mk) + ":" + keyTwo.get(mk);
            }
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            setObject(NMSObject.construct(getObject(), Client.CUSTOM_PAYLOAD, tag, length, data));
        } else {
            Object serializer = new WrappedPacketDataSerializer(data).getObject();
            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
                setObject(NMSObject.construct(getObject(), Client.CUSTOM_PAYLOAD, tag, serializer));
            } else setObject(NMSObject.construct(getObject(), Client.CUSTOM_PAYLOAD, serializer));
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            lengthField = wrapped.getFieldByType(int.class, 0);
            dataField = wrapped.getFieldByType(byte[].class, 0);
        } else {
            wrappedPDS = Reflections.getNMSClass("PacketDataSerializer");
            dataSerializer = wrapped.getFieldByType(wrappedPDS.getParent(), 0);
        }

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            minecraftKeyWrapper = Reflections.getNMSClass("MinecraftKey");
            keyOne = minecraftKeyWrapper.getFieldByType(String.class, 0);
            keyTwo = minecraftKeyWrapper.getFieldByType(String.class, 1);
            mkField = wrapped.getFieldByType(minecraftKeyWrapper.getParent(), 0);
        } else tagField = wrapped.getFieldByType(String.class, 0);
    }
}
