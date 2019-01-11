package cc.funkemunky.api.updater;


import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Color;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

public class UpdaterListener implements Listener {

    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission("api.admin")
                && Atlas.getInstance().getConfig().getBoolean("updater.notifyOnJoin") && Atlas.getInstance().getUpdater().needsToUpdate()) {
            event.getPlayer().sendMessage(Color.translate("&8[&a&lAtlas&8] &7A new version of Atlas has been released (&f" + Atlas.getInstance().getUpdater().getVersion() + "&7)!"));
        }
    }
}
