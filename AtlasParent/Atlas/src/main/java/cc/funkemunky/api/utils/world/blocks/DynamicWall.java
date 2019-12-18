package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.CollisionFactory;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Stairs;

@SuppressWarnings("Duplicates")
public class DynamicWall implements CollisionFactory {

    private static final double width = 0.25;
    private static final double min = .5 - width;
    private static final double max = .5 + width;

    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        ComplexCollisionBox box = new ComplexCollisionBox(new SimpleCollisionBox(min, 0, min, max, 1.5, max));
        boolean east =  wallConnects(version,b, BlockFace.EAST );
        boolean north = wallConnects(version,b, BlockFace.NORTH);
        boolean south = wallConnects(version,b, BlockFace.SOUTH);
        boolean west =  wallConnects(version,b, BlockFace.WEST );

        if (east) box.add(new SimpleCollisionBox(max, 0, min, 1, 1.5, max));
        if (west) box.add(new SimpleCollisionBox(0, 0, min, max, 1.5, max));
        if (north) box.add(new SimpleCollisionBox(min, 0, 0, max, 1.5, min));
        if (south) box.add(new SimpleCollisionBox(min, 0, max, max, 1.5, 1));
        return box;
    }


    private static boolean wallConnects(ProtocolVersion v, Block fenceBlock, BlockFace direction) {
        Block targetBlock = fenceBlock.getRelative(direction,1);
        BlockState sFence = fenceBlock.getState();
        BlockState sTarget = targetBlock.getState();
        Material target = sTarget.getType();
        Material fence = sFence.getType();

        if (!isWall(target)&&DynamicFence.isBlacklisted(target))
            return false;

        switch (target) {
            case ACACIA_STAIRS:
            case SANDSTONE_STAIRS:
            case SMOOTH_STAIRS:
            case SPRUCE_WOOD_STAIRS:
            case BIRCH_WOOD_STAIRS:
            case BRICK_STAIRS:
            case COBBLESTONE_STAIRS:
            case DARK_OAK_STAIRS:
            case JUNGLE_WOOD_STAIRS:
            case QUARTZ_STAIRS:
            case RED_SANDSTONE_STAIRS:
            case WOOD_STAIRS:
            case NETHER_BRICK_STAIRS: {
                if (v.isBelow(ProtocolVersion.V1_12)) return false;
                Stairs stairs = (Stairs) sTarget.getData();
                return stairs.getFacing() == direction;
            }
            default: {
                return isWall(target) || (target.isSolid() && !target.isTransparent());
            }
        }
    }

    public static boolean isWall(Material m) {
        switch (m) {
            case COBBLE_WALL:
                return true;
            default:
                return false;
        }
    }

}
