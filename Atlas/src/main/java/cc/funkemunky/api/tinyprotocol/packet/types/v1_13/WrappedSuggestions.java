package cc.funkemunky.api.tinyprotocol.packet.types.v1_13;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.GeneralObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class WrappedSuggestions extends GeneralObject {
    public static WrappedClass suggestionsClass;
    private static WrappedField suggestionListField;
    private static WrappedField sRangeField;
    private static boolean canBeUsed;

    public List<WrappedSuggestion> suggestions;
    public WrappedStringRange stringRange;

    public WrappedSuggestions(WrappedStringRange range, WrappedSuggestion... suggestions) {
        this(range, Arrays.asList(suggestions));
    }

    public WrappedSuggestions(WrappedStringRange range, List<WrappedSuggestion> suggestions) {
        super(suggestions);

        if(canBeUsed) wrap(range, suggestions.stream()
                .map(WrappedSuggestion::getObject)
                .collect(Collectors.toList()));
    }

    public WrappedSuggestions(Object object) {
        super(object, suggestionsClass);

        if(canBeUsed) {
            List<Object> suggestionObjects = suggestionListField.get(object);

            suggestions = suggestionObjects
                    .stream()
                    .map(WrappedSuggestion::new)
                    .collect(Collectors.toList());
            suggestionObjects.clear();

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

    public static class SuggestionsBuilder extends GeneralObject {
        private final String input;
        private final int start;
        private final String remaining;
        private final List<WrappedSuggestion> result = new ArrayList<>();
        private static WrappedClass wrapped;

        public SuggestionsBuilder(final String input, final int start) {
            this.input = input;
            this.start = start;
            this.remaining = input.substring(start);
        }

        public String getInput() {
            return input;
        }

        public int getStart() {
            return start;
        }

        public String getRemaining() {
            return remaining;
        }

        public WrappedSuggestions build() {
            return new WrappedSuggestions(suggestionsClass
                    .getMethod("create", String.class, List.class)
                    .invoke(null, input, result.stream()
                            .map(WrappedSuggestion::getObject)
                            .collect(Collectors.toList())));
        }

        public SuggestionsBuilder suggest(final String text) {
            if (text.equals(remaining)) {
                return this;
            }
            result.add(new WrappedSuggestion(new WrappedStringRange(start, input.length()), text));
            return this;
        }

        public SuggestionsBuilder add(final SuggestionsBuilder other) {
            result.addAll(other.result);
            return this;
        }

        public SuggestionsBuilder createOffset(final int start) {
            return new SuggestionsBuilder(input, start);
        }

        public SuggestionsBuilder restart() {
            return new SuggestionsBuilder(input, start);
        }

        static {
            if (canBeUsed) {
                wrapped = Reflections.getClass("com.mojang.brigadier.suggestion.SuggestionsBuilder");
            }
        }
    }
}
