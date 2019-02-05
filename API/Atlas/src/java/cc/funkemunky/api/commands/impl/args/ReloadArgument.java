package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ReloadArgument extends FunkeArgument {
    public ReloadArgument(FunkeCommand command) {
        super(command, "reload", "reload", "reload the configuration.", "atlas.reload");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        Atlas.getInstance().reloadConfig();

        List<Plugin> dependingPls = new ArrayList<>();
        Arrays.stream(Bukkit.getPluginManager().getPlugins()).filter(pl -> pl.getDescription().getDepend().contains("Atlas")).forEach(pl -> dependingPls.add(pl));

        dependingPls.forEach(pl -> MiscUtils.unloadPlugin(pl.getName()));
        MiscUtils.unloadPlugin("Atlas");
        MiscUtils.loadPlugin("Atlas");

        dependingPls.forEach(pl -> MiscUtils.loadPlugin(pl.getName()));

        sender.sendMessage(Color.Green + "Successfully reloaded the Atlas configuration file!");
    }
}
