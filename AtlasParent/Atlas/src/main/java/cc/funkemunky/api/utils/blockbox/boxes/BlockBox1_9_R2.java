package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_9_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_9_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class BlockBox1_9_R2 implements BlockBox {
    @Override
    public List<BoundingBox> getCollidingBoxes(World world, BoundingBox box) {
        List<AxisAlignedBB> aabbs = new ArrayList<>();
        List<BoundingBox> boxes = new ArrayList<>();

        double minX = box.minX;
        double maxX = box.maxX;
        double minY = box.minY;
        double maxY = box.maxY;
        double minZ = box.minZ;
        double maxZ = box.maxZ;


        for (double x = minX; x < maxX; x++) {
            for (double z = minZ; z < maxZ; z++) {
                for (double y = minY; y < maxY; y++) {
                    org.bukkit.block.Block block = BlockUtils.getBlock(new Location(world, x, y, z));
                    if (block != null && !block.getType().equals(Material.AIR)) {
                        if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                            aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                        } else {
                            net.minecraft.server.v1_9_R2.World nmsWorld = ((CraftWorld) world).getHandle();
                            net.minecraft.server.v1_9_R2.BlockPosition blockPos = new net.minecraft.server.v1_9_R2.BlockPosition(x, y, z);
                            net.minecraft.server.v1_9_R2.IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(blockPos);
                            net.minecraft.server.v1_9_R2.Block nmsBlock = nmsiBlockData.getBlock();
                            List<AxisAlignedBB> preBoxes = new ArrayList<>();
                            nmsBlock.updateState(nmsiBlockData, nmsWorld, blockPos);
                            nmsBlock.a(nmsiBlockData, nmsWorld, blockPos, (AxisAlignedBB) box.toAxisAlignedBB(), preBoxes, null);

                            if (preBoxes.size() > 0) {
                                aabbs.addAll(preBoxes);
                            } else {
                                aabbs.add(nmsBlock.a(nmsiBlockData, nmsWorld, blockPos));
                            }
                        }
                        /*
                        else {
                            BoundingBox blockBox = new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G());
                        }*/

                    }
                }
            }
        }

        for (AxisAlignedBB aabb : aabbs) {
            if(aabb == null) continue;

            boxes.add(new BoundingBox((float)aabb.a, (float)aabb.b, (float)aabb.c, (float)aabb.d, (float)aabb.e, (float)aabb.f));
        }
        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.toVector(), loc.toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_9_R2.World world = ((org.bukkit.craftbukkit.v1_9_R2.CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new net.minecraft.server.v1_9_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new net.minecraft.server.v1_9_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).p();
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_9_R2.EntityLiving entity = ((org.bukkit.craftbukkit.v1_9_R2.entity.CraftLivingEntity) player).getHandle();
        return entity.cw() != null && entity.cw().getItem().f(entity.cw()) != net.minecraft.server.v1_9_R2.EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = ((WorldServer) entityPlayer.getWorld()).tracker.trackedEntities.get(entityPlayer.getId());
        return entry.b().getId();
    }

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().cl();
    }
}
