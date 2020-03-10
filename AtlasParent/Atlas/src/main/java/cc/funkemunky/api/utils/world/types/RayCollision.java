package cc.funkemunky.api.utils.world.types;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.Tuple;
import cc.funkemunky.api.utils.math.RayTrace;
import cc.funkemunky.api.utils.world.BlockData;
import cc.funkemunky.api.utils.world.CollisionBox;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

public class RayCollision implements CollisionBox {

    public double originX;
    public double originY;
    public double originZ;
    public double directionX;
    public double directionY;
    public double directionZ;

    public RayCollision(double originX, double originY, double originZ, double directionX, double directionY, double directionZ) {
        this.originX = originX;
        this.originY = originY;
        this.originZ = originZ;
        this.directionX = directionX;
        this.directionY = directionY;
        this.directionZ = directionZ;
    }

    public RayCollision(RayCollision ray) {
        this.originX = ray.originX;
        this.originY = ray.originY;
        this.originZ = ray.originZ;
        this.directionX = ray.directionX;
        this.directionY = ray.directionY;
        this.directionZ = ray.directionZ;
    }

    public RayCollision() {
        originX = 0;
        originY = 0;
        originZ = 0;
        directionX = 0;
        directionY = 0;
        directionZ = 0;
    }

    public RayCollision(LivingEntity e) {
        this(e.getEyeLocation());
    }

    public RayCollision(Location l) {
        this(l.toVector(),l.getDirection());
    }

    public RayCollision(Vector position, Vector direction) {
        this.originX = position.getX();
        this.originY = position.getY();
        this.originZ = position.getZ();
        this.directionX = direction.getX();
        this.directionY = direction.getY();
        this.directionZ = direction.getZ();
    }

    public Vector getOrigin() {
        return new Vector(originX, originY, originZ);
    }

    public Vector getDirection() {
        return new Vector(directionX, directionY, directionZ);
    }

    @Override
    public boolean isCollided(CollisionBox other) {
        if (other instanceof SimpleCollisionBox) {
            return intersect(this, (SimpleCollisionBox) other);
        } else if (other instanceof RayCollision) {
            return false; // lol no support
        }
        return false;
    }

    @Override
    public void draw(WrappedEnumParticle particle, Collection<? extends Player> players) {
        MiscUtils.drawRay(this,particle, players);
    }

    @Override
    public CollisionBox copy() {
        return new RayCollision(originX,originY,originZ,directionX,directionY,directionZ);
    }

    @Override
    public CollisionBox offset(double x, double y, double z) {
        originX+=x;
        originY+=y;
        originY+=z;
        return this;
    }
    
    public List<CollisionBox> boxesOnRay(World world, double distance) {
        int amount = Math.round((float)(distance / 0.5));
        Location[] locs = new Location[Math.max(2, amount)]; //We do a max to prevent NegativeArraySizeException.
        for (int i = 0; i < locs.length; i++) {
            double ix = i / 2d;

            double fx = (originX + (directionX * ix));
            double fy = (originY + (directionY * ix));
            double fz = (originZ + (directionZ * ix));

            locs[i] = new Location(world, fx, fy, fz);
        }
        return Arrays.stream(locs).parallel()
                .filter(loc -> loc.getWorld().isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4))
                .map(Location::getBlock)
                .filter(block -> block.getType().isSolid())
                .map(block -> BlockData.getData(block.getType()).getBox(block, ProtocolVersion.getGameVersion()))
                .filter(this::isCollided).collect(Collectors.toList());
    }

    @Override
    public void downCast(List<SimpleCollisionBox> list) {/*Do Nothing, Ray cannot be down-casted*/}

    @Override
    public boolean isNull() {
        return true;
    }

    public static double distance(RayCollision ray, SimpleCollisionBox box) {
        Tuple<Double,Double> pair = new Tuple<>();
        if (intersect(ray,box,pair))
            return pair.one;
        return -1;
    }

    public static double distance(RayCollision ray, SimpleCollisionBox box, boolean exact, double range) {
        double dist = RayCollision.distance(ray, box);

        if(!exact || dist == -1) return dist;

        RayTrace trace = new RayTrace(ray.getOrigin(), ray.getDirection());

        Vector point = trace.positionOfIntersection(box, Math.max(0, dist - range), 0.01);

        return ray.getOrigin().distance(point);
    }

    public static double distance(RayCollision ray, SimpleCollisionBox box, boolean exact) {
        return distance(ray, box, exact, 0.12);
    }

    public static boolean intersect(RayCollision ray, SimpleCollisionBox aab) {
        double invDirX = 1.0 / ray.directionX;
        double invDirY = 1.0 / ray.directionY;
        double invDirZ = 1.0 / ray.directionZ;
        Vector lb = aab.min(), rt = aab.max();

        double t1 = (lb.getX() - ray.originX) * invDirX;
        double t2 = (rt.getX() - ray.originX) * invDirX;
        double t3 = (lb.getY() - ray.originY) * invDirY;
        double t4 = (rt.getY() - ray.originY) * invDirY;
        double t5 = (lb.getZ() - ray.originZ) * invDirZ;
        double t6 = (rt.getZ() - ray.originZ) * invDirZ;

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        if (tmax < 0)
        {
            return false;
        }

        if (tmin > tmax)
        {
            return false;
        }

        return true;
    }

    // Result X = near
    // Result Y = far
    public static boolean intersect(RayCollision ray, SimpleCollisionBox aab, Tuple<Double,Double> result) {
        double invDirX = 1.0 / ray.directionX;
        double invDirY = 1.0 / ray.directionY;
        double invDirZ = 1.0 / ray.directionZ;
        Vector lb = aab.min(), rt = aab.max();

        double t1 = (lb.getX() - ray.originX) * invDirX;
        double t2 = (rt.getX() - ray.originX) * invDirX;
        double t3 = (lb.getY() - ray.originY) * invDirY;
        double t4 = (rt.getY() - ray.originY) * invDirY;
        double t5 = (lb.getZ() - ray.originZ) * invDirZ;
        double t6 = (rt.getZ() - ray.originZ) * invDirZ;

        double tmin = Math.max(Math.max(Math.min(t1, t2), Math.min(t3, t4)), Math.min(t5, t6));
        double tmax = Math.min(Math.min(Math.max(t1, t2), Math.max(t3, t4)), Math.max(t5, t6));

        result.one = tmin;
        result.two = tmax;
        if (tmax < 0)
        {
            return false;
        }

        if (tmin > tmax)
        {
            return false;
        }

        return true;
    }

    public Vector collisionPoint(SimpleCollisionBox box) {
        Tuple<Double, Double> p = new Tuple<>();
        if (box==null||!intersect(this,box,p))
            return null;
        Vector vector = new Vector(directionX,directionY,directionZ);
        vector.normalize();
        vector.multiply(p.one);
                vector.add(new Vector(originX,originY,originZ));
        return vector;
    }

    public Vector collisionPoint(double dist) {
        Vector vector = new Vector(directionX,directionY,directionZ);
        vector.normalize();
        vector.multiply(dist);
        vector.add(new Vector(originX,originY,originZ));
        return vector;
    }
    
}