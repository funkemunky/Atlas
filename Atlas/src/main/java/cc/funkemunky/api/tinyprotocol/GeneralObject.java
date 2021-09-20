package cc.funkemunky.api.tinyprotocol;

import cc.funkemunky.api.reflections.types.WrappedClass;
import lombok.*;

import java.util.Arrays;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
public class GeneralObject {

    private Object object;
    private WrappedClass wrapper;

    public GeneralObject(Object object) {
        this.wrapper = new WrappedClass(object.getClass());
    }

    public GeneralObject(WrappedClass wrapper) {
        this.wrapper = wrapper;
    }

    public <T> T wrap(Object... args) {
        val classes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);

        T instance = wrapper.getConstructor(classes).newInstance(args);
        object = instance;

        return instance;
    }
}
