package cc.funkemunky.api.utils.blockbox;

import cc.funkemunky.api.utils.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface BlockBox {

    boolean isChunkLoaded(Location loc);

    boolean isRiptiding(LivingEntity entity);

    float getMovementFactor(Player player);
}
