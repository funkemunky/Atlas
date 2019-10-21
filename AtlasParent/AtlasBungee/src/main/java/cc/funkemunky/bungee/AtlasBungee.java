package cc.funkemunky.bungee;

import cc.funkemunky.bungee.listeners.JoinListener;
import cc.funkemunky.bungee.utils.ClassScanner;
import cc.funkemunky.bungee.utils.asm.Init;
import cc.funkemunky.bungee.utils.reflection.Reflections;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Comparator;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;

public class AtlasBungee extends Plugin {

    public static AtlasBungee INSTANCE;
    public String outChannel = "atlasIn";
    public ScheduledExecutorService executorService;

    public void onEnable() {
        INSTANCE = this;
        getProxy().registerChannel(outChannel);
        executorService = Executors.newSingleThreadScheduledExecutor();
        registerListeners();
    }

    private void registerListeners() {
        BungeeCord.getInstance().getPluginManager().registerListener(this, new JoinListener());
    }

    public void initializeScanner(Class<?> mainClass, Plugin plugin, boolean loadListeners) {
        ClassScanner.scanFile(null, mainClass)
                .stream()
                .map(Reflections::getClass)
                .sorted(Comparator.comparing(c -> c.getAnnotation(Init.class).priority(), Comparator.reverseOrder()))
                .forEach(c -> {
                    Object obj = c.equals(mainClass) ? plugin : c.getConstructor().newInstance();

                });
    }
}
