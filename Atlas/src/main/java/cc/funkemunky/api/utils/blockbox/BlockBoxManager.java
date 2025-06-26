package cc.funkemunky.api.utils.blockbox;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import cc.funkemunky.api.utils.world.types.SimpleCollisionBox;
import lombok.Getter;
import org.bukkit.Bukkit;

import java.util.logging.Level;

@Getter
public class BlockBoxManager {
    private BlockBox blockBox;

    public static SimpleCollisionBox cbox = new SimpleCollisionBox(6, 3)
            .expandMin(0, -2, 0);

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
