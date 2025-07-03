package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.BlockUtils;
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
import org.bukkit.material.Gate;
import org.bukkit.material.Stairs;

import java.util.Optional;

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

    static boolean isBlacklisted(Material m) {
        XMaterial material = XMaterial.matchXMaterial(m);
        switch(material) {
            case BEACON:
            case STICK:
            case MELON:
            case DAYLIGHT_DETECTOR:
            case BARRIER:
                return true;
            default:
                return !Materials.checkFlag(m, Materials.SOLID)
                        || Materials.checkFlag(m, Materials.WALL)
                        || Materials.checkFlag(m, Materials.FENCE)
                        || m.name().contains("DAYLIGHT");
        }
    }

    private static boolean fenceConnects(ProtocolVersion v,Block fenceBlock, BlockFace direction) {
        BlockUtils.getRelativeAsync(fenceBlock, direction, 1);
        Optional<Block> targetBlock = BlockUtils.getRelativeAsync(fenceBlock, direction, 1);

        if(!targetBlock.isPresent()) return false;
        BlockState sFence = fenceBlock.getState();
        Material target = targetBlock.get().getType();
        Material fence = sFence.getType();

        if (!isFence(target)&&isBlacklisted(target))
            return false;

        if(Materials.checkFlag(target, Materials.STAIRS)) {
            if (v.isBelow(ProtocolVersion.V1_12)) return false;

            return dir(fenceBlock.getData()).getOppositeFace() == direction;
        } else if(target.name().contains("GATE")) {

            BlockFace f1 = dir(targetBlock.get().getData());
            BlockFace f2 = f1.getOppositeFace();
            return direction == f1 || direction == f2;
        } else {
            if (fence == target) return true;
            if (isFence(target))
                return !fence.name().contains("NETHER") && !target.name().contains("NETHER");
            else return isFence(target) || (target.isSolid() && !target.isTransparent());
        }
    }

    private static boolean isFence(Material material) {
        return Materials.checkFlag(material, Materials.FENCE) && material.name().contains("FENCE");
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
