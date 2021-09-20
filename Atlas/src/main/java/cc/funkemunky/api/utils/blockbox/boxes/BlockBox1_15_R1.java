package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.server.v1_15_R1.GenericAttributes;
import net.minecraft.server.v1_15_R1.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_15_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_15_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BlockBox1_15_R1 implements BlockBox {

    @Override
    public boolean isChunkLoaded(Location loc) {
        World world = ((CraftWorld) loc.getWorld())
                .getHandle();

        return !world.isClientSide
                && world.isLoaded(new net.minecraft.server.v1_15_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()))
                && world.getChunkAtWorldCoords(
                        new net.minecraft.server.v1_15_R1.BlockPosition(loc.getBlockX(), 0, loc.getBlockZ())).r();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity) entity).getHandle().isRiptiding();
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.MOVEMENT_SPEED)
                .getValue();
    }

    @Override
    public float getWidth(Entity entity) {
        return (float) ((CraftEntity)entity).getWidth();
    }

    @Override
    public float getHeight(Entity entity) {
        return (float) ((CraftEntity)entity).getHeight();
    }
}
