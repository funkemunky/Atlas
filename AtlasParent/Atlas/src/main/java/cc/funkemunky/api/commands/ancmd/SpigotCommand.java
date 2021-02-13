package cc.funkemunky.api.commands.ancmd;

import org.apache.commons.lang.Validate;
import org.bukkit.command.CommandException;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.plugin.Plugin;

import java.util.List;

@Deprecated
public class SpigotCommand extends org.bukkit.command.Command {

    private Plugin owningPlugin;
    protected SpigotCompleter completer;
    private CommandExecutor executor;
    private boolean notAno = false;


    public SpigotCommand(String label, CommandExecutor executor, Plugin owner) {
        super(label);
        this.executor = executor;
        this.owningPlugin = owner;
        this.usageMessage = "";
    }

    public SpigotCommand(String label, CommandExecutor executor, Plugin owner, boolean notAno) {
        super(label);
        this.executor = executor;
        this.owningPlugin = owner;
        this.usageMessage = "";
        this.notAno = notAno;
    }

    @Override
    public boolean execute(CommandSender sender, String commandLabel, String[] args) {
        if (!owningPlugin.isEnabled()) {
            return false;
        }
        if(notAno) {
            return executor.onCommand(sender, this, commandLabel, args);
        } else {
            boolean success = false;

            if (!testPermission(sender)) {
                return true;
            }

            try {
                success = executor.onCommand(sender, this, commandLabel, args);
            } catch (Throwable ex) {
                throw new CommandException("Unhandled exception executing ancmd '" + commandLabel + "' in plugin " + owningPlugin.getDescription().getFullName(), ex);
            }

            if (!success && usageMessage.length() > 0) {
                for (String line : usageMessage.replace("<ancmd>", commandLabel).split("\n")) {
                    sender.sendMessage(line);
                }
            }

            return success;
        }
    }

    @Override
    public java.util.List<String> tabComplete(CommandSender sender, String alias, String[] args) throws CommandException, IllegalArgumentException {
        Validate.notNull(sender, "Sender cannot be null");
        Validate.notNull(args, "Arguments cannot be null");
        Validate.notNull(alias, "Alias cannot be null");

        List<String> completions = null;
        try {
            if (completer != null) {
                completions = completer.onTabComplete(sender, this, alias, args);
            }
            if (completions == null && executor instanceof TabCompleter) {
                completions = ((TabCompleter) executor).onTabComplete(sender, this, alias, args);
            }
        } catch (Throwable ex) {
            StringBuilder message = new StringBuilder();
            message.append("Unhandled exception during tab completion for ancmd '/").append(alias).append(' ');
            for (String arg : args) {
                message.append(arg).append(' ');
            }
            message.deleteCharAt(message.length() - 1).append("' in plugin ")
                    .append(owningPlugin.getDescription().getFullName());
            throw new CommandException(message.toString(), ex);
        }

        if (completions == null) {
            return super.tabComplete(sender, alias, args);
        }
        return completions;
    }

}
