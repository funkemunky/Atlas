package cc.funkemunky.api.config;

import lombok.RequiredArgsConstructor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.List;

@RequiredArgsConstructor
public class ConfigUtils {
    private final FileConfiguration config;
    private final JavaPlugin plugin;

    public ConfigUtils(JavaPlugin plugin) {
        this.plugin = plugin;
        this.config = plugin.getConfig();
    }

    public boolean getBooleanOrDefault(String path, boolean def) {
        return (boolean) getOrDefault(path, def);
    }

    public double getDoubleOrDefault(String path, double def) {
        return (double) getOrDefault(path, def);
    }

    public String getStringOrDefault(String path, String def) {
        return (String) getOrDefault(path, def);
    }

    public List<?> getListOrDefault(String path, List<?> def) {
        return (List<?>) getOrDefault(path, def);
    }

    public long getLongOrDefault(String path, long def) {
        return (long) getOrDefault(path, def);
    }

    public Object getOrDefault(String path, Object def) {
        Object object;

        if((object = config.get(path)) != null) {
            return object;
        }

        config.set(path, def);
        plugin.saveConfig();

        return def;
    }
}
