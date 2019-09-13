package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_7_R4.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Deprecated
public class BlockBox1_7_R4 implements BlockBox {

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

                net.minecraft.server.v1_7_R4.World nmsWorld = ((CraftWorld) world).getHandle();
                net.minecraft.server.v1_7_R4.Block nmsBlock = ((CraftWorld) world).getHandle().getType((int)x, (int)y, (int)z);
                List<AxisAlignedBB> preBoxes = new ArrayList<>();

                nmsBlock.updateShape(nmsWorld, x, y, z);
                nmsBlock.a(nmsWorld, x, y, z, (AxisAlignedBB) box.toAxisAlignedBB(), preBoxes, null);


                if (preBoxes.size() > 0) {
                    for (AxisAlignedBB aabb : preBoxes) {
                        BoundingBox bb = new BoundingBox((float)aabb.a,(float)aabb.b,(float)aabb.c,(float)aabb.d,(float)aabb.e,(float)aabb.f);

                        if(bb.collides(box)) {
                            boxes.add(bb);
                        }
                    }
                } else {
                    BoundingBox bb = new BoundingBox((float)nmsBlock.x(), (float)nmsBlock.z(), (float)nmsBlock.B(), (float)nmsBlock.y(), (float)nmsBlock.A(), (float)nmsBlock.C()).add(x, y, z, x, y, z);
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

        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.toVector(), loc.toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_7_R4.World world = ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld) loc.getWorld()).getHandle();

        return !world.isStatic && world.isLoaded(loc.getBlockX(), 0, loc.getBlockZ()) && world.getChunkAtWorldCoords(loc.getBlockX(), loc.getBlockZ()).d;
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_7_R4.EntityHuman entity = ((org.bukkit.craftbukkit.v1_7_R4.entity.CraftHumanEntity) player).getHandle();
        return entity.bF() != null && entity.bF().getItem().d(entity.bF()) != net.minecraft.server.v1_7_R4.EnumAnimation.NONE;
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
        return ((CraftPlayer) player).getHandle().bl();
    }
}
