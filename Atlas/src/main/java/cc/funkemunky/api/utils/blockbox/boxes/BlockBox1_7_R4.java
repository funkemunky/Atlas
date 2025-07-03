package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.utils.blockbox.BlockBox;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import net.minecraft.server.v1_7_R4.AxisAlignedBB;
import net.minecraft.server.v1_7_R4.GenericAttributes;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.v1_7_R4.entity.CraftPlayer;
import org.bukkit.craftbukkit.v1_7_R4.util.CraftMagicNumbers;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class BlockBox1_7_R4 implements BlockBox {

    @Override
    public boolean isChunkLoaded(Location loc) {
        net.minecraft.server.v1_7_R4.World world =
                ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld) loc.getWorld()).getHandle();

        return !world.isStatic
                && world.isLoaded(loc.getBlockX(), 0, loc.getBlockZ())
                && world.getChunkAtWorldCoords(loc.getBlockX(), loc.getBlockZ()).d;
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return false;
    }

    @Override
    public float getMovementFactor(Player player) {
        return (float) ((CraftPlayer) player).getHandle().getAttributeInstance(GenericAttributes.d).getValue();
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
        final net.minecraft.server.v1_7_R4.World world =
                ((org.bukkit.craftbukkit.v1_7_R4.CraftWorld) block.getWorld()).getHandle();
        final int x = block.getX(), y = block.getY(), z = block.getZ();
        final AxisAlignedBB collide = BlockBoxManager.cbox.copy().offset(x, y, z).toAxisAlignedBB();

        List<AxisAlignedBB> boxes = new ArrayList<>();

        net.minecraft.server.v1_7_R4.Block vblock = CraftMagicNumbers.getBlock(block);

        vblock.a(world, x, y, z, collide, boxes, null);

        if(boxes.size() == 0) {
            AxisAlignedBB box = vblock.a(world, x, y, z);

            return new SimpleCollisionBox(box.a, box.b, box.c, box.d, box.e, box.f);
        } else if(boxes.size() == 1) {
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
