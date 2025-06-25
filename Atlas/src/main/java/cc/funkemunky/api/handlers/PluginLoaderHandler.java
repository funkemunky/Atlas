package cc.funkemunky.api.handlers;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.AutoLoad;
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

public class PluginLoaderHandler implements Listener {

    @Getter
    private Set<Plugin> loadedPlugins = new HashSet<>();

    @EventHandler(priority = EventPriority.HIGH)
    public void onEvent(PluginEnableEvent event) {
        val description = event.getPlugin().getDescription();
        if(description.getDepend().contains("Atlas") || description.getSoftDepend().contains("Atlas")) {
            Atlas.getInstance().alog("&7Plugin &f" + description.getName() + " &7has been detected!");
            loadedPlugins.add(event.getPlugin());

            loadPlugin(event.getPlugin());
        }
    }

    @EventHandler
    public void onEvent(PluginDisableEvent event) {
        if(loadedPlugins.contains(event.getPlugin())) {
            val description = event.getPlugin().getDescription();
            Atlas.getInstance().alog("&7Plugin &f" + description.getName() + " &7is being unloaded.");
            loadedPlugins.remove(event.getPlugin());

            Atlas.getInstance().getPluginCommandManagers().computeIfPresent(description.getName(), (key, obj) -> {
                obj.unregisterCommands();
                return null;
            });
        }
    }

    public boolean isPluginLoaded(Plugin plugin) {
        return loadedPlugins.contains(plugin);
    }

    private void loadPlugin(Plugin plugin) {
        try {
            val plClass = Class.forName(plugin.getDescription().getMain());
            if(plClass.isAnnotationPresent(AutoLoad.class)) {
                Atlas.getInstance().alog("&cAutoload is commencing...");
                Atlas.getInstance().initializeScanner((JavaPlugin) plugin, true, true);
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
