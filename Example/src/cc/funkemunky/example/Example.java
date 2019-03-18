package cc.funkemunky.example;

import cc.funkemunky.api.Atlas;
import org.bukkit.plugin.java.JavaPlugin;

public class Example extends JavaPlugin {

    public void onEnable() {
        Atlas.getInstance().initializeScanner(getClass(), this, Atlas.getInstance().getCommandManager());
    }
}
