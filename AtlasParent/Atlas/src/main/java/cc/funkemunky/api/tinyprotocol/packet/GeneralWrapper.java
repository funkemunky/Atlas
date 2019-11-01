package cc.funkemunky.api.tinyprotocol.packet;

import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/* This class can be used for packets that do not have dedicated wrappers */
@Getter
public class GeneralWrapper extends NMSObject {
    private List<PacketField<Object>> objects = new ArrayList<>();
    private List<PacketField<Integer>> integers = new ArrayList<>();
    private List<PacketField<Long>> longs = new ArrayList<>();
    private List<PacketField<Float>> floats = new ArrayList<>();
    private List<PacketField<Byte>> bytes = new ArrayList<>();
    private List<PacketField<Double>> doubles = new ArrayList<>();
    private List<PacketField<Short>> shorts = new ArrayList<>();
    private List<PacketField<Collection>> collections = new ArrayList<>();
    private List<PacketField<Map>> maps = new ArrayList<>();
    private List<PacketField<Array>> arrays = new ArrayList<>();
    private List<PacketField<Enum>> enums = new ArrayList<>();
    private List<WrappedConstructor> constructors;
    private WrappedClass objectClass;

    public GeneralWrapper(Object object, Player player) {
        super(object, player);

        objectClass = new WrappedClass(object.getClass());
        constructors = objectClass.getConstructors();
    }


    @Override
    public void process(Player player, ProtocolVersion version) {
        objectClass.getFields().forEach(field -> {
            switch(field.getType().getSimpleName()) {
                case "Integer":
                case "int": {
                    integers.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                case "Float":
                case "float": {
                    floats.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                case "Long":
                case "long": {
                    longs.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                case "Byte":
                case "byte": {
                    bytes.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                case "Double":
                case "double": {
                    doubles.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                case "Short":
                case "short": {
                    shorts.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                case "List":
                case "ArrayList":
                case "Set":
                case "Collection":
                case "CopyOnWriteArrayList":
                case "HashSet":
                case "TreeSet":
                case "SortedSet": {
                    collections.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                case "Map":
                case "HashMap":
                case "SortedMap":
                case "WeakHashMap":
                case "TreeMap":
                case "ConcurrentHashMap": {
                    maps.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
                default: {
                    if(field.getType().isArray()) {
                        arrays.add(new PacketField<>(field, field.get(getObject())));
                    } else if(field.getType().isEnum()) {
                        enums.add(new PacketField<>(field, field.get(getObject())));;
                    } else objects.add(new PacketField<>(field, field.get(getObject())));
                    break;
                }
            }
        });
    }
}
