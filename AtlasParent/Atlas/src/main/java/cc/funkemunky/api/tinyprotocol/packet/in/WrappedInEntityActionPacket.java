package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInEntityActionPacket extends NMSObject {

    // Fields
    private static WrappedField actionField;
    private static WrappedClass packet = Reflections.getNMSClass(Client.ENTITY_ACTION), enumClass;

    // Decoded data
    private EnumPlayerAction action;

    public WrappedInEntityActionPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            int oridinal = fetch(actionField);
            action = EnumPlayerAction.values()[Math.min(8, oridinal - 1)];
        } else {
            Enum action = fetch(actionField);
            this.action = EnumPlayerAction.valueOf(action.name());
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8))
        setObject(NMSObject.construct(getObject(), Client.ENTITY_ACTION, Math.min(8, action.ordinal())));
        else setObject(NMSObject.construct(getObject(), Client.ENTITY_ACTION, enumClass.getEnum(action.name())));
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
            actionField = packet.getFieldByType(int.class, 1);
        } else {
            actionField = packet.getFieldByType(Enum.class, 0);
            enumClass = new WrappedClass(actionField.getType());
        }
    }
}
