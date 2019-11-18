package cc.funkemunky.api.tinyprotocol.packet.types.v1_13;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.GeneralObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import com.mojang.brigadier.suggestion.Suggestion;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WrappedSuggestions extends GeneralObject {
    public static WrappedClass suggestionsClass;
    private static WrappedField suggestionListField;
    private static WrappedField sRangeField;
    private static boolean canBeUsed;

    public List<WrappedSuggestions> suggestions;
    public WrappedStringRange stringRange;

    public WrappedSuggestions(WrappedStringRange range, Suggestion... suggestions) {
        this(range, Arrays.asList(suggestions));
    }

    public WrappedSuggestions(WrappedStringRange range, List<Suggestion> suggestions) {
        super(suggestions);

        if(canBeUsed) wrap(range, suggestions);
    }

    public WrappedSuggestions(Object object) {
        super(object, suggestionsClass);

        if(canBeUsed) {
            suggestions = ((List<Object>) suggestionListField.get(object))
                    .stream()
                    .map(WrappedSuggestions::new)
                    .collect(Collectors.toList());
            stringRange = new WrappedStringRange(sRangeField.get(object));
        }
    }

    static {
        if((canBeUsed = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13))) {
            suggestionsClass = Reflections.getClass("com.mojang.brigadier.suggestion.Suggestions");
            suggestionListField = suggestionsClass.getFieldByType(WrappedSuggestion.suggestionClass.getParent(), 0);
            sRangeField = suggestionsClass.getFieldByType(WrappedStringRange.srClass.getParent(), 0);
        }
    }
}
