package cc.funkemunky.api.commands;

import lombok.Getter;
import lombok.val;
import org.bukkit.plugin.Plugin;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Deprecated
public class FunkeCommandManager {
    private final Map<Plugin, List<FunkeCommand>> commands;

    public FunkeCommandManager() {
        commands = new ConcurrentHashMap<>();
    }

    public void addCommand(Plugin plugin, FunkeCommand command) {
        val list = commands.getOrDefault(plugin, new ArrayList<>());

        list.add(command);

        commands.put(plugin, list);
    }

    public void removeAllCommands() {
        commands.clear();
    }

    public void removeCommand(String name) {
        commands.keySet().forEach(key -> {
            val list = commands.get(key);

            list.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(name)).forEach(list::remove);
        });
    }

    public void removeAll(Plugin plugin) {
        commands.remove(plugin);
    }

    public void removeCommand(FunkeCommand command) {
        commands.keySet().stream().filter(key -> commands.get(key).contains(command)).forEach(key -> {
            val list = commands.get(key);

            list.remove(command);

            commands.put(key, list);
        });
    }
}

