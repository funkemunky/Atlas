package cc.funkemunky.api.abstraction;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public interface AbstractVersion {

    AbstractVersion version = getVersion();

    static AbstractVersion getVersion() {
        if(version == null) {
            WrappedClass wrapped = Reflections.getClass("cc.funkemunky.api.abstraction.impl.Abstract"
                    + ProtocolVersion.getGameVersion().name());

            return wrapped.getConstructor().newInstance();
        }
        return version;
    }

    //All methods
    CollisionBox getBlockBox(Block block);

    List<CollisionBox> getCollidingBoxes(World world, SimpleCollisionBox box);

    boolean isRiptiding(LivingEntity entity);

    boolean isGliding(Player player);

    boolean isChunkLoaded(Location location);

    float getMovementFactor(LivingEntity entity);

    float getBlockFriction(Block block);

}
