package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;

public class WrappedChunkCoordIntPair extends NMSObject {

    private static WrappedClass vanillaClass = Reflections.getClass(Packet.Type.CHUNKCOORDINTPAIR);

    private static WrappedField fieldX = fetchField(vanillaClass, int.class, 0),
            fieldZ = fetchField(vanillaClass, int.class, 1);
    private static WrappedConstructor constructor = vanillaClass.getConstructor(int.class, int.class);

    public WrappedChunkCoordIntPair(Object object) {
        super(object);
    }

    public int x, z;

    public WrappedChunkCoordIntPair(int x, int z) {
        super((Object)constructor.newInstance(x, z));
        this.x = x;
        this.z = z;
    }

    @Override
    public void updateObject() {
        fieldX.set(getObject(), x);
        fieldZ.set(getObject(), z);
    }
}
