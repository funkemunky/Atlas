package cc.funkemunky.api.commands.ancmd;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.tab.TabHandler;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.v1_13.DontImportIfNotLatestThanks;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.JsonMessage;
import cc.funkemunky.api.utils.MathUtils;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.Getter;
import lombok.val;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.SimpleCommandMap;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;
import java.util.stream.IntStream;

@Getter
@Deprecated
public class CommandManager implements CommandExecutor {
    private Map<String, CommandRegister> commands = new ConcurrentHashMap<>();
    private Plugin plugin;
    @Getter
    public SimpleCommandMap map;
    public List<SpigotCommand> registered = new ArrayList<>();
    private static DontImportIfNotLatestThanks stuff;

    public CommandManager(Plugin plugin) {
        this.plugin = plugin;

        createCommandMap(plugin);
    }

    public void createCommandMap(Plugin plugin) {
        if (plugin.getServer().getPluginManager() instanceof SimplePluginManager) {
            SimplePluginManager manager = (SimplePluginManager) plugin.getServer().getPluginManager();
            try {
                Field field = SimplePluginManager.class.getDeclaredField("commandMap");
                field.setAccessible(true);
                map = (SimpleCommandMap) field.get(manager);
            } catch (IllegalArgumentException | SecurityException | NoSuchFieldException | IllegalAccessException e) {
                e.printStackTrace();
            }
        }
    }

    @Deprecated
    public void registerCommands(Plugin plugin, Object clazz) {
        registerCommands(clazz);
    }

    public void registerCommands(Object clazz) {
        try {
            new WrappedClass(clazz.getClass()).getMethods().stream()
                    .filter(method -> method.getMethod().isAnnotationPresent(Command.class)
                            && method.getMethod().getParameterCount() > 0
                            && method.getParameters().get(0) == CommandAdapter.class)
                    .forEach(method -> {
                        Command annotation = method.getMethod().getAnnotation(Command.class);

                        registerCommand(annotation, annotation.name(), method, clazz);
                        if(annotation.aliases().length > 0) {
                            Arrays.stream(annotation.aliases())
                                    .forEach(alias -> registerCommand(annotation, alias, method, clazz));
                        }
                    });
        } catch(Exception e) {
            e.printStackTrace();
        }
    }

    @Deprecated
    public void unregisterCommands(Plugin plugin) {
        commands.keySet().stream().filter(key -> commands.get(key).getPlugin().getName().equals(plugin.getName())).forEach(key -> {

            val split = key.split(".");

            val name = split[0];

            val cmd = map.getCommand(name);

            if(cmd != null) {
                cmd.unregister(map);
            }

            commands.remove(key);
        });
    }

    public void unregisterCommands() {
        registered.forEach(cmd -> {
            Atlas.getInstance().alog(true,Color.Yellow + "Unregistered " + cmd.getLabel());
            unregisterBukkitCommand(cmd);
        });
        commands.clear();
    }

    private static WrappedClass scmClass = new WrappedClass(SimpleCommandMap.class);
    private static WrappedField fieldKnownCommands = scmClass.getFieldByType(Map.class, 0);

    public void unregisterCommand(String name) {
        registered.stream()
                .filter(cmd -> cmd.getName().equalsIgnoreCase(name) || cmd.getLabel().equalsIgnoreCase(name))
                .forEach(cmd -> {
                    Atlas.getInstance().alog(true, Color.Yellow + "Unregistered " + cmd.getLabel());
                    unregisterBukkitCommand(cmd);
                });
    }

    public void unregisterBukkitCommand(org.bukkit.command.Command cmd) {
        try {
            Object map = fieldKnownCommands.get(getMap());
            @SuppressWarnings("unchecked")
            HashMap<String, Command> knownCommands = (HashMap<String, Command>) map;
            knownCommands.remove(cmd.getName());
            for (String alias : cmd.getAliases()){
                if(knownCommands.containsKey(alias) && knownCommands.get(alias).toString()
                        .contains(Atlas.getInstance().getName())) {
                    knownCommands.remove(alias);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command cmd, String label, String[] argsArray) {
        List<String> argsList = new ArrayList<>();
        String[] args;
        if(label.contains(":")) {
            val splitLabel = (label.replace(label.split(":")[0] + ":", ""))
                    .split(":");
            argsList.addAll(Arrays.asList(splitLabel));
            argsList.addAll(Arrays.asList(argsArray));
            args = argsList.toArray(new String[0]);
        } else args = argsArray;
        for(int arg = args.length; arg >= 0 ; arg--) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(label.toLowerCase());
            for (int x = 0; x < arg; x++) {
                buffer.append("." + args[x].toLowerCase());
            }
            String bufferString = buffer.toString();
            if(commands.containsKey(buffer.toString())) {
                CommandRegister entry = commands.get(buffer.toString());

                Command command = entry.getMethod().getMethod().getAnnotation(Command.class);
                Atlas.getInstance().getProfile().start("anCommand:" + cmd.getLabel());

                if(command.async()) {
                    Atlas.getInstance().getService().execute(() -> {
                        if(command.opOnly() && !sender.isOp()) {
                            sender.sendMessage(Color.translate(command.noPermissionMessage()));
                        } else if(command.playerOnly() && !(sender instanceof Player)) {
                            sender.sendMessage(Color.Red + "This command is for players only!");
                        } else if(command.consoleOnly() && !(sender instanceof ConsoleCommandSender)) {
                            sender.sendMessage(Color.Red + "This command can only be run via terminal.");
                        } else {
                            int subCommand = bufferString.split("\\.").length - 1;
                            String[] modArgs = IntStream.range(0, args.length - subCommand)
                                    .mapToObj(i -> args[i + subCommand]).toArray(String[]::new);

                            String labelFinal = IntStream.range(0, subCommand).mapToObj(x -> " " + args[x])
                                    .collect(Collectors.joining("", label, ""));
                            if(command.permission().length == 0 || Arrays.stream(command.permission())
                                    .anyMatch(sender::hasPermission)) {
                                CommandAdapter adapter = new CommandAdapter(sender, cmd, sender instanceof Player
                                        ? (Player) sender : null, labelFinal, command, modArgs);
                                try {
                                    entry.getMethod().invoke(entry.getObject(), adapter);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            } else {
                                sender.sendMessage(Color.translate(command.noPermissionMessage()));
                            }
                        }
                    });
                } else {
                    if(command.opOnly() && !sender.isOp()) {
                        sender.sendMessage(Color.translate(command.noPermissionMessage()));
                    } else if(command.playerOnly() && !(sender instanceof Player)) {
                        sender.sendMessage(Color.Red + "This command is for players only!");
                    } else if(command.consoleOnly() && !(sender instanceof ConsoleCommandSender)) {
                        sender.sendMessage(Color.Red + "This command can only be run via terminal.");
                    } else {
                        int subCommand = bufferString.split("\\.").length - 1;
                        String[] modArgs = IntStream.range(0, args.length - subCommand)
                                .mapToObj(i -> args[i + subCommand]).toArray(String[]::new);

                        String labelFinal = IntStream.range(0, subCommand).mapToObj(x -> " " + args[x])
                                .collect(Collectors.joining("", label, ""));
                        if(command.permission().length == 0 || Arrays.stream(command.permission())
                                .anyMatch(sender::hasPermission)) {
                            CommandAdapter adapter = new CommandAdapter(sender, cmd, sender instanceof Player
                                    ? (Player) sender : null, labelFinal, command, modArgs);
                            try {
                                entry.getMethod().invoke(entry.getObject(), adapter);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        } else {
                            sender.sendMessage(Color.translate(command.noPermissionMessage()));
                        }
                    }
                }

                Atlas.getInstance().getProfile().stop("anCommand:" + cmd.getLabel());
                break;
            }
        }
        return true;
    }

    public ColorScheme getDefaultScheme() {
        return new ColorScheme(Color.Gold + Color.Bold, Color.Gray, Color.Yellow, Color.White,
                Color.Dark_Gray, Color.Red, Color.White);
    }

    public void runHelpMessage(CommandAdapter command, CommandSender sender, ColorScheme scheme) {
        try {
            int page = command.getArgs().length > 0 ? Integer.parseInt(command.getArgs()[0]) : 1;
            Set<Command> argumentSet = new HashSet<>();

            commands.keySet().stream()
                    .filter(key -> key.contains(".") && key.startsWith(command.getLabel().toLowerCase()))
                    .forEach(key -> {
                CommandRegister reg = commands.get(key);

                Command cmd = reg.getMethod().getMethod().getAnnotation(Command.class);

                argumentSet.add(cmd);
            });

            List<Command> arguments = new ArrayList<>(argumentSet);

            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
            sender.sendMessage(scheme.getTitle() + command.getAnnotation().display() + scheme.getBody()
                    + " Help " + scheme.getValue() + "Page (" + page + " / "
                    + (int) MathUtils.round(arguments.size() / 6D) + ")");
            sender.sendMessage("");
            sender.sendMessage(Color.translate(scheme.getBody2nd()) + "<> " + scheme.getBody() + "= required. "
                    + scheme.getBody2nd() + " [] " + scheme.getBody() +  "= optional.");
            sender.sendMessage("");
            if (sender instanceof Player) {
                StringBuilder cmdaliasesFormatted = new StringBuilder();
                List<String> cmdaliases = Arrays.asList(command.getAnnotation().aliases());
                if (cmdaliases.size() > 0) {
                    for (String aliase : cmdaliases) {
                        cmdaliasesFormatted.append(scheme.getValue()).append(aliase).append(scheme.getBody())
                                .append(", ");
                    }
                    int length = cmdaliasesFormatted.length();
                    cmdaliasesFormatted = new StringBuilder(cmdaliasesFormatted.substring(0, length - 2));
                } else {
                    cmdaliasesFormatted = new StringBuilder(scheme.getError() + "None");
                }
                JsonMessage cmdMessage = new JsonMessage();
                String commandText = Color.translate((command.getAnnotation().permission().length > 0
                        ? scheme.getTitle() + "Permissions: " + scheme.getValue() + " "
                        + Arrays.toString(command.getAnnotation().permission())
                        : scheme.getTitle() + "Permission: " + scheme.getValue() + "none")
                        + "\n" + scheme.getTitle() +  "Aliases: " + scheme.getValue() + cmdaliasesFormatted);
                cmdMessage.addText(scheme.getBody()+ "/" + scheme.getValue() + command.getLabel().toLowerCase()
                        + scheme.getBody() + " to " + command.getAnnotation().description()).addHoverText(commandText);
                cmdMessage.sendToPlayer((Player) sender);
                for (int i = (page - 1) * 6; i < Math.min(page * 6, arguments.size()); i++) {
                    JsonMessage message = new JsonMessage();

                    Command argument = arguments.get(i);
                    StringBuilder aliasesFormatted = new StringBuilder();
                    List<String> aliases = Arrays.asList(argument.aliases());
                    if (aliases.size() > 0) {
                        for (String aliase : aliases) {
                            aliasesFormatted.append(scheme.getValue()).append(aliase).append(scheme.getBody())
                                    .append(", ");
                        }
                        int length = aliasesFormatted.length();
                        aliasesFormatted = new StringBuilder(aliasesFormatted.substring(0, length - 2));
                    } else {
                        aliasesFormatted = new StringBuilder(scheme.getError() + "None");
                    }


                    String hoverText = Color.translate((argument.permission().length > 0 ? scheme.getTitle()
                            + "Permissions: " + scheme.getValue() + " " + Arrays.toString(argument.permission())
                            : scheme.getTitle() + "Permission: " + scheme.getValue() + "none")
                            + "\n" + scheme.getTitle() +  "Aliases: " + scheme.getValue() + aliasesFormatted.toString());
                    message.addText(scheme.getBody()+ "/" + command.getLabel().toLowerCase()
                            + scheme.getValue() + " " + (argument.display().length() > 0 ? argument.display()
                            : argument.name()) + scheme.getBody() + " to " + argument.description())
                            .addHoverText(hoverText);
                    message.sendToPlayer((Player) sender);
                }
            } else {
                sender.sendMessage(scheme.getBody()+ "/" + scheme.getValue() + command.getLabel().toLowerCase()
                        + scheme.getBody() + " to " + command.getAnnotation().description());
                for (int i = (page - 1) * 6; i < Math.min(arguments.size(), page * 6); i++) {
                    Command argument = arguments.get(i);
                    sender.sendMessage(scheme.getBody()+ "/" + command.getLabel().toLowerCase() + scheme.getValue()
                            + " " + (argument.display().length() > 0 ? argument.display() : argument.name())
                            + scheme.getBody() + " to " + argument.description());
                }
            }
            sender.sendMessage(MiscUtils.line(Color.Dark_Gray));
        } catch(NumberFormatException e) {
            sender.sendMessage(scheme.getError() + "The page input must be a number only!");
        }
    }

    public void registerCommand(Command command, String label, WrappedMethod method, Object clazz) {
        Command annotation = method.getMethod().getAnnotation(Command.class);

        CommandRegister cmdReg = new CommandRegister(plugin, method, clazz);

        commands.put(annotation.name().toLowerCase(), cmdReg);

        val split = ("/" + annotation.name().toLowerCase()).split("\\.");
        if(split.length > 0) {
            String[] requirements;
            if(split.length > 1) {
                requirements = new String[split.length - 1];

                System.arraycopy(split, 0, requirements, 0, requirements.length);
            } else requirements = new String[]{"/"};

            TabHandler.INSTANCE.addTabComplete(requirements, split[split.length - 1].replace("/", ""));
        }
        Arrays.stream(annotation.aliases()).forEach(alias -> commands.put(alias.toLowerCase(), cmdReg));
        Atlas.getInstance().alog(true, Color.Yellow + "Registered ancmd: " + annotation.name());

        String cmdLabel = label.split("\\.")[0].toLowerCase();

        if (map.getCommand(cmdLabel) == null) {
            SpigotCommand cmd = new SpigotCommand(cmdLabel, this, plugin);
            Arrays.stream(annotation.tabCompletions()).forEach(string -> {
                val split1 = ("/" + annotation.name().toLowerCase()).split(".");
                String[] requirements1;
                if(split1.length > 1) {
                    requirements1 = new String[split1.length - 1];

                    System.arraycopy(split1, 0, requirements1, 0, requirements1.length);
                } else requirements1 = new String[]{"/"};

                TabHandler.INSTANCE.addTabComplete(requirements1, split1[split1.length - 1]
                        .replace("/", ""));
            });

            map.register(plugin.getName(), cmd);

            registered.add(cmd);

            if(label.contains(".")) {
                String[] args = label.split(".");

                if(args.length > 1) {
                    StringBuilder completeLabel = new StringBuilder();
                    for(int i = 0 ; i < args.length - 1 ; i++) {
                        completeLabel.append(args[i]).append(".");
                    }

                    completeLabel.deleteCharAt(completeLabel.length() - 1);

                    cmd.completer.addCompleter(completeLabel.toString(), args[label.length() - 1]);
                }
            }
        }
        if (!command.description().equalsIgnoreCase("") && Objects.equals(cmdLabel, label)) {
            map.getCommand(cmdLabel).setDescription(command.description());
        }
        if (!command.usage().equalsIgnoreCase("") && Objects.equals(cmdLabel, label)) {
            map.getCommand(cmdLabel).setUsage(command.usage());
        }
    }

    @Deprecated
    public void registerCommand(Plugin plugin, Command command, String label, Method method, Object clazz) {
        registerCommand(command, label, new WrappedMethod(new WrappedClass(clazz.getClass()), method), clazz);
    }

    public void addCompletionsForCommand(String command, String...completions) {
        if(commands.containsKey(command)) {
            CommandRegister reg = commands.get(command);

            String[] split = command.split(".");
            String label = split[0];

            SpigotCommand cmd = (SpigotCommand) map.getCommand(label);

            for (String completion : completions) {
                cmd.completer.addCompleter(command, completion);
            }
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            stuff = new DontImportIfNotLatestThanks();
        }
    }
}