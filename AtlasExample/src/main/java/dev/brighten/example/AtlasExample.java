package dev.brighten.example;

import cc.funkemunky.api.Atlas;
import org.bukkit.Bukkit;
import org.bukkit.event.HandlerList;
import org.bukkit.plugin.java.JavaPlugin;

public class AtlasExample extends JavaPlugin {

    public void onEnable() {
        print("&aAtlasExample v" + getDescription().getVersion(), true);

        print("scanner", true);
        Atlas.getInstance().initializeScanner(this, true, true);
    }

    public void onDisable() {
        print("&cAtlasExample", false);

        print("listeners", false);
        HandlerList.unregisterAll(this);

        print("tasks", false);
        Bukkit.getScheduler().cancelTasks(this);
    }

    private void print(String msg, boolean enable) {
        Atlas.getInstance().alog("&7"
                + (enable ? "Enabling " : "Disabling ") + msg + "...");
    }
}
