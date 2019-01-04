package cc.funkemunky.api.commands;

import lombok.Getter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Getter
public abstract class FunkeArgument {
    private FunkeCommand parent;
    private String name, display, description;
    private List<String> aliases = new ArrayList<>();
    private Map<Integer, List<String>> tabComplete = new HashMap<>();
    private String[] permission;

    public FunkeArgument(String name, String display, String description) {
        this.name = name;
        this.display = display;
        this.description = description;
    }

    public FunkeArgument(String name, String display, String description, String... permission) {
        this.name = name;
        this.display = display;
        this.description = description;
        this.permission = permission;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }

    public void addTabComplete(int arg, String name) {
        List<String> completion = tabComplete.getOrDefault(arg, new ArrayList<>());

        completion.add(name);

        tabComplete.put(arg, completion);
    }

    public abstract void onArgument(CommandSender sender, Command cmd, String[] args);

    public FunkeCommand getParent() {
        return this.parent;
    }
}

