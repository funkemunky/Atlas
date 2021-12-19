package cc.funkemunky.api.listeners;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.server.PluginDisableEvent;

@Init
public class PluginShutdownListeners implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onEvent(PluginDisableEvent event) {
        if(event.getPlugin().getDescription().getDepend().contains("Atlas")) {
            Atlas.getInstance().alog("&c" + event.getPlugin().getName() + "" +
                    " &7has been shutdown. Removing its hooks and listeners...");
            Atlas.getInstance().getFunkeCommandManager().removeAll(event.getPlugin());
            Atlas.getInstance().getPacketProcessor().removeListeners(event.getPlugin());
            Atlas.getInstance().getBukkitCommandManager(event.getPlugin()).unregisterCommands();
            Atlas.getInstance().removePluginInstances(event.getPlugin());
            Atlas.getInstance().alog("&aCompleted!");
        }
    }
}
