package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class ToggleArgument extends FunkeArgument {
    public ToggleArgument(FunkeCommand parent, String name, String display, String description, String... permission) {
        super(parent, name, display, description, permission);

        addTabComplete(2, "event", "events", "packets");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if(args.length > 1) {
            switch(args[1].toLowerCase()) {
                case "event":
                case "events": {
                    Atlas.getInstance().getEventManager().paused = !Atlas.getInstance().getEventManager().paused;
                    sender.sendMessage(Color.translate("&7The Atlas AtlasEvent System has been " + (Atlas.getInstance().getEventManager().paused ? "&cpaused" : "&aunpaused") + "&7."));
                    break;
                }
                case "packets": {
                    Atlas.getInstance().getTinyProtocolHandler().paused = !Atlas.getInstance().getTinyProtocolHandler().paused;
                    sender.sendMessage(Color.translate("&7The Atlas Packet System has been " + (Atlas.getInstance().getTinyProtocolHandler().paused ? "&cpaused" : "&aunpaused") + "&7."));
                    break;
                }
                default: {
                    sender.sendMessage(getParent().getCommandMessages().getErrorColor() + getParent().getCommandMessages().getInvalidArguments());
                    break;
                }
            }
        } else sender.sendMessage(getParent().getCommandMessages().getErrorColor() + getParent().getCommandMessages().getInvalidArguments());
    }
}
