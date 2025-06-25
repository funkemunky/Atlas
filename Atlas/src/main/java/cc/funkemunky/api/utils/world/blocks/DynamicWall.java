package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.Materials;
import cc.funkemunky.api.utils.XMaterial;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.CollisionFactory;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.block.BlockState;
import org.bukkit.material.Stairs;

import java.util.Optional;

@SuppressWarnings("Duplicates")
public class DynamicWall implements CollisionFactory {

    private static final double width = 0.25;
    private static final double min = .5 - width;
    private static final double max = .5 + width;

    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        boolean var3 = wallConnects(version, b, BlockFace.NORTH);
        boolean var4 = wallConnects(version, b, BlockFace.SOUTH);
        boolean var5 = wallConnects(version, b, BlockFace.WEST);
        boolean var6 = wallConnects(version, b, BlockFace.EAST);

        double var7 = 0.25;
        double var8 = 0.75;
        double var9 = 0.25;
        double var10 = 0.75;

        if (var3) {
            var9 = 0.0;
        }

        if (var4) {
            var10 = 1.0;
        }

        if (var5) {
            var7 = 0.0;
        }

        if (var6) {
            var8 = 1.0;
        }

        if (var3 && var4 && !var5 && !var6) {
            var7 = 0.3125;
            var8 = 0.6875;
        } else if (!var3 && !var4 && var5 && var6) {
            var9 = 0.3125;
            var10 = 0.6875;
        }

        return new SimpleCollisionBox(var7, 0.0, var9, var8, 1.5, var10);
    }


    private static boolean wallConnects(ProtocolVersion v, Block fenceBlock, BlockFace direction) {
        Optional<Block> targetBlock = BlockUtils.getRelativeAsync(fenceBlock, direction, 1);

        if(!targetBlock.isPresent()) return false;
        Material target = targetBlock.get().getType();

        if (!isWall(target)&&DynamicFence.isBlacklisted(target))
            return false;

        if(Materials.checkFlag(target, Materials.STAIRS)) {
            if (v.isBelow(ProtocolVersion.V1_12)) return false;

            return dir(fenceBlock.getData()).getOppositeFace() == direction;
        } else return isWall(target) || (target.isSolid() && !target.isTransparent());
    }

    private static boolean isWall(Material m) {
        return Materials.checkFlag(m, Materials.WALL);
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
