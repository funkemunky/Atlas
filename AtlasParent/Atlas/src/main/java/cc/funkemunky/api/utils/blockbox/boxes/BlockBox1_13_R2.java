package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.reflection.MinecraftReflection;
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
import org.bukkit.craftbukkit.v1_13_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_13_R2.entity.CraftPlayer;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.stream.Stream;

public class BlockBox1_13_R2 implements BlockBox {

    private static MethodInvoker getShape = Reflection.getMethod(Reflection.getMinecraftClass("IBlockData"), Reflection.getMinecraftClass("VoxelShape"), 0, Reflection.getClass("IBlockAccess"), Reflection.getClass("BlockPosition"));
    private static MethodInvoker getBoundingBox = Reflection.getMethod(Reflection.getMinecraftClass("VoxelShape"), Reflection.getMinecraftClass("AxisAlignedBB"), 0);

    @Override
    public List<BoundingBox> getCollidingBoxes(org.bukkit.World world, BoundingBox box) {
        World vWorld = ((CraftWorld) world).getHandle();

        Stream<VoxelShape> voxelShapes = vWorld.a(null, (AxisAlignedBB)box.toAxisAlignedBB(), 0,0,0);

        List<BoundingBox> boxes = new ArrayList<>();

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
