package cc.funkemunky.api.tinyprotocol.packet.types.v1_13;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.GeneralObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;

//Add Message object for tooltip functionality later.
public class WrappedSuggestion extends GeneralObject {
    public static WrappedClass suggestionClass;
    private static WrappedField sRangeAccessor;
    private static WrappedField textAccessor;
    private static boolean canBeUsed;

    public WrappedStringRange range;
    public String text;

    public WrappedSuggestion(WrappedStringRange range, String text) {
        super(suggestionClass);

        this.range = range;
        this.text = text;

        if(canBeUsed) wrap(range.getObject(), text);
    }

    public WrappedSuggestion(String string) {
        super(suggestionClass);

        this.range = new WrappedStringRange(0, string.length());
        this.text = string;

        if(canBeUsed) wrap(range.getObject(), text);
    }

    public WrappedSuggestion(Object object) {
        super(object, suggestionClass);

        range = new WrappedStringRange(sRangeAccessor.get(object));
        text = textAccessor.get(object);
    }

    public WrappedSuggestion build() {
        return wrap(range.getObject(), text);
    }

    static {
        if((canBeUsed = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13))) {
            suggestionClass = Reflections.getClass("com.mojang.brigadier.suggestion.Suggestion");
            sRangeAccessor = suggestionClass.getFieldByType(WrappedStringRange.srClass.getParent(), 0);
            textAccessor = suggestionClass.getFieldByType(String.class, 0);
        }
    }
}
