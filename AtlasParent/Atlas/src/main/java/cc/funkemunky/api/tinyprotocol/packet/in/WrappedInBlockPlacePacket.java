package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
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
import org.bukkit.inventory.ItemStack;

@Getter
//TODO Test 1.15
public class WrappedInBlockPlacePacket extends NMSObject {
    private static final String packet = Client.BLOCK_PLACE;

    // Fields
    private static WrappedField fieldFace, fieldFace1_9, fieldBlockPosition, fieldItemStack, enumHand;
    private static WrappedField fieldPosX, fieldPosY, fieldPosZ, fieldVecX, fieldVecY, fieldVecZ;
    private static WrappedField fieldMissed, fieldMovingObjectBS;

    private static final IntVector a = new IntVector(-1, -1, -1);

    private static WrappedClass movingObjectBSObject, blockPlacePacket, enumHandClass;

    // Decoded data
    private WrappedEnumDirection face;
    private ItemStack itemStack;
    private IntVector blockPosition;
    private boolean mainHand;
    private boolean missed;
    private float vecX, vecY, vecZ;

    public WrappedInBlockPlacePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            blockPosition = new IntVector(fetch(fieldPosX), fetch(fieldPosY), fetch(fieldPosZ));
            face = WrappedEnumDirection.values()[Math.min(fieldFace.get(getObject()), 5)];
            itemStack = toBukkitStack(fieldItemStack.get(getObject()));
            vecX = fieldVecX.get(getObject());
            vecY = fieldVecY.get(getObject());
            vecZ = fieldVecZ.get(getObject());
            mainHand = true;
            missed = false;
        } else if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            BaseBlockPosition bbp = new BaseBlockPosition(fieldBlockPosition.get(getObject()));
            blockPosition = new IntVector(bbp.getX(), bbp.getY(), bbp.getZ());
            face = WrappedEnumDirection.values()[Math.min(fieldFace.get(getObject()), 5)];
            itemStack = toBukkitStack(fieldItemStack.get(getObject()));
            vecX = fieldVecX.get(getObject());
            vecY = fieldVecY.get(getObject());
            vecZ = fieldVecZ.get(getObject());
            mainHand = true;
            missed = false;
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_15)) {
            BaseBlockPosition bbp = new BaseBlockPosition(fieldBlockPosition.get(getObject()));
            blockPosition = new IntVector(bbp.getX(), bbp.getY(), bbp.getZ());
            face = WrappedEnumDirection.values()[((Enum) fieldFace1_9.get(getObject())).ordinal()];
            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
                vecX = fieldVecX.get(getObject());
                vecY = fieldVecY.get(getObject());
                vecZ = fieldVecZ.get(getObject());
            }
            mainHand = ((Enum) enumHand.get(getObject())).name().toLowerCase().contains("main");
            missed = false;
        } else {
            Object movingBS = fieldMovingObjectBS.get(getObject());
            face = WrappedEnumDirection.values()[((Enum) fieldFace1_9.get(movingBS)).ordinal()];
            BaseBlockPosition bbp = new BaseBlockPosition(fieldBlockPosition.get(movingBS));
            blockPosition = new IntVector(bbp.getX(), bbp.getY(), bbp.getZ());
            mainHand = ((Enum) enumHand.get(getObject())).name().toLowerCase().contains("main");
            missed = fieldMissed.get(movingBS);
        }
    }

    @Deprecated
    public BaseBlockPosition getPosition() {
        return new BaseBlockPosition(blockPosition.getX(), blockPosition.getY(), blockPosition.getZ());
    }

    //TODO Redo this method.
    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_15)) {

        }
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)) {
            setObject(NMSObject.construct(getObject(), packet, getPosition().getAsBlockPosition(), face.toVanilla(),
                    mainHand ? enumHandClass.getEnum("MAIN_HAND") : enumHandClass.getEnum("OFF_HAND")));
        } else if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            setObject(NMSObject.construct(getObject(), packet, getPosition().getAsBlockPosition(), face.toVanilla(),
                    mainHand ? enumHandClass.getEnum("MAIN_HAND") : enumHandClass.getEnum("OFF_HAND"),
                    vecX, vecY, vecZ));
        } else if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            setObject(NMSObject.construct(getObject(), packet, getPosition().getAsBlockPosition(), face.ordinal(),
                    CraftReflection.getVanillaItemStack(itemStack), vecX, vecY, vecZ));
        } else {
            setObject(NMSObject.construct(getObject(), packet, blockPosition.getX(), blockPosition.getY(), blockPosition.getZ(),
                    CraftReflection.getVanillaItemStack(itemStack), vecX, vecY, vecZ));
        }
    }

    static {
        if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_15)) {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInUseItem");
            movingObjectBSObject = Reflections.getNMSClass("MovingObjectPositionBlock");
            fieldMovingObjectBS = blockPlacePacket.getFieldByType(movingObjectBSObject.getParent(), 0);
            fieldFace1_9 = movingObjectBSObject.getFieldByType(WrappedEnumDirection.enumDirection.getParent(), 0);
            fieldBlockPosition = movingObjectBSObject.getFieldByType(MinecraftReflection.blockPos.getParent(), 0);
            fieldMissed = movingObjectBSObject.getFieldByType(boolean.class, 0);
            enumHandClass = Reflections.getNMSClass("EnumHand");
            enumHand = blockPlacePacket.getFieldByType(enumHandClass.getParent(), 0);
        } else if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInUseItem");
            fieldBlockPosition = blockPlacePacket.getFieldByType(Object.class, 0);
            fieldFace1_9 = blockPlacePacket.getFieldByType(Enum.class, 1);
            if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
                fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
                fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
                fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
            }
            enumHand = blockPlacePacket.getFieldByType(Enum.class, 0);
            enumHandClass = Reflections.getNMSClass("EnumHand");
        } else if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInBlockPlace");
            fieldBlockPosition = blockPlacePacket.getFieldByType(MinecraftReflection.blockPos.getParent(), 1);
            fieldFace = blockPlacePacket.getFieldByType(int.class, 0);
            fieldItemStack = blockPlacePacket.getFieldByType(MinecraftReflection.itemStack.getParent(), 0);
            fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
            fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
            fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
        } else {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInBlockPlace");
            fieldPosX = blockPlacePacket.getFieldByType(int.class, 0);
            fieldPosY = blockPlacePacket.getFieldByType(int.class, 1);
            fieldPosZ = blockPlacePacket.getFieldByType(int.class, 2);
            fieldFace = blockPlacePacket.getFieldByType(int.class, 3);
            fieldItemStack = blockPlacePacket.getFieldByType(MinecraftReflection.itemStack.getParent(), 0);
            fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
            fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
            fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
        }
    }
}