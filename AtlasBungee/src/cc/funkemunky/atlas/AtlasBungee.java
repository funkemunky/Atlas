package cc.funkemunky.atlas;

import cc.funkemunky.atlas.configuration.Config;
import cc.funkemunky.atlas.database.DatabaseManager;
import cc.funkemunky.atlas.utils.ClassScanner;
import cc.funkemunky.atlas.configuration.ConfigSetting;
import cc.funkemunky.atlas.utils.Init;
import cc.funkemunky.atlas.utils.MiscUtils;
import lombok.Getter;
import net.md_5.bungee.BungeeCord;
import net.md_5.bungee.api.plugin.Command;
import net.md_5.bungee.api.plugin.Listener;
import net.md_5.bungee.api.plugin.Plugin;
import net.md_5.bungee.command.ConsoleCommandSender;

import java.util.Arrays;
import java.util.Comparator;

@Getter
public class AtlasBungee extends Plugin {

    @Getter
    private static AtlasBungee instance;
    private ConsoleCommandSender consoleSender;
    private DatabaseManager databaseManager;
    private Config config;
    public void onEnable() {
        instance = this;
        config = new Config(this);
        databaseManager = new DatabaseManager();

        getProxy().registerChannel("Atlas_Data_Outgoing");
        getProxy().registerChannel("Atlas_Data_Request");
        getProxy().registerChannel("Atlas_Data_Incoming");
        consoleSender = getInstance().getConsoleSender();

        initializeScanner(getClass(), config, this);
    }

    public void initializeScanner(Class<?> mainClass, Config config, Plugin plugin) {
        ClassScanner.scanFile(null, mainClass).stream().filter(c -> {
            try {
                Class clazz = Class.forName(c);

                return clazz.isAnnotationPresent(Init.class);
            } catch(Exception e) {
                e.printStackTrace();
            }
            return false;
        }).sorted(Comparator.comparingInt(c -> {
            try {
                Class clazz = Class.forName(c);

                Init annotation = (Init) clazz.getAnnotation(Init.class);

                return annotation.priority().getPriority();
            } catch(Exception e) {
                e.printStackTrace();
            }
            return 3;
        })).forEachOrdered(c -> {
            try {
                Class clazz = Class.forName(c);

                if(clazz.isAnnotationPresent(Init.class)) {
                    Object obj = clazz.equals(mainClass) ? plugin : clazz.newInstance();
                    Init annotation = (Init) clazz.getAnnotation(Init.class);

                    if (obj instanceof Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " Bukkit listener. Registering...");
                        getProxy().getPluginManager().registerListener(plugin, (Listener) obj);
                    } else if(obj instanceof Command) {
                        BungeeCord.getInstance().getPluginManager().registerCommand(plugin, (Command) obj);
                    }

                    Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(ConfigSetting.class)).forEach(field -> {
                        String name = field.getAnnotation(ConfigSetting.class).name();
                        String path = field.getAnnotation(ConfigSetting.class).path() + "." + (name.length() > 0 ? name : field.getName());
                        try {
                            field.setAccessible(true);
                            MiscUtils.printToConsole("&eFound " + field.getName() + " ConfigSetting (default=" + field.get(obj) + ").");
                            if(config.getConfiguration().get(path) == null) {
                                MiscUtils.printToConsole("&eValue not found in configuration! Setting default into config...");
                                config.getConfiguration().set(path, field.get(obj));
                                config.save();
                            } else {
                                field.set(obj, config.getConfiguration().get(path));

                                MiscUtils.printToConsole("&eValue found in configuration! Set value to &a" + config.getConfiguration().get(path));
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });

                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
