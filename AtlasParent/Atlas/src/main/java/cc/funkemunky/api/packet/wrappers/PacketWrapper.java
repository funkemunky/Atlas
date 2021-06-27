package cc.funkemunky.api.packet.wrappers;

import org.bson.ByteBuf;

public abstract class PacketWrapper {
    protected ByteBuf byteBuf;

    public PacketWrapper(ByteBuf byteBuf) {
        this.byteBuf = byteBuf;
    }
}
