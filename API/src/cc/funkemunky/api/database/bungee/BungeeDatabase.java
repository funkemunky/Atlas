package cc.funkemunky.api.database.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.database.Database;
import cc.funkemunky.api.database.DatabaseType;
import cc.funkemunky.api.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.messaging.PluginMessageListener;

import java.io.*;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;

public class BungeeDatabase extends Database implements PluginMessageListener {
    private Set<String> requests = new HashSet<>();
    public BungeeDatabase(String name, Plugin plugin, DatabaseType type) {
        super(name, plugin, type);

        Atlas.getInstance().getServer().getMessenger().registerIncomingPluginChannel(Atlas.getInstance(), "Atlas_Data_Incoming", this);
    }

    @Override
    public void loadDatabase() {
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(bStream);

            outputStream.writeInt(Bukkit.getPort());
            outputStream.writeUTF(getName());
            outputStream.writeUTF("*");

            Atlas.getInstance().getServer().sendPluginMessage(Atlas.getInstance(), "Atlas_Data_Request", bStream.toByteArray());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveDatabase() {
        //Empty as this is unnecessary.
    }

    @Override
    public void inputField(String key, Object object) {
        try {
            ByteArrayOutputStream bStream = new ByteArrayOutputStream();
            DataOutputStream outputStream = new DataOutputStream(bStream);

            outputStream.writeUTF(getName());
            outputStream.writeUTF(key);
            outputStream.writeUTF(object.getClass().getName());
            outputStream.writeUTF(object.toString());

            Atlas.getInstance().getServer().sendPluginMessage(Atlas.getInstance(), "Atlas_Data_Outgoing", bStream.toByteArray());
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getField(String key) {
        requests.add(key);

        try {
            long tick = 0;
            Atlas.getInstance().getSchedular().schedule(() -> requests.remove(key), 10, TimeUnit.SECONDS);
            while(requests.contains(key)) {

            }
        } catch(Exception e) {
            e.printStackTrace();
        } finally {
            return getDatabaseValues().get(key);
        }
    }

    @Override
    public void onPluginMessageReceived(String s, Player player, byte[] bytes) {

        try {
            ByteArrayInputStream bStream = new ByteArrayInputStream(bytes);
            DataInputStream inputStream = new DataInputStream(bStream);
            String db = inputStream.readUTF();

            if(db.equals(getName())) {
                String key = inputStream.readUTF();
                switch(key) {
                    case "*": {
                        while(inputStream.read(bytes) < bytes.length) {
                            String[] line = inputStream.readUTF().split(";");

                            if(line.length > 2) {
                                String keyLoop = line[0], className = line[1], objectString = line[2];

                                getDatabaseValues().put(keyLoop, MiscUtils.parseObjectFromString(objectString, Class.forName(className)));
                            }
                        }
                        break;
                    }
                    case "Error": break;
                    default: {
                        String className = inputStream.readUTF();
                        String objectString = inputStream.readUTF();
                        Object object = MiscUtils.parseObjectFromString(objectString, Class.forName(className));

                        getDatabaseValues().put(key, object);
                        requests.remove(key);
                        break;
                    }

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
