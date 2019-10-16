package cc.funkemunky.api.handlers;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

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
    public static boolean fromBungee = false;

    private static Map<Player, ModData> mods = new HashMap<>();

    public static ModData getMods(Player player) {
        return mods.getOrDefault(player, null);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        if(enabled && !fromBungee) {
            Player player = event.getPlayer();

            new BukkitRunnable()
            {
                @Override
                public void run()
                {
                    sendFmlPacket(player, (byte) -2, (byte) 0);
                    sendFmlPacket(player, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
                    sendFmlPacket(player, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
                }
            }.runTaskLater(Atlas.getInstance(), 20L);
        }
    }

    /**
     * Sends a packet through the FML|HS channel
     *
     * @param player The player to send the packet to
     * @param data The data to send with the packet
     */
    private static void sendFmlPacket(Player player, byte... data)
    {
        player.sendPluginMessage(Atlas.getInstance(), "FML|HS", data);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data)
    {
        // ModList has ID 2
        if (data[0] == 2)
        {
            ModData modData = getModData(data);
            mods.put(player, modData);
        }
    }

    /**
     * Fetches a {@link ModData} object from the raw mods data
     *
     * @param data The input data
     * @return A ModData object
     */
    private ModData getModData(byte[] data)
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

    public static void runBungeeModChecker(Player player, Map<String, String> modStrings) {
        mods.put(player, new ModData(modStrings));
    }
}
