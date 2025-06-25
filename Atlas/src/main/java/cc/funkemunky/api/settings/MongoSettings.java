package cc.funkemunky.api.settings;

import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;

@Init
public class MongoSettings {
    @ConfigSetting(path = "database.mongo", name = "enabled")
    public static boolean enabled = false;

    @ConfigSetting(path = "database.mongo", name = "name")
    public static String database = "Atlas";

    @ConfigSetting(path = "database.mongo", name = "ip")
    public static String ip = "127.0.0.1";

    @ConfigSetting(path = "database.mongo", name = "port")
    public static int port = 27017;

    @ConfigSetting(path = "database.mongo", name = "username")
    public static String username = "username";

    @ConfigSetting(path = "database.mongo", name = "password")
    public static String password = "password";
}
