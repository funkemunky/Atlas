package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.bungee.objects.Version;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.util.UUID;
import java.util.logging.Level;


@Init
public class BungeeAPI {

    @ConfigSetting(name = "bungee")
    public static boolean bungee = true;
    
    public static void broadcastMessage(String message) {
        broadcastMessage(message, "");
    }

    public static void broadcastMessage(String message, String permission) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(stream);

            oStream.writeUTF("broadcastMsg");
            oStream.writeObject(message);
            oStream.writeObject(permission);
            oStream.close();

            Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray(), "atlas:out");
        } catch(IOException e) {
            Atlas.getInstance().getLogger().log(Level.WARNING, "Failed to send broadcast msg", e);
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
            Atlas.getInstance().getLogger().log(Level.WARNING, "Failed to move player to server", e);
            return;
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
            Atlas.getInstance().getLogger().log(Level.WARNING, "Failed to kick player", e);
            return;
        }

        Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
    }

    public static void requestVersionFromBungee(UUID uuid) {
         try {
             ByteArrayOutputStream stream = new ByteArrayOutputStream();
             ObjectOutputStream dataOutput = new ObjectOutputStream(stream);
             dataOutput.writeUTF("version");
             dataOutput.writeObject(uuid);
             dataOutput.close();

             Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
         } catch(IOException e) {
             Atlas.getInstance().getLogger().log(Level.WARNING, "Failed to request version from Bungee", e);
         }
    }

    public static void sendMessageToPlayer(String name, String message) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(stream);
            oStream.writeUTF("Message");
            oStream.writeUTF(name);
            oStream.writeUTF(Color.translate(message));
            oStream.close();
            Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
        } catch (IOException e) {
            Atlas.getInstance().getLogger().log(Level.WARNING, "Failed to send message to player: " + name, e);
        }
    }

    public static void sendMessageToPlayer(BungeePlayer player, String message) {
        sendMessageToPlayer(player.name, message);
    }

    @Deprecated
    public static int getPlayerVersion(Player player) {
        if(player == null) return -1;

        if(Atlas.getInstance().getBungeeManager().getVersionsMap().containsKey(player.getUniqueId())) {
            return Atlas.getInstance().getBungeeManager().getVersionsMap().get(player.getUniqueId()).version;
        }

        return -1;
    }

    public static Version getVersion(UUID uuid) {
        return Atlas.getInstance().getBungeeManager().getVersionsMap().computeIfAbsent(uuid, key -> {
            requestVersionFromBungee(uuid);
            return null;
        });
    }

    public static void sendCommand(String command) {
        try {
            ByteArrayOutputStream stream = new ByteArrayOutputStream();
            ObjectOutputStream oStream = new ObjectOutputStream(stream);
            oStream.writeUTF("commandBungee");
            oStream.writeObject(command);
            oStream.close();
            var array = stream.toByteArray();

            Atlas.getInstance().getBungeeManager().sendData(array, "atlas:out");
        } catch (IOException e) {
            Atlas.getInstance().getLogger().log(Level.WARNING, "Failed to send command to Bungee", e);
        }
    }

    public static void kickPlayer(UUID uuid, String reason) {
        kickPlayer(Bukkit.getOfflinePlayer(uuid).getName(), reason);
    }
}
