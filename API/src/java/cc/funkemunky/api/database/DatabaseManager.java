package cc.funkemunky.api.database;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.database.bungee.BungeeDatabase;
import cc.funkemunky.api.database.flatfile.FlatfileDatabase;
import cc.funkemunky.api.database.mongo.MongoDatabase;
import cc.funkemunky.api.database.sql.MySQLDatabase;
import lombok.Getter;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class DatabaseManager {
    private Map<String, Database> databases = new ConcurrentHashMap<>();

    public void createDatabase(String name, DatabaseType type) {
        Database database;
        switch(type) {
            case FLATFILE:
                database = new FlatfileDatabase(name, Atlas.getInstance());
                break;
            case MONGO:
                database = new MongoDatabase(name, Atlas.getInstance());
                break;
            case SQL:
                database = new MySQLDatabase(name, Atlas.getInstance());
                break;
            case BUNGEE:
                database = new BungeeDatabase(name, Atlas.getInstance());
                break;
            default:
                database = new FlatfileDatabase(name, Atlas.getInstance());
                break;
        }

        databases.put(name, database);
    }

    public Database getDatabase(String name) {
        return databases.get(name);
    }

    public boolean isDatabase(String name) {
        return databases.containsKey(name);
    }
}
