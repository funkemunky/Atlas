package cc.funkemunky.api.utils.world.state.states;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.world.state.BlockStateManager;
import org.bukkit.Material;
import org.bukkit.block.Lectern;
import org.bukkit.block.data.Directional;
import org.bukkit.block.data.type.Lantern;

@Init(requireProtocolVersion = ProtocolVersion.V1_14)
public class Version1_14 {
    public Version1_14() {
        BlockStateManager.stateInterfaceMap.put(Material.LANTERN, (field, block) -> {
            switch (field.toLowerCase()) {
                case "hanging": {
                    return ((Lantern)block.getBlockData()).isHanging();
                }
                case "waterlogged": {
                    return ((Lantern)block.getBlockData()).isWaterlogged();
                }
            }
            return null;
        });
        BlockStateManager.stateInterfaceMap.put(Material.LECTERN, (field, block) -> {
            switch(field.toLowerCase()) {
                case "facing": {
                    return ((Directional)block.getBlockData()).getFacing();
                }
            }
            return null;
        });
    }
}
