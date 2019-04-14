package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import lombok.val;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class BlockBox1_13_R2 implements BlockBox {
    @Override
    public List<BoundingBox> getCollidingBoxes(World world, BoundingBox box) {
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
                for (int y = minY; y < maxY; y++) {
                    org.bukkit.block.Block block = BlockUtils.getBlock(new Location(world, x, y, z));
                    if (!block.getType().equals(Material.AIR)) {
                        if (BlockUtils.collisionBoundingBoxes.containsKey(block.getType())) {
                            aabbs.add((AxisAlignedBB) BlockUtils.collisionBoundingBoxes.get(block.getType()).add(block.getLocation().toVector()).toAxisAlignedBB());
                        } else {
                            final int aX = x, aY = y, aZ = z;
                            FutureTask<?> task = new FutureTask<>(() -> {
                                net.minecraft.server.v1_13_R2.BlockPosition pos = new net.minecraft.server.v1_13_R2.BlockPosition(aX, aY, aZ);
                                net.minecraft.server.v1_13_R2.World nmsWorld = ((org.bukkit.craftbukkit.v1_13_R2.CraftWorld) world).getHandle();
                                net.minecraft.server.v1_13_R2.IBlockData nmsiBlockData = ((org.bukkit.craftbukkit.v1_13_R2.CraftWorld) world).getHandle().getType(pos);
                                net.minecraft.server.v1_13_R2.Block nmsBlock = nmsiBlockData.getBlock();

                                net.minecraft.server.v1_13_R2.VoxelShape shape = nmsiBlockData.getBlock().a(nmsiBlockData, (net.minecraft.server.v1_13_R2.IBlockAccess) nmsWorld, pos);
                                if (shape.toString().equals("EMPTY")) {
                                    aabbs.add(new net.minecraft.server.v1_13_R2.AxisAlignedBB(block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ(), block.getLocation().getX() + 1, block.getLocation().getY() + 1, block.getLocation().getZ() + 1));
                                } else {
                                    aabbs.add(shape.a().d(block.getLocation().getX(), block.getLocation().getY(), block.getLocation().getZ()));
                                }

                                if (nmsBlock instanceof net.minecraft.server.v1_13_R2.BlockShulkerBox) {
                                    net.minecraft.server.v1_13_R2.TileEntity tileentity = nmsWorld.getTileEntity(pos);
                                    net.minecraft.server.v1_13_R2.BlockShulkerBox shulker = (net.minecraft.server.v1_13_R2.BlockShulkerBox) nmsBlock;

                                    if (tileentity instanceof net.minecraft.server.v1_13_R2.TileEntityShulkerBox) {
                                        net.minecraft.server.v1_13_R2.TileEntityShulkerBox entity = (net.minecraft.server.v1_13_R2.TileEntityShulkerBox) tileentity;
                                        //Bukkit.broadcastMessage("entity");
                                        aabbs.add(entity.a(nmsiBlockData));

                                        val loc = block.getLocation();
                                        if (entity.r().toString().contains("OPEN") || entity.r().toString().contains("CLOSING")) {
                                            aabbs.add(new net.minecraft.server.v1_13_R2.AxisAlignedBB(loc.getX(), loc.getY(), loc.getZ(), loc.getX() + 1, loc.getY() + 1.5, loc.getZ() + 1));
                                        }
                                    }
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

        aabbs.stream().filter(Objects::nonNull).forEach(aabb -> boxes.add(ReflectionsUtil.toBoundingBox(aabb)));
        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.clone().toVector(), loc.clone().toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_13_R2.World world = ((org.bukkit.craftbukkit.v1_13_R2.CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new net.minecraft.server.v1_13_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new net.minecraft.server.v1_13_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).y();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity) entity).getHandle().isRiptiding();
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_13_R2.EntityLiving entity = ((org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity) player).getHandle();
        return entity.cW() != null && entity.cW().l() != net.minecraft.server.v1_13_R2.EnumAnimation.NONE;
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

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().cK();
    }
}
