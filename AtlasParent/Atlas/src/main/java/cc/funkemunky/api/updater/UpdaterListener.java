package cc.funkemunky.api.updater;


import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

@Init
public class UpdaterListener implements Listener {

    @ConfigSetting(path = "updater")
    private static boolean notifyOnJoin = true;
    /*We set the event priority to lowest so it appears after (if no other plugin sets it to this)
    every other message a player is sent on join.*/
    @EventHandler(priority = EventPriority.LOWEST)
    public void onEvent(PlayerJoinEvent event) {
        if(event.getPlayer().hasPermission("api.admin")
                && notifyOnJoin && Atlas.getInstance().getUpdater().needsToUpdate()) {
            event.getPlayer().sendMessage(Color
                    .translate("&8[&a&lAtlas&8] &7A new version of Atlas has been released (&f"
                            + Atlas.getInstance().getUpdater().getLatestUpdate() + "&7)!"));
        }
    }
}
