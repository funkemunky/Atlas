package cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying.versions;

import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying.AtlasPacketPlayInFlying;
import net.minecraft.server.v1_7_R4.PacketPlayInFlying;

public class v1_7R4 extends AtlasPacketPlayInFlying {

    public v1_7R4(Object packet) {
        super(packet);
    }

    @Override
    public double getX() {
        return getFlying().c();
    }

    @Override
    public double getY() {
        return getFlying().d();
    }

    @Override
    public double getZ() {
        return getFlying().e();
    }

    @Override
    public float getYaw() {
        return getFlying().g();
    }

    @Override
    public float getPitch() {
        return getFlying().h();
    }

    @Override
    public boolean isOnGround() {
        return getFlying().i();
    }

    @Override
    public boolean isPos() {
        return getFlying().j();
    }

    @Override
    public boolean isLook() {
        return getFlying().k();
    }

    private PacketPlayInFlying getFlying() {
        return (PacketPlayInFlying) packet;
    }
}
