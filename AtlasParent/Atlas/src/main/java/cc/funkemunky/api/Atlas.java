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
import cc.funkemunky.api.reflections.impl.BukkitReflection;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.utils.*;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import dev.brighten.db.Carbon;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
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
    private CommandManager commandManager;
    private FunkeCommandManager funkeCommandManager;
    private Updater updater;
    private BaseProfiler profile;
    private Metrics metrics;
    private TinyProtocolHandler tinyProtocolHandler;
    private int currentThread = 0;
    private long profileStart;
    private EventManager eventManager;
    private int currentTicks;
    private PluginLoaderHandler pluginLoaderHandler;
    private BungeeManager bungeeManager;
    private boolean done;
    private ExecutorService service;
    private File file;
    private Map<UUID, List<Entity>> entities = Collections.synchronizedMap(new HashMap<>());
    private Map<Location, Block> blocksMap = new ConcurrentHashMap<>();

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

        profileStart = System.currentTimeMillis();
        profile = new BaseProfiler();

        MiscUtils.printToConsole(Color.Gray + "Loading utilities and managers...");
        blockBoxManager = new BlockBoxManager();
        commandManager = new CommandManager(this);
        funkeCommandManager = new FunkeCommandManager();

        updater = new Updater();

        runTasks();

        MiscUtils.printToConsole(Color.Gray + "Starting scanner...");

        initializeScanner(getClass(), this,
                null,
                true,
                true);

        funkeCommandManager.addCommand(this, new AtlasCommand());

        bungeeManager = new BungeeManager();

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
                MiscUtils.printToConsole(Color.Green + "Atlas v" + updater.getVersion()
                        + " has been downloaded. Please restart/reload your server to import it.");
            }
        }

        Bukkit.getOnlinePlayers().forEach(player -> TinyProtocolHandler.getInstance().injectPlayer(player));

        MiscUtils.printToConsole(Color.Green + "Successfully loaded Atlas and its utilities!");
        done = true;
    }

    public void onDisable() {
        MiscUtils.printToConsole(Color.Gray + "Unloading all Atlas hooks...");
        HandlerList.unregisterAll(this);
        Bukkit.getScheduler().cancelTasks(this);

        eventManager.clearAllRegistered();
        getCommandManager().unregisterCommands();

        MiscUtils.printToConsole(Color.Gray
                + "Disabling all plugins that depend on Atlas to prevent any errors...");
        Arrays.stream(Bukkit.getPluginManager().getPlugins())
                .filter(plugin -> plugin.getDescription().getDepend().contains("Atlas"))
                .forEach(plugin -> MiscUtils.unloadPlugin(plugin.getName()));
        shutdownExecutor();

        MiscUtils.printToConsole(Color.Red + "Completed shutdown process.");
    }


    private void shutdownExecutor() {
        service.shutdown();
        schedular.shutdown();
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

        //Setting up map
        for (World world : Bukkit.getWorlds()) {
            entities.put(world.getUID(), new ArrayList<>());
        }

        RunUtils.taskTimer(() -> {
            for (World world : Bukkit.getWorlds()) {
                entities.remove(world.getUID());
                entities
                        .put(world.getUID(),
                                Collections.synchronizedList(new ArrayList<>(world.getEntities())));
            }
        }, 2L, 5L);

        //This allows us to cache the blocks to improve the performance of getting blocks.
        schedular.scheduleAtFixedRate(() -> {
            profile.start("task::chunkLoader");
            for(World world : Bukkit.getWorlds()) {
                Object provider = MinecraftReflection.getChunkProvider(world);
                List<Chunk> vChunks = MinecraftReflection.getVanillaChunks(provider)
                        .parallelStream()
                        .map(BukkitReflection::getChunkFromVanilla)
                        .collect(Collectors.toList());

                List<Block> blocksList = Collections.synchronizedList(new ArrayList<>());
                vChunks.parallelStream().forEach(chunk -> {
                    for(int y = 0 ; y < world.getMaxHeight() ; y++) {
                        //The << is a reverse of what is needed to get chunk from loc.
                        int x = chunk.getX() << 4, z = chunk.getZ() << 4;
                        Block block = chunk.getBlock(x & 15, y, z & 15);

                        blocksList.add(block);
                    }
                });

                for (Block block : blocksList) {
                    blocksMap.put(block.getLocation(), block);
                }
                blocksList.clear(); //Clearing to save the java gc from this monster.
            }
            profile.stop("task::chunkLoader");
        }, 10L, 60L, TimeUnit.SECONDS);
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
        names
                .stream()
                .map(name -> {
                    if(loader != null) {
                        try {
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
                    String[] required = c.getAnnotation(Init.class).requirePlugins();

                    if(required.length > 0) {
                        return Arrays.stream(required)
                                .anyMatch(name -> Bukkit.getPluginManager().isPluginEnabled(name));
                    }
                    return true;
                })
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
                                    plugin.saveConfig();
                                } else {
                                    Object configObj = plugin.getConfig().get(setting.path() + "." + name);
                                    MiscUtils.printToConsole("&7Set field to value &e" + configObj + "&7.");
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
                RunUtils.task(() -> blocksMap.put(key, key.getBlock()));
            }
            return null;
        });
    }
}
