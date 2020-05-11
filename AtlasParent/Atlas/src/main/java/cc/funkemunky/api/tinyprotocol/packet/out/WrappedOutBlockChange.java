package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.utils.ReflectionsUtil;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutBlockChange extends NMSObject {
    private static final String packet = Packet.Server.BLOCK_CHANGE;

    //1.7.10 and below
    private static FieldAccessor<Integer> legacyX;
    private static FieldAccessor<Integer> legacyY;
    private static FieldAccessor<Integer> legacyZ;
    private static WrappedField blockDataIntField;
    private static WrappedMethod getDataMethod;

    //1.8 and newer
    private static FieldAccessor<Object> blockPosAccessor;

    private static WrappedField blockField;


    private static WrappedClass blockChangeClass = Reflections.getNMSClass(packet);

    private BaseBlockPosition position;
    private Material type;

    public WrappedOutBlockChange(Object packet) {
        super(packet);
    }

    public WrappedOutBlockChange(Block block) {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            setPacket(packet, block.getX(), block.getY(), block.getZ(), ReflectionsUtil.getWorldHandle(block.getWorld()));
        } else {
            setPacket(packet, ReflectionsUtil.getWorldHandle(block.getWorld()), new BaseBlockPosition(block.getX(), block.getY(), block.getZ()).getAsBlockPosition());
        }
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            position = new BaseBlockPosition(fetch(legacyX), fetch(legacyY), fetch(legacyZ));
            type = CraftReflection.getTypeFromVanillaBlock(fetch(blockField));
        } else {
            position = new BaseBlockPosition(fetch(blockPosAccessor));
            type = CraftReflection.getTypeFromVanillaBlock(MinecraftReflection.getBlockFromBlockData(fetch(blockField)));
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            legacyX.set(getObject(), position.getX());
            legacyY.set(getObject(), position.getY());
            legacyZ.set(getObject(), position.getZ());
            blockField.set(getObject(), CraftReflection.getVanillaBlock(type));
        } else {
            blockPosAccessor.set(getObject(), position.getAsBlockPosition());
            blockField.set(getObject(), MinecraftReflection.getBlockData(CraftReflection.getVanillaBlock(type)));
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            legacyX = fetchField(packet, int.class, 0);
            legacyY = fetchField(packet, int.class, 1);
            legacyZ = fetchField(packet, int.class, 2);

            blockField = blockChangeClass.getFirstFieldByType(MinecraftReflection.block.getParent());
            blockDataIntField = blockChangeClass.getFieldByName("data");
            getDataMethod = Reflections.getNMSClass("World").getMethod("getData", int.class, int.class, int.class);
        } else {
            blockPosAccessor = fetchField(packet, Object.class, 0);
            blockField = blockChangeClass.getFieldByType(ReflectionsUtil.iBlockData, 0);
        }
    }
}
