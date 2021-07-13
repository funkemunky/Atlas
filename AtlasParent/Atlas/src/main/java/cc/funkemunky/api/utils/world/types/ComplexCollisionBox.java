package cc.funkemunky.api.utils.world.types;

import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import cc.funkemunky.api.utils.world.CollisionBox;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class ComplexCollisionBox implements CollisionBox {
    private List<CollisionBox> boxes = new ArrayList<>();

    public ComplexCollisionBox(CollisionBox... boxes) {
        Collections.addAll(this.boxes, boxes);
    }

    public boolean add(CollisionBox collisionBox) {
        return boxes.add(collisionBox);
    }

    @Override
    public boolean isCollided(CollisionBox other) {
        return boxes.stream().anyMatch(box -> box.isCollided(other));
    }

    @Override
    public boolean isIntersected(CollisionBox other) {
        return boxes.stream().anyMatch(box -> box.isIntersected(other));
    }

    public void draw(WrappedEnumParticle particle, Collection<? extends Player> players) {
        for (CollisionBox b : boxes)
            b.draw(particle,players);
    }

    @Override
    public ComplexCollisionBox copy() {
        ComplexCollisionBox cc = new ComplexCollisionBox();
        for (CollisionBox b : boxes)
            cc.boxes.add(b.copy());
        return cc;
    }

    @Override
    public ComplexCollisionBox offset(double x, double y, double z) {
        for (CollisionBox b : boxes)
            b.offset(x,y,z);
        return this;
    }

    @Override
    public ComplexCollisionBox shrink(double x, double y, double z) {
        for (CollisionBox b : boxes)
            b.shrink(x,y,z);
        return this;
    }

    @Override
    public ComplexCollisionBox expand(double x, double y, double z) {
        for (CollisionBox b : boxes)
            b.expand(x,y,z);
        return this;
    }

    @Override
    public void downCast(List<SimpleCollisionBox> list) {
        for (CollisionBox box : boxes)
            box.downCast(list);
    }

    @Override
    public boolean isNull() {
        for(CollisionBox box : boxes)
            if (!box.isNull())
                return false;
        return true;
    }

}