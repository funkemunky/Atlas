package cc.funkemunky.api.mongo;


import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import com.mongodb.MongoClient;
import com.mongodb.MongoCredential;
import com.mongodb.client.MongoDatabase;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;

@Getter
@Setter
@Init
public class Mongo {

    private MongoDatabase mongoDatabase;
    private MongoClient client;

    @ConfigSetting(path = "mongo", name = "database")
    private String database = "Atlas";

    @ConfigSetting(path = "mongo", name = "ip")
    private String ip = "127.0.0.1";

    @ConfigSetting(path = "mongo", name = "port")
    private int port = 27017;

    @ConfigSetting(path = "mongo", name = "enabled")
    private boolean enabled = false;

    @ConfigSetting(path = "mongo", name = "username")
    private String username = "username";

    @ConfigSetting(path = "mongo", name = "password")
    private String password = "password";

    @Getter
    private boolean connected = false;

    public Mongo() {
        connect();
    }

    public Mongo(String database) {
        this.database = database;
    }

    public void connect() {
       if(enabled) {
           try {
               this.client = new MongoClient(ip, port);
               if(enabled) {
                   val credential = MongoCredential.createCredential(username, database, password.toCharArray());
                   client.getCredentialsList().add(credential);
               }
           } catch (Exception e) {
               e.printStackTrace();
               Bukkit.getServer().getLogger().severe("Could not connect to the database!");
               this.connected = false;
               return;
           }
           Bukkit.getServer().getConsoleSender().sendMessage(Color.translate("&aConnected to the Mongo database."));
           this.mongoDatabase = client.getDatabase(database);
           this.connected = true;
       }
    }

    public void disconnect() {
        client.close();
    }

}
