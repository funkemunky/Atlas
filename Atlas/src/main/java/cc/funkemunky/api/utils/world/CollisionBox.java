package cc.funkemunky.api.utils.world;

import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public interface CollisionBox {
    boolean isCollided(CollisionBox other);
    boolean isIntersected(CollisionBox other);
    void draw(WrappedEnumParticle particle, Collection<? extends Player> players);
    CollisionBox copy();
    CollisionBox offset(double x, double y, double z);
    CollisionBox shrink(double x, double y, double z);
    CollisionBox expand(double x, double y, double z);
    void downCast(List<SimpleCollisionBox> list);
    boolean isNull();
}