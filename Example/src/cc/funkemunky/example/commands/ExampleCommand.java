package cc.funkemunky.example.commands;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.ancmd.Command;
import cc.funkemunky.api.commands.ancmd.CommandAdapter;
import cc.funkemunky.api.utils.Init;

@Init(commands = true) //This is needed to signify to the class scanner we implemented in the main class that there are commands to register here.
public class ExampleCommand {

    //Runs the help message for the main commmand "/command".
    @Command(name = "command", display = "Example Command", description = "show the help page.", permission = "cmd.perm", aliases = "aCommand")
    public void onCommand(CommandAdapter command) {
        Atlas.getInstance().getCommandManager().runHelpMessage(command, command.getSender(), Atlas.getInstance().getCommandManager().getDefaultScheme());
    }

    //One of the arguments for "/command". Run as "/command arg1".
    @Command(name = "command.arg1", display = "arg1", permission = "cmd.perm.arg1", description = "view a test argument in action.")
    public void onCommandArg(CommandAdapter command) {
        command.getSender().sendMessage("You can an argument of the example command.");
    }
}
