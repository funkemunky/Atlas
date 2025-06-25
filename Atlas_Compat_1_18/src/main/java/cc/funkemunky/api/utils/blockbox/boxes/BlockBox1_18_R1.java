package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import cc.funkemunky.api.utils.world.BlockData;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import net.minecraft.core.BlockPosition;
import net.minecraft.world.entity.ai.attributes.AttributeModifiable;
import net.minecraft.world.entity.ai.attributes.GenericAttributes;
import net.minecraft.world.level.World;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.state.IBlockData;
import net.minecraft.world.phys.AxisAlignedBB;
import net.minecraft.world.phys.shapes.VoxelShape;
import net.minecraft.world.phys.shapes.VoxelShapeCollision;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R1.block.CraftBlock;
import org.bukkit.craftbukkit.v1_18_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.List;

public class BlockBox1_18_R1 implements BlockBox {

    @Override
    public boolean isChunkLoaded(Location loc) {
        World world = ((CraftWorld)loc.getWorld()).getHandle();

        return !world.k_() && world.b(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)
                && world.d(loc.getBlockX() >> 4, loc.getBlockX() >> 4).s();
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity)entity).getHandle().eG();
    }

    @Override
    public float getMovementFactor(Player player) {
        AttributeModifiable attribute = ((CraftPlayer)player).getHandle()
                .a(GenericAttributes.d);
        return (float) (attribute != null ? attribute.f() : 0);
    }

    @Override
    public float getWidth(Entity entity) {
        return (float) entity.getWidth();
    }

    @Override
    public float getHeight(Entity entity) {
        return (float) entity.getHeight();
    }

    @Override
    public CollisionBox getCollisionBox(org.bukkit.block.Block block) {
        final World world =
                ((org.bukkit.craftbukkit.v1_18_R1.CraftWorld) block.getWorld()).getHandle();
        final int x = block.getX(), y = block.getY(), z = block.getZ();

        IBlockData iblockData = ((CraftBlock)block).getNMS();
        Block vblock = iblockData.b();
        BlockPosition blockPos = new BlockPosition(x, y, z);

        VoxelShape shape = vblock.a(iblockData, world, blockPos, VoxelShapeCollision.a());

        List<AxisAlignedBB> boxes = shape.d();

        if (boxes.size() == 0) {
            return BlockData.getData(block.getType()).getBox(block, ProtocolVersion.getGameVersion());
        } else if (boxes.size() == 1) {
            AxisAlignedBB box = boxes.get(0);

            return new SimpleCollisionBox(box.a, box.b, box.c, box.d, box.e, box.f);
        } else {
            ComplexCollisionBox complexBox = new ComplexCollisionBox();

            for (AxisAlignedBB box : boxes) {
                complexBox.add(new SimpleCollisionBox(box.a, box.b, box.c, box.d, box.e, box.f));
            }

            return complexBox;
        }
    }
}
