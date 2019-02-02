package cc.funkemunky.atlas.database;

import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
public abstract class Database {
    private String name;
    private Plugin plugin;
    private boolean bungee;
    private DatabaseType type;
    private Map<String, Object> databaseValues;

    public Database(String name, Plugin plugin, DatabaseType type) {
        this.name = name;
        this.plugin = plugin;
        this.type = type;
        bungee = false;

        databaseValues = new ConcurrentHashMap<>();
    }

    public Database(String name, Plugin plugin, boolean bungee, DatabaseType type) {
        this.name = name;
        this.plugin = plugin;
        this.bungee = bungee;
        this.type = type;
    }

    public abstract void loadDatabase();

    public abstract void saveDatabase();

    public abstract void inputField(String string, Object object);

    public abstract Object getField(String key);
}
