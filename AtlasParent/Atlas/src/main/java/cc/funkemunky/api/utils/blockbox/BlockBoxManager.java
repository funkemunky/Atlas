package cc.funkemunky.api.utils.blockbox;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@Getter
public class BlockBoxManager {
    private BlockBox blockBox;

    public BlockBoxManager() {
        try {
            blockBox = (BlockBox) Reflection.getClass("cc.funkemunky.api.utils.blockbox.boxes.BlockBox"
                    + ProtocolVersion.getGameVersion()
                    .getServerVersion().replaceAll("v", "")).newInstance();
        } catch (InstantiationException | IllegalAccessException e) {
            Bukkit.getLogger().log(Level.SEVERE,"There was an error loading BlockBox API for version "
                    + ProtocolVersion.getGameVersion().name());
        }
    }
}
