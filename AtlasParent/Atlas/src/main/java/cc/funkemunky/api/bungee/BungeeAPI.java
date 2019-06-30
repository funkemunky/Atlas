package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
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
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream oStream = new DataOutputStream(stream);

        try {
            oStream.writeUTF("broadcastMessage");
            oStream.writeUTF(Color.translate(message));
            oStream.writeUTF(permission);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Atlas.getInstance().getBungeeManager().sendData(stream.toByteArray());
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

    public void kickPlayer(UUID uuid, String reason) {
        kickPlayer(Bukkit.getOfflinePlayer(uuid).getName(), reason);
    }
}
