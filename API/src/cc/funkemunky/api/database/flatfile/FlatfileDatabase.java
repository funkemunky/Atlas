package cc.funkemunky.api.database.flatfile;

import cc.funkemunky.api.database.Database;
import cc.funkemunky.api.utils.FunkeFile;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import javax.persistence.SecondaryTable;

@Getter
@Setter
public class FlatfileDatabase extends Database {
    private FunkeFile file;
    public FlatfileDatabase(String name, Plugin plugin) {
        super(name, plugin);

        file = new FunkeFile(plugin, "database", name + ".txt");
    }

    @Override
    public void loadDatabase() {
        file.readFile();
        file.getLines().forEach(line -> {
            String[] info =
        });
    }

    @Override
    public void saveDatabase() {

    }

    @Override
    public void inputField(String string, Object object) {
        getDatabaseValues().put(string, object.getClass().getSimpleName() + ";" + object.toString());
    }

    @Override
    public Object getField(String key) {
        return null;
    }
}
