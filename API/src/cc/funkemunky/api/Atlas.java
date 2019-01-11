package cc.funkemunky.api;

import cc.funkemunky.api.commands.FunkeCommandManager;
import cc.funkemunky.api.commands.impl.AtlasCommand;
import cc.funkemunky.api.metrics.Metrics;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.updater.Updater;
import cc.funkemunky.api.updater.UpdaterListener;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

@Getter
public class Atlas extends JavaPlugin {
    @Getter
    private static Atlas instance;
    private TinyProtocolHandler tinyProtocolHandler;
    private BlockBoxManager blockBoxManager;
    private ExecutorService threadPool;
    private ConsoleCommandSender consoleSender;
    private FunkeCommandManager funkeCommandManager;
    private Updater updater;
    private Metrics metrics;

    public void onEnable() {
        instance = this;
        saveDefaultConfig();

        metrics = new Metrics(this);

        consoleSender = Bukkit.getConsoleSender();

        MiscUtils.printToConsole(Color.Red + "Loading Atlas...");
        threadPool = Executors.newFixedThreadPool(4);
        tinyProtocolHandler = new TinyProtocolHandler();
        blockBoxManager = new BlockBoxManager();
        funkeCommandManager = new FunkeCommandManager();
        new BlockUtils();
        new ReflectionsUtil();
        new Color();
        new MiscUtils();
        updater = new Updater();

        funkeCommandManager.addCommand(new AtlasCommand());

        Bukkit.getPluginManager().registerEvents(new UpdaterListener(), this);

        if(updater.needsToUpdate()) {
            MiscUtils.printToConsole(Color.Yellow + "There is an update available. See more information with /atlas update.");

            if(getConfig().getBoolean("updater.autoDownload")) {
                MiscUtils.printToConsole(Color.Gray + "Downloading new version...");
                updater.downloadNewVersion();
                MiscUtils.printToConsole(Color.Green + "Atlas v" + updater.getVersion() + " has been downloaded. Please restart/reload your server to import it.");
            }
        }

        MiscUtils.printToConsole(Color.Green + "Successfully loaded Atlas and its utilities!");
    }
}
