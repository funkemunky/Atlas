package cc.funkemunky.api.commands;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.JsonMessage;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Getter
@Setter
@Deprecated
public abstract class FunkeCommand
        implements CommandExecutor, TabCompleter {
    private static FunkeCommand instance;
    private String name, display, permission, description, adminPerm = "api.admin";
    private boolean helpPage, playerOnly;
    private final List<FunkeArgument> arguments;
    private CommandMessages commandMessages;

    public FunkeCommand(JavaPlugin plugin, String name, String display, String description, String permission) {
        this.name = name;
        this.display = display;
        this.permission = permission;
        this.description = description;

        commandMessages = new CommandMessages("No permission.", "Invalid arguments. Please check the help page for more information.", "You must be a player to use this feature", "Only console can use this feature.", Color.Gray, Color.Yellow, Color.Gold, Color.Red, Color.White, Color.Green);

        this.arguments = new ArrayList<>();
        instance = this;
        helpPage = true;
        plugin.getCommand(name).setExecutor(this);
        plugin.getCommand(name).setTabCompleter(this);

        this.addArguments();
    }

    public FunkeCommand(JavaPlugin plugin, String name, String display, String description, String permission, boolean registerLater) {
        this.name = name;
        this.display = display;
        this.permission = permission;
        this.description = description;

        commandMessages = new CommandMessages("No permission.", "Invalid arguments. Please check the help page for more information.", "You must be a player to use this feature", "Only console can use this feature.", Color.Gray, Color.Yellow, Color.Gold, Color.Red, Color.White, Color.Green);

        this.arguments = new ArrayList<>();
        instance = this;
        helpPage = true;

        if(!registerLater) {
            plugin.getCommand(name).setExecutor(this);
            plugin.getCommand(name).setTabCompleter(this);
        }

        this.addArguments();
    }

    public FunkeCommand(JavaPlugin plugin, String name, String display, String permission, String description, boolean helpPage, boolean registerLater) {
        this.name = name;
        this.display = display;
        this.permission = permission;
        this.description = description;
        this.helpPage = helpPage;

        commandMessages = new CommandMessages("No permission.", "Invalid arguments. Please check the help page for more information.", "You must be a player to use this feature", "Only console can use this feature.", Color.Gray, Color.Yellow, Color.Gold, Color.Red, Color.White, Color.Green);
        commandMessages = new CommandMessages("No permission.", "Invalid arguments. Please check the help page for more information.", "You must be a player to use this feature", "Only console can use this feature.", Color.Gray, Color.Yellow, Color.Gold, Color.Red, Color.White, Color.Green);

        this.arguments = new ArrayList<>();
        instance = this;
        if(!registerLater) {
            plugin.getCommand(name).setExecutor(this);
            plugin.getCommand(name).setTabCompleter(this);
        }

        this.addArguments();
    }

    public List<String> onTabComplete(CommandSender commandSender, Command command, String label, String[] args) {
        Atlas.getInstance().getProfile().start("command:" + getName() + "#tabComplete");
        List<String> toReturn = new ArrayList<>();

        if (label.equalsIgnoreCase(name)) {
            final FunkeArgument[] funkeArgument = new FunkeArgument[1];
            arguments.forEach(argument -> {
                if (args.length == 1 && argument.getName().toLowerCase().startsWith(args[0].toLowerCase()) && !args[0].contains(argument.getName())) {
                    toReturn.add(argument.getName());
                }

                if (argument.getName().equalsIgnoreCase(args[0])) {
                    funkeArgument[0] = argument;
                } else if (getArgumentByAlias(args[0]) != null) {
                    funkeArgument[0] = getArgumentByAlias(args[0]);
                }
            });

            if (funkeArgument[0] != null) {
                funkeArgument[0].getTabComplete().getOrDefault(args.length, new ArrayList<>()).forEach(string -> {
                    String[] split = string.split(",").length == 0 ? new String[]{string} : string.split(","), conditional = split.length > 1 ? new String[]{split[1], split[2]} : new String[0];

                    String arg = split[0];

                    if (conditional.length > 0) {
                        if (args[Integer.parseInt(conditional[1]) - 1].equalsIgnoreCase(conditional[0].replaceAll("!", "")) == !conditional[0].startsWith("!") && arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                            toReturn.add(arg);
                        }
                    } else if (arg.toLowerCase().startsWith(args[args.length - 1].toLowerCase())) {
                        toReturn.add(arg);
                    }
                });
            }

        }
        Atlas.getInstance().getProfile().stop("command:" + getName() + "#tabComplete");
        return toReturn.size() == 0 ? null : toReturn;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Atlas.getInstance().getProfile().start("command:" + getName() + "#onCommand");
        if (this.permission != null && !sender.hasPermission(this.permission)) {
            sender.sendMessage(commandMessages.getErrorColor() + commandMessages.getNoPermission());
            return true;
        }
        if(playerOnly && !(sender instanceof Player)) {
            sender.sendMessage(commandMessages.getErrorColor() + commandMessages.getPlayerOnly());
            return true;
        }

        if(helpPage) {
            try {
                int page = args.length > 0 ? Integer.parseInt(args[0]) : 1;
                sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
                sender.sendMessage(commandMessages.getTitleColor() + Color.Bold + this.display + commandMessages.getSecondaryColor() + " Command Help " + commandMessages.getValueColor() + "Page (" + page + " / " + (int) MathUtils.round(arguments.size() / 6D) + ")");
                sender.sendMessage("");
                sender.sendMessage(Color.translate(commandMessages.getSecondaryColor() + "<> " + commandMessages.getPrimaryColor() + "= required. " + commandMessages.getSecondaryColor() + " [] " + commandMessages.getPrimaryColor() +  "= optional."));
                sender.sendMessage("");
                if (sender instanceof Player) {
                    for (int i = (page - 1) * 6; i < Math.min(page * 6, arguments.size()); i++) {
                        FunkeArgument argument = arguments.get(i);
                        JsonMessage message = new JsonMessage();

                        StringBuilder aliasesFormatted = new StringBuilder();
                        List<String> aliases = argument.getAliases();
                        if (aliases.size() > 0) {
                            for (String aliase : aliases) {
                                aliasesFormatted.append(Color.White).append(aliase).append(Color.Gray).append(", ");
                            }
                            int length = aliasesFormatted.length();
                            aliasesFormatted = new StringBuilder(aliasesFormatted.substring(0, length - 2));
                        } else {
                            aliasesFormatted = new StringBuilder(commandMessages.getErrorColor() + "None");
                        }


                        String hoverText = Color.translate((argument.getPermission() != null && argument.getPermission().length > 0 ? commandMessages.getTitleColor() + "Permissions: " + commandMessages.getValueColor() + " " + Arrays.toString(argument.getPermission()) : commandMessages.getTitleColor() + "Permission: " + commandMessages.getValueColor() + "none")
                                + "\n" + commandMessages.getTitleColor() +  "Aliases: " + commandMessages.getValueColor() + aliasesFormatted);
                        message.addText(commandMessages.getPrimaryColor()+ "/" + label.toLowerCase() + commandMessages.getValueColor() + " " + argument.getDisplay() + commandMessages.getPrimaryColor() + " to " + argument.getDescription()).addHoverText(hoverText);
                        message.sendToPlayer((Player) sender);
                    }
                } else {
                    for (int i = (page - 1) * 6; i < Math.min(arguments.size(), page * 6); i++) {
                        FunkeArgument argument = arguments.get(i);
                        sender.sendMessage(commandMessages.getPrimaryColor() + "/" + label.toLowerCase() + commandMessages.getValueColor() + " " + argument.getDisplay() + commandMessages.getPrimaryColor() + " to " + argument.getDescription());
                    }
                }
                sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            } catch (NumberFormatException e) {
                for (FunkeArgument argument : this.arguments) {

                    if (!args[0].equalsIgnoreCase(argument.getName()) && !argument.getAliases().contains(args[0].toLowerCase()))
                        continue;

                    if ((argument.getPermission() == null || sender.hasPermission(adminPerm)
                            || Arrays.stream(argument.getPermission()).anyMatch(sender::hasPermission))) {
                        if(!argument.isPlayerOnly() || sender instanceof Player) {
                            argument.onArgument(sender, cmd, args);
                        } else {
                            sender.sendMessage(commandMessages.getErrorColor() + commandMessages.getPlayerOnly());
                        }
                        break;
                    }
                    sender.sendMessage(commandMessages.getErrorColor() + commandMessages.getNoPermission());
                    break;
                }
            }
        }
        Atlas.getInstance().getProfile().stop("command:" + getName() + "#onCommand");
        return true;
    }

    private FunkeArgument getArgumentByAlias(String alias) {
        return arguments.stream().filter(arg -> arg.getAliases().stream().anyMatch(alias2 -> alias2.equalsIgnoreCase(alias))).findFirst().orElse(null);
    }

    protected abstract void addArguments();
}

