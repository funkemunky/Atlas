package cc.funkemunky.bungee.listeners;

import cc.funkemunky.bungee.AtlasBungee;
import cc.funkemunky.bungee.data.ModData;
import cc.funkemunky.bungee.data.user.User;
import cc.funkemunky.bungee.utils.asm.Init;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.UserConnection;
import net.md_5.bungee.api.connection.ProxiedPlayer;
import net.md_5.bungee.api.event.PluginMessageEvent;
import net.md_5.bungee.api.event.PostLoginEvent;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.event.EventHandler;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

@Init
public class JoinListener implements Listener {

    public static boolean isForgeSupport = false;
    public JoinListener() {
        if(BungeeCord.getInstance().getChannels().contains("FML|HS")) {
            BungeeCord.getInstance().unregisterChannel("FML|HS");
        }


        try {
            BungeeCord.getInstance().getConfig().getClass().getMethod("isForgeSupport");

            isForgeSupport = BungeeCord.getInstance().getConfig().isForgeSupport();
        } catch (NoSuchMethodException e) {
            isForgeSupport = false;
        }

    }

    @EventHandler
    public void onJoin(PostLoginEvent event) {
        ProxiedPlayer player = event.getPlayer();

        BungeeCord.getInstance().getScheduler().schedule(AtlasBungee.INSTANCE, () -> {
            User user = User.getUser(event.getPlayer().getUniqueId());
            if(!isForgeSupport) {
                if(user.brand != null && (user.brand.toLowerCase().contains("fml") || user.brand.toLowerCase().contains("forge"))) {
                    sendFmlPacket(user, (byte) -2, (byte) 0);
                    sendFmlPacket(user, (byte) 0, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
                    sendFmlPacket(user, (byte) 2, (byte) 0, (byte) 0, (byte) 0, (byte) 0);
                }
            } else if(event.getPlayer().isForgeUser()) {
                if(user != null) {
                    user.modData = new ModData(event.getPlayer().getModList());
                }
            }
        }, 1, TimeUnit.SECONDS);

        User user = User.getUser(event.getPlayer().getUniqueId());

        int version = -1;
        try {
            Class<?> Via = Class.forName("us.myles.ViaVersion.api.Via");
            Class<?> clazzViaAPI = Class.forName("us.myles.ViaVersion.api.ViaAPI");
            Object ViaAPI = Via.getMethod("getAPI").invoke(null);
            Method getPlayerVersion = clazzViaAPI.getMethod("getPlayerVersion", Object.class);
            version = (int) getPlayerVersion.invoke(ViaAPI, player);
        } catch(Exception e) {
            version = player.getPendingConnection().getVersion();
        }
        user.version = version;
    }

    @EventHandler
    public void onPME(PluginMessageEvent event) {
        // ModList has ID 2
        if (event.getData().length > 0 && event.getData()[0] == 2)
        {
            UserConnection connection = (UserConnection) event.getSender();
            User user = User.getUser(connection.getUniqueId());

            user.modData = getModData(event.getData());
        }
    }

    /**
     * Sends a packet through the FML|HS channel
     *
     * @param user The player to send the packet to
     * @param data The data to send with the packet
     */
    private static void sendFmlPacket(User user, byte... data)
    {
        user.getPlayer().sendData(user.legacy ? "FML|HS" : "fml:handshake", data);
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
