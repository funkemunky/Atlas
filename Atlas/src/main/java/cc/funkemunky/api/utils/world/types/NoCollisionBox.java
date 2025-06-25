package cc.funkemunky.api.utils.world.types;

import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import cc.funkemunky.api.utils.world.CollisionBox;
import org.bukkit.entity.Player;

import java.util.Collection;
import java.util.List;

public class NoCollisionBox implements CollisionBox {

    public static final NoCollisionBox INSTANCE = new NoCollisionBox();

    private NoCollisionBox() { }

    @Override
    public boolean isCollided(CollisionBox other) {
        return false;
    }

    @Override
    public boolean isIntersected(CollisionBox other) {
        return false;
    }

    @Override
    public NoCollisionBox offset(double x, double y, double z) {
        return this;
    }

    @Override
    public NoCollisionBox shrink(double x, double y, double z) {
        return this;
    }

    @Override
    public NoCollisionBox expand(double x, double y, double z) {
        return this;
    }

    @Override
    public void downCast(List<SimpleCollisionBox> list) { /**/ }

    @Override
    public boolean isNull() {
        return true;
    }

    @Override
    public NoCollisionBox copy() {
        return this;
    }

    @Override
    public void draw(WrappedEnumParticle particle, Collection<? extends Player> players) {
        // ...?
    }
}