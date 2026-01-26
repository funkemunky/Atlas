package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumDirection;
import cc.funkemunky.api.utils.math.IntVector;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInBlockDigPacket extends NMSObject {
    private static final WrappedClass packet = Reflections.getNMSClass(Client.BLOCK_DIG);

    // 1.8+ Fields
    private static WrappedField fieldBlockPosition, fieldDirection, fieldDigType;

    // 1.7.10 and below fields
    private static WrappedField fieldPosX, fieldPosY, fieldPosZ, fieldFace, fieldIntAction;

    // Decoded data
    private IntVector blockPosition;
    private WrappedEnumDirection direction;
    private EnumPlayerDigType action;


    public WrappedInBlockDigPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            set(fieldPosX, blockPosition.getX());
            set(fieldPosY, blockPosition.getY());
            set(fieldPosZ, blockPosition.getZ());
            set(fieldFace, direction.ordinal()); //TODO Test if this causes errors.
            set(fieldIntAction, action.ordinal()); //TODO Test if this causes errors.
        } else {
            set(fieldBlockPosition, new BaseBlockPosition(
                    blockPosition.getX(),
                    blockPosition.getY(),
                    blockPosition.getZ()).getObject());
            set(fieldDirection, direction.toVanilla());
            set(fieldDigType, action.toVanilla());
        }
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            blockPosition = new IntVector(fetch(fieldPosX), fetch(fieldPosY), fetch(fieldPosZ));
            direction = WrappedEnumDirection.values()[Math.min(fetch(fieldFace), 5)];
            action = EnumPlayerDigType.values()[(int)fetch(fieldIntAction)];
        } else {
            BaseBlockPosition bbp = new BaseBlockPosition(fetch(fieldBlockPosition));
            blockPosition = new IntVector(bbp.getX(), bbp.getY(), bbp.getZ());
            direction = WrappedEnumDirection.fromVanilla(fetch(fieldDirection));
            action = EnumPlayerDigType.fromVanilla(fetch(fieldDigType));
        }
    }

    @Deprecated
    public BaseBlockPosition getPosition() {
        return new BaseBlockPosition(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    public enum EnumPlayerDigType {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS;

        public static WrappedClass classDigType;

        public static EnumPlayerDigType fromVanilla(Enum obj) {
            return EnumPlayerDigType.values()[obj.ordinal()];
        }

        public <T> T toVanilla() {
            return (T) classDigType.getEnum(name());
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldPosX = packet.getFieldByType(int.class, 0);
            fieldPosY = packet.getFieldByType(int.class, 1);
            fieldPosZ =  packet.getFieldByType(int.class, 2);
            fieldFace =  packet.getFieldByType(int.class, 3);
            fieldIntAction =  packet.getFieldByType(int.class, 4);
        } else {
            fieldBlockPosition = packet.getFieldByType(MinecraftReflection.blockPos.getParent(), 0);
            fieldDirection = packet.getFieldByType(WrappedEnumDirection.enumDirection.getParent(), 0);
            EnumPlayerDigType.classDigType = Reflections.getNMSClass("PacketPlayInBlockDig$EnumPlayerDigType");
            fieldDigType = packet.getFieldByType(EnumPlayerDigType.classDigType.getParent(), 0);
        }
    }
}