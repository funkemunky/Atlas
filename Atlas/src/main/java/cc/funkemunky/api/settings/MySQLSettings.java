package cc.funkemunky.api.settings;

import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;

@Init
public class MySQLSettings {
    @ConfigSetting(path = "database.mysql", name = "name")
    public static String database = "Atlas";

    @ConfigSetting(path = "database.mysql", name = "ip")
    public static String ip = "127.0.0.1";

    @ConfigSetting(path = "database.mysql", name = "port")
    public static int port = 3306;

    @ConfigSetting(path = "database.mysql", name = "username")
    public static String username = "username";

    @ConfigSetting(path = "database.mysql", name = "password")
    public static String password = "password";
}
