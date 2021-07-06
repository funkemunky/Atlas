package cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.transaction.versions;

import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.transaction.AtlasPacketPlayInTransaction;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import net.minecraft.server.v1_9_R1.PacketPlayInTransaction;

public class v1_9R1 extends AtlasPacketPlayInTransaction {

    public v1_9R1(Object packet) {
        super(packet);
    }

    //This field doesn't have a public method within the object
    private static final FieldAccessor<Boolean> getAccepted =
            Reflection.getField(PacketPlayInTransaction.class, boolean.class, 0);

    @Override
    public int getId() {
        return getTransaction().a();
    }

    @Override
    public short getAction() {
        return getTransaction().b();
    }

    @Override
    public boolean isAccepted() {
        return getAccepted.get(getTransaction());
    }

    private PacketPlayInTransaction getTransaction() {
        return (PacketPlayInTransaction) packet;
    }
}
