package cc.funkemunky.atlas.database;

import cc.funkemunky.atlas.AtlasBungee;
import cc.funkemunky.atlas.database.flatfile.FlatfileDatabase;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DatabaseManager {
    private Map<String, Database> databases = new ConcurrentHashMap<>();
    private Map<String, FlatfileDatabase> bungeeDatabases = new ConcurrentHashMap<>();

    public void createDatabase(String name, DatabaseType type) {
        Database database;
        switch(type) {
            case FLATFILE:
                database = new FlatfileDatabase(name, AtlasBungee.getInstance());
                break;
            default:
                database = new FlatfileDatabase(name, AtlasBungee.getInstance());
                break;
        }

        databases.put(name, database);
    }

    public void createBungeeDatabase(String name) {
        FlatfileDatabase database = new FlatfileDatabase(name, true, AtlasBungee.getInstance());

        bungeeDatabases.put(name, database);
    }

    public Database getDatabase(String name) {
        return databases.get(name);
    }

    public boolean isDatabase(String name) {
        return databases.containsKey(name);
    }

    public FlatfileDatabase getBungeeDatabase(String name) {
        return bungeeDatabases.get(name);
    }

    public boolean isBungeeDatabase(String name) {
        return bungeeDatabases.containsKey(name);
    }
}
