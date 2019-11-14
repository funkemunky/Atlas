package cc.funkemunky.api.utils.messages;

import cc.funkemunky.api.utils.Tuple;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public abstract class Modifier {
    public final String event;
    public final String value;

    public abstract ModifierType getEvent();

    public abstract Tuple<String, String> getFormatter();
}
