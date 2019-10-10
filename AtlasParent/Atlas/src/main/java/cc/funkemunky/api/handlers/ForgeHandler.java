package cc.funkemunky.api.handlers;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Init
public class ForgeHandler implements Listener, PluginMessageListener {

    public ForgeHandler() {
        Atlas.getInstance().getServer().getMessenger()
                .registerIncomingPluginChannel(Atlas.getInstance(), "FML|HS", this);
        Atlas.getInstance().getServer().getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), "FML|HS");
    }

    @ConfigSetting(path = "forge", name = "enabled")
    private static boolean enabled = true;

    @ConfigSetting(path = "forge", name = "bungee")
    private static boolean fromBungee = false;

    private static Map<Player, ModData> mods = new HashMap<>();

    public static ModData getMods(Player player) {
        return mods.getOrDefault(player, null);
    }

    @EventHandler
    public void onLogin(PlayerLoginEvent event) {
        if(enabled) {
            event.getPlayer().sendPluginMessage(Atlas.getInstance(), "FML|HS", new byte[] {-2, 0});
            event.getPlayer().sendPluginMessage(Atlas.getInstance(), "FML|HS", new byte[] {0, 2, 0, 0, 0, 0});
            event.getPlayer().sendPluginMessage(Atlas.getInstance(), "FML|HS", new byte[] {2, 0, 0, 0, 0});
        }
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        if(!enabled) return;
        if (s.equals("FML|HS")) {
            mods.put(player, getModData(bytes));
        }
    }

    public static void runBungeeModChecker(Player player, Map<String, String> modStrings) {
        mods.put(player, new ModData(modStrings));
    }
    /**
     * Credit: https://github.com/Mas281/ForgeModBlocker/
     * Fetches a {@link ModData} object from the raw mods data
     *
     * @param data The input data
     * @return A ModData object
     */
    private static ModData getModData(byte[] data)
    {
        Map<String, String> mods = new HashMap<>();

        boolean store = false;
        String tempName = null;

        for (int i = 2; i < data.length; store = !store)
        {
            int end = i + data[i] + 1;
            byte[] range = Arrays.copyOfRange(data, i + 1, end);

            String string = new String(range);

            if (store)
            {
                mods.put(tempName, string);
            }
            else
            {
                tempName = string;
            }

            i = end;
        }

        return new ModData(mods);
    }
}
