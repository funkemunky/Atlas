package cc.funkemunky.api;

import cc.funkemunky.api.commands.FunkeCommandManager;
import cc.funkemunky.api.commands.ancmd.CommandManager;
import cc.funkemunky.api.commands.impl.AtlasCommand;
import cc.funkemunky.api.database.DatabaseManager;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.EventManager;
import cc.funkemunky.api.metrics.Metrics;
import cc.funkemunky.api.mongo.Mongo;
import cc.funkemunky.api.profiling.BaseProfiler;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import cc.funkemunky.api.utils.blockbox.impl.BoundingBoxes;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.InvalidDescriptionException;
import org.bukkit.plugin.InvalidPluginException;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.Arrays;
import java.util.Comparator;
import java.util.concurrent.*;

@Getter
@Setter
@Init
public class Atlas extends JavaPlugin {
    @Getter
    private static Atlas instance;
    private BlockBoxManager blockBoxManager;
    private ExecutorService[] threadPool;
    private ScheduledExecutorService schedular;
    private ConsoleCommandSender consoleSender;
    private CommandManager commandManager;
    private FunkeCommandManager funkeCommandManager;
    private Updater updater;
    private BaseProfiler profile;
    private Metrics metrics;
    private Mongo mongo;
    private DatabaseManager databaseManager;
    private int currentThread = 0;
    private long profileStart;
    private BoundingBoxes boxes;
    private TinyProtocolHandler tinyProtocolHandler;
    private EventManager eventManager;

    @ConfigSetting(path = "updater")
    private boolean autoDownload = false;

    @ConfigSetting(name = "metrics")
    private boolean metricsEnabled = true;

    @ConfigSetting(path = "init", name = "reloadDependingPlugins")
    private boolean enableDependingPlugins = true;

    @ConfigSetting(name = "threadCount")
    private int threadCount = 4;

    public void onEnable() {
        instance = this;
        consoleSender = Bukkit.getConsoleSender();

        MiscUtils.printToConsole(Color.Red + "Loading Atlas...");
        saveDefaultConfig();
        eventManager = new EventManager();

        MiscUtils.printToConsole(Color.Gray + "Starting scanner...");
        initializeScanner(getClass(), this, commandManager);

        threadPool = new ExecutorService[threadCount];
        tinyProtocolHandler = new TinyProtocolHandler();

        profileStart = System.currentTimeMillis();
        profile = new BaseProfiler();

        MiscUtils.printToConsole(Color.Gray + "Firing up the thread turbines...");
        for (int i = 0; i < threadPool.length; i++) {
            threadPool[i] = Executors.newSingleThreadExecutor();
        }
        schedular = Executors.newSingleThreadScheduledExecutor();

        MiscUtils.printToConsole(Color.Gray + "Loading utilities and managers...");
        blockBoxManager = new BlockBoxManager();
        commandManager = new CommandManager(this);
        funkeCommandManager = new FunkeCommandManager();
        new BlockUtils();
        new ReflectionsUtil();
        new Color();
        new MiscUtils();

        updater = new Updater();

        funkeCommandManager.addCommand(new AtlasCommand());

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "Atlas_Request");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "Atlas_Outgoing");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "Atlas_Incoming");
        }

        MiscUtils.printToConsole(Color.Gray + "Loading other managers and utilities...");
        boxes = new BoundingBoxes();

        mongo = new Mongo();
        databaseManager = new DatabaseManager();

        if(metricsEnabled) {
            MiscUtils.printToConsole(Color.Gray + "Setting up bStats Metrics...");
            metrics = new Metrics(this);
        }

        MiscUtils.printToConsole(Color.Gray + "Checking for updates...");
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
        MiscUtils.printToConsole(Color.Gray + "Unloading all Atlas hooks...");
        cc.funkemunky.api.event.system.EventManager.clearRegistered();
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        MiscUtils.printToConsole(Color.Gray + "Disabling all plugins that depend on Atlas to prevent any errors...");
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(plugin -> plugin.getDescription().getDepend().contains("Atlas")).forEach(plugin -> {
            MiscUtils.unloadPlugin(plugin.getName());
        });
        shutDownAllThreads();
        schedular.shutdownNow();
        MiscUtils.printToConsole(Color.Red + "Completed shutdown process.");
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

    public void shutDownAllThreads() {
        for (ExecutorService executorService : threadPool) {
            executorService.shutdownNow();
        }
    }

    public ExecutorService getThreadPool() {
        ExecutorService service = threadPool[currentThread];

        currentThread = currentThread >= threadPool.length - 1 ? 0 : currentThread + 1;

        return service;
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
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + "(deprecated) Atlas listener. Registering...");
                        cc.funkemunky.api.event.system.EventManager.register(plugin, (cc.funkemunky.api.event.system.Listener) obj);
                    } else if(obj instanceof AtlasListener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + "Atlas listener. Registering...");
                        eventManager.registerListeners((AtlasListener) obj, plugin);
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
