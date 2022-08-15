package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.reflections.types.WrappedField;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class GeneralField {
    public final WrappedField field;
    private final Object object;

    public <T> T getObject() {
        return (T) object;
    }
}
