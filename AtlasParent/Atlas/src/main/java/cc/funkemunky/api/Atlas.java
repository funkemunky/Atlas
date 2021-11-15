package cc.funkemunky.api;

import cc.funkemunky.api.bungee.BungeeManager;
import cc.funkemunky.api.commands.FunkeCommandManager;
import cc.funkemunky.api.commands.ancmd.CommandManager;
import cc.funkemunky.api.commands.impl.AtlasCommand;
import cc.funkemunky.api.events.AtlasListener;
import cc.funkemunky.api.events.EventManager;
import cc.funkemunky.api.events.impl.TickEvent;
import cc.funkemunky.api.handlers.PluginLoaderHandler;
import cc.funkemunky.api.metrics.Metrics;
import cc.funkemunky.api.profiling.BaseProfiler;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.tinyprotocol.listener.PacketProcessor;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import cc.funkemunky.api.utils.config.Configuration;
import cc.funkemunky.api.utils.config.ConfigurationProvider;
import cc.funkemunky.api.utils.config.YamlConfiguration;
import cc.funkemunky.api.utils.objects.RemoteClassLoader;
import cc.funkemunky.api.utils.world.WorldInfo;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import dev.brighten.db.Carbon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.World;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;

@Getter
@Setter
@Init
public class Atlas extends JavaPlugin {
    @Getter
    private static Atlas instance;
    private BlockBoxManager blockBoxManager;
    private ScheduledExecutorService schedular;
    private ConsoleCommandSender consoleSender;
    private FunkeCommandManager funkeCommandManager;
    private Updater updater;
    private BaseProfiler profile;
    private Metrics metrics;
    private TinyProtocolHandler tinyProtocolHandler;
    private int currentThread = 0;
    private long profileStart;
    private EventManager eventManager;
    private PacketProcessor packetProcessor;
    private int currentTicks;
    private PluginLoaderHandler pluginLoaderHandler;
    private BungeeManager bungeeManager;
    private boolean done;
    private ExecutorService service;
    private File file;
    private final Map<String, Map<String, Configuration>> pluginConfigs = new HashMap<>();
    @Deprecated
    private final Map<UUID, Entity> entities = Collections.synchronizedMap(new HashMap<>());
    @Deprecated
    private final Map<Integer, UUID> entityIds = Collections.synchronizedMap(new HashMap<>());
    private final Map<Tuple<UUID, Integer>, Entity> trackedEntities = new ConcurrentHashMap<>();
    private Map<String, CommandManager> pluginCommandManagers = new HashMap<>();
    private Map<String, BukkitCommandManager> bukkitCommandManagers = new HashMap<>();
    private final Map<UUID, WorldInfo> worldInfoMap = new HashMap<>();

    @ConfigSetting(path = "updater", name = "autoDownload")
    private static boolean autoDownload = false;
    
    @ConfigSetting(path = "logging", name = "verbose")
    private static boolean verboseLogging = true;

    @ConfigSetting(name = "metrics")
    private static boolean metricsEnabled = true;

    @ConfigSetting(path = "init", name = "reloadDependingPlugins")
    private static boolean enableDependingPlugins = true;

    @ConfigSetting(path = "ticking", name = "runAsync")
    private static boolean runAsync = false;

    @ConfigSetting(name = "debug")
    public static boolean debugMode = false;

    public void onEnable() {
        instance = this;

        ConfigurationProvider.providers.size(); //Just to load the static fields in class
        registerConfig(this);
        consoleSender = Bukkit.getConsoleSender();

        alog("&cLoading Atlas...");

        alog(Color.Gray + "Firing up the thread turbines...");
        service = Executors.newFixedThreadPool(3);
        schedular = Executors.newSingleThreadScheduledExecutor();
        eventManager = new EventManager();
        Carbon.setup();

        pluginLoaderHandler = new PluginLoaderHandler();
        tinyProtocolHandler =  new TinyProtocolHandler();

        packetProcessor = new PacketProcessor();

        profileStart = System.currentTimeMillis();
        profile = new BaseProfiler();

        alog(Color.Gray + "Loading utilities and managers...");
        blockBoxManager = new BlockBoxManager();
        funkeCommandManager = new FunkeCommandManager();

        updater = new Updater();

        runTasks();

        alog(Color.Gray + "Starting scanner...");

        initializeScanner(getClass(), this,
                null,
                true,
                true);

        funkeCommandManager.addCommand(this, new AtlasCommand());

        alog(Color.Gray + "Loading other managers and utilities...");

        if(metricsEnabled) {
            alog(Color.Gray + "Setting up bStats Metrics...");
            metrics = new Metrics(this);
        }

        alog(Color.Gray + "Checking for updates...");
        if(updater.needsToUpdate()) {
            alog(Color.Yellow
                    + "There is an update available. See more information with /atlas update.");

            if(autoDownload) {
                alog(Color.Gray + "Downloading new version...");
                updater.downloadNewVersion();
                alog(Color.Green + "Atlas v" + updater.getLatestUpdate()
                        + " has been downloaded. Please restart/reload your server to import it.");
            }
        }

        alog(Color.Green + "Loading WorldInfo system...");
        Bukkit.getWorlds().forEach(w -> worldInfoMap.put(w.getUID(), new WorldInfo(w)));

        bungeeManager = new BungeeManager();

        alog(Color.Green + "Successfully loaded Atlas and its utilities!");
        done = true;
    }

    public void onDisable() {
        alog(Color.Gray + "Unloading all Atlas hooks...");
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);
        packetProcessor.shutdown();

        eventManager.clearAllRegistered();
        eventManager = null;
        getCommandManager(this).unregisterCommands();

        funkeCommandManager = null;
        tinyProtocolHandler.shutdown();
        tinyProtocolHandler = null;

        //unregistering worldinfo
        synchronized (worldInfoMap) {
            worldInfoMap.forEach((key, value) -> value.shutdown());
            worldInfoMap.clear();
        }

        alog(Color.Gray
                + "Disabling all plugins that depend on Atlas to prevent any errors...");
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getDescription().getDepend().contains("Atlas")
                        || plugin.getDescription().getSoftDepend().contains("Atlas"))
                .forEach(plugin -> MiscUtils.unloadPlugin(plugin.getName()));
        shutdownExecutor();

        alog(Color.Red + "Completed shutdown process.");
    }


    private void shutdownExecutor() {
        service.shutdown();
        schedular.shutdown();
    }

    public Configuration registerConfig(Plugin plugin) {
        return registerConfig(plugin, "config");
    }

    public Configuration registerConfig(Plugin plugin, String name) {
        File configFile = new File(plugin.getDataFolder(), name +".yml");
        if(!configFile.exists()){
            configFile.getParentFile().mkdirs();
            MiscUtils.copy(plugin.getResource(name + ".yml"), configFile);
        }
        try {
            Configuration yaml = ConfigurationProvider
                    .getProvider(YamlConfiguration.class)
                    .load(configFile);

            pluginConfigs.compute(plugin.getName(), (key, value) -> {
                if(value == null) return new HashMap<>();

                value.put(name, yaml);

                return value;
            });

            return yaml;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Deprecated
    public CommandManager getCommandManager(Plugin plugin) {
        return pluginCommandManagers.computeIfAbsent(plugin.getName(), key -> new CommandManager(plugin));
    }

    public BukkitCommandManager getBukkitCommandManager(Plugin plugin) {
        return bukkitCommandManagers.computeIfAbsent(plugin.getName(), key -> new BukkitCommandManager(plugin));
    }

    public WorldInfo getWorldInfo(World world) {
        return worldInfoMap.computeIfAbsent(world.getUID(), key -> new WorldInfo(world));
    }

    public void removePluginInstances(Plugin plugin) {
        pluginCommandManagers.remove(plugin.getName());
        bukkitCommandManagers.remove(plugin.getName());
        Atlas.getInstance().getFunkeCommandManager().removeAll(plugin);
        pluginConfigs.remove(plugin.getName());
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

        RunUtils.taskTimer(() -> {
            trackedEntities.clear();
            for (Player player : Bukkit.getOnlinePlayers()) {
                for (Entity nearbyEntity : player.getNearbyEntities(20, 15, 20)) {
                    Tuple<UUID, Integer> key = new Tuple<>(nearbyEntity.getWorld().getUID(), nearbyEntity.getEntityId());

                    if(trackedEntities.containsKey(key)) continue;

                    trackedEntities.put(key, nearbyEntity);
                }
            }
        }, 40L, 20L);
    }
    private void runTickEvent() {
        service.execute(() -> {
            TickEvent tickEvent = new TickEvent(currentTicks++);
            Atlas.getInstance().getEventManager().callEvent(tickEvent);
        });
    }

    public void initializeScanner(Class<? extends Plugin> mainClass, Plugin plugin, ClassLoader loader,
                                  boolean loadListeners, boolean loadCommands) {
        initializeScanner(mainClass, plugin, loader, ClassScanner.scanFile(null, mainClass), loadListeners, loadCommands);
    }

    @Deprecated
    public Entity getEntityById(World world, int id) {
        return trackedEntities.compute(new Tuple<>(world.getUID(), id), (key, entity) -> {
            if(entity == null) {
                entity = getWorldInfo(world).getEntity(id).orElse(null);
            }
            return entity;
        });
    }

    public void initializeScanner(Class<? extends Plugin> mainClass, Plugin plugin, ClassLoader loader, Set<String> names,
                                  boolean loadListeners, boolean loadCommands) {
        names.stream()
                .map(name -> {
                    if(loader != null) {
                        try {
                            if(loader instanceof RemoteClassLoader) {
                                return new WrappedClass(((RemoteClassLoader)loader).findClass(name));
                            } else
                            return new WrappedClass(Class.forName(name, true, loader));
                        } catch (ClassNotFoundException e) {
                            e.printStackTrace();
                        }
                        return null;
                    } else {
                        return Reflections.getClass(name);
                    }
                })
                .filter(c -> {
                    if(c == null) return false;
                    Init init = c.getAnnotation(Init.class);
                    //If the current version is 1.8, but the required version is 1.9, this will return false.
                    if(ProtocolVersion.getGameVersion().isBelow(init.requireProtocolVersion())) return false;

                    String[] required = init.requirePlugins();

                    if(required.length > 0) {
                        if(init.requireType() == Init.RequireType.ALL) {
                            return Arrays.stream(required)
                                    .allMatch(name -> {
                                        if(name.contains("||")) {
                                            return Arrays.stream(name.split("\\|\\|"))
                                                    .anyMatch(n2 -> Bukkit.getPluginManager().isPluginEnabled(n2));
                                        } else if(name.contains("&&")) {
                                            return Arrays.stream(name.split("\\|\\|"))
                                                    .allMatch(n2 -> Bukkit.getPluginManager().isPluginEnabled(n2));
                                        } else return Bukkit.getPluginManager().isPluginEnabled(name);
                                    });
                        } else {
                            return Arrays.stream(required)
                                    .anyMatch(name -> {
                                        if(name.contains("||")) {
                                            return Arrays.stream(name.split("\\|\\|"))
                                                    .anyMatch(n2 -> Bukkit.getPluginManager().isPluginEnabled(n2));
                                        } else if(name.contains("&&")) {
                                            return Arrays.stream(name.split("\\|\\|"))
                                                    .allMatch(n2 -> Bukkit.getPluginManager().isPluginEnabled(n2));
                                        } else return Bukkit.getPluginManager().isPluginEnabled(name);
                                    });
                        }
                    }
                    return true;
                })
                .sorted(Comparator.comparing(c ->
                        c.getAnnotation(Init.class).priority().getPriority(), Comparator.reverseOrder()))
                .forEach(c -> {
                    if(debugMode) getLogger().log(Level.INFO, "Working on class " + c.getParent().getSimpleName());
                    Object obj = c.getParent().equals(mainClass) ? plugin : c.getConstructor().newInstance();
                    Init annotation = c.getAnnotation(Init.class);

                    if(loadListeners) {
                        if(obj instanceof AtlasListener) {
                            Atlas.getInstance().getEventManager().registerListeners((AtlasListener)obj, plugin);
                            alog(true,"&7Registered Atlas listener &e"
                                    + c.getParent().getSimpleName() + "&7.");
                        }
                        if(obj instanceof Listener) {
                            Bukkit.getPluginManager().registerEvents((Listener)obj, plugin);
                            alog(true,"&7Registered Bukkit listener &e"
                                    + c.getParent().getSimpleName() + "&7.");
                        }
                    }

                    if(loadCommands && annotation.commands()) {
                        alog(true,"&7Registering commands in class &e"
                                + c.getParent().getSimpleName() + "&7...");
                        Atlas.getInstance().getCommandManager(plugin).registerCommands(obj);
                    }

                    if(obj instanceof BaseCommand) {
                        alog(true,"&7Found BaseCommand for class &e"
                                + c.getParent().getSimpleName() + "&7! Registering commands...");
                        getBukkitCommandManager(plugin).registerCommand((BaseCommand)obj);
                    }

                    for (WrappedMethod method : c.getMethods()) {
                        if(method.getMethod().isAnnotationPresent(Invoke.class)) {
                            alog(true,"&7Invoking method &e" + method.getName() + " &7in &e"
                                    + c.getClass().getSimpleName() + "&7...");
                            method.invoke(obj);
                        }
                    }

                    for (WrappedField field : c.getFields()) {
                        if(field.isAnnotationPresent(Instance.class)) {
                            alog(true,"&7Setting instance of &e"
                                    + c.getClass().getSimpleName() + " &7on field &e"
                                    + field.getField().getName() + "&7...");
                            field.set(obj, obj);
                        } else if(field.isAnnotationPresent(ConfigSetting.class)) {
                            ConfigSetting setting = field.getAnnotation(ConfigSetting.class);

                            String name = setting.name().length() > 0
                                    ? setting.name()
                                    : field.getField().getName();

                            alog(true, "&7Found ConfigSetting &e%s &7(default=&f%s&7).",
                                    field.getField().getName(),
                                    (setting.hide() ? "HIDDEN" : field.get(obj)));


                            if(plugin.getConfig().get(setting.path() + "." + name) == null) {
                                alog(true,"&7Value not set in config! Setting value...");
                                plugin.getConfig().set(setting.path() + "." + name, field.get(obj));
                                plugin.saveConfig();
                            } else {
                                Object configObj = plugin.getConfig().get(setting.path() + "." + name);
                                alog(true, "&7Set field to value &e%s&7.",
                                        (setting.hide() ? "HIDDEN" : configObj));
                                field.set(obj, configObj);
                            }
                        }
                    }
                });
    }
    
    public void alog(String log, Object... values) {
        alog(false, log, values);
    }
    
    public void alog(boolean verbose, String log, Object... values) {
        if(!verbose || verboseLogging) {
            if(values.length > 0)
            MiscUtils.printToConsole(log, values);
            else MiscUtils.printToConsole(log);
        }
    }

    public void initializeScanner(Class<? extends Plugin> mainClass, Plugin plugin) {
        initializeScanner(mainClass, plugin, null, true, true);
    }

    public void initializeScanner(Plugin plugin) {
        initializeScanner(plugin.getClass(), plugin);
    }

    public void initializeScanner(Plugin plugin, boolean loadListeners, boolean loadCommands) {
        initializeScanner(plugin.getClass(), plugin, null, loadListeners, loadCommands);
    }
}
