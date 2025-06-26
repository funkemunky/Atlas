package cc.funkemunky.api.utils.world.blocks;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.world.CollisionBox;
import cc.funkemunky.api.utils.world.types.CollisionFactory;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import org.bukkit.block.Block;

public class TrapDoorHandler implements CollisionFactory {
    @Override
    public CollisionBox fetch(ProtocolVersion version, Block block) {
        byte data = block.getState().getData().getData();
        double var2 = 0.1875;

        if ((data & 4) != 0) {
            if ((data & 3) == 0) {
                return new SimpleCollisionBox(0.0, 0.0, 1.0 - var2, 1.0, 1.0, 1.0);
            }

            if ((data & 3) == 1) {
                return new SimpleCollisionBox(0.0, 0.0, 0.0, 1.0, 1.0, var2);
            }

            if ((data & 3) == 2) {
                return new SimpleCollisionBox(1.0 - var2, 0.0, 0.0, 1.0, 1.0, 1.0);
            }

            if ((data & 3) == 3) {
                return new SimpleCollisionBox(0.0, 0.0, 0.0, var2, 1.0, 1.0);
            }
        } else {
            if ((data & 8) != 0) {
                return new SimpleCollisionBox(0.0, 1.0 - var2, 0.0, 1.0, 1.0, 1.0);
            } else {
                return new SimpleCollisionBox(0.0, 0.0, 0.0, 1.0, var2, 1.0);
            }
        }
        return null;
    }
}
