package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.Material2;
import cc.funkemunky.api.utils.world.types.CollisionFactory;
import cc.funkemunky.api.utils.world.types.ComplexCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;

import static org.bukkit.Material.NETHER_FENCE;

public class DynamicFence implements CollisionFactory {

    private static final double width = 0.125;
    private static final double min = .5 - width;
    private static final double max = .5 + width;

    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        ComplexCollisionBox box = new ComplexCollisionBox(new SimpleCollisionBox(min, 0, min, max, 1.5, max));
        boolean east =  fenceConnects(version, b, BlockFace.EAST );
        boolean north = fenceConnects(version, b, BlockFace.NORTH);
        boolean south = fenceConnects(version, b, BlockFace.SOUTH);
        boolean west =  fenceConnects(version, b, BlockFace.WEST );
        if (east) box.add(new SimpleCollisionBox(max, 0, min, 1, 1.5, max));
        if (west) box.add(new SimpleCollisionBox(0, 0, min, max, 1.5, max));
        if (north) box.add(new SimpleCollisionBox(min, 0, 0, max, 1.5, min));
        if (south) box.add(new SimpleCollisionBox(min, 0, max, max, 1.5, 1));
        return box;
    }

    public static boolean isBlacklisted(Material m) {
        switch (m) {
            case BARRIER:
            case STONE_SLAB2:
            case STEP:
            case STICK:
            case PUMPKIN:
            case MELON_BLOCK:
            case BEACON:

            case STAINED_GLASS_PANE:
            case THIN_GLASS:
            case IRON_FENCE:

            case COBBLE_WALL:

            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:

            case DAYLIGHT_DETECTOR:
                return true;
            default:
                return m == Material2.DAYLIGHT_DETECTOR_INVERTED;
        }
    }

    private static boolean fenceConnects(ProtocolVersion v,Block fenceBlock, BlockFace direction) {
        Block targetBlock = fenceBlock.getRelative(direction,1);
        BlockState sFence = fenceBlock.getState();
        BlockState sTarget = targetBlock.getState();
        Material target = sTarget.getType();
        Material fence = sFence.getType();

        if (!isFence(target)&&isBlacklisted(target))
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
            case FENCE_GATE:
            case ACACIA_FENCE_GATE:
            case BIRCH_FENCE_GATE:
            case DARK_OAK_FENCE_GATE:
            case JUNGLE_FENCE_GATE:
            case SPRUCE_FENCE_GATE: {
                Gate gate = (Gate) sTarget.getData();
                BlockFace f1 = gate.getFacing();
                BlockFace f2 = f1.getOppositeFace();
                return direction == f1 || direction == f2;
            }
            default: {
                if (fence == target) return true;
                if (isFence(target))
                    return (fence != NETHER_FENCE) && (target != NETHER_FENCE);
                else return isFence(target) || (target.isSolid() && !target.isTransparent());
            }
        }
    }

    public static boolean isFence(Material material) {
        switch (material) {
            case ACACIA_FENCE:
            case BIRCH_FENCE:
            case DARK_OAK_FENCE:
            case JUNGLE_FENCE:
            case FENCE:
            case NETHER_FENCE:
            case SPRUCE_FENCE:
                return true;
            default:
                return false;
        }
    }

}
