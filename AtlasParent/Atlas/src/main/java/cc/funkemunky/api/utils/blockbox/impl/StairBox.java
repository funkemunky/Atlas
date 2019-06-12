package cc.funkemunky.api.utils.blockbox.impl;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.ReflectionsUtil;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.lang.reflect.Method;
import java.util.Collections;
import java.util.List;

public class StairBox extends BlockBox {
    public StairBox(Material material) {
        super(material, new BoundingBox(0,0,0,1,1,1));
    }

    @Override
    List<BoundingBox> getBox(Block block) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            return Atlas.getInstance().getBlockBoxManager().getBlockBox().getSpecificBox(block.getLocation());
        } else {
            Object vBlock = ReflectionsUtil.getVanillaBlock(block);
            Object world = ReflectionsUtil.getWorldHandle(block.getWorld());
            Method voxelShapeMethod = ReflectionsUtil.getMethod(ReflectionsUtil.getNMSClass("BlockStairs"), "a", ReflectionsUtil.iBlockData, ReflectionsUtil.iBlockAccess, ReflectionsUtil.blockPosition);
            Object voxelShape = ReflectionsUtil.getMethodValue(voxelShapeMethod, vBlock, ReflectionsUtil.getBlockData(block), world, ReflectionsUtil.getBlockPosition(block.getLocation()));

            return Collections.singletonList(ReflectionsUtil.toBoundingBox(ReflectionsUtil.getMethodValue(ReflectionsUtil.getMethod(ReflectionsUtil.getNMSClass("VoxelShape"), "a"), voxelShape)).add(block.getLocation().toVector()));
        }
    }
}
