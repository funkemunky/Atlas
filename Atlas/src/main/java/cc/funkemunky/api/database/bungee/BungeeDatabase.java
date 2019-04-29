package cc.funkemunky.api.database.bungee;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.bungee.BungeeObject;
import cc.funkemunky.api.bungee.Criteria;
import cc.funkemunky.api.bungee.RequestType;
import cc.funkemunky.api.database.Database;
import cc.funkemunky.api.database.DatabaseType;
import lombok.val;
import org.bukkit.plugin.Plugin;

import java.io.IOException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;

public class BungeeDatabase extends Database {
    public BungeeDatabase(String name, Plugin plugin) {
        super(name, plugin, DatabaseType.BUNGEE);

        //Running the updater.
        Atlas.getInstance().getSchedular().scheduleAtFixedRate(this::loadDatabase, Atlas.getInstance().getDatabaseManager().getBungeeRate(), Atlas.getInstance().getDatabaseManager().getBungeeRate(), TimeUnit.SECONDS);
    }

    @Override
    public void loadDatabase() {
        Atlas.getInstance().getBungeeManager().requestObject(RequestType.DATABASE, getName(), "*");

        try {
            val objects = Atlas.getInstance().getBungeeManager().getObjectWithCriteria(Criteria.STARTS_WITH, "db_" + getName(), 6500L);

            objects.forEach(obj -> getDatabaseValues().put(obj.getId().replace("db_", ""), obj.getObject()));
        } catch (InterruptedException | ExecutionException | TimeoutException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void saveDatabase() {
        getDatabaseValues().keySet().forEach(key -> {
            String id = "db_" + key;

            BungeeObject object = new BungeeObject(id, System.currentTimeMillis(), getDatabaseValues().get(key));

            try {
                Atlas.getInstance().getBungeeManager().sendObject(object);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }

    @Override
    public void inputField(String key, Object object) {
        String id = "db_" + key;
        BungeeObject bObject = new BungeeObject(id, System.currentTimeMillis(), object);

        try {
            Atlas.getInstance().getBungeeManager().sendObject(bObject);
        } catch (IOException e) {
            e.printStackTrace();
        }

        getDatabaseValues().put(key, object);
    }

    @Override
    public Object getField(String key) {
        return getDatabaseValues().getOrDefault(key, null);
    }
}
