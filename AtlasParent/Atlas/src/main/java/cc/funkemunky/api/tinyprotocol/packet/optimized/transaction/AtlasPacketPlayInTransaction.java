package cc.funkemunky.api.tinyprotocol.packet.optimized.transaction;

public abstract class AtlasPacketPlayInTransaction {

    protected final Object packet;

    public AtlasPacketPlayInTransaction(Object packet) {
        this.packet = packet;
    }

    public abstract int getId();

    public abstract short getAction();

    public abstract boolean isAccepted();

}
