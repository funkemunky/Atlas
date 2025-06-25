package cc.funkemunky.api.tinyprotocol.packet.types.v1_13;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.var;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
public class DontImportIfNotLatestThanks {

    private static WrappedClass commandDispatcherClass = Reflections.getNMSClass("CommandDispatcher");
    private Object commandDispatcher;
    private CommandDispatcher bukkitDispatcher;
    private WrappedMethod updateCommands = new WrappedClass(Player.class).getMethod("updateCommands");

    public DontImportIfNotLatestThanks() {
        commandDispatcher =
                MinecraftReflection.minecraftServer
                        .getFieldByType(commandDispatcherClass.getParent(), 0)
                        .get(CraftReflection.getMinecraftServer());

        bukkitDispatcher = commandDispatcherClass.getFieldByType(CommandDispatcher.class, 0)
                .get(commandDispatcher);
    }
    public <T> T getSuggestions(String input, String... options) {
        int start = input.startsWith("/") ? 1 : 0;

        var suggest = new SuggestionsBuilder(input, start);

        for (int i = 0; i < options.length; i++) {
            String option = options[i];

            suggest = suggest.suggest(i, new LiteralMessage(option));
        }

        return (T) suggest;
    }

    public String[] getArrayFromSuggestions(Suggestions suggestions) {
        return suggestions.getList().stream()
                .map(sug -> sug.getTooltip().getString())
                .toArray(String[]::new);
    }

    public void registerTabComplete(String... args) {
        LiteralArgumentBuilder builder = LiteralArgumentBuilder.literal(args[0]);

        for (String arg : args) {
            builder = (LiteralArgumentBuilder) builder.then(LiteralArgumentBuilder.literal(arg));
        }

        bukkitDispatcher.register(builder);

        Bukkit.getOnlinePlayers().forEach(pl -> updateCommands.invoke(pl));
    }
}
