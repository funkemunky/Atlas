package cc.funkemunky.atlas.database.flatfile;

import cc.funkemunky.atlas.database.Database;
import cc.funkemunky.atlas.database.DatabaseType;
import cc.funkemunky.atlas.utils.FunkeFile;
import cc.funkemunky.atlas.utils.MiscUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.plugin.Plugin;

@Getter
@Setter
public class FlatfileDatabase extends Database {
    private FunkeFile file;
    public FlatfileDatabase(String name, Plugin plugin) {
        super(name, plugin, DatabaseType.FLATFILE);

        file = new FunkeFile(plugin, "databases", name + ".txt");
    }

    public FlatfileDatabase(String name, boolean bungee, Plugin plugin) {
        super(name, plugin, bungee, DatabaseType.FLATFILE);

        file = new FunkeFile(plugin, (bungee ? "bungee-" : "") + "databases", name + ".txt");
    }

    @Override
    public void loadDatabase() {
        file.readFile();
        file.getLines().forEach(line -> {
            String[] info = line.split(":");

            if(info.length >= 3) {
                String key = info[0], valueString = info[2];

                try {
                    Class<?> classObject = Class.forName(info[1]);
                    Object value = classObject.getSimpleName().equals("String") ? info[2] : MiscUtils.parseObjectFromString(valueString, classObject);
                    getDatabaseValues().put(key, value);
                } catch(Exception e) {
                    MiscUtils.printToConsole("&cError parsing " + key + " value from string!");
                }
            }
        });
    }

    @Override
    public void saveDatabase() {
        file.clear();
        getDatabaseValues().keySet().forEach(key -> {
            Object value = getDatabaseValues().get(key);

            file.addLine(key + ":" + value.getClass().getName() + ":" + value.toString());
        });
        file.write();
    }

    @Override
    public void inputField(String string, Object object) {
        getDatabaseValues().put(string, object);
    }

    @Override
    public Object getField(String key) {
        return getDatabaseValues().get(key);
    }
}
