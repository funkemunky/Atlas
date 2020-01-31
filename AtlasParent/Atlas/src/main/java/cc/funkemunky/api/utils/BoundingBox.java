package cc.funkemunky.api.utils;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class BoundingBox {

    public float minX, minY, minZ, maxX, maxY, maxZ;

    public BoundingBox(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        this.minX = minX;
        this.minY = minY;
        this.minZ = minZ;
        this.maxX = maxX;
        this.maxY = maxY;
        this.maxZ = maxZ;
    }

    public BoundingBox(Vector min, Vector max) {
        this.minX = (float) Math.min(min.getX(), max.getX());
        this.minY = (float) Math.min(min.getY(), max.getY());
        this.minZ = (float) Math.min(min.getZ(), max.getZ());
        this.maxX = (float) Math.max(min.getX(), max.getX());
        this.maxY = (float) Math.max(min.getY(), max.getY());
        this.maxZ = (float) Math.max(min.getZ(), max.getZ());
    }
    
    public BoundingBox(BoundingBox one, BoundingBox two) {
        this.minX = Math.min(one.minX, two.minX);
        this.minY = Math.min(one.minY, two.minY);
        this.minZ = Math.min(one.minZ, two.minZ);
        this.maxX = Math.max(one.maxX, two.maxX);
        this.maxY = Math.max(one.maxY, two.maxY);
        this.maxZ = Math.max(one.maxZ, two.maxZ);
    }

    public BoundingBox add(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(Vector vector) {
        float x = (float) vector.getX(), y = (float) vector.getY(), z = (float) vector.getZ();

        float newMinX = minX + x;
        float newMaxX = maxX + x;
        float newMinY = minY + y;
        float newMaxY = maxY + y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox grow(float x, float y, float z) {
        float newMinX = minX - x;
        float newMaxX = maxX + x;
        float newMinY = minY - y;
        float newMaxY = maxY + y;
        float newMinZ = minZ - z;
        float newMaxZ = maxZ + z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox shrink(float x, float y, float z) {
        float newMinX = minX + x;
        float newMaxX = maxX - x;
        float newMinY = minY + y;
        float newMaxY = maxY - y;
        float newMinZ = minZ + z;
        float newMaxZ = maxZ - z;

        return new BoundingBox(newMinX, newMinY, newMinZ, newMaxX, newMaxY, newMaxZ);
    }

    public BoundingBox add(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX + minX, this.minY + minY, this.minZ + minZ, this.maxX + maxX, this.maxY + maxY, this.maxZ + maxZ);
    }

    public BoundingBox subtract(float minX, float minY, float minZ, float maxX, float maxY, float maxZ) {
        return new BoundingBox(this.minX - minX, this.minY - minY, this.minZ - minZ, this.maxX - maxX, this.maxY - maxY, this.maxZ - maxZ);
    }

    public boolean intersectsWithBox(Vector vector) {
        return (vector.getX() > this.minX && vector.getX() < this.maxX) && ((vector.getY() > this.minY && vector.getY() < this.maxY) && (vector.getZ() > this.minZ && vector.getZ() < this.maxZ));
    }

    @Deprecated
    public List<BoundingBox> getCollidingBlockBoxes(Player player) {
        List<BoundingBox> toReturn = new ArrayList<>();
        int minX = MathUtils.floor(this.minX);
        int maxX = MathUtils.floor(this.maxX + 1);
        int minY = MathUtils.floor(this.minY);
        int maxY = MathUtils.floor(this.maxY + 1);
        int minZ = MathUtils.floor(this.minZ);
        int maxZ = MathUtils.floor(this.maxZ + 1);


        for (double x = minX; x < maxX; x++) {
            for (double z = minZ; z < maxZ; z++) {
                for (double y = minY; y < maxY; y++) {
                    Location loc = new Location(player.getWorld(), x, y, z);

                    if (Atlas.getInstance().getBlockBoxManager().getBlockBox().isChunkLoaded(loc)) {
                        org.bukkit.block.Block block = BlockUtils.getBlock(loc);
                        if (block != null && block.getType().getId() != 0) {
                            toReturn.addAll(BlockUtils.getBlockBoundingBox(block));
                        }
                    }
                }
            }
        }
        return toReturn;
    }

    public Vector getMinimum() {
        return new Vector(minX, minY, minZ);
    }

    public Vector getMaximum() {
        return new Vector(maxX, maxY, maxZ);
    }

    public List<Tuple<Block, BoundingBox>> getCollidingBlocks(World world) {
        return Atlas.getInstance().getBlockBoxManager().getBlockBox().getCollidingBoxes(world, this).stream()
                .map(bb -> new Tuple<>(bb.getMinimum().toLocation(world).getBlock(), bb))
                .collect(Collectors.toList());
    }

    public List<Block> getAllBlocks(Player player) {
        Location min = new Location(player.getWorld(), MathUtils.floor(minX), MathUtils.floor(minY), MathUtils.floor(minZ));
        Location max = new Location(player.getWorld(), MathUtils.floor(maxX), MathUtils.floor(maxY), MathUtils.floor(maxZ));
        List<Block> all = new ArrayList<>();
        for (float x = (float) min.getX(); x < max.getX(); x++) {
            for (float y = (float) min.getY(); y < max.getY(); y++) {
                for (float z = (float) min.getZ(); z < max.getZ(); z++) {
                    Block block = BlockUtils.getBlock(new Location(player.getWorld(), x, y, z));
                    if (block != null && block.getType().getId() != 0) {
                        all.add(block);
                    }
                }
            }
        }
        return all;
    }

    public boolean inBlock(Player player) {
        return getCollidingBlocks(player.getWorld()).size() > 0;
    }

    public boolean intersectsWithBox(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX > this.minX && otherBox.minX < this.maxX && otherBox.maxY > this.minY && otherBox.minY < this.maxY && otherBox.maxZ > this.minZ && otherBox.minZ < this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX > minX && otherMinX < maxX && otherMaxY > minY && otherMinY < maxY && otherMaxZ > minZ && otherMinZ < maxZ;
        }
    }

    public boolean collides(Vector vector) {
        return (vector.getX() >= this.minX && vector.getX() <= this.maxX) && ((vector.getY() >= this.minY && vector.getY() <= this.maxY) && (vector.getZ() >= this.minZ && vector.getZ() <= this.maxZ));
    }

    public boolean collides(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX >= this.minX && otherBox.minX <= this.maxX && otherBox.maxY >= this.minY && otherBox.minY <= this.maxY && otherBox.maxZ >= this.minZ && otherBox.minZ <= this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX >= minX && otherMinX <= maxX && otherMaxY >= minY && otherMinY <= maxY && otherMaxZ >= minZ && otherMinZ <= maxZ;
        }
    }

    public boolean collidesHorizontally(Vector vector) {
        return (vector.getX() >= this.minX && vector.getX() <= this.maxX) && ((vector.getY() > this.minY && vector.getY() < this.maxY) && (vector.getZ() >= this.minZ && vector.getZ() <= this.maxZ));
    }

    public boolean collidesHorizontally(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX >= this.minX && otherBox.minX <= this.maxX && otherBox.maxY > this.minY && otherBox.minY < this.maxY && otherBox.maxZ >= this.minZ && otherBox.minZ <= this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX >= minX && otherMinX <= maxX && otherMaxY > minY && otherMinY < maxY && otherMaxZ >= minZ && otherMinZ <= maxZ;
        }
    }

    public boolean collidesVertically(Vector vector) {
        return (vector.getX() > this.minX && vector.getX() < this.maxX) && ((vector.getY() >= this.minY && vector.getY() <= this.maxY) && (vector.getZ() > this.minZ && vector.getZ() < this.maxZ));
    }

    public boolean collidesVertically(Object other) {
        if (other instanceof BoundingBox) {
            BoundingBox otherBox = (BoundingBox) other;
            return otherBox.maxX > this.minX && otherBox.minX < this.maxX && otherBox.maxY >= this.minY && otherBox.minY <= this.maxY && otherBox.maxZ > this.minZ && otherBox.minZ < this.maxZ;
        } else {
            float otherMinX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "a"), other);
            float otherMinY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "b"), other);
            float otherMinZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "c"), other);
            float otherMaxX = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "d"), other);
            float otherMaxY = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "e"), other);
            float otherMaxZ = (float) (double) ReflectionsUtil.getFieldValue(ReflectionsUtil.getFieldByName(other.getClass(), "f"), other);
            return otherMaxX > minX && otherMinX < maxX && otherMaxY >= minY && otherMinY <= maxY && otherMaxZ > minZ && otherMinZ < maxZ;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and Z dimensions, calculate the offset between them
     * in the X dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateXOffset(BoundingBox other, double offsetX) {
        if (other.maxY > this.minY && other.minY < this.maxY && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetX > 0.0D && other.maxX <= this.minX) {
                double d1 = this.minX - other.maxX;

                if (d1 < offsetX) {
                    offsetX = d1;
                }
            } else if (offsetX < 0.0D && other.minX >= this.maxX) {
                double d0 = this.maxX - other.minX;

                if (d0 > offsetX) {
                    offsetX = d0;
                }
            }

            return offsetX;
        } else {
            return offsetX;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the X and Z dimensions, calculate the offset between them
     * in the Y dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateYOffset(BoundingBox other, double offsetY) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxZ > this.minZ && other.minZ < this.maxZ) {
            if (offsetY > 0.0D && other.maxY <= this.minY) {
                double d1 = this.minY - other.maxY;

                if (d1 < offsetY) {
                    offsetY = d1;
                }
            } else if (offsetY < 0.0D && other.minY >= this.maxY) {
                double d0 = this.maxY - other.minY;

                if (d0 > offsetY) {
                    offsetY = d0;
                }
            }

            return offsetY;
        } else {
            return offsetY;
        }
    }

    /**
     * if instance and the argument bounding boxes overlap in the Y and X dimensions, calculate the offset between them
     * in the Z dimension.  return var2 if the bounding boxes do not overlap or if var2 is closer to 0 then the
     * calculated offset.  Otherwise return the calculated offset.
     */
    public double calculateZOffset(BoundingBox other, double offsetZ) {
        if (other.maxX > this.minX && other.minX < this.maxX && other.maxY > this.minY && other.minY < this.maxY) {
            if (offsetZ > 0.0D && other.maxZ <= this.minZ) {
                double d1 = this.minZ - other.maxZ;

                if (d1 < offsetZ) {
                    offsetZ = d1;
                }
            } else if (offsetZ < 0.0D && other.minZ >= this.maxZ) {
                double d0 = this.maxZ - other.minZ;

                if (d0 > offsetZ) {
                    offsetZ = d0;
                }
            }

            return offsetZ;
        } else {
            return offsetZ;
        }
    }

    public BoundingBox addCoord(float x, float y, float z) {
        float d0 = this.minX;
        float d1 = this.minY;
        float d2 = this.minZ;
        float d3 = this.maxX;
        float d4 = this.maxY;
        float d5 = this.maxZ;

        if (x < 0.0D) {
            d0 += x;
        } else if (x > 0.0D) {
            d3 += x;
        }

        if (y < 0.0D) {
            d1 += y;
        } else if (y > 0.0D) {
            d4 += y;
        }

        if (z < 0.0D) {
            d2 += z;
        } else if (z > 0.0D) {
            d5 += z;
        }

        return new BoundingBox(d0,d1,d2,d3,d4,d5);
    }

    public <T> T toAxisAlignedBB() {
        return MinecraftReflection.toAABB(this);
    }

    public SimpleCollisionBox toCollisionBox() {
        return new SimpleCollisionBox(minX, minY, minZ, maxX, maxY, maxZ);
    }

    public String toString() {
        return "[" + minX + ", " + minY + ", " + minZ + ", " + maxX + ", " + maxY + ", " + maxZ + "]";
    }
}
