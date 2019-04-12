package cc.funkemunky.api.handlers;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.AutoLoad;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.MiscUtils;
import io.netty.util.internal.ConcurrentSet;
import lombok.Getter;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginEnableEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.lang.reflect.InvocationTargetException;
import java.util.*;
import java.util.concurrent.FutureTask;

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

    public boolean isPluginLoaded(Plugin plugin) {
        return loadedPlugins.contains(plugin);
    }

    private void loadPlugin(Plugin plugin) {
        try {
            val plClass = Class.forName(plugin.getDescription().getMain());
            if(plClass.isAnnotationPresent(AutoLoad.class)) {
                MiscUtils.printToConsole("&cAutoload is commencing...");
                List<FutureTask> tasks = new ArrayList<>();

                MiscUtils.printToConsole("&7Looking for extra FutureTasks set in the main class...");
                Arrays.stream(plClass.getDeclaredFields())
                        .filter(field -> field.isAnnotationPresent(AutoLoad.class) && field.getType().equals(FutureTask.class))
                        .forEachOrdered(field -> {
                            try {
                                val task = (FutureTask<?>) field.get(plugin);
                                tasks.add(task);
                            } catch(IllegalAccessException e) {
                                e.printStackTrace();
                            }
                        });

                Arrays.stream(plClass.getDeclaredMethods())
                        .filter(method -> method.isAnnotationPresent(AutoLoad.class) && method.getReturnType().equals(FutureTask.class) && method.getParameters().length == 0)
                        .forEachOrdered(method -> {
                            try {
                                val task = (FutureTask<?>) method.invoke(plugin);

                                tasks.add(task);
                            } catch(IllegalAccessException | InvocationTargetException e) {
                                e.printStackTrace();
                            }
                        });

                MiscUtils.printToConsole("&7Running scanner...");
                Atlas.getInstance().initializeScanner(plClass, (JavaPlugin) plugin, true, true, (FutureTask[]) tasks.toArray());
            }
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

}
