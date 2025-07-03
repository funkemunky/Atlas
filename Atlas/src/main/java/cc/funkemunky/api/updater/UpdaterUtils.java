package cc.funkemunky.api.updater;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.ReflectionsUtil;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.PluginDescriptionFile;

import java.io.File;
import java.util.Objects;

public class UpdaterUtils {

    public static File findPluginFile(String name) {
        File pluginDir = getPluginDirectory();
        File pluginFile = new File(pluginDir, name + ".jar");
        if (!pluginFile.isFile()) {
            for (final File f : Objects.requireNonNull(pluginDir.listFiles())) {
                try {
                    if (f.getName().endsWith(".jar")) {
                        final PluginDescriptionFile pdf = Atlas.getInstance().getPluginLoader().getPluginDescription(f);
                        if (pdf.getName().equalsIgnoreCase(name)) {
                            pluginFile = f;
                            break;
                        }
                    }
                }
                catch (InvalidDescriptionException e2) {
                    e2.printStackTrace();
                }
            }
        }
        return pluginFile;
    }

    public static File getPluginDirectory() {
        File pluginDir = new File("plugins");
        if (!pluginDir.isDirectory()) {
            pluginDir = ReflectionsUtil.getPluginFolder();
        }
        return pluginDir;
    }
}
