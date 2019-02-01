package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
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
        BoundingBox collisionBox = box;
        List<AxisAlignedBB> aabbs = new ArrayList<>();
        List<BoundingBox> boxes = new ArrayList<>();

        int minX = MathUtils.floor(box.minX);
        int maxX = MathUtils.floor(box.maxX + 1);
        int minY = MathUtils.floor(box.minY);
        int maxY = MathUtils.floor(box.maxY + 1);
        int minZ = MathUtils.floor(box.minZ);
        int maxZ = MathUtils.floor(box.maxZ + 1);


        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    Location loc = new Location(world, x, y, z);

                    if(isChunkLoaded(loc)) {
                        org.bukkit.block.Block block = BlockUtils.getBlock(loc);
                        if (!block.getType().equals(Material.AIR)) {
                            if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                                aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                            } else {
                                net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) world).getHandle();
                                net.minecraft.server.v1_8_R3.IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(x, y, z));
                                net.minecraft.server.v1_8_R3.Block nmsBlock = nmsiBlockData.getBlock();


                                nmsBlock.a(nmsWorld, new BlockPosition(x, y, z), nmsiBlockData, (AxisAlignedBB) box.toAxisAlignedBB(), aabbs, null);
                            }
                        /*
                        else {
                            BoundingBox blockBox = new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G());
                        }*/

                        }
                    }
                }
            }
        }

        aabbs.forEach(aabb -> boxes.add(ReflectionsUtil.toBoundingBox(aabb)));
        return boxes;
    }


    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        World world = loc.getWorld();

        AxisAlignedBB collisionBox = (AxisAlignedBB) new BoundingBox(loc.toVector(), loc.toVector()).grow(1f, 1f, 1f).toAxisAlignedBB();
        List<AxisAlignedBB> boxList = ((CraftWorld) world).getHandle().a(collisionBox);

        List<AxisAlignedBB> aabbs = new ArrayList<>();
        List<BoundingBox> boxes = new ArrayList<>();

        BoundingBox box = new BoundingBox(loc.toVector(), loc.toVector()).grow(2, 2, 2);
        int minX = MathUtils.floor(box.minX);
        int maxX = MathUtils.floor(box.maxX + 1);
        int minY = MathUtils.floor(box.minY);
        int maxY = MathUtils.floor(box.maxY + 1);
        int minZ = MathUtils.floor(box.minZ);
        int maxZ = MathUtils.floor(box.maxZ + 1);


        for (int x = minX; x < maxX; x++) {
            for (int z = minZ; z < maxZ; z++) {
                for (int y = minY - 1; y < maxY; y++) {
                    org.bukkit.block.Block block = BlockUtils.getBlock(new Location(world, x, y, z));
                    if (!block.getType().equals(Material.AIR) && block.getLocation().equals(loc)) {
                        if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                            aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                        } else {
                            net.minecraft.server.v1_8_R3.World nmsWorld = ((CraftWorld) world).getHandle();
                            net.minecraft.server.v1_8_R3.IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(new BlockPosition(x, y, z));
                            net.minecraft.server.v1_8_R3.Block nmsBlock = nmsiBlockData.getBlock();

                            nmsBlock.a(nmsWorld, new BlockPosition(x, y, z), nmsiBlockData, (AxisAlignedBB) box.toAxisAlignedBB(), aabbs, null);
                        }
                        /*
                        else {
                            BoundingBox blockBox = new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G());
                        }*/

                    }
                }
            }
        }
        aabbs.forEach(aabb -> boxes.add(ReflectionsUtil.toBoundingBox(aabb)));
        return boxes;
    }


    @Override
    public boolean isChunkLoaded(Location loc) {

        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).o();
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_8_R3.EntityHuman entity = ((org.bukkit.craftbukkit.v1_8_R3.entity.CraftHumanEntity) player).getHandle();
        return entity.bS() && entity.bZ().getItem().e(entity.bZ()) != net.minecraft.server.v1_8_R3.EnumAnimation.NONE;
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
}
