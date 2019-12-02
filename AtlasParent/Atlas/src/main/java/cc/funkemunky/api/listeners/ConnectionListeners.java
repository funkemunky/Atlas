package cc.funkemunky.api.listeners;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.utils.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

@Init
public class ConnectionListeners implements Listener {

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        TinyProtocolHandler.bungeeVersionCache.remove(event.getPlayer().getUniqueId());
    }
}
