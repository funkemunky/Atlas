package dev.brighten.example.commands;

import cc.funkemunky.api.commands.ancmd.Command;
import cc.funkemunky.api.commands.ancmd.CommandAdapter;
import cc.funkemunky.api.handlers.chat.ChatHandler;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.ItemBuilder;
import cc.funkemunky.api.utils.menu.button.Button;
import cc.funkemunky.api.utils.menu.type.impl.PagedMenu;
import org.bukkit.Location;
import org.bukkit.Material;

@Init(commands = true)
public class BlockDebugCommand {

    @Command(name = "setblock", aliases = "asetblock", description = "Set a block.", playerOnly = true)
    public void onCommand(CommandAdapter cmd) {
        cmd.getSender().sendMessage(Color.Green + "Type the coordinates to set. Type \"cancel\" to cancel.");
        ChatHandler.onChat(cmd.getPlayer(), false, (chat, message) -> {
            switch(message.toLowerCase()) {
                case "cancel":
                case "stop": {
                    ChatHandler.remove(cmd.getPlayer(), chat);
                    cmd.getSender().sendMessage(Color.Red + "Canceled");
                    break;
                }
                default: {
                    String[] split = message.split(",");

                    if(split.length >= 3) {
                        Location vector = null;
                        try {
                            vector = new Location(cmd.getPlayer().getWorld(),
                                    Integer.parseInt(split[0]),
                                    Integer.parseInt(split[1]),
                                    Integer.parseInt(split[2]));

                            cmd.getSender().sendMessage(Color.translate("&cSet location to &f" + message
                                    .replace(",", "&7,&f")));
                        } catch(Exception e) {
                            cmd.getSender().sendMessage(Color.Red + "There was an error parsing the coords.");
                        }
                        if(vector == null) return;
                        Location loc = vector;
                        PagedMenu menu = new PagedMenu("Choose a block.", 6);

                        for (Material value : (Material[]) new WrappedClass(Material.class)
                                .getMethod("values").invoke(null)) {
                            menu.addItem(new Button(false, new ItemBuilder(value).amount(1).build(),
                                    (player, info) -> {
                                        loc.getBlock().setType(info.getButton().getStack().getType());
                                        cmd.getPlayer().sendMessage(Color.translate(
                                                "Set block at " + message + " to " + value.name()));
                                    }));
                        }
                        menu.showMenu(cmd.getPlayer());
                    } else cmd.getSender().sendMessage(Color.Red + "There was an error parsing the coords.");
                    break;
                }
            }
        });

    }
}
