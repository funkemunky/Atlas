package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_8_R1.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class BlockBox1_8_R1 implements BlockBox {

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

        boolean chunkLoaded = isChunkLoaded(box.getMinimum().toLocation(world));

        if(chunkLoaded) {
            locs.parallelStream().forEach(loc -> {
                org.bukkit.block.Block block = loc.getBlock();
            if (block != null && !block.getType().equals(Material.AIR)) {
                int x = block.getX(), y = block.getY(), z = block.getZ();

                BlockPosition pos = new BlockPosition(x, y, z);
                World nmsWorld = ((CraftWorld) world).getHandle();
                IBlockData nmsiBlockData = ((CraftWorld) world).getHandle().getType(pos);
                Block nmsBlock = nmsiBlockData.getBlock();
                List<AxisAlignedBB> preBoxes = new ArrayList<>();

                nmsBlock.updateShape(nmsWorld, pos);
                nmsBlock.a(nmsWorld, pos, nmsiBlockData, (AxisAlignedBB) box.toAxisAlignedBB(), preBoxes, null);

                if (preBoxes.size() > 0) {
                    for (AxisAlignedBB aabb : preBoxes) {
                        BoundingBox bb = new BoundingBox((float)aabb.a,(float)aabb.b,(float)aabb.c,(float)aabb.d,(float)aabb.e,(float)aabb.f);

                        if(bb.collides(box)) {
                            boxes.add(bb);
                        }
                    }
                } else {
                    BoundingBox bb = new BoundingBox((float)nmsBlock.z(), (float)nmsBlock.B(), (float)nmsBlock.D(), (float)nmsBlock.A(), (float)nmsBlock.C(), (float)nmsBlock.E()).add(x, y, z, x, y, z);
                    if(bb.collides(box)) {
                        boxes.add(bb);
                    }
                }
                        /*
                        else {
                            BoundingBox blockBox = new BoundingBox((float) nmsBlock.B(), (float) nmsBlock.D(), (float) nmsBlock.F(), (float) nmsBlock.C(), (float) nmsBlock.E(), (float) nmsBlock.G());
                        }*/

            }
            });
        }

        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.toVector(), loc.toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {

        net.minecraft.server.v1_8_R1.World world = ((org.bukkit.craftbukkit.v1_8_R1.CraftWorld) loc.getWorld()).getHandle();

        return !world.isStatic && world.isLoaded(new net.minecraft.server.v1_8_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new net.minecraft.server.v1_8_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).o();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_8_R1.EntityHuman entity = ((org.bukkit.craftbukkit.v1_8_R1.entity.CraftHumanEntity) player).getHandle();
        return entity.bY() != null && entity.bY().getItem().e(entity.bY()) != net.minecraft.server.v1_8_R1.EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
    }

    @Override
    public int getTrackerId(Player player) {
        EntityPlayer entityPlayer = ((CraftPlayer) player).getHandle();
        EntityTrackerEntry entry = (EntityTrackerEntry) ((WorldServer) entityPlayer.getWorld()).tracker.trackedEntities.get(entityPlayer.getId());
        return entry.tracker.getId();
    }

    @Override
    public float getAiSpeed(Player player) {
        return ((CraftPlayer) player).getHandle().bH();
    }
}
