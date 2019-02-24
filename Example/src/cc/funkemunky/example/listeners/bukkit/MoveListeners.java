package cc.funkemunky.example.listeners.bukkit;

import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.example.event.CustomMoveEvent;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

@Init
public class MoveListeners implements Listener {


    @EventHandler
    public void onEvent(PlayerMoveEvent event) {
        CustomMoveEvent move = new CustomMoveEvent(event.getPlayer(), event.getTo(), event.getFrom());
        EventManager.callEvent(move);

        event.setCancelled(move.isCancelled());
    }
}
