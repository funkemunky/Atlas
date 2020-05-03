package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
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

    private static WrappedClass payloadClass = Reflections.getNMSClass(Server.CUSTOM_PAYLOAD);
    private static WrappedField tagField, dataField;

    private String tag;
    private byte[] data;

    @Override
    public void process(Player player, ProtocolVersion version) {
        tag = tagField.get(getObject());
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            data = new WrappedPacketDataSerializer(dataField.get(getObject())).getData();
        } else data = dataField.get(getObject());
    }

    @Override
    public void updateObject() {
        //TODO Update object.
    }

    static {
        tagField = payloadClass.getFieldByType(String.class, 0);
        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_7_10)) {
            dataField = payloadClass.getFieldByType(byte[].class, 0);
        } else dataField = payloadClass.getFieldByType(WrappedPacketDataSerializer.vanillaClass.getParent(), 0);
    }
}
