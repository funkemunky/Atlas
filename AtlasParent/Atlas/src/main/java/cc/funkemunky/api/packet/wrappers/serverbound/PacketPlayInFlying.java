package cc.funkemunky.api.packet.wrappers.serverbound;

import cc.funkemunky.api.packet.wrappers.PacketWrapper;
import org.bson.ByteBuf;

public class PacketPlayInFlying extends PacketWrapper {

    public PacketPlayInFlying(ByteBuf byteBuf) {
        super(byteBuf);
    }
}
