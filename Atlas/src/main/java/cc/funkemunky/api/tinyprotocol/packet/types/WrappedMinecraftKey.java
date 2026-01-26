package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

public class WrappedMinecraftKey extends NMSObject {

    public static WrappedClass vanilla = Reflections.getNMSClass("MinecraftKey");

    public WrappedMinecraftKey(Object object) {
        super(object);
    }

    public String namespace = "N/A", key = "N/A";

    private static WrappedField fieldNameSpace = fetchField(vanilla, String.class, 0),
            fieldKey = fetchField(vanilla, String.class, 1);

    @Override
    public void process(Player player, ProtocolVersion version) {
        namespace = fetch(fieldNameSpace);
        key = fetch(fieldKey);
    }

    @Override
    public void updateObject() {
        set(fieldNameSpace, namespace);
        set(fieldKey, key);
    }
}
