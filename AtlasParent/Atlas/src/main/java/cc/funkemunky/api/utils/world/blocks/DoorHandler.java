package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.state.BlockStateManager;
import cc.funkemunky.api.utils.world.types.CollisionFactory;
import cc.funkemunky.api.utils.world.types.NoCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.Bukkit;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;

import java.util.Optional;

public class DoorHandler implements CollisionFactory {
    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        Block blockTwo;
        if ((boolean)BlockStateManager.getInterface("top", b)) {
            Optional<Block> rel = BlockUtils.getRelativeAsync(b, BlockFace.DOWN);

            if(!rel.isPresent()) return NoCollisionBox.INSTANCE;

            if(BlockUtils.isDoor(rel.get())) {
                blockTwo = rel.get();
            }
            else {
                return NoCollisionBox.INSTANCE;
            }
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            Optional<Block> rel = BlockUtils.getRelativeAsync(b, BlockFace.UP);

            if(!rel.isPresent()) return NoCollisionBox.INSTANCE;
            if(BlockUtils.isDoor(rel.get())) {
                blockTwo = rel.get();
            }
            else {
                return NoCollisionBox.INSTANCE;
            }
        } else blockTwo = b;

        SimpleCollisionBox box;
        float offset = 0.1875F;
        int direction = (int) BlockStateManager.getInterface("facing", b);
        boolean open = (boolean) BlockStateManager.getInterface("open", b);
        boolean hinge = (boolean) BlockStateManager.getInterface("hinge", blockTwo);
        if (direction == 0) {
            if (open) {
                if (!hinge) {
                    box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, offset);
                } else {
                    box = new SimpleCollisionBox(0.0F, 0.0F, 1.0F - offset, 1.0F, 1.0F, 1.0F);
                }
            } else {
                box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, offset, 1.0F, 1.0F);
            }
        } else if (direction == 1) {
            if (open) {
                if (!hinge) {
                    box = new SimpleCollisionBox(1.0F - offset, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                } else {
                    box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, offset, 1.0F, 1.0F);
                }
            } else {
                box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, offset);
            }
        } else if (direction == 2) {
            if (open) {
                if (!hinge) {
                    box = new SimpleCollisionBox(0.0F, 0.0F, 1.0F - offset, 1.0F, 1.0F, 1.0F);
                } else {
                    box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, 1.0F, 1.0F, offset);
                }
            } else {
                box = new SimpleCollisionBox(1.0F - offset, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
            }
        } else {
            if (open) {
                if (!hinge) {
                    box = new SimpleCollisionBox(0.0F, 0.0F, 0.0F, offset, 1.0F, 1.0F);
                } else {
                    box = new SimpleCollisionBox(1.0F - offset, 0.0F, 0.0F, 1.0F, 1.0F, 1.0F);
                }
            } else {
                box = new SimpleCollisionBox(0.0F, 0.0F, 1.0F - offset, 1.0F, 1.0F, 1.0F);
            }
        }
//        if (state.isTopHalf())
//            box.offset(0,1,0);
        return box;
    }
}
