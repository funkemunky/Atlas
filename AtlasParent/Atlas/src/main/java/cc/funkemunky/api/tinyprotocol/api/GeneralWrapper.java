package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.val;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/* This class can be used for packets that do not have dedicated wrappers or change constantly between versions. */
@Getter
@RequiredArgsConstructor
public class GeneralWrapper extends NMSObject {
    private final WrappedClass objectClass;
    private List<WrappedField> fields;
    private List<WrappedMethod> methods;
    public GeneralWrapper(Object object) {
        super(object);
        objectClass = new WrappedClass(getObject().getClass());
        fields = objectClass.getFields();
        methods = objectClass.getMethods();
    }

    public GeneralWrapper(Object object, Player player) {
        super(object, player);
        objectClass = new WrappedClass(getObject().getClass());
        fields = objectClass.getFields();
        methods = objectClass.getMethods();
    }

    @Override
    public void process(Player player, ProtocolVersion version) {

    }

    @Override
    public void updateObject() {

    }

    public WrappedField getField(int index) {
        if(index < 0 || index >= fields.size()) {
            throw new IndexOutOfBoundsException("index (" + index
                    + ") is out of range (0, " + (fields.size() - 1) + ")");
        }

        return fields.get(index);
    }

    public <T> T read(Class<T> type, int index) {
        return objectClass.getFieldByType(type, index).get(getObject());
    }


    public <T> T build(Object... args) {
        val classes = Arrays.stream(args).map(Object::getClass).toArray(Class<?>[]::new);

        return objectClass.getConstructor(classes).newInstance(args);
    }


}
