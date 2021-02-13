package cc.funkemunky.api.commands;

import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

import java.util.*;

@Getter
@Setter
@Deprecated
public abstract class FunkeArgument {
    private FunkeCommand parent;
    private String name, display, description;
    private boolean playerOnly;
    private List<String> aliases = new ArrayList<>();
    private Map<Integer, List<String>> tabComplete = new HashMap<>();
    private String[] permission;

    public FunkeArgument(FunkeCommand parent, String name, String display, String description) {
        this.parent = parent;
        this.name = name;
        this.display = display;
        this.description = description;

        playerOnly = false;
    }

    public FunkeArgument(FunkeCommand parent, String name, String display, String description, String... permission) {
        this.parent = parent;
        this.name = name;
        this.display = display;
        this.description = description;
        this.permission = permission;
        playerOnly = false;
    }

    public void addAlias(String alias) {
        aliases.add(alias);
    }

    public void addTabComplete(int arg, String... name) {
        List<String> completion = tabComplete.getOrDefault(arg, new ArrayList<>());

        completion.addAll(Arrays.asList(name));

        tabComplete.put(arg, completion);
    }

    public abstract void onArgument(CommandSender sender, Command cmd, String[] args);

    public FunkeCommand getParent() {
        return this.parent;
    }
}

