package cc.funkemunky.api.utils.blockbox;

import cc.funkemunky.api.utils.world.CollisionBox;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public interface BlockBox {

    boolean isChunkLoaded(Location loc);

    boolean isRiptiding(LivingEntity entity);

    float getMovementFactor(Player player);

    float getWidth(Entity entity);

    float getHeight(Entity entity);
}
