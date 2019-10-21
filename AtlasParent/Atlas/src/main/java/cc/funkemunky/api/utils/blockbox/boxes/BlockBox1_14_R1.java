package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.reflection.MinecraftReflection;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_14_R1.AxisAlignedBB;
import net.minecraft.server.v1_14_R1.GenericAttributes;
import net.minecraft.server.v1_14_R1.VoxelShape;
import net.minecraft.server.v1_14_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_14_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_14_R1.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.HashSet;
import java.util.List;
import java.util.Vector;
import java.util.stream.Stream;

public class BlockBox1_14_R1 implements BlockBox {

    @Override
    public List<BoundingBox> getCollidingBoxes(org.bukkit.World world, BoundingBox box) {
        World vWorld = ((CraftWorld) world).getHandle();

        Stream<VoxelShape> voxelShapes = vWorld.b(null, (AxisAlignedBB) box.toAxisAlignedBB(), new HashSet<>());

        Vector<BoundingBox> boxes = new Vector<>();

        voxelShapes.parallel().map(VoxelShape::d)
                .forEach(list -> list.stream().map(MinecraftReflection::fromAABB).forEach(boxes::add));

        return boxes;
    }

    @Override
    public List<BoundingBox> getSpecificBox(Location loc) {
        return getCollidingBoxes(loc.getWorld(), new BoundingBox(loc.clone().toVector(), loc.clone().toVector()));
    }

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_14_R1.World world = ((org.bukkit.craftbukkit.v1_14_R1.CraftWorld) loc.getWorld())
                .getHandle();

        return !world.isClientSide
                && world.isLoaded(new net.minecraft.server.v1_14_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()))
                && world.getChunkAtWorldCoords(
                        new net.minecraft.server.v1_14_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).r();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity) entity).getHandle().isRiptiding();
    }

    @Override
    public boolean isUsingItem(Player player) {
        net.minecraft.server.v1_14_R1.EntityLiving entity =
                ((org.bukkit.craftbukkit.v1_14_R1.entity.CraftLivingEntity) player).getHandle();
        return entity.dl() != null && entity.dl().l() != net.minecraft.server.v1_14_R1.EnumAnimation.NONE;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
                .getValue();
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
