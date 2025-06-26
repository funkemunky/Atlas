package cc.funkemunky.api.handlers;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.RunUtils;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

@Init
public class ForgeHandler implements Listener, PluginMessageListener {

    public ForgeHandler() {
        Atlas.getInstance().getServer().getMessenger()
                .registerIncomingPluginChannel(Atlas.getInstance(), ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)
                        ? "fml:handshake" : "FML|HS", this);
        Atlas.getInstance().getServer().getMessenger()
                .registerOutgoingPluginChannel(Atlas.getInstance(), ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)
                        ? "fml:handshake" : "FML|HS");
        INSTANCE = this;
    }

    @ConfigSetting(path = "forge", name = "enabled")
    private static boolean enabled = false;

    @ConfigSetting(path = "forge", name = "bungee")
    public static boolean fromBungee = false;

    private static Map<Player, ModData> mods = new HashMap<>();

    private static ForgeHandler INSTANCE;

    @EventHandler
    public void onJoin(PlayerJoinEvent event)
    {
        if(enabled) {
            if(!Atlas.getInstance().getBungeeManager().isBungee()) {
                Player player = event.getPlayer();

                new BukkitRunnable() {
                    @Override
                    public void run() {
                        sendFmlPacket(player, (byte) -2);
                        sendFmlPacket(player, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
                    }
                }.runTaskLater(Atlas.getInstance(), 20L);
            } else {
                RunUtils.taskLater(() -> {
                    queryBungeeMods(event.getPlayer());
                }, 80L);
            }
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
        player.sendPluginMessage(Atlas.getInstance(), ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13) 
                ? "fml:handshake" : "FML|HS", data);
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] data)
    {
        // ModList has ID 2
        if (data[0] == 2)
        {
            ModData modData = getModData(data);
            if(modData != null && modData.getMods().size() > 0) {
                mods.put(player, modData);
            }
            sendFmlPacket(player, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
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

    public static ModData getMods(Player player) {
        return mods.computeIfAbsent(player, key -> {
            if(Atlas.getInstance().getBungeeManager().isBungee()) {
                ForgeHandler.INSTANCE.queryBungeeMods(player);
            }

            return null;
        });
    }

    public static void runBungeeModChecker(Player player, Map<String, String> modStrings) {
        mods.put(player, new ModData(modStrings));
    }

    private void queryBungeeMods(Player player) {
        if(Atlas.getInstance().getBungeeManager().isBungee()) {
            try {
                ByteArrayOutputStream bytesOut = new ByteArrayOutputStream();
                ObjectOutputStream output = new ObjectOutputStream(bytesOut);

                output.writeUTF("mods");
                output.writeObject(player.getUniqueId());
                output.close();

                player.sendPluginMessage(Atlas.getInstance(), "atlas:out", bytesOut.toByteArray());
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
