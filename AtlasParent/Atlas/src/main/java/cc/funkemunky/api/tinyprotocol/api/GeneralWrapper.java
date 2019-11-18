package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.utils.Tuple;
import lombok.Getter;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* This class can be used for packets that do not have dedicated wrappers */
@Getter
public class GeneralWrapper {
    private List<GeneralField> fields = new ArrayList<>();
    private List<WrappedMethod> methods;
    private WrappedClass objectClass;

    public GeneralWrapper(Object object) {
        objectClass = new WrappedClass(object.getClass());
        methods = objectClass.getMethods();

        objectClass.getFields().forEach(field -> fields.add(new GeneralField(field, field.get(object))));
    }

    public <T> T build(Object... args) {
        val classes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);

        return objectClass.getConstructor(classes).newInstance(args);
    }
}
