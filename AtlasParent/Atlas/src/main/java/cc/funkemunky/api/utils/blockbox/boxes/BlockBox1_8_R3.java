package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockBox1_8_R3 implements BlockBox {
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
                    Location loc = new Location(world, x, y, z);

                    org.bukkit.block.Block block = BlockUtils.getBlock(loc);

                    if(block == null || block.getType().equals(Material.AIR)) continue;

                    if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                        aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                    } else {
                        net.minecraft.server.v1_8_R3.BlockPosition pos = new BlockPosition(x, y, z);
                        net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) world).getHandle();
                        net.minecraft.server.v1_8_R3.IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(pos);
                        net.minecraft.server.v1_8_R3.Block nmsBlock = nmsiBlockData.getBlock();
                        List<AxisAlignedBB> preBoxes = new ArrayList<>();

                        nmsBlock.updateShape(nmsWorld, pos);
                        nmsBlock.a(nmsWorld, pos, nmsiBlockData, (AxisAlignedBB) box.toAxisAlignedBB(), preBoxes, null);

                        if (preBoxes.size() > 0) {
                            aabbs.addAll(preBoxes);
                        } else {
                            boxes.add(new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G()).add(block.getLocation().toVector()));
                        }
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

        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()));
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_8_R3.EntityHuman entity = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity) player).getHandle();
        return entity.bS() && entity.bZ() != null && entity.bZ().getItem().e(entity.bZ()) != net.minecraft.server.v1_8_R3.EnumAnimation.NONE;
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = ((WorldServer) entityPlayer.getWorld()).tracker.trackedEntities.get(entityPlayer.getId());
        return entry.tracker.getId();
    }

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().bI();
    }
}
