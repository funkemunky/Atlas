package cc.funkemunky.api.utils.world.state;

import cc.funkemunky.api.utils.Init;
import org.bukkit.Material;
import org.bukkit.block.Block;

import java.util.HashMap;
import java.util.Map;

public class BlockStateManager {

    public static final Map<Material, StateInterface> stateInterfaceMap = new HashMap<>();

    public static Object getInterface(String field, Block block) {
        StateInterface face = stateInterfaceMap.get(block.getType());

        if(face == null) return null;

        return face.getField(field, block);
    }

    @FunctionalInterface
    public interface StateInterface {
        Object getField(String field, Block block);
    }
}
