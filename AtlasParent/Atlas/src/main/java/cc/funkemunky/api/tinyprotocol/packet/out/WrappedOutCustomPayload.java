package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedPacketDataSerializer;
import lombok.Getter;
import org.bukkit.entity.Player;

//TODO Make this compatible with 1.13 and newer.
@Getter
public class WrappedOutCustomPayload extends NMSObject {

    public WrappedOutCustomPayload(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutCustomPayload(String tag, byte[] data) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            setObject(payloadClass.getConstructor().newInstance());

            this.tag = tag;
            this.data = data;

            updateObject();
        } else {
            setObject(payloadClass.getConstructor(minecraftKeyWrapper.getParent(),
                    WrappedPacketDataSerializer.vanillaClass.getParent())
                    .newInstance(minecraftKeyWrapper.getConstructor(String.class)
                            .newInstance(tag), new WrappedPacketDataSerializer(data).getObject()));
        }
    }

    private static WrappedClass payloadClass = Reflections.getNMSClass(Server.CUSTOM_PAYLOAD);
    private static WrappedField tagField, dataField;

    private String tag;
    private byte[] data;

    //1.13+
    private static WrappedClass minecraftKeyWrapper;
    private static WrappedField keyOne, keyTwo;

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            Object mk = tagField.get(getObject());
            tag = keyOne.get(mk) + ":" + keyTwo.get(mk);
        } else tag = tagField.get(getObject());
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            data = new WrappedPacketDataSerializer((Object)fetch(dataField)).getData();

        } else data = dataField.get(getObject());
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            dataField.set(getObject(), new WrappedPacketDataSerializer(data).getObject());
        } else dataField.set(getObject(), data);

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            if(tag.contains(":")) {
                Object mk = tagField.get(getObject());
                String[] split = tag.split(":");
                keyOne.set(mk, split[0]);
                keyTwo.set(mk, split[1]);
            } else {
                System.out.println("Tag (" + tag + ") must contain a ':' to be valid.");
            }
        } else tagField.set(getObject(), tag);
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            minecraftKeyWrapper = Reflections.getNMSClass("MinecraftKey");
            keyOne = minecraftKeyWrapper.getFieldByType(String.class, 0);
            keyTwo = minecraftKeyWrapper.getFieldByType(String.class, 1);
            tagField = payloadClass.getFieldByType(minecraftKeyWrapper.getParent(), 0);
        } else tagField = payloadClass.getFieldByType(String.class, 0);
        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_7_10)) {
            dataField = payloadClass.getFieldByType(byte[].class, 0);
        } else dataField = payloadClass.getFieldByType(WrappedPacketDataSerializer.vanillaClass.getParent(), 0);
    }
}
