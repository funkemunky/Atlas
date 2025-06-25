package dev.brighten.example.commands;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.commands.ancmd.Command;
import cc.funkemunky.api.commands.ancmd.CommandAdapter;
import cc.funkemunky.api.utils.Color;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.api.utils.MiscUtils;
import cc.funkemunky.api.utils.Tuple;
import lombok.val;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.HoverEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.entity.Entity;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

@Init(commands = true)
public class ExampleCommand {

    @Command(name = "example", description = "an example command", display = "example",
            usage = "/<command>", permission = "atlas.command.example", aliases = "atlasexample")
    public void onCommand(CommandAdapter cmd) {
        Atlas.getInstance().getCommandManager(Atlas.getInstance()).runHelpMessage(cmd, cmd.getSender(),
                Atlas.getInstance().getCommandManager(Atlas.getInstance()).getDefaultScheme());
    }

    @Command(name = "example.execute", description = "execute a test message",
            display = "execute", usage = "/<command> <arg>",
            permission = "atlas.command.example.execute", aliases = "atlasexample.execute")
    public void onExecute(CommandAdapter cmd) {
        cmd.getSender().sendMessage(Color.translate("#9F8DFAYou have initiated the test command."));
    }

    @Command(name = "testjson", playerOnly = true)
    public void onTestJson(CommandAdapter cmd) {
        Player sender = cmd.getPlayer();

        List<TextComponent> components = new ArrayList<>();
        TextComponent types = new TextComponent(Color.translate(sender.getWorld().getEntities().stream()
                .map(ent -> ent.getType().getName())
                .filter(Objects::nonNull)
                .sorted(Comparator.comparing(v -> v))
                .map(n -> Color.Yellow + MiscUtils.injectColor(n, Color.Yellow))
                .collect(Collectors.joining("&8, &e")) + "&7: "));

        components.add(types);

        List<Tuple<Entity, String>> names = new ArrayList<>();
        sender.getWorld().getEntities().stream().sorted(Comparator.comparing(ent -> ent.getType().name()))
                .map(ent -> new Tuple<>(ent, ent.getName())).forEach(names::add);

        for (int i = 0; i < names.size(); i++) {
            Tuple<Entity, String> tuple = names.get(i);
            Entity ent = tuple.one;

            String name = ent.getName();

            boolean livingEntity = ent instanceof LivingEntity;
            val component = new TextComponent(Color.Gray + MiscUtils.injectColor(Color.strip(name), Color.Gray)
                    + (i + 1 < names.size() ? Color.Dark_Gray + ", " + Color.Gray : ""));

            if(livingEntity) {
                LivingEntity living = (LivingEntity) ent;
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                                new TextComponent(Color.translate("&eType&7: &f" + ent.getType().name())),
                                new TextComponent(Color.translate("\n&eLiving&7: &ftrue")),
                                new TextComponent(Color.translate("\n&eHealth&7: &f" + living.getHealth()))}));
            } else {
                component.setHoverEvent(new HoverEvent(HoverEvent.Action.SHOW_TEXT,
                        new BaseComponent[]{
                                new TextComponent(Color.translate("\n&eType&7: &f" + ent.getType().name())),
                                new TextComponent(Color.translate("\n&eLiving&7: &ffalse"))}));
            }

            components.add(component);
        }

        sender.spigot().sendMessage(components.stream().toArray(TextComponent[]::new));
    }
}
