package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_14_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBox1_14_R1 implements BlockBox {

    @Override
    public List<BoundingBox> getCollidingBoxes(org.bukkit.World world, BoundingBox box) {
        int minX = MathUtils.floor(box.minX);
        int maxX = MathUtils.floor(box.maxX + 1);
        int minY = MathUtils.floor(box.minY);
        int maxY = MathUtils.floor(box.maxY + 1);
        int minZ = MathUtils.floor(box.minZ);
        int maxZ = MathUtils.floor(box.maxZ + 1);

        List<Location> locs = new ArrayList<>();

        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    Location loc = new Location(world, x, y, z);
                    locs.add(loc);
                }
            }
        }

        List<BoundingBox> boxes = Collections.synchronizedList(new ArrayList<>());

        locs.parallelStream().forEach(loc -> {
            org.bukkit.block.Block block = BlockUtils.getBlock(loc);
            if (block != null && !block.getType().equals(Material.AIR)) {
                int x = block.getX(), y = block.getY(), z = block.getZ();

                BlockPosition pos = new BlockPosition(x, y, z);
                World nmsWorld = ((CraftWorld) world).getHandle();
                IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(pos);
                Block nmsBlock = nmsiBlockData.getBlock();

                VoxelShape shape = nmsiBlockData.getCollisionShape(nmsWorld, pos);

                if (!shape.toString().equals("EMPTY")) {
                    for (AxisAlignedBB aabb : shape.d()) {
                        BoundingBox bb = new BoundingBox((float) aabb.minX, (float) aabb.minY, (float) aabb.minZ, (float) aabb.maxX, (float) aabb.maxY, (float) aabb.maxZ);

                        if (bb.collides(box)) {
                            boxes.add(bb);
                        }
                    }

                    if (nmsBlock instanceof BlockShulkerBox) {
                        TileEntity tileentity = nmsWorld.getTileEntity(pos);
                        BlockShulkerBox shulker = (BlockShulkerBox) nmsBlock;

                        if (tileentity instanceof TileEntityShulkerBox) {
                            TileEntityShulkerBox entity = (TileEntityShulkerBox) tileentity;
                            //Bukkit.broadcastMessage("entity");
                            boxes.add(ReflectionsUtil.toBoundingBox(entity.a(nmsiBlockData)));

                            if (entity.s().toString().contains("OPEN") || entity.s().toString().contains("CLOSING")) {
                                boxes.add(new BoundingBox(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1.5f, block.getZ() + 1));
                            }
                        }
                    }
                } else {
                    AxisAlignedBB aabb = nmsiBlockData.getCollisionShape(nmsWorld, pos).getBoundingBox();
                    if(aabb != null) {
                        BoundingBox bb = ReflectionsUtil.toBoundingBox(aabb).add(x, y, z, x, y, z);

                        if (bb.collides(box)) {
                            boxes.add(bb);
                        }
                    }
                }
            }
        });

        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.clone().toVector(), loc.clone().toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_14_R1.World world = ((org.bukkit.craftbukkit.v1_14_R1.CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new net.minecraft.server.v1_14_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new net.minecraft.server.v1_14_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).r();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity) entity).getHandle().isRiptiding();
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_14_R1.EntityLiving entity = ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity) player).getHandle();
        return entity.dl() != null && entity.dl().l() != net.minecraft.server.v1_14_R1.EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    @Override
    public int getTrackerId(Player player) {
        return player.getEntityId();
    }

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().da();
    }
}
