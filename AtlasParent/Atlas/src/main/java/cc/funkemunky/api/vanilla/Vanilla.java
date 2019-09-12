package cc.funkemunky.api.vanilla;

import cc.funkemunky.api.tinyprotocol.packet.types.WrappedEnumAnimation;
import cc.funkemunky.api.utils.BoundingBox;
import net.minecraft.server.v1_8_R1.ItemArmorStand;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;

public interface Vanilla {
    List<BoundingBox> getCollidingBoxes(World world, BoundingBox box);

    List<BoundingBox> getSpecificBox(Block block);

    List<BoundingBox> getCollidingBoxes(Entity entity, BoundingBox box);

    boolean isChunkLoaded(Location loc);

    boolean isUsingItem(Player player);

    boolean isRiptiding(LivingEntity entity);

    WrappedEnumAnimation getAnimation(ItemStack stack);

    float getMovementFactor(Player player);

    float getFrictionFactor(Block block);

    long getPing(Player player);

    double getTPS();
}
