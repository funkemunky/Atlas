package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
@Getter
@Setter
public class WrappedWatchableObject extends NMSObject {
    private static String type = Type.WATCHABLE_OBJECT;
    private FieldAccessor<Integer> objectTypeField;
    private FieldAccessor<Integer> dataValueIdField;
    private FieldAccessor<Object> watchedObjectField;
    private FieldAccessor<Boolean> watchedField;

    private int objectType, dataValueId;
    private Object watchedObject;
    private boolean watched;

    public WrappedWatchableObject(Object object) {
        super(object);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        objectTypeField = fetchField(type, int.class, 0);
        dataValueIdField = fetchField(type, int.class, 1);
        watchedObjectField = fetchField(type, Object.class, 0);
        watchedField = fetchField(type, boolean.class, 0);
        objectType = fetch(objectTypeField);
        dataValueId = fetch(dataValueIdField);
        watchedObject = fetch(watchedObjectField);
        watched = fetch(watchedField);
    }

    public void setPacket(String packet, int type, int data, Object watchedObject) {
        Class<?> c = Reflection.getClass(Reflection.NMS_PREFIX + "." + packet);

        try {
            Object o = c.getConstructor(int.class, int.class, Object.class).newInstance(type, data, watchedObject);

            setObject(o);
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
    }
}
