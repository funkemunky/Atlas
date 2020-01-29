package cc.funkemunky.api.tinyprotocol.packet.types.v1_13;

import com.mojang.brigadier.LiteralMessage;
import com.mojang.brigadier.suggestion.Suggestions;
import com.mojang.brigadier.suggestion.SuggestionsBuilder;
import lombok.var;

public class DontImportIfNotLatestThanks {

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
}
