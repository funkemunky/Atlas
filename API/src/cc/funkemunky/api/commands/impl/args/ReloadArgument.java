package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ReloadArgument extends FunkeArgument {
    public ReloadArgument(FunkeCommand command) {
        super(command, "reload", "reload", "reload the configuration.", "atlas.reload");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        Atlas.getInstance().reloadConfig();
        sender.sendMessage(Color.Green + "Successfully reloaded the Atlas configuration file!");
    }
}
