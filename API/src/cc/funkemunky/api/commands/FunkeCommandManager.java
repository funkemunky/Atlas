package cc.funkemunky.api.commands;

import lombok.Getter;

import java.util.List;
import java.util.concurrent.CopyOnWriteArrayList;

@Getter
public class FunkeCommandManager {
    private final List<FunkeCommand> commands;

    public FunkeCommandManager() {
        commands = new CopyOnWriteArrayList<>();
    }

    public void addCommand(FunkeCommand command) {
        commands.add(command);
    }

    public void removeAllCommands() {
        commands.clear();
    }

    public void removeCommand(String name) {
        commands.stream().filter(cmd -> cmd.getName().equalsIgnoreCase(name)).forEach(commands::remove);
    }

    public void removeCommand(FunkeCommand command) {
        commands.remove(command);
    }
}

