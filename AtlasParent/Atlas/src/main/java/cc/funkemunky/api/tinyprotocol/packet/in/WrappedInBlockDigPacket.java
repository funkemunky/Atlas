package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumDirection;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.Arrays;

@Getter
public class WrappedInBlockDigPacket extends NMSObject {
    private static final String packet = Client.BLOCK_DIG;

    private static WrappedClass packetClass = Reflections.getNMSClass(packet);
    private static WrappedField fieldBlockPos, fieldPosX, fieldPosY, fieldPosZ, fieldDirection,
            fieldDigType, face, intAction;

    // Decoded data
    private BaseBlockPosition position;
    private WrappedEnumDirection direction;
    private EnumPlayerDigType action;

    public WrappedInBlockDigPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            position = new BaseBlockPosition(fetch(fieldPosX), fetch(fieldPosY), fetch(fieldPosZ));
            direction = WrappedEnumDirection.values()[Math.min(fetch(face), 5)];
            action = EnumPlayerDigType.values()[(int)fetch(intAction)];
        } else {
            position = new BaseBlockPosition(fetch(fieldBlockPos));
            direction = WrappedEnumDirection.fromVanilla(fetch(fieldDirection));
            action = EnumPlayerDigType.fromVanilla(fetch(fieldDigType));
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            setObject(NMSObject.construct(getObject(), packet,
                    position.getX(), position.getY(), position.getZ(), direction.ordinal(), action));
        } else setObject(NMSObject.construct(getObject(), packet,
                position.getAsBlockPosition(), direction.toVanilla(), action.toVanilla()));
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldPosX = packetClass.getFieldByType(int.class, 0);
            fieldPosY = packetClass.getFieldByType(int.class, 1);
            fieldPosZ = packetClass.getFieldByType(int.class, 2);
            face = packetClass.getFieldByType(int.class, 3);
            intAction = packetClass.getFieldByType(int.class, 4);
        } else {
            fieldBlockPos = packetClass.getFieldByType(Object.class, 0);
            fieldDirection = packetClass.getFieldByType(Object.class, 1);
            fieldDigType = packetClass.getFieldByType(Object.class, 2);
        }
    }

    public enum EnumPlayerDigType {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS,
        UNKNOWN; //Not an actual vanilla object.

        private static WrappedClass enumPlayerDigType;

        public <T> T toVanilla() {
            if(enumPlayerDigType == null) return (T) this;
            return (T) enumPlayerDigType.getEnum(name());
        }

        public static EnumPlayerDigType fromVanilla(Enum object) {
            return Arrays.stream(values()).filter(val -> val.name().equals(object.name())).findFirst()
                    .orElse(EnumPlayerDigType.UNKNOWN);
        }

        static {
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
                enumPlayerDigType = Reflections.getNMSClass("PacketPlayInBlockDig.EnumPlayerDigType");
            }
        }
    }

}
