package cc.funkemunky.api.utils.world.types;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import cc.funkemunky.api.utils.KLocation;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.Tuple;
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

    public KLocation getOrigin() {
        return new KLocation(originX, originY, originZ, 0, 0);
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

    public static double distance(RayCollision ray, SimpleCollisionBox box ) {
        Tuple<Double,Double> pair = new Tuple<>();
        if (intersect(ray,box,pair))
            return pair.one;
        return -1;
    }

    public static boolean intersect(RayCollision ray, SimpleCollisionBox aab) {
        double invDirX = 1.0D / ray.directionX;
        double invDirY = 1.0D / ray.directionY;
        double invDirZ = 1.0D / ray.directionZ;
        double tFar;
        double tNear;
        if (invDirX >= 0.0D) {
            tNear = (aab.xMin - ray.originX) * invDirX;
            tFar = (aab.xMax - ray.originX) * invDirX;
        } else {
            tNear = (aab.xMax - ray.originX) * invDirX;
            tFar = (aab.xMin - ray.originX) * invDirX;
        }

        double tymin;
        double tymax;
        if (invDirY >= 0.0D) {
            tymin = (aab.yMin - ray.originY) * invDirY;
            tymax = (aab.yMax - ray.originY) * invDirY;
        } else {
            tymin = (aab.yMax - ray.originY) * invDirY;
            tymax = (aab.yMin - ray.originY) * invDirY;
        }

        if (tNear <= tymax && tymin <= tFar) {
            double tzmin;
            double tzmax;
            if (invDirZ >= 0.0D) {
                tzmin = (aab.zMin - ray.originZ) * invDirZ;
                tzmax = (aab.zMax - ray.originZ) * invDirZ;
            } else {
                tzmin = (aab.zMax - ray.originZ) * invDirZ;
                tzmax = (aab.zMin - ray.originZ) * invDirZ;
            }

            if (tNear <= tzmax && tzmin <= tFar) {
                tNear = tymin <= tNear && !Double.isNaN(tNear) ? tNear : tymin;
                tFar = tymax >= tFar && !Double.isNaN(tFar) ? tFar : tymax;
                tNear = tzmin > tNear ? tzmin : tNear;
                tFar = tzmax < tFar ? tzmax : tFar;
                if (tNear < tFar && tFar >= 0.0D) {
//                    result.x = tNear;
//                    result.y = tFar;
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
    }

    // Result X = near
    // Result Y = far
    public static boolean intersect(RayCollision ray, SimpleCollisionBox aab, Tuple<Double,Double> result) {
        double invDirX = 1.0D / ray.directionX;
        double invDirY = 1.0D / ray.directionY;
        double invDirZ = 1.0D / ray.directionZ;
        double tFar;
        double tNear;
        if (invDirX >= 0.0D) {
            tNear = (aab.xMin - ray.originX) * invDirX;
            tFar = (aab.xMax - ray.originX) * invDirX;
        } else {
            tNear = (aab.xMax - ray.originX) * invDirX;
            tFar = (aab.xMin - ray.originX) * invDirX;
        }

        double tymin;
        double tymax;
        if (invDirY >= 0.0D) {
            tymin = (aab.yMin - ray.originY) * invDirY;
            tymax = (aab.yMax - ray.originY) * invDirY;
        } else {
            tymin = (aab.yMax - ray.originY) * invDirY;
            tymax = (aab.yMin - ray.originY) * invDirY;
        }

        if (tNear <= tymax && tymin <= tFar) {
            double tzmin;
            double tzmax;
            if (invDirZ >= 0.0D) {
                tzmin = (aab.zMin - ray.originZ) * invDirZ;
                tzmax = (aab.zMax - ray.originZ) * invDirZ;
            } else {
                tzmin = (aab.zMax - ray.originZ) * invDirZ;
                tzmax = (aab.zMin - ray.originZ) * invDirZ;
            }

            if (tNear <= tzmax && tzmin <= tFar) {
                tNear = tymin <= tNear && !Double.isNaN(tNear) ? tNear : tymin;
                tFar = tymax >= tFar && !Double.isNaN(tFar) ? tFar : tymax;
                tNear = tzmin > tNear ? tzmin : tNear;
                tFar = tzmax < tFar ? tzmax : tFar;
                if (tNear < tFar && tFar >= 0.0D) {
                    if (result != null) {
                        result.one = tNear;
                        result.two = tFar;
                    }
                    return true;
                } else {
                    return false;
                }
            } else {
                return false;
            }
        } else {
            return false;
        }
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