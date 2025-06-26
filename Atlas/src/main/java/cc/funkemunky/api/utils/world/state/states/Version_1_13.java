package cc.funkemunky.api.utils.world.state.states;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.world.state.BlockStateManager;
import org.bukkit.Material;
import org.bukkit.block.data.Bisected;
import org.bukkit.block.data.type.Door;

import java.util.Arrays;

@Init(requireProtocolVersion = ProtocolVersion.V1_13)
public class Version_1_13 {
    public Version_1_13() {
        Arrays.stream(Material.values())
                .filter(m -> m.name().contains("DOOR") && !m.name().contains("TRAP"))
                .forEach(m -> {
                    BlockStateManager.stateInterfaceMap.put(m, (field, block) -> {
                        Door door = (Door) block.getBlockData();
                        switch (field.toLowerCase()) {
                            case "hinge": {
                                return door.getHinge().equals(Door.Hinge.RIGHT);
                            }
                            case "top": {
                                return door.getHalf().equals(Bisected.Half.TOP);
                            }
                            case "facing": {
                                return door.getFacing().ordinal() - 1;
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
