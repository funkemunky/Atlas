package cc.funkemunky.api.database.sql;

import cc.funkemunky.api.database.Database;
import cc.funkemunky.api.database.DatabaseType;
import cc.funkemunky.api.utils.ConfigSetting;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.MiscUtils;
import org.bukkit.plugin.Plugin;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

@Init
public class MySQLDatabase extends Database {
    private Connection connection;

    @ConfigSetting(path = "database.mysql", name = "ip")
    private String ip = "localhost";

    @ConfigSetting(path = "database.mysql", name = "username")
    private String username = "root";

    @ConfigSetting(path = "database.mysql", name = "password")
    private String password = "password";

    private String database;

    public MySQLDatabase(String name, Plugin plugin) {
        super(name, plugin, DatabaseType.SQL);

        database = name;
        connectIfDisconected();
    }

    @Override
    public void loadDatabase() {

    }

    @Override
    public void saveDatabase() {

    }

    @Override
    public void inputField(String string, Object object) {
        try {
            connectIfDisconected();
            PreparedStatement statement = connection.prepareStatement("insert into data ('key', 'value') \\nVALUES ('" + string + "', '" + object.getClass().getName() + ";" + object.toString() + "')");

            statement.executeUpdate();
            statement.close();

            getDatabaseValues().put(string, object);
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public Object getField(String key) {
        try {
            connectIfDisconected();
            PreparedStatement statement = connection.prepareStatement("select value from data where key='" + key + "'");

            ResultSet set = statement.executeQuery();

            if(set.next()) {
                String value = set.getString("value");

                String[] splitValue = value.split(";");

                Class<?> className = Class.forName(splitValue[0]);
                return MiscUtils.parseObjectFromString(splitValue[1], className);
            }
        } catch(Exception e) {
            e.printStackTrace();;
        }
        return getDatabaseValues().get(key);
    }

    private void connectIfDisconected() {
        try {
            if(connection == null || connection.isClosed()) {
                try {
                    Class.forName("com.mysql.jdbc.Driver");
                    connection = DriverManager.getConnection("jdbc:mysql://" + ip + "/" + database + "?user=" + username + "&password=" + password);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
