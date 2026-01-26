package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInKeepAlivePacket extends NMSObject {
    private static final String packet = Client.KEEP_ALIVE;

    private static WrappedField timeField;

    private long time;

    public WrappedInKeepAlivePacket(long time) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) setPacket(packet, (int) time);
        else setPacket(packet, time);
    }

    public WrappedInKeepAlivePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        Object object = timeField.get(getObject());

        if(object instanceof Long) {
            time = (long) object;
        } else time = (int) object;
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)) {
            setObject(NMSObject.construct(getObject(), packet, (int) time));
        } else setObject(NMSObject.construct(getObject(), packet, time));
    }

    static {
        timeField = Reflections.getNMSClass(packet)
                .getFieldByType(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_12)
                        ? int.class : long.class, 0);
    }
}