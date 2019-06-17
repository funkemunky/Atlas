package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class DevArgument extends FunkeArgument {
    public DevArgument(FunkeCommand parent, String name, String display, String description) {
        super(parent, name, display, description);
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        Atlas.getInstance().getBoxes().addBox();
    }
}
