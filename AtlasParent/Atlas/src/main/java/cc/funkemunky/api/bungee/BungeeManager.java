package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.events.BungeeReceiveEvent;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.handlers.ForgeHandler;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

@Getter
public class BungeeManager implements PluginMessageListener {
    private String channelOut = "BungeeCord", channelIn = "BungeeCord";
    private String atlasIn = "atlasIn", atlasOut = "atlasOut";
    private BungeeAPI bungeeAPI;
    private Map<UUID, BungeePlayer> bungeePlayers = new HashMap<>();

    public BungeeManager() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), channelOut);
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), atlasOut);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), channelIn, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), atlasIn, this);
        bungeeAPI = new BungeeAPI();
    }

    public void sendData(byte[] data, String out) {
        Bukkit.getServer().sendPluginMessage(Atlas.getInstance(), out, data);
    }

    public void sendData(byte[] data) {
        sendData(data, channelOut);
    }

    public void sendObjects(String server, Object... objects) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(output);

        boolean override  = server.toLowerCase().contains("override");
        if(override) {
            stream.writeUTF("sendObjects");
        } else {
            stream.writeUTF("Forward");
            stream.writeUTF(server);
            stream.writeUTF("Forward");
        }

        ByteArrayOutputStream objectOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(objectOutput);

        objectStream.writeShort(objects.length);

        for (Object object : objects) objectStream.writeObject(object);

        byte[] array = objectOutput.toByteArray();

        stream.writeShort(array.length);
        stream.write(array);

        sendData(output.toByteArray(), override ? atlasOut : channelOut); //This is where we finally send the data
    }

    @Override
    public void onPluginMessageReceived(String channel, Player player, byte[] bytes) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
        if(channel.equals("Bungee")) {
            try {
                DataInputStream input = new DataInputStream(stream);

                String type = input.readUTF();

                switch(type) {
                    case "Forward": {
                        byte[] array = new byte[input.readShort()];
                        input.readFully(array);

                        ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(array));

                        Object[] objects = new Object[objectInput.readShort()];

                        for (int i = 0; i < objects.length; i++) {
                            objects[i] = objectInput.readObject();
                        }

                        Atlas.getInstance().getEventManager().callEvent(new BungeeReceiveEvent(objects, type));
                        break;
                    }
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if(ForgeHandler.fromBungee && channel.equals("atlasIn")) {
            try {
                ObjectInputStream objectInput = new ObjectInputStream(stream);

                if(objectInput.readUTF().equals("mods")) {
                    Map<String, String> mods = (Map<String, String>) objectInput.readObject();
                    ForgeHandler.runBungeeModChecker(player, mods);
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }
}
