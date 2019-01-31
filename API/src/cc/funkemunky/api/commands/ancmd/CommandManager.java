package cc.funkemunky.api.commands.ancmd;

import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.Getter;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandMap;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.SimplePluginManager;

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
        List<String> toFormatArgs = Arrays.asList(strings);
        toFormatArgs.add(0, label);
        String[] beforeArgs = (String[]) toFormatArgs.toArray();

        for(int arg = beforeArgs.length; arg >= 1 ; arg--) {
            StringBuffer buffer = new StringBuffer();
            for (int x = 0; x < arg; x++) {
                buffer.append("." + beforeArgs[x].toLowerCase());
            }

            if(commands.containsKey(buffer.toString())) {
                Map.Entry<Method, Object> entry = commands.get(buffer.toString());

                Command command = entry.getKey().getAnnotation(Command.class);

                String[] args = beforeArgs.length > 1 ? Arrays.copyOfRange(beforeArgs, buffer.toString().split(" ").length, beforeArgs.length) : new String[0];

                if(Arrays.stream(command.permission()).anyMatch(sender::hasPermission)) {
                    CommandAdapter adapter = new CommandAdapter(sender, cmd, sender instanceof Player ? (Player) sender : null, args);
                    try {
                        entry.getKey().invoke(entry.getValue(), adapter);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    sender.sendMessage(Color.translate(command.noPermissionMessage()));
                }
                break;
            }
        }
        return true;
    }

    public void registerCommand(Command command, String label, Method method, Object clazz) {
        Command annotation = method.getAnnotation(Command.class);

        commands.put(annotation.name().toLowerCase(), new AbstractMap.SimpleEntry<>(method, clazz));

        Arrays.stream(annotation.aliases()).forEach(alias -> commands.put(alias.toLowerCase(), new AbstractMap.SimpleEntry<>(method, clazz)));
        MiscUtils.printToConsole(Color.Yellow + "Registered ancmd: " + annotation.name());

        String cmdLabel = label.split("\\.")[0].toLowerCase();
        if (map.getCommand(cmdLabel) == null) {
            org.bukkit.command.Command cmd = new SpigotCommand(cmdLabel, this, plugin);
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
