package cc.funkemunky.api.commands;

import lombok.Getter;

import java.util.ArrayList;
import java.util.List;

@Getter
public class FunkeCommandManager {
    private final List<FunkeCommand> commands;

    public FunkeCommandManager() {
        commands = new ArrayList<>();
    }

    public void addCommand(FunkeCommand command) {
        commands.add(command);
    }

    public void removeAllCommands() {
        commands.clear();
    }
}

