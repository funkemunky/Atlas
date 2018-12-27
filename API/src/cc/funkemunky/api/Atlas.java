package cc.funkemunky.api;

import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import cc.funkemunky.api.utils.BlockUtils;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.ReflectionsUtil;
import cc.funkemunky.api.utils.blockbox.BlockBoxManager;
import lombok.Getter;
import org.bukkit.Bukkit;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

@Getter
public class Atlas extends JavaPlugin {
    @Getter
    private static Atlas instance;
    private TinyProtocolHandler tinyProtocolHandler;
    private BlockBoxManager blockBoxManager;
    private Executor threadPool;
    private ConsoleCommandSender consoleSender;

    public void onEnable() {
        instance = this;

        consoleSender = Bukkit.getConsoleSender();

        MiscUtils.printToConsole(Color.Red + "Loading Atlas...");
        threadPool = Executors.newFixedThreadPool(4);
        tinyProtocolHandler = new TinyProtocolHandler();
        blockBoxManager = new BlockBoxManager();
        new BlockUtils();
        new ReflectionsUtil();
        new Color();
        new MiscUtils();

        MiscUtils.printToConsole(Color.Green + "Successfully loaded Atlas and its utilities!");
    }
}
