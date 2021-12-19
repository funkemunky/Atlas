package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.CollisionFactory;
import cc.funkemunky.api.utils.world.types.NoCollisionBox;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.material.Door;
import org.bukkit.material.MaterialData;

import java.util.Optional;

public class DoorHandler implements CollisionFactory {
    @Override
    public CollisionBox fetch(ProtocolVersion version, Block b) {
        Door state = (Door) b.getState().getData();
        byte data = state.getData();
        if (( data & 0b01000 ) != 0) {
            Optional<Block> rel = BlockUtils.getRelativeAsync(b, BlockFace.DOWN);

            if(!rel.isPresent()) return NoCollisionBox.INSTANCE;
            MaterialData state2 = rel.get().getState().getData();
            if (state2 instanceof Door) {
                data = state2.getData();
            }
            else {
                return NoCollisionBox.INSTANCE;
            }
        } else {
            Optional<Block> rel = BlockUtils.getRelativeAsync(b, BlockFace.UP);

            if(!rel.isPresent()) return NoCollisionBox.INSTANCE;
            MaterialData state2 = rel.get().getState().getData();
            if (state2 instanceof Door) {
                state = (Door) state2;
            }
            else {
                return NoCollisionBox.INSTANCE;
            }
        }

        SimpleCollisionBox box;
        float offset = 0.1875F;
        int direction = (data & 0b11);
        boolean open = (data & 0b100) != 0;
        boolean hinge = (state.getData() & 1) == 1;


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
