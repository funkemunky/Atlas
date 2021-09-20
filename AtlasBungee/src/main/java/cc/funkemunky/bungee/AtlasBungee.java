package cc.funkemunky.bungee;

import cc.funkemunky.bungee.utils.StringUtils;
import cc.funkemunky.bungee.utils.asm.ClassScanner;
import cc.funkemunky.bungee.utils.asm.Init;
import cc.funkemunky.bungee.utils.reflection.Reflections;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AtlasBungee extends Plugin {

    public static AtlasBungee INSTANCE;
    public String outChannel = "atlas:in", inChannel = "atlas:out";
    public ScheduledExecutorService executorService;

    public void onEnable() {
        INSTANCE = this;
        executorService = Executors.newSingleThreadScheduledExecutor();

        initializeScanner(this);
    }

    public void onDisable() {
        executorService.shutdown();
    }

    public void initializeScanner(Class<? extends Plugin> mainClass, Plugin plugin, boolean loadListeners) {
        ClassScanner.scanFile(null, mainClass)
                .stream()
                .map(Reflections::getClass)
                .filter(c -> {
                    Init ann = c.getAnnotation(Init.class);

                    if(ann == null) return false;

                    if(ann.requirePlugins().length > 0) {
                        if(ann.requireType() == Init.RequireType.ALL) {
                            return Arrays.stream(ann.requirePlugins())
                                    .allMatch(name -> plugin.getProxy().getPluginManager().getPlugin(name) != null);
                        } else {
                            return Arrays.stream(ann.requirePlugins())
                                    .anyMatch(name -> plugin.getProxy().getPluginManager().getPlugin(name) != null);
                        }
                    }
                    return true;
                })
                .sorted(Comparator.comparing(c -> c.getAnnotation(Init.class).priority(), Comparator.reverseOrder()))
                .forEach(c -> {
                    Object obj = c.getParent().equals(mainClass) ? plugin : c.getConstructor().newInstance();

                    if(loadListeners && obj instanceof Listener) {
                        BungeeCord.getInstance().getPluginManager().registerListener(plugin, (Listener)obj);
                        StringUtils.printMessage("&7Registered listeners in object &e" + c.getParent().getSimpleName() + "&7.");
                    }
                });
    }

    public void initializeScanner(Plugin plugin, boolean loadListeners) {
        initializeScanner(plugin.getClass(), plugin, loadListeners);
    }

    public void initializeScanner(Plugin plugin) {
        initializeScanner(plugin, true);
    }
}
