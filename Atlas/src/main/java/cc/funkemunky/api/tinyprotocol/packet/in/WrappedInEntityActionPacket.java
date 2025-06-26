package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInEntityActionPacket extends NMSObject {

    // Fields
    private static final String packet = Client.ENTITY_ACTION;

    // Fields
    private static FieldAccessor<Integer> fieldAction1_7;
    private static FieldAccessor<Enum> fieldAction1_8;

    // Decoded data
    private EnumPlayerAction action;

    public WrappedInEntityActionPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            action = EnumPlayerAction.values()[Math.min(8, fetch(fieldAction1_7) - 1)];
        } else {
            action = EnumPlayerAction.values()[((Enum)fetch(fieldAction1_8)).ordinal()];
        }
    }

    @Override
    public void updateObject() {

    }

    public enum EnumPlayerAction {
        START_SNEAKING,
        STOP_SNEAKING,
        STOP_SLEEPING,
        START_SPRINTING,
        STOP_SPRINTING,
        START_RIDING_JUMP,
        STOP_RIDING_JUMP,
        OPEN_INVENTORY,
        START_FALL_FLYING
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldAction1_7 = fetchField(packet, int.class, 1);
        } else fieldAction1_8 = fetchField(packet, Enum.class, 0);
    }
}
