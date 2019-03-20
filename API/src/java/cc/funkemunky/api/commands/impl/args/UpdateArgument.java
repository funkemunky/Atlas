package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class UpdateArgument extends FunkeArgument {
    public UpdateArgument(FunkeCommand command) {
        super(command, "update", "update <check, download> [args]", "manage updates for Atlas.", "atlas.update");

        addTabComplete(2, "check", "download", "update");
        addTabComplete(3, "confirm,!check,2");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if(args.length > 1) {
            switch (args[1].toLowerCase()) {
                case "check": {
                    sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
                    sender.sendMessage(Color.translate("&eLatest Version: &f" + Atlas.getInstance().getUpdater().getVersion()));
                    sender.sendMessage(Color.translate("&eCurrent Version: &f" + Atlas.getInstance().getDescription().getVersion()));
                    sender.sendMessage("");

                    if (Atlas.getInstance().getUpdater().needsToUpdateIfImportant()) {
                        sender.sendMessage(Color.Red + Color.Italics + "It is important that you update since compatibility is shaky.");
                    } else if (Atlas.getInstance().getUpdater().needsToUpdate()) {
                        sender.sendMessage(Color.Yellow + Color.Italics + "You are fine not updating if you don't have to.");
                    } else {
                        sender.sendMessage(Color.Green + "You are on the latest version, no need to worry.");
                    }
                    sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
                    break;
                }
                case "download":
                case "update": {
                    if (args.length > 2) {
                        sender.sendMessage(Color.Red + "Downloading the latest version...");
                        Atlas.getInstance().getUpdater().downloadNewVersion();
                        sender.sendMessage(Color.Green + "Downloaded! Restart your server to use the downloaded version.");
                    } else {
                        sender.sendMessage(Color.Green + "You need to confirm this decision by typing " + Color.Yellow + " /atlas " + args[1].toLowerCase() + " confirm" + Color.Gray + "!");
                    }
                    break;
                }
                default: {
                    sender.sendMessage(Color.Red + "Invalid arguments! Check the help to see where you made a mistake.");
                    break;
                }
            }
        } else {
            sender.sendMessage(Color.Red + "Invalid arguments! Check the help to see where you made a mistake.");
        }
    }
}
