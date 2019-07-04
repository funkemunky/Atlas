package cc.funkemunky.api;

import cc.funkemunky.api.bungee.BungeeManager;
import cc.funkemunky.api.commands.FunkeCommandManager;
import cc.funkemunky.api.commands.ancmd.CommandManager;
import cc.funkemunky.api.commands.impl.AtlasCommand;
import cc.funkemunky.api.database.DatabaseManager;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.EventManager;
import cc.funkemunky.api.events.impl.TickEvent;
import cc.funkemunky.api.handlers.PluginLoaderHandler;
import cc.funkemunky.api.metrics.Metrics;
import cc.funkemunky.api.profiling.BaseProfiler;
import cc.funkemunky.api.settings.MongoSettings;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.api.packets.ChannelInjector;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import cc.funkemunky.api.utils.blockbox.impl.BoundingBoxes;
import cc.funkemunky.carbon.Carbon;
import lombok.Getter;
import lombok.Setter;
import one.util.streamex.StreamEx;
import org.bukkit.Bukkit;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.craftbukkit.libs.jline.internal.Nullable;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
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
    private Carbon carbon;
    private TinyProtocolHandler tinyProtocolHandler;
    private int currentThread = 0;
    private long profileStart;
    private BoundingBoxes boxes;
    private EventManager eventManager;
    private int currentTicks;
    private PluginLoaderHandler pluginLoaderHandler;
    private BungeeManager bungeeManager;
    private boolean done;
    private DatabaseManager databaseManager;
    private ChannelInjector channelInjector;
    private ExecutorService service;

    @ConfigSetting(path = "updater")
    private boolean autoDownload = false;

    @ConfigSetting(name = "metrics")
    private boolean metricsEnabled = true;

    @ConfigSetting(path = "init", name = "reloadDependingPlugins")
    private boolean enableDependingPlugins = true;

    @ConfigSetting(path = "ticking", name = "runAsync")
    private boolean runAsync = false;

    public void onEnable() {
        instance = this;
        consoleSender = Bukkit.getConsoleSender();

        MiscUtils.printToConsole(Color.Red + "Loading Atlas...");
        saveDefaultConfig();

        MiscUtils.printToConsole(Color.Gray + "Firing up the thread turbines...");
        service = Executors.newSingleThreadScheduledExecutor();

        eventManager = new EventManager();
        carbon = new Carbon();

        MiscUtils.printToConsole(Color.Gray + "Starting scanner...");;

        initializeScanner(getClass(), this, true, true);

        pluginLoaderHandler = new PluginLoaderHandler();
        tinyProtocolHandler =  new TinyProtocolHandler();
        databaseManager = new DatabaseManager();

        profileStart = System.currentTimeMillis();
        profile = new BaseProfiler();

        MiscUtils.printToConsole(Color.Gray + "Loading utilities and managers...");
        blockBoxManager = new BlockBoxManager();
        commandManager = new CommandManager(this);
        funkeCommandManager = new FunkeCommandManager();
        new BlockUtils();
        new ReflectionsUtil();
        new Color();
        new MiscUtils();

        updater = new Updater();
        //channelInjector = new ChannelInjector();

        runTasks();

        funkeCommandManager.addCommand(this, new AtlasCommand());

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            getServer().getMessenger().registerOutgoingPluginChannel(this, "Atlas_Request");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "Atlas_Outgoing");
            getServer().getMessenger().registerOutgoingPluginChannel(this, "Atlas_Incoming");
        }

        MiscUtils.printToConsole(Color.Gray + "Loading other managers and utilities...");
        boxes = new BoundingBoxes();

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

        bungeeManager = new BungeeManager();

        //Bukkit.getOnlinePlayers().forEach(player -> channelInjector.addChannel(player));

        MiscUtils.printToConsole(Color.Green + "Successfully loaded Atlas and its utilities!");
        done = true;
    }

    public void onDisable() {
        MiscUtils.printToConsole(Color.Gray + "Unloading all Atlas hooks...");
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        //Bukkit.getOnlinePlayers().forEach(player -> channelInjector.removeChannel(player));

        eventManager.clearAllRegistered();
        getCommandManager().unregisterCommands();

        MiscUtils.printToConsole(Color.Gray + "Disabling all plugins that depend on Atlas to prevent any errors...");
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(plugin -> plugin.getDescription().getDepend().contains("Atlas")).forEach(plugin -> {
            MiscUtils.unloadPlugin(plugin.getName());
        });
        shutdownExecutor();
        schedular.shutdownNow();

        MiscUtils.printToConsole(Color.Red + "Completed shutdown process.");
    }

    private void initCarbon() {
        carbon = new Carbon();

        if(MongoSettings.enabled) {
            carbon.initMongo(MongoSettings.ip, MongoSettings.port, MongoSettings.database, MongoSettings.username, MongoSettings.password);
        }
    }

    public <T extends Object> T executeTask(FutureTask<T> future) {
        service.submit(future);
        try {
            return future.get();
        } catch (Exception ex) {
            ex.getCause().printStackTrace();
        }
        return null;
    }

    public void executeTask(Runnable runnable) {
        service.execute(runnable);
    }

    public void shutdownExecutor() {
        service.shutdownNow();
    }

    private void runTasks() {
        //This allows us to use ticks for intervalTime comparisons to allow for more parallel calculations to actual Minecraft
        //and it also has the added benefit of being lighter than using System.currentTimeMillis.
        //WARNING: This may be a bit buggy with "legacy" versions of PaperSpigot since they broke the runnable.
        //If you are using PaperSpigot,
        if(!runAsync) {
            new BukkitRunnable() {
                public void run() {
                    runTickEvent();
                }
            }.runTaskTimer(this, 1L, 1L);
        } else {
            getSchedular().scheduleAtFixedRate(this::runTickEvent, 50L, 50L, TimeUnit.MILLISECONDS);
        }
    }

    private void runTickEvent() {
        TickEvent tickEvent = new TickEvent(currentTicks++);

        Atlas.getInstance().getEventManager().callEvent(tickEvent);
    }

    public void initializeScanner(Class<?> mainClass, JavaPlugin plugin, CommandManager manager, boolean loadListeners, boolean loadCommands, @Nullable FutureTask<?>... otherThingsToLoad) {
        StreamEx.of(ClassScanner.scanFile(null, mainClass)).filter(c -> {
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

                    if (loadListeners && obj instanceof Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " Bukkit listener. Registering...");
                        plugin.getServer().getPluginManager().registerEvents((Listener) obj, plugin);
                    }
                    if(loadListeners && obj instanceof cc.funkemunky.api.event.system.Listener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " (deprecated) Atlas listener. Registering...");
                        cc.funkemunky.api.event.system.EventManager.register(plugin, (cc.funkemunky.api.event.system.Listener) obj);
                    }
                    if(loadListeners && obj instanceof AtlasListener) {
                        MiscUtils.printToConsole("&eFound " + clazz.getSimpleName() + " Atlas listener. Registering...");
                        eventManager.registerListeners((AtlasListener) obj, plugin);
                    }
                    if(loadCommands && obj instanceof CommandExecutor && clazz.isAnnotationPresent(Commands.class)) {
                        Commands commands = (Commands) clazz.getAnnotation(Commands.class);

                        Arrays.stream(commands.commands()).forEach(label -> {
                            if(label.length() > 0) {
                                plugin.getCommand(label).setExecutor((CommandExecutor) obj);
                                MiscUtils.printToConsole("&eRegistered ancmd " + label + " from Command Executor: " + clazz.getSimpleName());
                            }
                        });
                    }

                    if(loadCommands && annotation.commands()) manager.registerCommands(obj);

                    if(otherThingsToLoad != null) Arrays.stream(otherThingsToLoad).forEachOrdered(FutureTask::run);

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

    public void initializeScanner(Class<?> mainClass, JavaPlugin plugin) {
        initializeScanner(mainClass, plugin, getCommandManager(), true, true, null);
    }

    public void initializeScanner(Class<?> mainClass, JavaPlugin plugin, CommandManager manager) {
        initializeScanner(mainClass, plugin, manager, true, true, null);
    }

    public void initializeScanner(Class<?> mainClass, JavaPlugin plugin, boolean loadListeners, boolean loadCommands) {
        initializeScanner(mainClass, plugin, getCommandManager(), loadListeners, loadCommands, null);
    }

    public void initializeScanner(Class<?> mainClass, JavaPlugin plugin, boolean loadListeners, boolean loadCommands, @Nullable FutureTask<?>... otherThingsToLoad) {
        initializeScanner(mainClass, plugin, getCommandManager(), loadListeners, loadCommands, otherThingsToLoad);
    }
}
