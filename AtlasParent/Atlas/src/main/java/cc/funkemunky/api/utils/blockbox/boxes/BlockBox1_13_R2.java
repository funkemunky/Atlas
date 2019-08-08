package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.MethodInvoker;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_13_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

public class BlockBox1_13_R2 implements BlockBox {

    private static MethodInvoker getShape = Reflection.getMethod(Reflection.getMinecraftClass("IBlockData"), Reflection.getMinecraftClass("VoxelShape"), 0, Reflection.getClass("IBlockAccess"), Reflection.getClass("BlockPosition"));
    private static MethodInvoker getBoundingBox = Reflection.getMethod(Reflection.getMinecraftClass("VoxelShape"), Reflection.getMinecraftClass("AxisAlignedBB"), 0);

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
                List<AxisAlignedBB> preBoxes = new ArrayList<>();

                VoxelShape shape = nmsiBlockData.getCollisionShape(nmsWorld, pos);

                if (!shape.toString().equals("EMPTY")) {
                    for (AxisAlignedBB aabb : shape.d()) {
                        BoundingBox bb = new BoundingBox((float)aabb.minX,(float)aabb.minY,(float)aabb.minZ,(float)aabb.maxX,(float)aabb.maxY,(float)aabb.maxZ);

                        if(bb.collides(box)) {
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

                            if (entity.r().toString().contains("OPEN") || entity.r().toString().contains("CLOSING")) {
                                boxes.add(new BoundingBox(block.getX(), block.getY(), block.getZ(), block.getX() + 1, block.getY() + 1.5f, block.getZ() + 1));
                            }
                        }
                    }
                } else {
                    AxisAlignedBB aabb = (AxisAlignedBB) getBoundingBox.invoke(getShape.invoke(nmsiBlockData, nmsWorld, pos));

                    if(aabb != null) {
                        BoundingBox bb = ReflectionsUtil.toBoundingBox(aabb).add(x, y, z, x, y, z);

                        if(bb.collides(box)) {
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
