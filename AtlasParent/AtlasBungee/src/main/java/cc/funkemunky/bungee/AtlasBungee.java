package cc.funkemunky.bungee;

import cc.funkemunky.bungee.listeners.JoinListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

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


}
