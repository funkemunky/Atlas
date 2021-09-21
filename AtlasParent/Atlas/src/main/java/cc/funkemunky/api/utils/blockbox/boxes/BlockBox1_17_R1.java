package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.blockbox.BlockBox;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.World;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_17_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_17_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BlockBox1_17_R1 implements BlockBox {

    @Override
    public boolean isChunkLoaded(Location loc) {
        World world = ((CraftWorld)loc.getWorld()).getHandle();

        return !world.isClientSide() && world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)
                && world.getChunkAt(loc.getBlockX() >> 4, loc.getBlockX() >> 4).s();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity)entity).getHandle().isRiptiding();
    }

    @Override
    public float getMovementFactor(Player player) {
        AttributeModifiable attribute = ((CraftPlayer)player).getHandle()
                .getAttributeInstance(GenericAttributes.d);
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
