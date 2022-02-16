package cc.funkemunky.api.utils.world.state.states;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.world.state.BlockStateManager;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.material.Door;

import java.util.Arrays;

@Init
public class Version_1_7_10 {
    public Version_1_7_10() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            Arrays.stream(Material.values())
                    .filter(m -> m.name().contains("DOOR") && !m.name().contains("TRAP"))
                    .forEach(m -> {
                        BlockStateManager.stateInterfaceMap.put(m, (field, block) -> {
                            Door door = (Door) block.getType().getNewData(block.getData());
                            switch (field.toLowerCase()) {
                                case "hinge": {
                                    return door.getHinge();
                                }
                                case "top": {
                                    return door.isTopHalf();
                                }
                                case "facing": {
                                    return door.getFacing().ordinal();
                                }
                                case "open": {
                                    return door.isOpen();
                                }
                            }
                            return null;
                        });
                    });
        }
    }
}
