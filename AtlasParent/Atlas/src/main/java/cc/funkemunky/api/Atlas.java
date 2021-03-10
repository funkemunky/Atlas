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
import cc.funkemunky.api.packet.PacketHandler;
import cc.funkemunky.api.profiling.BaseProfiler;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.BukkitReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import cc.funkemunky.api.utils.objects.RemoteClassLoader;
import co.aikar.commands.BaseCommand;
import co.aikar.commands.BukkitCommandManager;
import dev.brighten.db.Carbon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.*;
import org.bukkit.block.Block;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.event.HandlerList;
import org.bukkit.event.Listener;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.io.File;
import java.util.*;
import java.util.concurrent.*;
import java.util.logging.Level;
import java.util.stream.Collectors;

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
    @Deprecated
    private TinyProtocolHandler tinyProtocolHandler;
    private PacketHandler packetHandler;
    private int currentThread = 0;
    private long profileStart;
    private EventManager eventManager;
    private int currentTicks;
    private PluginLoaderHandler pluginLoaderHandler;
    private BungeeManager bungeeManager;
    private boolean done;
    private ExecutorService service;
    private File file;
    private final Map<UUID, Entity> entities = new ConcurrentHashMap<>();
    private final Map<Integer, UUID> entityIds = new ConcurrentHashMap<>();
    private Map<Location, Block> blocksMap = new ConcurrentHashMap<>();
    private Map<String, CommandManager> pluginCommandManagers = new HashMap<>();
    private Map<String, BukkitCommandManager> bukkitCommandManagers = new HashMap<>();

    @ConfigSetting(path = "updater", name = "autoDownload")
    private static boolean autoDownload = false;

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
        saveDefaultConfig();
        consoleSender = Bukkit.getConsoleSender();

        MiscUtils.printToConsole(Color.Red + "Loading Atlas...");

        MiscUtils.printToConsole(Color.Gray + "Firing up the thread turbines...");
        service = Executors.newFixedThreadPool(2);
        schedular = Executors.newSingleThreadScheduledExecutor();
        eventManager = new EventManager();
        Carbon.setup();

        pluginLoaderHandler = new PluginLoaderHandler();
        tinyProtocolHandler =  new TinyProtocolHandler();
        packetHandler = new PacketHandler();

        profileStart = System.currentTimeMillis();
        profile = new BaseProfiler();

        MiscUtils.printToConsole(Color.Gray + "Loading utilities and managers...");
        blockBoxManager = new BlockBoxManager();
        funkeCommandManager = new FunkeCommandManager();

        updater = new Updater();

        runTasks();

        MiscUtils.printToConsole(Color.Gray + "Starting scanner...");

        initializeScanner(getClass(), this,
                null,
                true,
                true);

        funkeCommandManager.addCommand(this, new AtlasCommand());

        MiscUtils.printToConsole(Color.Gray + "Loading other managers and utilities...");

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
                MiscUtils.printToConsole(Color.Green + "Atlas v" + updater.getLatestUpdate()
                        + " has been downloaded. Please restart/reload your server to import it.");
            }
        }

        Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().injectPlayer(player));
        bungeeManager = new BungeeManager();
        MiscUtils.printToConsole(Color.Green + "Successfully loaded Atlas and its utilities!");
        done = true;
    }

    public void onDisable() {
        MiscUtils.printToConsole(Color.Gray + "Unloading all Atlas hooks...");
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        eventManager.clearAllRegistered();
        getCommandManager(this).unregisterCommands();

        MiscUtils.printToConsole(Color.Gray
                + "Disabling all plugins that depend on Atlas to prevent any errors...");
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getDescription().getDepend().contains("Atlas")
                        || plugin.getDescription().getSoftDepend().contains("Atlas"))
                .forEach(plugin -> MiscUtils.unloadPlugin(plugin.getName()));
        shutdownExecutor();

        MiscUtils.printToConsole(Color.Red + "Completed shutdown process.");
    }


    private void shutdownExecutor() {
        service.shutdown();
        schedular.shutdown();
    }

    @Deprecated
    public CommandManager getCommandManager(Plugin plugin) {
        return pluginCommandManagers.computeIfAbsent(plugin.getName(), key -> new CommandManager(plugin));
    }

    public BukkitCommandManager getBukkitCommandManager(Plugin plugin) {
        return bukkitCommandManagers.computeIfAbsent(plugin.getName(), key -> new BukkitCommandManager(plugin));
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
            synchronized (entities) {
                for (World world : Bukkit.getWorlds()) {
                    for (Entity entity : world.getEntities()) {
                       entities.put(entity.getUniqueId(), entity);
                    }
                }
                entities.keySet().parallelStream().filter(uuid -> entities.get(uuid) == null)
                        .sequential().forEach(entities::remove);
            }
            synchronized (entityIds) {
                entities.forEach((id, entity) -> entityIds.put(entity.getEntityId(), entity.getUniqueId()));
            }
        }, 2L, 5L);
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
                .filter(Objects::nonNull)
                .filter(c -> {
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
                            MiscUtils.printToConsole("&7Registered Atlas listener &e"
                                    + c.getParent().getSimpleName() + "&7.");
                        }
                        if(obj instanceof Listener) {
                            Bukkit.getPluginManager().registerEvents((Listener)obj, plugin);
                            MiscUtils.printToConsole("&7Registered Bukkit listener &e"
                                    + c.getParent().getSimpleName() + "&7.");
                        }
                    }

                    if(loadCommands && annotation.commands()) {
                        MiscUtils.printToConsole("&7Registering commands in class &e"
                                + c.getParent().getSimpleName() + "&7...");
                        Atlas.getInstance().getCommandManager(plugin).registerCommands(obj);
                    }

                    if(obj instanceof BaseCommand) {
                        MiscUtils.printToConsole("&7Found BaseCommand for class &e"
                                + c.getParent().getSimpleName() + "&7! Registering commands...");
                        getBukkitCommandManager(plugin).registerCommand((BaseCommand)obj);
                    }

                    c.getMethods(method -> method.getMethod().isAnnotationPresent(Invoke.class))
                            .forEach(method -> {
                                MiscUtils.printToConsole("&7Invoking method &e" + method.getName() + " &7in &e"
                                        + c.getClass().getSimpleName() + "&7...");
                                method.invoke(obj);
                            });

                    c.getFields(field -> field.isAnnotationPresent(Instance.class))
                            .forEach(field -> {
                                MiscUtils.printToConsole("&7Setting instance of &e"
                                        + c.getClass().getSimpleName() + " &7on field &e"
                                        + field.getField().getName() + "&7...");
                                field.set(obj, obj);
                            });

                    c.getFields(field -> field.isAnnotationPresent(ConfigSetting.class))
                            .forEach(field -> {
                                ConfigSetting setting = field.getAnnotation(ConfigSetting.class);

                                String name = setting.name().length() > 0
                                        ? setting.name()
                                        : field.getField().getName();

                                MiscUtils.printToConsole("&7Found ConfigSetting &e" + field.getField().getName()
                                        + " &7(default=&f" + (setting.hide() ? "HIDDEN" : field.get(obj)) + "&7.");

                                if(plugin.getConfig().get(setting.path() + "." + name) == null) {
                                    MiscUtils.printToConsole("&7Value not set in config! Setting value...");
                                    plugin.getConfig().set(setting.path() + "." + name, field.get(obj));
                                    plugin.saveConfig();
                                } else {
                                    Object configObj = plugin.getConfig().get(setting.path() + "." + name);
                                    MiscUtils.printToConsole("&7Set field to value &e"
                                            + (setting.hide() ? "HIDDEN" : configObj) + "&7.");
                                    field.set(obj, configObj);
                                }
                            });
                });
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

    //Always wait to load chunks if you never want this to return as null. It may add delays tho when it is null.
    //Max time is 5 seconds to load chunks.
    public Block getBlock(Location location, boolean waitToLoadChunks) {
        return blocksMap.computeIfAbsent(location, key -> {
            if(waitToLoadChunks) {
                FutureTask<Block> blockTask = new FutureTask<>(key::getBlock);

                try {
                    return blockTask.get(5000L, TimeUnit.SECONDS);
                } catch(InterruptedException | ExecutionException | TimeoutException e) {
                    e.printStackTrace();
                }
            } else {
                return BlockUtils.getBlock(location);
            }
            return null;
        });
    }
}
