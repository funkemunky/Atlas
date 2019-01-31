package cc.funkemunky.api;

import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.commands.FunkeCommandManager;
import cc.funkemunky.api.commands.ancmd.CommandManager;
import cc.funkemunky.api.commands.impl.AtlasCommand;
import cc.funkemunky.api.database.DatabaseManager;
import cc.funkemunky.api.event.system.EventManager;
import cc.funkemunky.api.metrics.Metrics;
import cc.funkemunky.api.mongo.Mongo;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Getter
@Init
public class Atlas extends JavaPlugin {
    @Getter
    private static Atlas instance;
    private TinyProtocolHandler tinyProtocolHandler;
    private BlockBoxManager blockBoxManager;
    private ExecutorService threadPool;
    private ConsoleCommandSender consoleSender;
    private CommandManager commandManager;
    private FunkeCommandManager funkeCommandManager;
    private Updater updater;
    private Metrics metrics;
    private Mongo mongo;
    private DatabaseManager databaseManager;

    @ConfigSetting(path = "updater")
    private boolean autoDownload = false;

    @ConfigSetting(path = "init")
    private boolean loadDependingPluginsOnStart = true;

    @ConfigSetting(name = "metrics")
    private boolean metricsEnabled = true;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        consoleSender = Bukkit.getConsoleSender();

        MiscUtils.printToConsole(Color.Red + "Loading Atlas...");
        threadPool = Executors.newFixedThreadPool(4);
        tinyProtocolHandler = new TinyProtocolHandler();
        blockBoxManager = new BlockBoxManager();
        commandManager = new CommandManager(this);
        funkeCommandManager = new FunkeCommandManager();
        new BlockUtils();
        new ReflectionsUtil();
        new Color();
        new MiscUtils();

        updater = new Updater();

        funkeCommandManager.addCommand(new AtlasCommand());

        MiscUtils.printToConsole(Color.Gray + "Starting scanner...");
        initializeScanner(getClass(), this, commandManager);

        mongo = new Mongo();
        databaseManager = new DatabaseManager();

        if(metricsEnabled) {
            metrics = new Metrics(this);
        }

        if(updater.needsToUpdate()) {
            MiscUtils.printToConsole(Color.Yellow + "There is an update available. See more information with /atlas update.");

            if(autoDownload) {
                MiscUtils.printToConsole(Color.Gray + "Downloading new version...");
                updater.downloadNewVersion();
                MiscUtils.printToConsole(Color.Green + "Atlas v" + updater.getVersion() + " has been downloaded. Please restart/reload your server to import it.");
            }
        }

        MiscUtils.printToConsole(Color.Green + "Successfully loaded Atlas and its utilities!");
    }

    public void onDisable() {
        EventManager.clearRegistered();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        threadPool.shutdownNow();
    }

    public void executeTask(Runnable runnable) {
        Future future = getThreadPool().submit(runnable);
        try {
            if(future.isDone()) {
                future.get();
            }
        } catch (Exception ex) {
            ex.getCause().printStackTrace();
        }
    }

    public void initializeScanner(Class<?> mainClass, JavaPlugin plugin, CommandManager manager) {
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
                        plugin.getServer().getPluginManager().registerEvents((Listener) obj, plugin);
                    } else if(obj instanceof cc.funkemunky.api.event.system.Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " Atlas listener. Registering...");
                        EventManager.register((cc.funkemunky.api.event.system.Listener) obj);
                    } else if(obj instanceof CommandExecutor && clazz.isAnnotationPresent(Commands.class)) {
                        Commands commands = (Commands) clazz.getAnnotation(Commands.class);

                        Arrays.stream(commands.commands()).forEach(label -> {
                            if(label.length() > 0) {
                                plugin.getCommand(label).setExecutor((CommandExecutor) obj);
                                MiscUtils.printToConsole("&eRegistered ancmd " + label + " from Command Executor: " + clazz.getSimpleName());
                            }
                        });
                    }

                    if(annotation.commands()) manager.registerCommands(obj);

                    Arrays.stream(clazz.getDeclaredFields()).filter(field -> field.isAnnotationPresent(ConfigSetting.class)).forEach(field -> {
                        String name = field.getAnnotation(ConfigSetting.class).name();
                        String path = field.getAnnotation(ConfigSetting.class).path() + "." + (name.length() > 0 ? name : field.getName());
                        try {
                            field.setAccessible(true);
                            MiscUtils.printToConsole("&eFound " + field.getName() + " ConfigSetting (default=" + field.get(obj) + ").");
                            if(plugin.getConfig().get(path) == null) {
                                MiscUtils.printToConsole("&eValue not found in configuration! Setting default into config...");
                                plugin.getConfig().set(path, field.get(obj));
                                plugin.saveConfig();
                            } else {
                                field.set(obj, plugin.getConfig().get(path));

                                MiscUtils.printToConsole("&eValue found in configuration! Set value to &a" + plugin.getConfig().get(path));
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
