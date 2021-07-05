package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.blockbox.BlockBox;
import lombok.val;
import net.minecraft.server.v1_16_R3.GenericAttributes;
import net.minecraft.server.v1_16_R3.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BlockBox1_16_R3 implements BlockBox {

    @Override
    public boolean isChunkLoaded(Location loc) {
        World world = ((CraftWorld)loc.getWorld()).getHandle();

        return !world.isClientSide && world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)
                && world.getChunkAt(loc.getBlockX() >> 4, loc.getBlockX() >> 4).r();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity)entity).getHandle().isRiptiding();
    }

    @Override
    public float getMovementFactor(Player player) {
        val attribute = ((CraftPlayer)player).getHandle()
                .getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        return (float) (attribute != null ? attribute.getValue() : 0);
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
