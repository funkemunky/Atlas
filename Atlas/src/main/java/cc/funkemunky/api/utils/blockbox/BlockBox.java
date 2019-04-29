package cc.funkemunky.api.utils.blockbox;

import cc.funkemunky.api.utils.BoundingBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface BlockBox {
    List<BoundingBox> getCollidingBoxes(World world, BoundingBox box);

    List<BoundingBox> getSpecificBox(Location location);

    boolean isChunkLoaded(Location loc);

    boolean isUsingItem(Player player);

    boolean isRiptiding(LivingEntity entity);

    float getMovementFactor(Player player);

    int getTrackerId(Player player);

    float getAiSpeed(Player player);
}
