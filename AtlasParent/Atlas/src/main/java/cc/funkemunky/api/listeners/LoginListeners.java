package cc.funkemunky.api.listeners;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.Priority;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;

@Init
public class LoginListeners implements Listener {

    @EventHandler(priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onJoin(PlayerJoinEvent event) {
        Atlas.getInstance().getTinyProtocolHandler().injectPlayer(event.getPlayer());
    }
}
