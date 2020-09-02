package cc.funkemunky.api.abstraction.impl;

import cc.funkemunky.api.abstraction.AbstractVersion;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.MathHelper;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.NoCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import lombok.val;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.CraftWorld;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class AbstractV1_7_10 implements AbstractVersion {

    @Override
    public CollisionBox getBlockBox(Block block) {
        val nmsBlock = CraftMagicNumbers.getBlock(block);
        val nmsWorld = ((CraftWorld)block.getWorld()).getHandle();
        List<AxisAlignedBB> listAABBS = new ArrayList<>();

        nmsBlock.a(nmsWorld, block.getX(), block.getY(), block.getZ(),
                new SimpleCollisionBox(block.getLocation(), 2, 1).toAxisAlignedBB(),
                listAABBS, null);

        if(listAABBS.size() > 1) {
            return new ComplexCollisionBox(listAABBS.stream().map(SimpleCollisionBox::new)
                    .toArray(SimpleCollisionBox[]::new));
        } else if(listAABBS.size() > 0) new SimpleCollisionBox(listAABBS.get(0));

        return NoCollisionBox.INSTANCE;
    }

    @Override
    public List<CollisionBox> getCollidingBoxes(World world, SimpleCollisionBox box) {
        int minx = MathHelper.floor_double(box.xMin), miny = MathHelper.floor_double(box.yMin),
                minz = MathHelper.floor_double(box.zMin);
        int maxx = MathHelper.floor_double(box.xMax), maxy = MathHelper.floor_double(box.yMax),
                maxz = MathHelper.floor_double(box.zMax);

        List<CollisionBox> boxes = new ArrayList<>();
        for(int x = minx ; x < maxx; x++) {
            for(int y = miny ; y < maxy ; y++) {
                for(int z = minz ; z < maxz ; z++) {
                    Block block = BlockUtils.getBlock(new Location(world, x, y, z));

                    if(block == null) continue;

                    List<AxisAlignedBB> listAABBS = new ArrayList<>();
                    val nmsBlock = CraftMagicNumbers.getBlock(block);
                    val nmsWorld = ((CraftWorld)block.getWorld()).getHandle();
                    nmsBlock.a(nmsWorld, block.getX(), block.getY(), block.getZ(),
                            new SimpleCollisionBox(block.getLocation(), 2, 1).toAxisAlignedBB(),
                            listAABBS, null);

                    if(listAABBS.size() > 1) {
                        boxes.add(new ComplexCollisionBox(listAABBS.stream().map(SimpleCollisionBox::new)
                                .toArray(SimpleCollisionBox[]::new)));
                    } else if(listAABBS.size() > 0) boxes.add(new SimpleCollisionBox(listAABBS.get(0)));
                }
            }
        }

        return boxes;
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public boolean isGliding(Player player) {
        return false;
    }

    @Override
    public boolean isChunkLoaded(Location location) {
        val world = ((CraftWorld)location.getWorld()).getHandle();
        return !world.isLoaded(location.getBlockX(), location.getBlockY(), location.getBlockZ())
                && world.getChunkAtWorldCoords(location.getBlockX(), location.getBlockZ()).d;
    }

    @Override
    public float getMovementFactor(LivingEntity entity) {
        return ((CraftLivingEntity)entity).getHandle().bl();
    }

    @Override
    public float getBlockFriction(Block block) {
        return CraftMagicNumbers.getBlock(block).frictionFactor;
    }
}
