package cc.funkemunky.api.handlers;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.AutoLoad;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.Getter;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashSet;
import java.util.Set;

@Init
public class PluginLoaderHandler implements Listener {

    @Getter
    private Set<Plugin> loadedPlugins = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onEvent(PluginEnableEvent event) {
        val description = event.getPlugin().getDescription();
        if(description.getDepend().contains("Atlas")) {
            MiscUtils.printToConsole("&7Plugin &f" + description.getName() + " &7has been detected!");
            loadedPlugins.add(event.getPlugin());

            loadPlugin(event.getPlugin());
        }
    }

    @EventHandler
    public void onEvent(PluginDisableEvent event) {
        if(loadedPlugins.contains(event.getPlugin())) {
            val description = event.getPlugin().getDescription();
            MiscUtils.printToConsole("&7Plugin &f" + description.getName() + " &7is being unloaded.");
            loadedPlugins.remove(event.getPlugin());
        }
    }

    public boolean isPluginLoaded(Plugin plugin) {
        return loadedPlugins.contains(plugin);
    }

    private void loadPlugin(Plugin plugin) {
        try {
            val plClass = Class.forName(plugin.getDescription().getMain());
            if(plClass.isAnnotationPresent(AutoLoad.class)) {
                MiscUtils.printToConsole("&cAutoload is commencing...");
                Atlas.getInstance().initializeScanner((JavaPlugin) plugin, true, true);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
