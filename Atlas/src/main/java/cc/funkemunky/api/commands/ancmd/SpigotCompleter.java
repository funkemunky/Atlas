package cc.funkemunky.api.commands.ancmd;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Deprecated
public class SpigotCompleter implements TabCompleter {

    private Map<String, List<String>> completers = new HashMap<>();

    public void addCompleter(String label, String completer) {
        List<String> completers = this.completers.getOrDefault(label, new ArrayList<>());

        completers.add(completer);

        this.completers.put(label, completers);
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
                return completers.get(cmdLabel);
            }
        }
        return null;
    }

}