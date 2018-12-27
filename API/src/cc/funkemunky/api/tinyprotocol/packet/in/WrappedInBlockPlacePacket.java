package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.EnumDirection;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedInBlockPlacePacket extends NMSObject {
    private static final String packet = Client.BLOCK_PLACE;

    // Fields
    private static FieldAccessor<Integer> fieldFace;
    private static FieldAccessor<Enum> fieldFace1_9;
    private static FieldAccessor<Object> fieldBlockPosition;
    private static FieldAccessor<Object> fieldItemStack;
    private static FieldAccessor<Integer> fieldPosX;
    private static FieldAccessor<Integer> fieldPosY;
    private static FieldAccessor<Integer> fieldPosZ;
    private static FieldAccessor<Float> fieldVecX;
    private static FieldAccessor<Float> fieldVecY;
    private static FieldAccessor<Float> fieldVecZ;

    // Decoded data
    private EnumDirection face;
    private ItemStack itemStack;
    private BaseBlockPosition position;
    private float vecX, vecY, vecZ;

    public WrappedInBlockPlacePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldPosX = fetchField(packet, int.class, 0);
            fieldPosY = fetchField(packet, int.class, 1);
            fieldPosZ = fetchField(packet, int.class, 2);
            fieldFace = fetchField(packet, int.class, 3);
            fieldItemStack = fetchField(packet, Object.class, 0);
            fieldVecX = fetchField(packet, float.class, 0);
            fieldVecY = fetchField(packet, float.class, 1);
            fieldVecZ = fetchField(packet, float.class, 2);
            position = new BaseBlockPosition(fetch(fieldPosX), fetch(fieldPosY), fetch(fieldPosZ));
            face = EnumDirection.values()[Math.min(fetch(fieldFace), 5)];
            itemStack = toBukkitStack(fetch(fieldItemStack));
            vecX = fetch(fieldVecX);
            vecY = fetch(fieldVecY);
            vecZ = fetch(fieldVecZ);
        } else if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            fieldBlockPosition = fetchField(packet, Object.class, 1);
            fieldFace = fetchField(packet, int.class, 0);
            fieldItemStack = fetchField(packet, Object.class, 2);
            fieldVecX = fetchField(packet, float.class, 0);
            fieldVecY = fetchField(packet, float.class, 1);
            fieldVecZ = fetchField(packet, float.class, 2);
            position = new BaseBlockPosition(fetch(fieldBlockPosition));
            face = EnumDirection.values()[Math.min(fetch(fieldFace), 5)];
            itemStack = toBukkitStack(fetch(fieldItemStack));
            vecX = fetch(fieldVecX);
            vecY = fetch(fieldVecY);
            vecZ = fetch(fieldVecZ);
        } else if (!getObject().toString().contains("BlockPlace")) {
            fieldBlockPosition = fetchField("PacketPlayInUseItem", Object.class, 0);
            fieldFace1_9 = fetchField("PacketPlayInUseItem", Enum.class, 1);
            fieldVecX = fetchField("PacketPlayInUseItem", float.class, 0);
            fieldVecY = fetchField("PacketPlayInUseItem", float.class, 1);
            fieldVecZ = fetchField("PacketPlayInUseItem", float.class, 2);
            position = new BaseBlockPosition(fetch(fieldBlockPosition));
            face = EnumDirection.values()[fetch(fieldFace1_9).ordinal()];
            vecX = fetch(fieldVecX);
            vecY = fetch(fieldVecY);
            vecZ = fetch(fieldVecZ);
        }
    }
}