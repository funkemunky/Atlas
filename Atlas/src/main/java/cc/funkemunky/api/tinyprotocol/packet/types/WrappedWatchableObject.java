package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@AllArgsConstructor
@Getter
@Setter
public class WrappedWatchableObject extends NMSObject {
    private static String type = Type.WATCHABLE_OBJECT;
    private static FieldAccessor<Integer> firstIntField;
    private static FieldAccessor<Integer> dataValueIdField;
    private static FieldAccessor<Object> dataWatcherObjectField;
    private static FieldAccessor<Integer> dataWatcherObjectIdField;
    private static FieldAccessor<Object> dataSerializerField;
    private static FieldAccessor<Object> watchedObjectField;
    private static FieldAccessor<Boolean> watchedField;
    private static WrappedClass c = Reflections.getNMSClass(type);

    private int firstInt, dataValueId;
    private Object watchedObject, dataWatcherObject, serializer;
    private boolean watched;

    public WrappedWatchableObject(Object object) {
        super(object);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            firstInt = fetch(firstIntField);
            dataValueId = fetch(dataValueIdField);
        } else {
            firstInt = -1;
            dataWatcherObject = fetch(dataWatcherObjectField);
            dataValueId = dataWatcherObjectIdField.get(dataWatcherObject);
            serializer = dataSerializerField.get(dataWatcherObject);
        }
        watchedObject = fetch(watchedObjectField);
        watched = fetch(watchedField);
    }

    //For 1.8.9 and below.
    public void setPacket(int type, int data, Object watchedObject) {
        Object o = c.getConstructor(int.class, int.class, Object.class).newInstance(type, data, watchedObject);

        setObject(o);
    }

    //For 1.9 and above.
    public void setPacket(Object serializer, int data, Object watchedObject) {
        WrappedClass dwoC = Reflections.getNMSClass("DataWatcherObject"),
                dwsC = Reflections.getNMSClass("DataWatcherSerializer");

        setPacket(c.getConstructor(dwoC.getParent(), Object.class)
                .newInstance(
                        dwoC.getConstructor(int.class, dwsC.getParent())
                                .newInstance(data, serializer),
                        watchedObject));
    }

    //For 1.9 and above.
    public void setPacket(Object dataWatcherObject, Object watchedObject) {
        setObject(c.getConstructor(dataWatcherObject.getClass(), Object.class)
                .newInstance(dataWatcherObject, watchedObject));
    }

    @Override
    public void updateObject() {

    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            firstIntField = fetchField(type, int.class, 0);
            dataValueIdField = fetchField(type, int.class, 1);
            watchedObjectField = fetchField(type, Object.class, 0);
        } else {
            dataWatcherObjectField = fetchField(type, Object.class, 0);
            watchedObjectField = fetchField(type, Object.class, 1);
            dataWatcherObjectIdField = fetchField("DataWatcherObject", int.class, 0);
            dataSerializerField = fetchField("DataWatcherObject", Object.class, 0);
        }
        watchedField = fetchField(type, boolean.class, 0);
    }
}
