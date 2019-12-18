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
public class DynamicPane implements CollisionFactory {

    private static final double width = 0.0625;
    private static final double min = .5 - width;
    private static final double max = .5 + width;

    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        ComplexCollisionBox box = new ComplexCollisionBox(new SimpleCollisionBox(min, 0, min, max, 1, max));
        boolean east =  fenceConnects(version,b, BlockFace.EAST );
        boolean north = fenceConnects(version,b, BlockFace.NORTH);
        boolean south = fenceConnects(version,b, BlockFace.SOUTH);
        boolean west =  fenceConnects(version,b, BlockFace.WEST );

        if (version.isBelow(ProtocolVersion.V1_9) && !(east||north||south||west)) {
            east = true;
            west = true;
            north = true;
            south = true;
        }

        if (east) box.add(new SimpleCollisionBox(max, 0, min, 1, 1, max));
        if (west) box.add(new SimpleCollisionBox(0, 0, min, max, 1, max));
        if (north) box.add(new SimpleCollisionBox(min, 0, 0, max, 1, min));
        if (south) box.add(new SimpleCollisionBox(min, 0, max, max, 1, 1));
        return box;
    }


    private static boolean fenceConnects(ProtocolVersion v, Block fenceBlock, BlockFace direction) {
        Block targetBlock = fenceBlock.getRelative(direction,1);
        BlockState sFence = fenceBlock.getState();
        BlockState sTarget = targetBlock.getState();
        Material target = sTarget.getType();
        Material fence = sFence.getType();

        if (!isPane(target)&&DynamicFence.isBlacklisted(target))
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
                return isPane(target) || (target.isSolid() && !target.isTransparent());
            }
        }
    }

    public static boolean isPane(Material m) {
        switch (m) {
            case THIN_GLASS:
            case STAINED_GLASS_PANE:
            case IRON_FENCE:
                return true;
            default:
                return false;
        }
    }

}
