package cc.funkemunky.api.utils.world.types;

import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import cc.funkemunky.api.utils.BoundingBox;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.world.CollisionBox;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Collection;
import java.util.List;

public class SimpleCollisionBox implements CollisionBox {
    public double xMin, yMin, zMin, xMax, yMax, zMax;

    public SimpleCollisionBox() {
        this(0, 0, 0, 0, 0, 0);
    }

    public SimpleCollisionBox(double xMin, double yMin, double zMin, double xMax, double yMax, double zMax) {
        if (xMin < xMax) {
            this.xMin = xMin;
            this.xMax = xMax;
        } else {
            this.xMin = xMax;
            this.xMax = xMin;
        }
        if (yMin < yMax) {
            this.yMin = yMin;
            this.yMax = yMax;
        } else {
            this.yMin = yMax;
            this.yMax = yMin;
        }
        if (zMin < zMax) {
            this.zMin = zMin;
            this.zMax = zMax;
        } else {
            this.zMin = zMax;
            this.zMax = zMin;
        }
    }

    public SimpleCollisionBox(Vector min, Vector max) {
        this(min.getX(), min.getY(), min.getZ(), max.getX(), max.getY(), max.getZ());
    }
    
    public SimpleCollisionBox(Location loc, double width, double height) {
        this(loc.toVector(), width, height);
    }

    public SimpleCollisionBox(Vector vec, double width, double height) {
        this(vec.getX(), vec.getY(), vec.getZ(), vec.getX(), vec.getY(), vec.getZ());

        expand(width / 2, 0, width / 2);
        yMax+= height;
    }

    public SimpleCollisionBox(BoundingBox box) {
        this(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
    }

    public SimpleCollisionBox(Object aabb) {
        this(MinecraftReflection.fromAABB(aabb));
    }

    private void sort() {
        double temp = 0;
        if (xMin >= xMax) {
            temp = xMin;
            this.xMin = xMax;
            this.xMax = temp;
        }
        if (yMin >= yMax) {
            temp = yMin;
            this.yMin = yMax;
            this.yMax = temp;
        }
        if (zMin >= zMax) {
            temp = zMin;
            this.zMin = zMax;
            this.zMax = temp;
        }
    }

    public SimpleCollisionBox copy() {
        return new SimpleCollisionBox(xMin, yMin, zMin, xMax, yMax, zMax);
    }

    public SimpleCollisionBox offset(double x, double y, double z) {
        this.xMin += x;
        this.yMin += y;
        this.zMin += z;
        this.xMax += x;
        this.yMax += y;
        this.zMax += z;
        return this;
    }

    @Override
    public void downCast(List<SimpleCollisionBox> list) {
        list.add(this);
    }

    @Override
    public boolean isNull() {
        return false;
    }

    public SimpleCollisionBox expandMin(double x, double y, double z) {
        this.xMin += x;
        this.yMin += y;
        this.zMin += z;
        return this;
    }

    public SimpleCollisionBox expandMax(double x, double y, double z) {
        this.xMax += x;
        this.yMax += y;
        this.zMax += z;
        return this;
    }

    public SimpleCollisionBox expand(double x, double y, double z) {
        this.xMin -= x;
        this.yMin -= y;
        this.zMin -= z;
        this.xMax += x;
        this.yMax += y;
        this.zMax += z;
        return this;
    }

    public SimpleCollisionBox expand(double value) {
        this.xMin -= value;
        this.yMin -= value;
        this.zMin -= value;
        this.xMax += value;
        this.yMax += value;
        this.zMax += value;
        return this;
    }

    public Vector[] corners() {
        sort();
        Vector[] vectors = new Vector[8];
        vectors[0] = new Vector(xMin,yMin,zMin);
        vectors[1] = new Vector(xMin,yMin,zMax);
        vectors[2] = new Vector(xMax,yMin,zMin);
        vectors[3] = new Vector(xMax,yMin,zMax);
        vectors[4] = new Vector(xMin,yMax,zMin);
        vectors[5] = new Vector(xMin,yMax,zMax);
        vectors[6] = new Vector(xMax,yMax,zMin);
        vectors[7] = new Vector(xMax,yMax,zMax);
        return vectors;
    }

    @Override
    public boolean isCollided(CollisionBox other) {
        if (other instanceof SimpleCollisionBox) {
            SimpleCollisionBox box = ((SimpleCollisionBox) other);
            return box.xMax > this.xMin && box.xMin < this.xMax
                    && box.yMax > this.yMin && box.yMin < this.yMax
                    && box.zMax > this.zMin && box.zMin < this.zMax;
        } else {
            return other.isCollided(this);
            // throw new IllegalStateException("Attempted to check collision with " + other.getClass().getSimpleName());
        }
    }

    public void draw(WrappedEnumParticle particle, Collection<? extends Player> players) {
        SimpleCollisionBox box = this.copy().expand(0.025);
        MiscUtils.drawCuboid(box, particle, players);
    }

    public BoundingBox toBoundingBox() {
        return new BoundingBox(new Vector(xMin, yMin, zMin), new Vector(xMax, yMax, zMax));
    }

    public double distance(SimpleCollisionBox box) {
        return Math.hypot(xMax - box.xMax, zMin - box.zMin);
    }
}