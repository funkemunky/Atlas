package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_9_R1.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_9_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BlockBox1_9_R1 implements BlockBox {
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
                    org.bukkit.block.Block block = BlockUtils.getBlock(new Location(world, x, y, z));
                    if (!block.getType().equals(Material.AIR)) {
                        if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                            aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                        } else {
                            net.minecraft.server.v1_9_R1.World nmsWorld = ((org.bukkit.craftbukkit.v1_9_R1.CraftWorld) world).getHandle();
                            net.minecraft.server.v1_9_R1.BlockPosition pos = new net.minecraft.server.v1_9_R1.BlockPosition(x, y, z);
                            net.minecraft.server.v1_9_R1.IBlockData nmsiBlockData = ((org.bukkit.craftbukkit.v1_9_R1.CraftWorld) world).getHandle().getType(pos);
                            net.minecraft.server.v1_9_R1.Block nmsBlock = nmsiBlockData.getBlock();

                            FutureTask<List<AxisAlignedBB>> task = new FutureTask<>(() -> {
                                List<AxisAlignedBB> preBoxes = new ArrayList<>();
                                nmsBlock.updateState(nmsiBlockData, nmsWorld, pos);
                                nmsBlock.a(nmsiBlockData, nmsWorld, pos, (AxisAlignedBB) box.toAxisAlignedBB(), preBoxes, null);

                                if (preBoxes.size() > 0) {
                                    aabbs.addAll(preBoxes);
                                } else {
                                    aabbs.add(nmsBlock.a(nmsiBlockData, nmsWorld, pos));
                                }

                                return null;
                            });

                            //We check if this isn't loaded and offload it to the main thread to prevent errors or corruption.
                            if (!isChunkLoaded(block.getLocation())) {
                                Bukkit.getScheduler().runTask(Atlas.getInstance(), task);
                            } else {
                                Atlas.getInstance().getBlockBoxManager().getExecutor().submit(task);
                            }

                            try {
                                task.get();
                            } catch (InterruptedException | ExecutionException e) {
                                e.printStackTrace();
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

        aabbs.stream().filter(object -> object != null).forEach(aabb -> boxes.add(ReflectionsUtil.toBoundingBox(aabb)));
        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.toVector(), loc.toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_9_R1.World world = ((org.bukkit.craftbukkit.v1_9_R1.CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new net.minecraft.server.v1_9_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new net.minecraft.server.v1_9_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).p();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_9_R1.EntityLiving entity = ((org.bukkit.craftbukkit.v1_9_R1.entity.CraftLivingEntity) player).getHandle();
        return entity.cv() != null && entity.cv().getItem().f(entity.cv()) != net.minecraft.server.v1_9_R1.EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = ((WorldServer) entityPlayer.getWorld()).tracker.trackedEntities.get(entityPlayer.getId());
        return entry.b().getId();
    }
}
