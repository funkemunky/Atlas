package cc.funkemunky.api.utils.blockbox;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.Getter;

@Getter
@Deprecated
public class BlockBoxManager {
    private BlockBox blockBox;

    public BlockBoxManager() {
        try {
            blockBox = (BlockBox) Reflection.getClass("cc.funkemunky.api.utils.blockbox.boxes.BlockBox" + ProtocolVersion.getGameVersion().getServerVersion().replaceAll("v", "")).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            e.printStackTrace();
        }
    }
}
