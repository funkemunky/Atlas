package cc.funkemunky.api.commands.ancmd;

import cc.funkemunky.api.commands.FunkeArgument;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.JsonMessage;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.MiscUtils;
import com.google.common.collect.Lists;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import javax.annotation.Nullable;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
public class CommandManager implements CommandExecutor {
    private Map<String, Map.Entry<Method, Object>> commands = new ConcurrentHashMap<>();
    private Plugin plugin;
    private CommandMap map;

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                map = (CommandMap) field.get(manager);
            } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    public void registerCommands(Object clazz) {
        try {

            Arrays.stream(clazz.getClass().getMethods()).filter(method -> method.isAnnotationPresent(Command.class) && method.getParameterCount() > 0 && method.getParameters()[0].getType() == CommandAdapter.class).forEach(method -> {
                Command annotation = method.getAnnotation(Command.class);

                registerCommand(annotation, annotation.name(), method, clazz);
                Arrays.stream(annotation.aliases()).forEach(alias -> registerCommand(annotation, alias, method, clazz));
            });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] strings) {
        List<String> toFormatArgs = Collections.singletonList(label);
        Arrays.stream(strings).forEach(string -> toFormatArgs.add(string));
        List<String> beforeArgs = new ArrayList<>(toFormatArgs);

        for(int arg = beforeArgs.size(); arg >= 1 ; arg--) {
            StringBuffer buffer = new StringBuffer();
            for (int x = 0; x < arg; x++) {
                buffer.append("." + beforeArgs.get(x).toLowerCase());
            }

            if(commands.containsKey(buffer.toString())) {
                Map.Entry<Method, Object> entry = commands.get(buffer.toString());

                Command command = entry.getKey().getAnnotation(Command.class);

                if(command.playerOnly() && !(sender instanceof Player)) {
                    sender.sendMessage(Color.Red + "This command is for players only!");
                } else if(command.consoleOnly() && !(sender instanceof ConsoleCommandSender)) {
                    sender.sendMessage(Color.Red + "This command can only be run via terminal.");
                } else {
                    int size = beforeArgs.size() - buffer.toString().split(" ").length;
                    String[] args = new String[size];

                    for (int i = 0; i < args.length; i++) {
                        int grabbyDabby = i + buffer.toString().split(" ").length;
                        args[i] = beforeArgs.get(grabbyDabby);
                    }

                    if(command.permission().length == 0 || Arrays.stream(command.permission()).anyMatch(sender::hasPermission)) {
                        CommandAdapter adapter = new CommandAdapter(sender, cmd, sender instanceof Player ? (Player) sender : null, label, args);
                        try {
                            entry.getKey().invoke(entry.getValue(), adapter);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        sender.sendMessage(Color.translate(command.noPermissionMessage()));
                    }
                }
                break;
            }
        }
        return true;
    }

    public ColorScheme getDefaultScheme() {
        return new ColorScheme(Color.Gold + Color.Bold, Color.Gray, Color.Yellow, Color.White, Color.Dark_Gray, Color.Red, Color.White);
    }

    public void runHelpMessage(CommandAdapter command, CommandSender sender, @Nullable ColorScheme scheme) {
        ColorScheme colorScheme = scheme != null ? scheme : getDefaultScheme();

        try {
            int page = command.getArgs().length > 0 ? Integer.parseInt(command.getArgs()[0]) : 1;
            Set<Command> argumentSet = new HashSet<>();

            commands.keySet().stream().filter(key -> key.contains(".") && key.startsWith(command.getLabel().toLowerCase())).forEach(key -> {
                Map.Entry<Method, Object> map = commands.get(key);

                Command cmd = map.getKey().getAnnotation(Command.class);

                argumentSet.add(cmd);
            });

            List<Command> arguments = new ArrayList<>(argumentSet);

            if (sender instanceof Player) {
                for (int i = (page - 1) * 6; i < Math.min(page * 6, arguments.size()); i++) {
                    JsonMessage message = new JsonMessage();

                    Command argument = arguments.get(i);
                    StringBuilder aliasesFormatted = new StringBuilder();
                    List<String> aliases = Arrays.asList(argument.aliases());
                    if (aliases.size() > 0) {
                        for (String aliase : aliases) {
                            aliasesFormatted.append(colorScheme.getValue()).append(aliase).append(colorScheme.getBody()).append(", ");
                        }
                        int length = aliasesFormatted.length();
                        aliasesFormatted = new StringBuilder(aliasesFormatted.substring(0, length - 2));
                    } else {
                        aliasesFormatted = new StringBuilder(colorScheme.getError() + "None");
                    }


                    String hoverText = Color.translate((argument.permission().length > 0 ? colorScheme.getTitle() + "Permissions: " + colorScheme.getValue() + " " + Arrays.toString(argument.permission()) : colorScheme.getTitle() + "Permission: " + colorScheme.getValue() + "none")
                            + "\n" + colorScheme.getTitle() +  "Aliases: " + colorScheme.getValue() + aliasesFormatted);
                    message.addText(colorScheme.getBody()+ "/" + command.getLabel().toLowerCase() + colorScheme.getValue() + " " + argument.display() + colorScheme.getBody() + " to " + argument.description()).addHoverText(hoverText);
                    message.sendToPlayer((Player) sender);
                }
            } else {
                for (int i = (page - 1) * 6; i < Math.min(arguments.size(), page * 6); i++) {
                    Command argument = arguments.get(i);
                    sender.sendMessage(colorScheme.getBody()+ "/" + command.getLabel().toLowerCase() + colorScheme.getValue() + " " + argument.display() + colorScheme.getBody() + " to " + argument.description());
                }
            }
        } catch(NumberFormatException e) {
            sender.sendMessage(colorScheme.getError() + "The page input must be a number only!");
        }
        /*try {
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
                        || sender.hasPermission(permission))) {
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
        }*/
    }

    public void registerCommand(Command command, String label, Method method, Object clazz) {
        Command annotation = method.getAnnotation(Command.class);

        commands.put(annotation.name().toLowerCase(), new AbstractMap.SimpleEntry<>(method, clazz));

        Arrays.stream(annotation.aliases()).forEach(alias -> commands.put(alias.toLowerCase(), new AbstractMap.SimpleEntry<>(method, clazz)));
        MiscUtils.printToConsole(Color.Yellow + "Registered ancmd: " + annotation.name());

        String cmdLabel = label.split("\\.")[0].toLowerCase();
        if (map.getCommand(cmdLabel) == null) {
            SpigotCommand cmd = new SpigotCommand(cmdLabel, this, plugin);

            Arrays.stream(annotation.tabCompletions()).forEach(string -> cmd.completer.addCompleter(string, method, clazz));
            map.register(plugin.getName(), cmd);
        }
        if (!command.description().equalsIgnoreCase("") && cmdLabel == label) {
            map.getCommand(cmdLabel).setDescription(command.description());
        }
        if (!command.usage().equalsIgnoreCase("") && cmdLabel == label) {
            map.getCommand(cmdLabel).setUsage(command.usage());
        }
    }
}
