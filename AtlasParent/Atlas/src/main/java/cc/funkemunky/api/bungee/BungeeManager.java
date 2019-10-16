package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.events.BungeeReceiveEvent;
import cc.funkemunky.api.bungee.objects.BungeePlayer;
import cc.funkemunky.api.handlers.ForgeHandler;
import com.google.common.io.ByteArrayDataOutput;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.libs.org.ibex.nestedvm.util.Seekable;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.*;

@Getter
public class BungeeManager implements PluginMessageListener {
    private String channelOut = "BungeeCord", channelIn = "BungeeCord";
    private String atlasIn = "atlasIn";
    private BungeeAPI bungeeAPI;
    private Map<UUID, BungeePlayer> bungeePlayers = new HashMap<>();

    public BungeeManager() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), channelOut);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), channelIn, this);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), atlasIn, this);
        bungeeAPI = new BungeeAPI();
    }

    public void sendData(byte[] data) {
        Bukkit.getServer().sendPluginMessage(Atlas.getInstance(), channelOut, data);
    }

    public void sendObjects(String server, Object... objects) throws IOException {
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        DataOutputStream stream = new DataOutputStream(output);

        stream.writeUTF("Forward");
        stream.writeUTF(server);
        stream.writeUTF("Forward");

        ByteArrayOutputStream objectOutput = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(objectOutput);

        objectStream.writeShort(objects.length);

        for (Object object : objects) objectStream.writeObject(object);

        byte[] array = objectOutput.toByteArray();

        stream.writeShort(array.length);
        stream.write(array);

        sendData(output.toByteArray()); //This is where we finally send the data
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

    /*public void requestObject(RequestType type, String... args) {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        DataOutputStream dStream = new DataOutputStream(stream);

        try {
            dStream.writeUTF("request");
            dStream.writeUTF(type.getTypeName());
            Arrays.stream(args).forEachOrdered(arg -> {
                try {
                    dStream.writeUTF(arg);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            });
        } catch (IOException e) {
            e.printStackTrace();
        }

        sendData(stream.toByteArray());
    }

    //Unfinished
    public List<BungeeObject> getObject(String id) throws InterruptedException, ExecutionException, TimeoutException {
        return getObjectWithCriteria(Criteria.EQUALS, id);
    }

    //Unfinished
    public List<BungeeObject> getObjectWithCriteria(Criteria criteria, String toCompare) throws InterruptedException, ExecutionException, TimeoutException {
        return getObjectWithCriteria(criteria, toCompare, 5000L);
    }

    //Unfinished
    public List<BungeeObject> getObjectWithCriteria(Criteria criteria, String toCompare, long timeoutMillis) throws InterruptedException, ExecutionException, TimeoutException {
        FutureTask<List<BungeeObject>> task = new FutureTask<>(() -> {
            var object = objects.stream().filter(obj -> {
                switch(criteria) {
                    case EQUALS: return obj.getId().equals(toCompare);
                    case EQUALS_IGNORE_CASE: return obj.getId().equalsIgnoreCase(toCompare);
                    case CONTAINS: return obj.getId().contains(toCompare);
                    case CONTAINS_IGNORE_CASE: return obj.getId().toLowerCase().contains(toCompare.toLowerCase());
                    case STARTS_WITH: return obj.getId().startsWith(toCompare);
                    case ENDS_WITH: return obj.getId().endsWith(toCompare);
                    default: return false;
                }
            }).collect(Collectors.toList());

            if(object.size() == 0) {
                requestObject(RequestType.OBJECT, toCompare);
            }

            return object;
        });

        executor.submit(task);

        return task.get(timeoutMillis, TimeUnit.MILLISECONDS);
    }*/
}
