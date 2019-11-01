package cc.funkemunky.api.tinyprotocol.packet;

import cc.funkemunky.api.reflections.types.WrappedField;
import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class PacketField<T> {
    private WrappedField field;
    private T value;
}
