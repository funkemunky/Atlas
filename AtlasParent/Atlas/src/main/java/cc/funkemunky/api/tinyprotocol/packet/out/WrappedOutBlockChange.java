package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
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
import cc.funkemunky.api.utils.XMaterial;
import lombok.Getter;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutBlockChange extends NMSObject {
    private static final String packet = Packet.Server.BLOCK_CHANGE;

    //1.7.10 and below
    private static WrappedField legacyX, legacyY, legacyZ, blockChangeBlockField;
    private static WrappedField blockDataIntField;
    private static WrappedMethod getDataMethod;

    //1.8 and newer
    private static WrappedField iBlockDataField, blockPosAccessor;


    private static final WrappedClass blockChangeClass = Reflections.getNMSClass(packet);
    private static WrappedClass nmsBlockClass;

    private BaseBlockPosition position;
    private XMaterial material;
    private byte data;

    public WrappedOutBlockChange(Object packet, Player player) {
        super(packet, player);
    }

    public WrappedOutBlockChange(Block block) {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            setPacket(packet, block.getX(), block.getY(), block.getZ(), ReflectionsUtil
                    .getWorldHandle(block.getWorld()));
        } else {
            setPacket(packet, ReflectionsUtil.getWorldHandle(block.getWorld()),
                    new BaseBlockPosition(block.getX(), block.getY(), block.getZ()).getAsBlockPosition());
        }
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            position = new BaseBlockPosition(fetch(legacyX), fetch(legacyY), fetch(legacyZ));
            data = (byte)(int)fetch(iBlockDataField);
        } else {
            position = new BaseBlockPosition(fetch(blockPosAccessor));
            Object iBlockData = fetch(iBlockDataField);
            material = XMaterial.matchXMaterial(MinecraftReflection
                    .getMaterialFromVanillaBlock(MinecraftReflection
                            .getBlockFromData(iBlockData)));
            data = MinecraftReflection.toLegacyData(material.parseMaterial(), iBlockData);
        }
    }

    @Override
    public void updateObject() {

    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            legacyX = fetchField(blockChangeClass, int.class, 0);
            legacyY = fetchField(blockChangeClass, int.class, 1);
            legacyZ = fetchField(blockChangeClass, int.class, 2);
            iBlockDataField = fetchField(blockChangeClass, int.class, 3);

            nmsBlockClass = Reflections.getNMSClass("Block");
            blockChangeBlockField = blockChangeClass.getFirstFieldByType(nmsBlockClass.getParent());
            blockDataIntField = blockChangeClass.getFieldByName("data");
            getDataMethod = Reflections.getNMSClass("World").getMethod("getData",
                    int.class, int.class, int.class);
        } else {
            blockPosAccessor = blockChangeClass.getFieldByType(MinecraftReflection.blockPos.getParent(), 0);
            iBlockDataField = blockChangeClass.getFieldByType(MinecraftReflection.iBlockData.getParent(), 0);
        }
    }
}
