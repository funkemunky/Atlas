package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.reflection.MinecraftReflection;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_8_R2.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Vector;
import java.util.stream.Collectors;

public class BlockBox1_8_R2 implements BlockBox {
   @Override
    public List<BoundingBox> getCollidingBoxes(org.bukkit.World world, BoundingBox box) {
       int minX = MathUtils.floor(box.minX);
       int maxX = MathUtils.floor(box.maxX + 1);
       int minY = MathUtils.floor(box.minY);
       int maxY = MathUtils.floor(box.maxY + 1);
       int minZ = MathUtils.floor(box.minZ);
       int maxZ = MathUtils.floor(box.maxZ + 1);

       if(!isChunkLoaded(box.getMinimum().toLocation(world))) return Collections.emptyList();

       List<Location> locs = new ArrayList<>();

       for (int x = minX; x < maxX; x++) {
           for (int z = minZ; z < maxZ; z++) {
               for (int y = minY - 1; y < maxY; y++) {
                   Location loc = new Location(world, x, y, z);
                   locs.add(loc);
               }
           }
       }

       WorldServer vanillaWorld = ((CraftWorld)world).getHandle();
       AxisAlignedBB aabb = (AxisAlignedBB) box.toAxisAlignedBB();

       Vector<AxisAlignedBB> vector = new Vector<>();

       locs.parallelStream().forEach(loc -> {
           BlockPosition pos = new BlockPosition(loc.getBlockX(), loc.getBlockY(), loc.getBlockZ());
           Block block = vanillaWorld.c(pos);
           block.a(vanillaWorld, pos, block.getBlockData(), aabb, vector, null);
       });

       return vector.parallelStream().map(MinecraftReflection::fromAABB).collect(Collectors.toList());
    }


    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.toVector(), loc.toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {

        net.minecraft.server.v1_8_R2.World world = ((org.bukkit.craftbukkit.v1_8_R2.CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new net.minecraft.server.v1_8_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())) && world.getChunkAtWorldCoords(new net.minecraft.server.v1_8_R2.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).o();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_8_R2.EntityHuman entity = ((org.bukkit.craftbukkit.v1_8_R2.entity.CraftHumanEntity) player).getHandle();
        return entity.bZ() != null && entity.bZ().getItem().e(entity.bZ()) != net.minecraft.server.v1_8_R2.EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
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
