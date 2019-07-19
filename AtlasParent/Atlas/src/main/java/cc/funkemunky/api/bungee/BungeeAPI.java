package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.utils.Color;
import org.bukkit.Bukkit;

import java.io.ByteArrayOutputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.util.UUID;

public class BungeeAPI {
    
    public void broadcastMessage(String message) {
        broadcastMessage(message, "");
    }

    public void broadcastMessage(String message, String permission) {
        for (UUID uuid : Atlas.getInstance().getBungeeManager().getBungeePlayers().keySet()) {
            BungeePlayer player = Atlas.getInstance().getBungeeManager().getBungeePlayers().get(uuid);

            if(!player.permissions.contains(permission)) continue;

            sendMessageToPlayer(player.name, message);
        }
    }

    public void movePlayerToServer(String playerName, String server) {
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

    public void movePlayerToServer(UUID uuid, String server) {
        movePlayerToServer(Bukkit.getOfflinePlayer(uuid).getName(), server);
    }

    public void kickPlayer(String name, String reason) {
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

    public void sendMessageToPlayer(String name, String message) {
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

    public void sendMessageToPlayer(BungeePlayer player, String message) {
        sendMessageToPlayer(player.name, message);
    }

    public void sendCommand(String command) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream oStream = new DataOutputStream(stream);

        try {
            oStream.writeUTF("commandBungee");
            oStream.writeUTF(command);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
    }

    public void queryUpdate(boolean waitUntilComplete) {
        //TODO Query update.
    }

    public void kickPlayer(UUID uuid, String reason) {
        kickPlayer(Bukkit.getOfflinePlayer(uuid).getName(), reason);
    }
}
