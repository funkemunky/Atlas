package cc.funkemunky.api.utils.blockbox.boxes;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.blockbox.BlockBox;
import cc.funkemunky.api.utils.world.BlockData;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.phys.AABB;
import net.minecraft.world.phys.shapes.VoxelShape;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.craftbukkit.CraftWorld;
import org.bukkit.craftbukkit.block.CraftBlock;
import org.bukkit.craftbukkit.entity.CraftLivingEntity;
import org.bukkit.craftbukkit.entity.CraftPlayer;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

public class BlockBox1_20_R1 implements BlockBox {
    @Override
    public boolean isChunkLoaded(Location loc) {
        ServerLevel world = ((CraftWorld)loc.getWorld()).getHandle();

        return !world.isClientSide() && world.hasChunk(loc.getBlockX() >> 4, loc.getBlockZ() >> 4)
                && world.getChunk(loc.getBlockX() >> 4, loc.getBlockX() >> 4).loaded;
    }

    @Override
    public boolean isRiptiding(LivingEntity entity) {
        return ((CraftLivingEntity)entity).getHandle().isAutoSpinAttack();
    }

    @Override
    public float getMovementFactor(Player player) {
        var attribute = ((CraftPlayer)player).getHandle().getAttribute(Attributes.MOVEMENT_SPEED).getValue();
        return (float) attribute;
    }

    @Override
    public float getWidth(Entity entity) {
        return (float)entity.getWidth();
    }

    @Override
    public float getHeight(Entity entity) {
        return (float)entity.getHeight();
    }

    @Override
    public CollisionBox getCollisionBox(Block block) {
        ServerLevel world = ((CraftWorld)block.getWorld()).getHandle();
        int x = block.getX();
        int y = block.getY();
        int z = block.getZ();


        BlockState state = ((CraftBlock)block).getNMS();
        BlockPos pos = new BlockPos(x, y, z);

        VoxelShape shape = state.getCollisionShape(world, pos);

        var boxes = shape.toAabbs();

        if(boxes.isEmpty()) {
            return BlockData.getData(block.getType()).getBox(block, ProtocolVersion.getGameVersion());
        } else if(boxes.size() == 1) {
            AABB box = boxes.get(0);

            return new SimpleCollisionBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ);
        } else {
            ComplexCollisionBox complexBox = new ComplexCollisionBox();
            boxes.forEach(box -> complexBox
                    .add(new SimpleCollisionBox(box.minX, box.minY, box.minZ, box.maxX, box.maxY, box.maxZ)));

            return complexBox;
        }
    }
}
