package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.events.BungeeReceiveEvent;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.bungee.objects.Version;
import cc.funkemunky.api.handlers.ForgeHandler;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.in.WrappedInCustomPayload;
import cc.funkemunky.api.utils.RunUtils;
import cc.funkemunky.api.utils.Tuple;
import lombok.Getter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;
import org.bukkit.scheduler.BukkitTask;

import java.io.*;
import java.util.*;
import java.util.logging.Level;

public class BungeeManager implements PluginMessageListener {
    @Getter
    private final String channelOut = "BungeeCord", channelIn = "BungeeCord";
    @Getter
    private final String atlasIn = "atlas:in", atlasOut = "atlas:out";
    @Getter
    private final Map<UUID, BungeePlayer> bungeePlayers = new HashMap<>();
    @Getter
    private final Map<UUID, Version> versionsMap = new HashMap<>();
    @Getter
    private boolean isBungee, atlasBungeeInstalled;
    @Getter
    private final List<String> bungeeServers = Collections.synchronizedList(new ArrayList<>());
    private BukkitTask serverCheckTask;
    private boolean receivedHeartbeat;
    private long lastHeartbeatReceive;
    private final Queue<Tuple<String, byte[]>> toSend = new LinkedList<>();

    public BungeeManager() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), channelOut);
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), atlasOut);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), channelIn, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), atlasIn, this);

        try {
            val wrappedClass = new WrappedClass(Class.forName("org.spigotmc.SpigotConfig"));

            isBungee = wrappedClass.getFieldByName("bungee").get(null);
        } catch(ClassNotFoundException e) {
            Bukkit.getLogger().severe("Class not found");
            //empty
            isBungee = false;
        }

        if(isBungee) {
            try {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ObjectOutputStream oos = new ObjectOutputStream(baos);

                oos.writeUTF("heartbeat");
                oos.writeUTF("reloadChannels");
                oos.close();

                byte[] array = baos.toByteArray();

                sendData(array, atlasOut);
            } catch(IOException e) {
                e.printStackTrace();
            }
        }

        if(isBungee)
        //If no players are online, we'll have to wait to send things
        serverCheckTask = RunUtils.taskTimerAsync(() -> {
            if(receivedHeartbeat) {
                receivedHeartbeat = false;
                try {

                    ByteArrayOutputStream baos = new ByteArrayOutputStream();
                    ObjectOutputStream oos = new ObjectOutputStream(baos);

                    oos.writeUTF("heartbeat");
                    oos.writeUTF("reloadChannels");
                    oos.close();

                    byte[] array = baos.toByteArray();

                    sendData(array, atlasOut);
                } catch(IOException e) {
                    e.printStackTrace();
                }

                //If we didn't receive a heartbeat in 8 seconds, we can reasonably assume we don't have atlasbungee.
                if(System.currentTimeMillis() - lastHeartbeatReceive > 8000L) {
                    atlasBungeeInstalled = false;
                    Bukkit.getLogger().log(Level.WARNING, "If you are running a BungeeCord server, it is"
                            + " recommended you also install AtlasBungee for certain features to work. Download it"
                            + " from: https://github.com/funkemunky/Atlas/releases");
                }
            }
            if(toSend.size() > 0 && Bukkit.getOnlinePlayers().size() > 0) {
                Tuple<String, byte[]> next = null;
                synchronized (toSend) {
                    while((next = toSend.poll()) != null) {
                        sendData(next.two, next.one);
                    }
                }
            }
        }, 60, 40);

        //Listening to payloads for use of Bungee APIs
        Atlas.getInstance().getPacketProcessor().process(Atlas.getInstance(), event -> {
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
                            Atlas.getInstance().alog(true,"&7Grabbed servers.");
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
                        case "heartbeat": {
                            atlasBungeeInstalled = true;
                            lastHeartbeatReceive = System.currentTimeMillis();
                            receivedHeartbeat = true;
                            break;
                        }
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
                            String brand = input.readUTF();
                            boolean legacy = input.readBoolean();

                            if(success) versionsMap.put(uuid, new Version(version, brand, legacy));
                            break;
                        }
                    }
                } catch (IOException | ClassNotFoundException e) {
                    e.printStackTrace();
                }
            }
            return;
        }, Packet.Client.CUSTOM_PAYLOAD);
    }

    public void sendData(byte[] data, String out) {
        boolean sent = false;
        for (Player onlinePlayer : Bukkit.getOnlinePlayers()) {
            onlinePlayer.sendPluginMessage(Atlas.getInstance(), out, data);
            sent = true;
            break;
        }

        if(!sent)
        toSend.add(new Tuple<>(out, data));
    }

    public void sendData(byte[] data) {
        sendData(data, channelOut);
    }

    public void sendObjects(String server, Object... objects) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ObjectOutputStream stream = new ObjectOutputStream(output);

        boolean override  = server.toLowerCase().contains("override");
        if(override) {
            stream.writeUTF("sendObjects");
            stream.writeObject(server);
        } else {
            stream.writeUTF("Forward");
            stream.writeUTF(server);
            stream.writeUTF("Forward");
        }

        ByteArrayOutputStream objectOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(objectOutput);

        objectStream.writeShort(objects.length);

        for (Object object : objects) objectStream.writeObject(object);

        objectStream.close();

        objectOutput.close();
        byte[] array = objectOutput.toByteArray();

        stream.writeShort(array.length);
        stream.write(array);

        stream.close();

        sendData(output.toByteArray(), override ? atlasOut : channelOut); //This is where we finally send the data
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

    }
}
