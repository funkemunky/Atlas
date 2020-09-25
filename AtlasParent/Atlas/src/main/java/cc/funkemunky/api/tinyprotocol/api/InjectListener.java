package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.utils.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Init
public class InjectListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        TinyProtocolHandler.getInstance().inject(event.getPlayer());
    }
}
