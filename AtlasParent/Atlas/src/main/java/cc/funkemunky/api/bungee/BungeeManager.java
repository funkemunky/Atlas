package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.events.BungeeReceiveEvent;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.handlers.ForgeHandler;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.RunUtils;
import cc.funkemunky.api.utils.Tuple;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;

@Getter
public class BungeeManager implements PluginMessageListener {
    private String channelOut = "BungeeCord", channelIn = "BungeeCord";
    private String atlasIn = "atlasIn", atlasOut = "atlasOut";
    private Map<UUID, BungeePlayer> bungeePlayers = new HashMap<>();
    private Map<UUID, Tuple<Boolean, Integer>> versionsMap = new HashMap<>();
    private boolean isBungee;
    private List<String> bungeeServers = Collections.synchronizedList(new ArrayList<>());
    private BukkitTask serverCheckTask;
    private long bungeePing, bungeeToPing, bungeeFromPing;

    public BungeeManager() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), channelOut);
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), atlasOut);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), channelIn, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), atlasIn, this);

        /*new BukkitRunnable() {
            public void run() {
                if(Atlas.getInstance().isDone() && Atlas.getInstance().isEnabled()) {
                    try {
                        ByteArrayOutputStream baos = new ByteArrayOutputStream();
                        DataOutputStream output = new DataOutputStream(baos);
                        output.writeUTF("GetServers");
                        sendData(baos.toByteArray());
                    } catch (IOException e) {
                        MiscUtils.printToConsole("&cFailed to check if the server is connected to Bungee!");
                        e.printStackTrace();
                    }
                    this.cancel();
                    runServerCheckTask();
                }
            }
        }.runTaskTimerAsynchronously(Atlas.getInstance(), 20L, 10L);*/
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
            stream.writeUTF(server);
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
                    case "GetServers": {
                        MiscUtils.printToConsole("&7Grabbed servers.");
                        bungeeServers.clear();
                        bungeeServers.addAll(Arrays.asList(input.readUTF().split(", ")));
                        break;
                    }
                }


            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        } else if(channel.equals("atlasIn")) {
            try {
                ObjectInputStream input = new ObjectInputStream(stream);

                String dataType = input.readUTF();
                switch (dataType) {
                    case "mods": {
                        UUID uuid = (UUID) input.readObject();
                        Object modsObject = input.readObject();

                        if (!(modsObject instanceof String)) {
                            Map<String, String> mods = (Map<String, String>) modsObject;
                            Player pl = Bukkit.getPlayer(uuid);
                            if (pl != null) {
                                ForgeHandler.runBungeeModChecker(pl, mods);
                            }
                        }
                        break;
                    }
                    case "sendObjects":
                        String type = input.readUTF();

                        byte[] array = new byte[input.readShort()];
                        input.readFully(array);

                        ObjectInputStream objectInput = new ObjectInputStream(new ByteArrayInputStream(array));

                        Object[] objects = new Object[objectInput.readShort()];

                        for (int i = 0; i < objects.length; i++) {
                            objects[i] = objectInput.readObject();
                        }

                        Atlas.getInstance().getEventManager().callEvent(new BungeeReceiveEvent(objects, type));
                        break;
                    case "version": {
                        boolean success = input.readBoolean();
                        int version = input.readInt();
                        UUID uuid = (UUID) input.readObject();

                        versionsMap.put(uuid, new Tuple<>(success, version));
                        break;
                    }
                    /*case "ping": {
                        String name = input.readUTF();
                        long start = input.readLong();
                        long halfPing = input.readLong();

                        bungeePing = System.currentTimeMillis() - start;
                        bungeeToPing = halfPing;
                        bungeeFromPing = MathUtils.getDelta(bungeePing, bungeeToPing);
                        break;
                    }*/
                }
            } catch (IOException | ClassNotFoundException e) {
                e.printStackTrace();
            }
        }
    }

    private void runServerCheckTask() {
        if(serverCheckTask != null) serverCheckTask.cancel();
        serverCheckTask = RunUtils.taskTimerAsync(() -> {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream output = new ObjectOutputStream(baos);
                output.writeUTF("GetServers");
                sendData(baos.toByteArray());

                baos = new ByteArrayOutputStream();
                output = new ObjectOutputStream(baos);

                output.writeUTF("ping");
                output.writeLong(System.currentTimeMillis());
                sendData(baos.toByteArray(), atlasOut);
            } catch (IOException e) {
                MiscUtils.printToConsole("&cFailed to check if the server is connected to Bungee!");
                e.printStackTrace();
            }
        }, 20*30, 20*60);
    }
}
