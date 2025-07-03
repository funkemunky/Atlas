package cc.funkemunky.api.commands.impl.args;

import cc.funkemunky.api.bungee.BungeeAPI;
import cc.funkemunky.api.commands.ancmd.Command;
import cc.funkemunky.api.commands.ancmd.CommandAdapter;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.Priority;

@Init(commands = true,priority = Priority.LOW)
public class BungeeCommand {

    @Command(name = "bungeecmd", display = "bungee [args]", description = "send command to bungee",
            permission = "atlas.command.bungee")
    public void onCommand(CommandAdapter cmd) {
        BungeeAPI.sendCommand(String.join(" ", cmd.getArgs()));
    }
}
