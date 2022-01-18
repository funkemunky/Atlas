package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

public class WrappedChunkCoordIntPair extends NMSObject {
    public static final WrappedClass objectClass = Reflections.getNMSClass("ChunkCoordIntPair");
    private static final WrappedField fieldX = fetchField(objectClass, "x"),
            fieldZ = fetchField(objectClass, "z");
    private static final WrappedConstructor chunkConst = objectClass.getConstructor(int.class, int.class);

    public WrappedChunkCoordIntPair(Object object) {
        super(object);
    }

    public WrappedChunkCoordIntPair(int x, int z) {
        this.x = x;
        this.z = z;

        setObject(chunkConst.newInstance(x, z));
    }

    @Getter
    private int x, z;

    @Override
    public void updateObject() {
        //Cant be updated
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        x = fetch(fieldX);
        z = fetch(fieldZ);
    }
}
