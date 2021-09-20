package cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.transaction.versions;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.transaction.AtlasPacketPlayInTransaction;

public class vReflection extends AtlasPacketPlayInTransaction {

    private static final WrappedClass transactionClass = Reflections.getNMSClass(Packet.Client.TRANSACTION);
    private static final WrappedField getWindowId = transactionClass.getFieldByType(int.class, 0),
            getActionNumber = transactionClass.getFieldByType(short.class, 0),
            getAccepted = transactionClass.getFieldByType(boolean.class, 0);

    public vReflection(Object packet) {
        super(packet);
    }

    @Override
    public int getId() {
        return getWindowId.get(packet);
    }

    @Override
    public short getAction() {
        return getActionNumber.get(packet);
    }

    @Override
    public boolean isAccepted() {
        return getAccepted.get(packet);
    }
}
