package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.blockbox.BlockBox;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import net.minecraft.server.v1_8_R3.AxisAlignedBB;
import net.minecraft.server.v1_8_R3.BlockPosition;
import net.minecraft.server.v1_8_R3.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_8_R3.util.CraftMagicNumbers;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockBox1_8_R3 implements BlockBox {

    @Override
    public boolean isChunkLoaded(Location loc) {

        net.minecraft.server.v1_8_R3.World world = ((CraftWorld) loc.getWorld()).getHandle();

        return !world.isClientSide && world.isLoaded(new BlockPosition(loc.getBlockX(), 0, loc.getBlockZ()));
    }
    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle()
                .getAttributeInstance(GenericAttributes.MOVEMENT_SPEED).getValue();
    }

    @Override
    public float getWidth(Entity entity) {
        return 0;
    }

    @Override
    public float getHeight(Entity entity) {
        return 0;
    }

    @Override
    public CollisionBox getCollisionBox(Block block) {
        final net.minecraft.server.v1_8_R3.World world =
                ((org.bukkit.craftbukkit.v1_8_R3.CraftWorld) block.getWorld()).getHandle();
        final int x = block.getX(), y = block.getY(), z = block.getZ();
        final AxisAlignedBB collide = BlockBoxManager.cbox.copy().offset(x, y, z).toAxisAlignedBB();

        List<AxisAlignedBB> boxes = new ArrayList<>();

        net.minecraft.server.v1_8_R3.Block vblock = CraftMagicNumbers.getBlock(block);
        net.minecraft.server.v1_8_R3.BlockPosition blockPos = new net.minecraft.server.v1_8_R3.BlockPosition(x, y, z);

        vblock.a(world, blockPos, vblock.getBlockData(), collide, boxes, null);

        if (boxes.size() == 0) {
            AxisAlignedBB box = vblock.a(world, blockPos, vblock.getBlockData());

            return new SimpleCollisionBox(box.a, box.b, box.c, box.d, box.e, box.f);
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