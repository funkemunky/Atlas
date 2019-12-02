package cc.funkemunky.bungee.listeners;

import cc.funkemunky.bungee.AtlasBungee;
import cc.funkemunky.bungee.data.ModData;
import cc.funkemunky.bungee.utils.asm.Init;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.event.PreLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Init
public class JoinListener implements Listener {

    public JoinListener() {
        BungeeCord.getInstance().registerChannel("FML|HS");
    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        AtlasBungee.INSTANCE.executorService.schedule(() -> {
            sendFmlPacket(player, (byte) -2, (byte) 0);
            sendFmlPacket(player, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
            sendFmlPacket(player, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
        }, 1, TimeUnit.SECONDS);
    }

    @EventHandler
    public void onPME(PluginMessageEvent event) {
        // ModList has ID 2
        if (event.getData()[0] == 2)
        {
            try {
                ModData modData = getModData(event.getData());
                ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                ObjectOutputStream data = new ObjectOutputStream(byteStream);
                data.writeUTF("mods");
                data.writeObject(modData.getModsMap());

                BungeeCord.getInstance().getServers()
                        .values()
                        .forEach(server -> server.sendData("atlasIn", byteStream.toByteArray()));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * Sends a packet through the FML|HS channel
     *
     * @param player The player to send the packet to
     * @param data The data to send with the packet
     */
    private static void sendFmlPacket(ProxiedPlayer player, byte... data)
    {
        player.sendData("FML|HS", data);
    }

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
}
