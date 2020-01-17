package dev.brighten.example.commands;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.ancmd.Command;
import cc.funkemunky.api.commands.ancmd.CommandAdapter;
import cc.funkemunky.api.utils.Init;

@Init(commands = true)
public class ExampleCommand {

    @Command(name = "example", description = "an example command", display = "example",
            usage = "/<command>", permission = "atlas.command.example", aliases = "atlasexample")
    public void onCommand(CommandAdapter cmd) {
        Atlas.getInstance().getCommandManager().runHelpMessage(cmd, cmd.getSender(),
                Atlas.getInstance().getCommandManager().getDefaultScheme());
    }

    @Command(name = "example.execute", description = "execute a test message",
            display = "execute", usage = "/<command> <arg>",
            permission = "atlas.command.example.execute", aliases = "atlasexample.execute")
    public void onExecute(CommandAdapter cmd) {
        cmd.getSender().sendMessage("You have initiated the test command.");
    }
}
