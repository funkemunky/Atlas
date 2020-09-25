package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.handlers.protocolsupport.ProtocolAPI;
import cc.funkemunky.api.tinyprotocol.api.channel.ChannelListener;
import cc.funkemunky.api.utils.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

@Init
public class InjectListener implements Listener {

    @EventHandler(priority = EventPriority.LOWEST)
    public void onJoin(PlayerJoinEvent event) {
        TinyProtocolHandler.getInstance().inject(event.getPlayer());

        if(!ChannelListener.registered
                || (!Atlas.getInstance().getBungeeManager().isBungee() && ProtocolAPI.INSTANCE.getPlayerVersion(event.getPlayer()) == -1)) {
            event.getPlayer().kickPlayer("Server not fully loaded yet");
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onQuit(PlayerQuitEvent event) {
        TinyProtocolHandler.getInstance().uninject(event.getPlayer());
    }
}
