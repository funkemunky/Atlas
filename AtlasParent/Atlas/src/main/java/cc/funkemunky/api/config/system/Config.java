package cc.funkemunky.api.config.system;

import cc.funkemunky.api.utils.FunkeFile;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;


//TODO Finish
public class Config {
    private FunkeFile file;
    private Map<String, String> mappings = new HashMap<>();

    public Config(Plugin plugin, String name) {
        file = new FunkeFile(plugin, "", name);
    }
}
