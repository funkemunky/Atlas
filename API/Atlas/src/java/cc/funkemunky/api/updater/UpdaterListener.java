package cc.funkemunky.api.updater;


import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Init
public class UpdaterListener implements Listener {

    @ConfigSetting(path = "updater")
    private boolean notifyOnJoin = true;
    @EventHandler
    public void onEvent(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission("api.admin")
                && notifyOnJoin && Atlas.getInstance().getUpdater().needsToUpdate()) {
            event.getPlayer().sendMessage(Color.translate("&8[&a&lAtlas&8] &7A new version of Atlas has been released (&f" + Atlas.getInstance().getUpdater().getVersion() + "&7)!"));
        }
    }
}
