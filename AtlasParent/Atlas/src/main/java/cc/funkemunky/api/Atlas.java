package cc.funkemunky.api;

import cc.funkemunky.api.bungee.BungeeManager;
import cc.funkemunky.api.commands.FunkeCommandManager;
import cc.funkemunky.api.commands.ancmd.CommandManager;
import cc.funkemunky.api.commands.impl.AtlasCommand;
import cc.funkemunky.api.config.system.Configuration;
import cc.funkemunky.api.config.system.ConfigurationProvider;
import cc.funkemunky.api.config.system.YamlConfiguration;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.EventManager;
import cc.funkemunky.api.events.impl.TickEvent;
import cc.funkemunky.api.handlers.PluginLoaderHandler;
import cc.funkemunky.api.metrics.Metrics;
import cc.funkemunky.api.profiling.BaseProfiler;
import cc.funkemunky.api.settings.MongoSettings;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import cc.funkemunky.api.utils.blockbox.impl.BoundingBoxes;
import cc.funkemunky.carbon.Carbon;
import lombok.Getter;
import lombok.Setter;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import org.yaml.snakeyaml.Yaml;

import javax.annotation.Nullable;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

@Getter
@Setter
@Init
public class Atlas extends JavaPlugin {
    @Getter
    private static Atlas instance;
    private BlockBoxManager blockBoxManager;
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
    private ExecutorService service;
    private Configuration atlasConfig;
    private File file;
    private Yaml yaml;

    @ConfigSetting(path = "updater", name = "autoDownload")
    private static boolean autoDownload = false;

    @ConfigSetting(name = "metrics")
    private static boolean metricsEnabled = true;

    @ConfigSetting(path = "init", name = "reloadDependingPlugins")
    private static boolean enableDependingPlugins = true;

    @ConfigSetting(path = "ticking", name = "runAsync")
    private static boolean runAsync = false;

    public void onEnable() {
        instance = this;
        yaml = new Yaml();
        file = new File(getDataFolder(), "config.yml");
        atlasConfig = YamlConfiguration.saveDefaultConfig(this, "config.yml");
        consoleSender = Bukkit.getConsoleSender();

        MiscUtils.printToConsole(Color.Red + "Loading Atlas...");

        MiscUtils.printToConsole(Color.Gray + "Firing up the thread turbines...");
        service = Executors.newFixedThreadPool(2);
        schedular = Executors.newSingleThreadScheduledExecutor();
        eventManager = new EventManager();
        carbon = new Carbon();

        pluginLoaderHandler = new PluginLoaderHandler();
        tinyProtocolHandler =  new TinyProtocolHandler();

        profileStart = System.currentTimeMillis();
        profile = new BaseProfiler();

        MiscUtils.printToConsole(Color.Gray + "Loading utilities and managers...");
        blockBoxManager = new BlockBoxManager();
        commandManager = new CommandManager(this);
        funkeCommandManager = new FunkeCommandManager();

        updater = new Updater();

        runTasks();
        initCarbon();

        MiscUtils.printToConsole(Color.Gray + "Starting scanner...");

        initializeScanner(getClass(),
                this,
                true,
                true,
                atlasConfig);

        funkeCommandManager.addCommand(this, new AtlasCommand());

        bungeeManager = new BungeeManager();

        MiscUtils.printToConsole(Color.Gray + "Loading other managers and utilities...");
        boxes = new BoundingBoxes();

        if(metricsEnabled) {
            MiscUtils.printToConsole(Color.Gray + "Setting up bStats Metrics...");
            metrics = new Metrics(this);
        }

        MiscUtils.printToConsole(Color.Gray + "Checking for updates...");
        if(updater.needsToUpdate()) {
            MiscUtils.printToConsole(Color.Yellow
                    + "There is an update available. See more information with /atlas update.");

            if(autoDownload) {
                MiscUtils.printToConsole(Color.Gray + "Downloading new version...");
                updater.downloadNewVersion();
                MiscUtils.printToConsole(Color.Green + "Atlas v" + updater.getVersion()
                        + " has been downloaded. Please restart/reload your server to import it.");
            }
        }

        Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().addChannel(player));

        MiscUtils.printToConsole(Color.Green + "Successfully loaded Atlas and its utilities!");
        done = true;
    }

    public void onDisable() {
        MiscUtils.printToConsole(Color.Gray + "Unloading all Atlas hooks...");
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().removeChannel(player));

        eventManager.clearAllRegistered();
        getCommandManager().unregisterCommands();

        MiscUtils.printToConsole(Color.Gray
                + "Disabling all plugins that depend on Atlas to prevent any errors...");
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getDescription().getDepend().contains("Atlas"))
                .forEach(plugin -> MiscUtils.unloadPlugin(plugin.getName()));
        shutdownExecutor();
        schedular.shutdown();


        MiscUtils.printToConsole(Color.Red + "Completed shutdown process.");
    }

    private void initCarbon() {
        carbon = new Carbon();

        if(MongoSettings.enabled) {
            carbon.initMongo(MongoSettings.database,
                    MongoSettings.ip,
                    MongoSettings.port,
                    MongoSettings.username,
                    MongoSettings.password);
        }
    }

    private void shutdownExecutor() {
        service.shutdown();
    }

    private void runTasks() {
        //This allows us to use ticks for intervalTime comparisons to allow for more parallel calculations to actual
        //Minecraft and it also has the added benefit of being lighter than using System.currentTimeMillis.
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
        service.execute(() -> {
            TickEvent tickEvent = new TickEvent(currentTicks++);
            Atlas.getInstance().getEventManager().callEvent(tickEvent);
        });
    }

    public void initializeScanner(Class<? extends JavaPlugin> mainClass, JavaPlugin plugin,
                                  boolean loadListeners,
                                  boolean loadCommands, @Nullable Configuration mainConfig) {
        ClassScanner.scanFile(null, mainClass)
                .stream()
                .map(Reflections::getClass)
                .sorted(Comparator.comparing(c ->
                        c.getAnnotation(Init.class).priority().getPriority(), Comparator.reverseOrder()))
                .forEach(c -> {
                    Object obj = c.getParent().equals(mainClass) ? plugin : c.getConstructor().newInstance();
                    Init annotation = c.getAnnotation(Init.class);

                    if(loadListeners) {
                        if(obj instanceof AtlasListener) {
                            Atlas.getInstance().getEventManager().registerListeners((AtlasListener)obj, plugin);
                            MiscUtils.printToConsole("&7Registered Atlas listener &e" + c.getParent().getSimpleName() + "&7.");
                        }
                        if(obj instanceof Listener) {
                            Bukkit.getPluginManager().registerEvents((Listener)obj, plugin);
                            MiscUtils.printToConsole("&7Registered Atlas listener &e" + c.getParent().getSimpleName() + "&7.");
                        }
                    }

                    if(loadCommands && annotation.commands()) {
                        MiscUtils.printToConsole("&7Registering commands in class &e" + c.getParent().getSimpleName() + "&7...");
                        Atlas.getInstance().getCommandManager().registerCommands(obj);
                    }
                    
                    if(mainConfig != null) {
                        c.getFields().stream()
                                .filter(field -> field.getField().isAnnotationPresent(ConfigSetting.class))
                                .forEach(field -> {
                                    ConfigSetting setting = field.getAnnotation(ConfigSetting.class);

                                    MiscUtils.printToConsole("&7Found ConfigSetting &e" + field.getField().getName()
                                            + " &7(default=&f" + field.get(obj) + "&7.");

                                    String name = setting.name().length() > 0
                                            ? setting.name()
                                            : field.getField().getName();

                                    if(mainConfig.get(setting.path() + "." + name) == null) {
                                        MiscUtils.printToConsole("&7Value not set in config! Setting value...");
                                        mainConfig.set(setting.path() + "." + name, field.get(obj));
                                    } else {
                                        Object configObj = mainConfig.get(setting.path() + "." + name);
                                        MiscUtils.printToConsole("&7Set field to value &e" + configObj + "&7.");
                                        field.set(null, configObj);
                                    }
                                });
                    } else {
                        c.getFields().stream()
                                .filter(field -> field.getField().isAnnotationPresent(ConfigSetting.class))
                                .forEach(field -> {
                                    ConfigSetting setting = field.getAnnotation(ConfigSetting.class);

                                    String name = setting.name().length() > 0
                                            ? setting.name()
                                            : field.getField().getName();

                                    MiscUtils.printToConsole("&7Found ConfigSetting &e" + field.getField().getName()
                                            + " &7(default=&f" + field.get(obj) + "&7.");

                                    if(plugin.getConfig().get(setting.path() + "." + name) == null) {
                                        MiscUtils.printToConsole("&7Value not set in config! Setting value...");
                                        plugin.getConfig().set(setting.path() + "." + name, field.get(obj));
                                    } else {
                                        Object configObj = plugin.getConfig().get(setting.path() + "." + name);
                                        MiscUtils.printToConsole("&7Set field to value &e" + configObj + "&7.");
                                        field.set(obj, configObj);
                                    }
                                });
                    }
                });

        if(mainConfig != null) {
            try {
                ConfigurationProvider.getProvider(YamlConfiguration.class)
                        .save(mainConfig, new File(plugin.getDataFolder(), "config.yml"));
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public void saveConfig() {
        try {
            ConfigurationProvider.getProvider(YamlConfiguration.class).save(atlasConfig, file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void reloadConfig() {
        try {
            if(!file.exists()) {
                YamlConfiguration.saveDefaultConfig(this, "config.yml");
            }
            ConfigurationProvider.getProvider(YamlConfiguration.class).load(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void initializeScanner(Class<? extends JavaPlugin> mainClass, JavaPlugin plugin) {
        initializeScanner(mainClass, plugin, true, true, null);
    }

    public void initializeScanner(JavaPlugin plugin) {
        initializeScanner(plugin.getClass(), plugin);
    }

    public void initializeScanner(JavaPlugin plugin, boolean loadListeners, boolean loadCommands) {
        initializeScanner(plugin.getClass(), plugin, loadListeners, loadCommands, null);
    }
}
