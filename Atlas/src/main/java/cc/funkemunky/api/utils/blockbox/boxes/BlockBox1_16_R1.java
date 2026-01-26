package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import cc.funkemunky.api.utils.world.BlockData;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import net.minecraft.server.v1_16_R1.*;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_16_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_16_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockBox1_16_R1 implements BlockBox {
    @Override
    public boolean isChunkLoaded(Location loc) {
        World world = ((CraftWorld)loc.getWorld()).getHandle();

        return !world.isClientSide && world.isChunkLoaded(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)
                && world.getChunkAt(loc.getBlockX() >> 4, loc.getBlockX() >> 4).r();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity)entity).getHandle().isRiptiding();
    }

    @Override
    public float getMovementFactor(Player player) {
        var attribute = ((CraftPlayer)player).getHandle()
                .getAttributeInstance(GenericAttributes.MOVEMENT_SPEED);
        return (float) (attribute != null ? attribute.getValue() : 0);
    }

    @Override
    public float getWidth(Entity entity) {
        return (float) ((CraftEntity)entity).getWidth();
    }

    @Override
    public float getHeight(Entity entity) {
        return (float) ((CraftEntity)entity).getHeight();
    }

    @Override
    public CollisionBox getCollisionBox(Block block) {
        final net.minecraft.server.v1_16_R1.World world =
                ((org.bukkit.craftbukkit.v1_16_R1.CraftWorld) block.getWorld()).getHandle();
        final int x = block.getX(), y = block.getY(), z = block.getZ();

        net.minecraft.server.v1_16_R1.IBlockData iblockData = ((CraftBlock)block).getNMS();
        net.minecraft.server.v1_16_R1.Block vblock = iblockData.getBlock();
        BlockPosition blockPos = new BlockPosition(x, y, z);

        VoxelShape shape = vblock.a(iblockData, world, blockPos, VoxelShapeCollision.a());

        List<AxisAlignedBB> boxes = shape.d();

        if (boxes.size() == 0) {
            return BlockData.getData(block.getType()).getBox(block, ProtocolVersion.getGameVersion());
        } else if (boxes.size() == 1) {
            AxisAlignedBB box = boxes.get(0);

            return new SimpleCollisionBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
        } else {
            ComplexCollisionBox complexBox = new ComplexCollisionBox();

            for (AxisAlignedBB box : boxes) {
                complexBox.add(new SimpleCollisionBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ));
            }

            return complexBox;
        }
    }
}
