package cc.funkemunky.api.commands.ancmd;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

@Getter
@Setter
@AllArgsConstructor
public class CommandAdapter {
    private CommandSender sender;
    private Command command;
    private Player player;
    private String[] args;
}
