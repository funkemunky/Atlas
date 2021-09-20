package cc.funkemunky.api.tinyprotocol;

import cc.funkemunky.api.reflections.types.WrappedClass;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class Parameter {
    public final WrappedClass wrappedClass;
    public final Object object;

    public Parameter(Class<?> c, Object object) {
        this.wrappedClass = new WrappedClass(c);
        this.object = object;
    }
}
