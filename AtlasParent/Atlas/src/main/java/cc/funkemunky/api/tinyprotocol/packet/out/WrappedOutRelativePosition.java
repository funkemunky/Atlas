package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutRelativePosition extends NMSObject {
    private static final String packet = Server.ENTITY;

    // Fields
    private static FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Byte> fieldX = fetchField(packet, byte.class, 0);
    private static FieldAccessor<Byte> fieldY = fetchField(packet, byte.class, 1);
    private static FieldAccessor<Byte> fieldZ = fetchField(packet, byte.class, 2);
    private static FieldAccessor<Byte> fieldYaw = fetchField(packet, byte.class, 0);
    private static FieldAccessor<Byte> fieldPitch = fetchField(packet, byte.class, 1);
    private static FieldAccessor<Boolean> fieldGround = fetchField(packet, boolean.class, 0);

    // Decoded data
    private int id;
    private byte x, y, z;
    private byte yaw, pitch;
    private boolean look, pos, ground;

    public WrappedOutRelativePosition(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        String name = getPacketName();

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            pos = name.equals(Server.LEGACY_REL_POSITION) || name.equals(Server.LEGACY_REL_POSITION_LOOK);
            look = name.equals(Server.LEGACY_REL_LOOK) || name.equals(Server.LEGACY_REL_POSITION_LOOK);
        } else {
            pos = name.equals(Server.REL_POSITION) || name.equals(Server.REL_POSITION_LOOK);
            look = name.equals(Server.REL_LOOK) || name.equals(Server.REL_POSITION_LOOK);
        }
        id = fetch(fieldId);
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);
        ground = fetch(fieldGround);
    }
}
