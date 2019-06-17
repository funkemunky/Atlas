package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import java.lang.reflect.InvocationTargetException;

@AllArgsConstructor
@Getter
@Setter
public class WrappedWatchableObject {
    private int a, b;
    private Object object, dataWatcherObject;

    public WrappedWatchableObject(Object watchable) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            FieldAccessor<Object> object = Reflection.getField(watchable.getClass(), "b", Object.class);
            FieldAccessor<Object> dObject = Reflection.getField(watchable.getClass(), "b", Object.class);
            a = b = -1;
            this.object = object.get(watchable);
            this.dataWatcherObject = dObject.get(watchable);
        } else {
            FieldAccessor<Integer> one = Reflection.getField(watchable.getClass(), int.class, 0),
                    two = Reflection.getField(watchable.getClass(), int.class, 1);
            FieldAccessor<Object> c = Reflection.getField(watchable.getClass(), Object.class, 0);

            a = one.get(watchable);
            b = two.get(watchable);
            this.object = c.get(watchable);
        }
    }

    public Object toWatchableObject() {
        try {
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                int dwoIndex = Reflection.getField(dataWatcherObject.getClass(), int.class, 0).get(dataWatcherObject);
                Object serializer = Reflection.getField(dataWatcherObject.getClass(), "b", Object.class).get(dataWatcherObject);
                return Reflection.getMinecraftClass("DataWatcher.Item").getConstructor(Reflection.getMinecraftClass("DataWatcherObject"), Object.class).newInstance(Reflection.getMinecraftClass("DataWatcherObject").getConstructor(int.class, Reflection.getMinecraftClass("DataWatcherSerializer")).newInstance(dwoIndex, serializer), object);
            } else {
                return Reflection.getMinecraftClass("WatchableObject").getConstructor(int.class, int.class, Object.class).newInstance(a, b, object);
            }
        } catch (InstantiationException | IllegalAccessException | InvocationTargetException | NoSuchMethodException e) {
            e.printStackTrace();
        }
        return null;
    }
}
