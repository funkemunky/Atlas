package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.Materials;
import cc.funkemunky.api.utils.XMaterial;
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
        Material target = targetBlock.getType();

        if (!isPane(target)&&DynamicFence.isBlacklisted(target))
            return false;

        if(Materials.checkFlag(target, Materials.STAIRS)) {
            if (v.isBelow(ProtocolVersion.V1_12)) return false;

            return dir(fenceBlock.getData()).getOppositeFace() == direction;
        }  else return isPane(target) || (target.isSolid() && !target.isTransparent());
    }

    private static boolean isPane(Material m) {
        final XMaterial mat = XMaterial.matchXMaterial(m);

        return mat == XMaterial.IRON_BARS || mat.name().contains("PANE")
                || mat.name().contains("THIN");
    }

    private static BlockFace dir(byte data) {
        switch(data & 3) {
            case 0:
            default:
                return BlockFace.EAST;
            case 1:
                return BlockFace.WEST;
            case 2:
                return BlockFace.SOUTH;
            case 3:
                return BlockFace.NORTH;
        }
    }

}
