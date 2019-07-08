package cc.funkemunky.api.bungee;

import cc.funkemunky.api.Atlas;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.TreeSet;

@Getter
public class BungeeManager implements PluginMessageListener {
    private String channelOut = "atlasdata:out", channelIn = "atlasdata:in";
    private SortedSet<BungeeObject> objects = new TreeSet<>(Comparator.comparing(BungeeObject::getTimeStamp, Comparator.reverseOrder()));
    private BungeeAPI bungeeAPI;

    public BungeeManager() {
        Bukkit.getMessenger().registerOutgoingPluginChannel(Atlas.getInstance(), channelOut);
        Bukkit.getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), channelIn, this);
        bungeeAPI = new BungeeAPI();
    }

    public void sendData(byte[] data) {
        Bukkit.getServer().sendPluginMessage(Atlas.getInstance(), channelOut, data);
    }

    public void sendObject(Object object) throws IOException {
        ByteArrayOutputStream stream = new ByteArrayOutputStream();
        ObjectOutputStream objectStream = new ObjectOutputStream(stream);

        objectStream.writeUTF("sendObject");
        objectStream.writeObject(object);

        sendData(stream.toByteArray());
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {
        ByteArrayInputStream stream = new ByteArrayInputStream(bytes);

        try {
            ObjectInputStream objectStream = new ObjectInputStream(stream);

            String type = objectStream.readUTF();

            if(type.equals("object")) {
                Object object;
                if((object = objectStream.readObject()) instanceof BungeeObject) {
                    BungeeObject bObject = (BungeeObject) object;

                    objects.add(bObject);
                }
            }
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
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
