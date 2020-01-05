package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.MathUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;


@Init
public class BungeeAPI {

    @ConfigSetting(name = "bungee")
    public static boolean bungee = true;
    
    public static void broadcastMessage(String message) {
        broadcastMessage(message, "");
    }

    public static void broadcastMessage(String message, String permission) {
        for (UUID uuid : Atlas.getInstance().getBungeeManager().getBungeePlayers().keySet()) {
            BungeePlayer player = Atlas.getInstance().getBungeeManager().getBungeePlayers().get(uuid);

            if(!player.permissions.contains(permission)) continue;

            sendMessageToPlayer(player.name, message);
        }
    }

    public static void movePlayerToServer(String playerName, String server) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream oStream = new DataOutputStream(stream);

        try {
            oStream.writeUTF("ConnectOther");
            oStream.writeUTF(playerName);
            oStream.writeUTF(server);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
    }

    public static void movePlayerToServer(UUID uuid, String server) {
        movePlayerToServer(Bukkit.getOfflinePlayer(uuid).getName(), server);
    }

    public static void kickPlayer(String name, String reason) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream oStream = new DataOutputStream(stream);

        try {
            oStream.writeUTF("KickPlayer");
            oStream.writeUTF(name);
            oStream.writeUTF(reason);
        } catch (IOException e) {
            e.printStackTrace();
        }

        Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
    }

    public static void sendMessageToPlayer(String name, String message) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream oStream = new DataOutputStream(stream);

        try {
            oStream.writeUTF("Message");
            oStream.writeUTF(name);
            oStream.writeUTF(Color.translate(message));
        } catch (IOException e) {
            e.printStackTrace();
        }

        Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
    }

    public static void sendMessageToPlayer(BungeePlayer player, String message) {
        sendMessageToPlayer(player.name, message);
    }

    public static int getPlayerVersion(Player player) {
        if(player == null) return -1;

        if(Atlas.getInstance().getBungeeManager().getVersionsMap().containsKey(player.getUniqueId())) {
            return Atlas.getInstance().getBungeeManager().getVersionsMap().get(player.getUniqueId()).two;
        }

        return -1;
    }

    public static void sendCommand(String command) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream oStream = new DataOutputStream(stream);

        try {
            oStream.writeUTF("commandBungee");
            oStream.writeUTF(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray(), "atlasOut");
    }

    public static void kickPlayer(UUID uuid, String reason) {
        kickPlayer(Bukkit.getOfflinePlayer(uuid).getName(), reason);
    }
}
