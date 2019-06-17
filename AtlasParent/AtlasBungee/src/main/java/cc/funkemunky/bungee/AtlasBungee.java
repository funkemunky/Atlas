package cc.funkemunky.bungee;

import cc.funkemunky.bungee.listeners.MessageListener;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Plugin;

public class AtlasBungee extends Plugin {

    public static AtlasBungee INSTANCE;

    public void onEnable() {
        INSTANCE = this;
    }

    private void registerListeners() {
        BungeeCord.getInstance().getPluginManager().registerListener(this, new MessageListener());
    }
}
