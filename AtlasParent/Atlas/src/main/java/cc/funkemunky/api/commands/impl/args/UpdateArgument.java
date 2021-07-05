package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.commands.FunkeCommand;
import cc.funkemunky.api.handlers.chat.ChatHandler;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;

import java.util.List;
import java.util.stream.Collectors;

public class UpdateArgument extends FunkeArgument {
    public UpdateArgument(FunkeCommand command) {
        super(command, "update", "update <check, download> [args]", "manage updates for Atlas.", "atlas.update");

        addTabComplete(2, "check", "download", "update");
    }

    @Override
    public void onArgument(CommandSender sender, Command cmd, String[] args) {
        if(args.length > 1) {
            switch (args[1].toLowerCase()) {
                case "check": {
                    sender.sendMessage(Color.Gray + "Running update check...");
                    // Refreshes the update information from GitHub.
                    Atlas.getInstance().getUpdater().runUpdateCheck();
                    sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
                    sender.sendMessage(Color.translate("&eLatest Version: &f"
                            + Atlas.getInstance().getUpdater().getLatestUpdate()));
                    sender.sendMessage(Color.translate("&eLatest Release Date:"));
                    sender.sendMessage(Color.White + Atlas.getInstance().getUpdater().getReleaseDate());
                    sender.sendMessage(Color.Yellow + "View update:");
                    sender.sendMessage(Color.White + Atlas.getInstance().getUpdater().getViewLink());
                    sender.sendMessage(Color.translate("&eCurrent Version: &f"
                            + Atlas.getInstance().getUpdater().getCurrentUpdate()));
                    sender.sendMessage("");

                    if(Atlas.getInstance().getUpdater().needsToUpdate()) {
                        sender.sendMessage(Color.Green + Color.Bold + "There is an update available!");
                    } else {
                        sender.sendMessage(Color.Green + "You are on the latest version, no need to worry.");
                    }
                    sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
                    break;
                }
                case "download":
                case "update": {
                    if(sender instanceof Player) {
                        Player player = (Player) sender;
                        player.sendMessage(Color.Gray + "You need to confirm this decision by typing "
                                + Color.Yellow + "'download' " + Color.Gray + "in chat or " + Color.Yellow
                                + "'cancel' " + Color.Gray +  "to cancel!");
                        ChatHandler.onChat(player, false, (chat, message) -> {
                            switch(message.toLowerCase()) {
                                case "cancel": {
                                    ChatHandler.remove(player, chat);
                                    sender.sendMessage(Color.Red + "Canceled download.");
                                    break;
                                }
                                case "download": {
                                    Atlas.getInstance().getSchedular().execute(() -> {
                                        sender.sendMessage(Color.Red + "Downloading the latest version...");
                                        //Downloads the latest version from git.
                                        Atlas.getInstance().getUpdater().downloadNewVersion();
                                        // The server needs restarting or reloading to prevent any errors from hiccups when reloading.
                                        sender.sendMessage(Color.translate("&7Downloaded! Would you like to " +
                                                "reload Atlas? Type &e'yes' &7to reload or anything else to cancel"));
                                    });
                                    ChatHandler.remove(player, chat);
                                    ChatHandler.onChat(player, true, (chat2, message2) -> {
                                        Atlas.getInstance().getSchedular().execute(() -> {
                                            if(message2.toLowerCase().contains("yes")) {
                                                player.sendMessage(Color.Red + "Reloading...");
                                                player.sendMessage(Color.Gray + "Grabbing plugins using Atlas...");
                                                List<String> atlasPlugins = Atlas.getInstance().getPluginLoaderHandler()
                                                        .getLoadedPlugins().stream()
                                                        .map(Plugin::getName).collect(Collectors.toList());

                                                player.sendMessage(Color.Gray + "Unloading dependant plugins...");
                                                for (int i = atlasPlugins.size() - 1; i > 0; --i) {
                                                    String pl = atlasPlugins.get(i);
                                                    player.sendMessage(Color.Gray + "Unloading " + pl + "...");
                                                    MiscUtils.unloadPlugin(pl);
                                                }

                                                player.sendMessage(Color.Gray + "Unloading Atlas...");
                                                MiscUtils.unloadPlugin("Atlas");

                                                player.sendMessage(Color.Gray + "Loading Atlas...");
                                                MiscUtils.loadPlugin("Atlas");

                                                player.sendMessage(Color.Gray + "Loading dependant plugins...");
                                                for (String pl : atlasPlugins) {
                                                    player.sendMessage(Color.Gray + "Loading " + pl + "...");
                                                    MiscUtils.loadPlugin(pl);
                                                }

                                                player.sendMessage(Color.Green + "Completed reload! " +
                                                        "Now running updated Atlas version.");
                                            } else player.sendMessage(Color.Red + "Restart server to run updated Atlas.");
                                        });
                                    });
                                    break;
                                }
                                default: {
                                    sender.sendMessage(Color.Red + "Message \"" + message + "\" is not an option.");
                                    break;
                                }
                            }
                        });
                    } else {
                        sender.sendMessage(Color.Red + "Downloading the latest version...");
                        Atlas.getInstance().getUpdater().downloadNewVersion(); //Downloads the latest version from git.
                        // The server needs restarting or reloading to prevent any errors from hiccups when reloading.
                        sender.sendMessage(Color.Green
                                + "Downloaded! Would you like to reload Atlas? Type 'yes' to reload and 'no' to cancel");
                    }
                    break;
                }
                default: {
                    sender.sendMessage(Color.Red
                            + "Invalid arguments! Check the help to see where you made a mistake.");
                    break;
                }
            }
        } else {
            sender.sendMessage(Color.Red
                    + "Invalid arguments! Check the help to see where you made a mistake.");
        }
    }
}
