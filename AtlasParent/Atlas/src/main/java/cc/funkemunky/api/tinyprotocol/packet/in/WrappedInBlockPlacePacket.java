package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.EnumDirection;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedInBlockPlacePacket extends NMSObject {
    private static final String packet = Client.BLOCK_PLACE;

    // Fields
    private static WrappedField fieldFace;
    private static WrappedField fieldFace1_9;
    private static WrappedField fieldBlockPosition;
    private static WrappedField fieldItemStack;
    private static WrappedField fieldPosX;
    private static WrappedField fieldPosY;
    private static WrappedField fieldPosZ;
    private static WrappedField fieldVecX;
    private static WrappedField fieldVecY;
    private static WrappedField fieldVecZ;
    private static WrappedField enumHand;

    private static WrappedClass movingObjectBSObject, blockPlacePacket;

    // Decoded data
    private EnumDirection face;
    private ItemStack itemStack;
    private BaseBlockPosition position;
    private boolean mainHand;
    private float vecX, vecY, vecZ;

    public WrappedInBlockPlacePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            position = new BaseBlockPosition(fieldPosX.get(getObject()),
                    fieldPosY.get(getObject()),
                    fieldPosZ.get(getObject()));
            face = EnumDirection.values()[Math.min(fieldFace.get(getObject()), 5)];
            itemStack = toBukkitStack(fieldItemStack.get(getObject()));
            vecX = fieldVecX.get(getObject());
            vecY = fieldVecY.get(getObject());
            vecZ = fieldVecZ.get(getObject());
            mainHand = true;
        } else if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            position = new BaseBlockPosition(fieldBlockPosition.get(getObject()));
            face = EnumDirection.values()[Math.min(fieldFace.get(getObject()), 5)];
            itemStack = toBukkitStack(fieldItemStack.get(getObject()));
            vecX = fieldVecX.get(getObject());
            vecY = fieldVecY.get(getObject());
            vecZ = fieldVecZ.get(getObject());
            mainHand = true;
        } else if (!getObject().toString().contains("BlockPlace")) {
            position = new BaseBlockPosition(fieldBlockPosition.get(getObject()));
            face = EnumDirection.values()[((Enum)fieldFace1_9.get(getObject())).ordinal()];
            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_14)) {
                vecX = fieldVecX.get(getObject());
                vecY = fieldVecY.get(getObject());
                vecZ = fieldVecZ.get(getObject());
            }
            mainHand = ((Enum)enumHand.get(getObject())).name().toLowerCase().contains("main");
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)) {
            movingObjectBSObject = Reflections.getClass("MovingObjectPositionBlock");
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInUseItem");

            fieldFace1_9 = movingObjectBSObject.getFieldByType(Enum.class, 0);
            fieldBlockPosition = movingObjectBSObject.getFieldByType(Object.class, 1);
            enumHand = blockPlacePacket.getFieldByType(Enum.class, 0);
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            blockPlacePacket = Reflections.getNMSClass("PacketPlayInUseItem");
            fieldBlockPosition = blockPlacePacket.getFieldByType(Object.class, 0);
            fieldFace1_9 = blockPlacePacket.getFieldByType(Enum.class, 1);
            fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
            fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
            fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
            enumHand = blockPlacePacket.getFieldByType(Enum.class, 0);
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            blockPlacePacket = Reflections.getNMSClass(packet);
            fieldBlockPosition = blockPlacePacket.getFieldByType(MinecraftReflection.blockPos.getParent(), 1);
            fieldFace = blockPlacePacket.getFieldByType(int.class, 0);
            fieldItemStack = blockPlacePacket.getFieldByType(MinecraftReflection.itemStack.getParent(), 0);
            fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
            fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
            fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
        } else {
            blockPlacePacket = Reflections.getNMSClass(packet);
            fieldPosX = blockPlacePacket.getFieldByType(int.class, 0);
            fieldPosY = blockPlacePacket.getFieldByType(int.class, 1);
            fieldPosZ = blockPlacePacket.getFieldByType(int.class, 2);
            fieldFace = blockPlacePacket.getFieldByType(int.class, 3);
            fieldItemStack = blockPlacePacket.getFieldByType(Object.class, 0);
            fieldVecX = blockPlacePacket.getFieldByType(float.class, 0);
            fieldVecY = blockPlacePacket.getFieldByType(float.class, 1);
            fieldVecZ = blockPlacePacket.getFieldByType(float.class, 2);
        }
    }
}