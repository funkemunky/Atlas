package cc.funkemunky.api.database.mongo;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.database.Database;
import cc.funkemunky.api.database.DatabaseType;
import com.mongodb.Block;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.model.Filters;
import org.bson.Document;
import org.bukkit.plugin.Plugin;

import java.util.HashMap;
import java.util.Map;

public class MongoDatabase extends Database {
    private MongoCollection<Document> collection;
    public MongoDatabase(String name, Plugin plugin) {
        super(name, plugin, DatabaseType.MONGO);

        collection = Atlas.getInstance().getCarbon().getMongo().getMongoDatabase().getCollection(name);
    }

    @Override
    public void loadDatabase() {
        collection.find().forEach((Block<? super Document>) doc -> {
            doc.keySet().forEach(key -> getDatabaseValues().put(key, doc.get(key)));
        });
    }

    @Override
    public void saveDatabase() {
        Map<String, Map<String, Object>> toSort = new HashMap<>();
        getDatabaseValues().keySet().forEach(key -> {
            String[] toFormat = key.split(";");

            Map<String, Object> objects = toSort.getOrDefault(toFormat[0], new HashMap<>());

            objects.put(toFormat[1], getDatabaseValues().get(key));

            toSort.put(key, objects);
        });

        toSort.keySet().forEach(key -> {
            Map<String, Object> toPutIntoDocument = toSort.get(key);

            Document document = new Document("id", key);

            toPutIntoDocument.keySet().forEach(docKey -> {
                document.put(docKey, toPutIntoDocument.get(docKey));
            });

            collection.findOneAndReplace(Filters.eq("id", key), document);
        });
    }

    /* IMPORTANT: You must put key to the object in a "docKey.valueKey" format, depending on what document you want things to go into */
    @Override
    public void inputField(String string, Object object) {
        getDatabaseValues().put(string, object);
    }

    @Override
    public Object getField(String key) {
        return getDatabaseValues().get(key);
    }
}
