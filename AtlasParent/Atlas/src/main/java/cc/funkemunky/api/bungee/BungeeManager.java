package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.events.BungeeReceiveEvent;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.bungee.objects.Version;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.Listen;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.handlers.ForgeHandler;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInCustomPayload;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;

@Getter
public class BungeeManager implements AtlasListener, PluginMessageListener {
    private String channelOut = "BungeeCord", channelIn = "BungeeCord";
    private String atlasIn = "atlas:in", atlasOut = "atlas:out";
    private Map<UUID, BungeePlayer> bungeePlayers = new HashMap<>();
    private Map<UUID, Version> versionsMap = new HashMap<>();
    private boolean isBungee;
    private List<String> bungeeServers = Collections.synchronizedList(new ArrayList<>());
    private BukkitTask serverCheckTask;

    public BungeeManager() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), channelOut);
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), atlasOut);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), channelIn, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), atlasIn, this);


        Atlas.getInstance().getEventManager().registerListeners(this, Atlas.getInstance());

        try {
            val wrappedClass = new WrappedClass(Class.forName("org.spigotmc.SpigotConfig"));

            isBungee = wrappedClass.getFieldByName("bungee").get(null);
        } catch(ClassNotFoundException e) {
            //empty
            isBungee = false;
        }
        if(BungeeAPI.bungee)
        isBungee = BungeeAPI.bungee;

        if(isBungee) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeUTF("heartbeat");
                oos.writeUTF("reloadChannels");

                sendData(baos.toByteArray(), atlasOut);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void sendData(byte[] data, String out) {
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendPluginMessage(Atlas.getInstance(), out, data);
            break;
        }
    }

    public void sendData(byte[] data) {
        sendData(data, channelOut);
    }

    public void sendObjects(String server, Object... objects) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(output);

        boolean override  = server.toLowerCase().contains("override");
        if(override) {
            stream.writeObject("sendObjects");
            stream.writeObject(server);
        } else {
            stream.writeObject("Forward");
            stream.writeObject(server);
            stream.writeObject("Forward");
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

    @Listen
    public void onPluginMessageReceived(PacketReceiveEvent event) {
        if(event.getType().equals(Packet.Client.CUSTOM_PAYLOAD)) {
            WrappedInCustomPayload wrapped = new WrappedInCustomPayload(event.getPacket(), event.getPlayer());
            byte[] bytes = wrapped.getData();
            if(bytes == null || bytes.length <= 0) return;

            String channel = wrapped.getTag();
            ByteArrayInputStream stream = new ByteArrayInputStream(bytes);
            if (channel.equals("Bungee")) {
                try {
                    DataInputStream input = new DataInputStream(stream);

                    String type = input.readUTF();

                    switch (type) {
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
            } else if (channel.equals(atlasIn)) {
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
                                    System.out.println("Received mods for " + pl.getName());
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
                            String brand = (String) input.readObject();
                            boolean legacy = input.readBoolean();

                            if(success) versionsMap.put(uuid, new Version(version, brand, legacy));
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
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

    }

    /*private void runServerCheckTask() {
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
    }*/
}
