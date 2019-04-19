package cc.funkemunky.api.commands.ancmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.AbstractMap;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

public class SpigotCompleter implements TabCompleter {

    private Map<String, Entry<Method, Object>> completers = new HashMap<String, Entry<Method, Object>>();

    public void addCompleter(String label, Method m, Object obj) {
        completers.put(label, new AbstractMap.SimpleEntry<Method, Object>(m, obj));
    }

    @SuppressWarnings("unchecked")
    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args) {
        for (int i = args.length; i >= 0; i--) {
            StringBuffer buffer = new StringBuffer();
            buffer.append(label.toLowerCase());
            for (int x = 0; x < i; x++) {
                if (!args[x].equals("") && !args[x].equals(" ")) {
                    buffer.append("." + args[x].toLowerCase());
                }
            }
            String cmdLabel = buffer.toString();
            if (completers.containsKey(cmdLabel)) {
                Entry<Method, Object> entry = completers.get(cmdLabel);
                try {
                    return (List<String>) entry.getKey().invoke(entry.getValue(),
                            new CommandAdapter(sender, command, sender instanceof Player ? (Player) sender : null, label, null, args));
                } catch (IllegalArgumentException | IllegalAccessException | InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
        }
        return null;
    }

}