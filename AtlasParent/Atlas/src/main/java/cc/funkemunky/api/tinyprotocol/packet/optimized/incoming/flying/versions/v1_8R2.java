package cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying.versions;

import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying.AtlasPacketPlayInFlying;
import net.minecraft.server.v1_8_R2.PacketPlayInFlying;

public class v1_8R2 extends AtlasPacketPlayInFlying {

    public v1_8R2(Object packet) {
        super(packet);
    }

    @Override
    public double getX() {
        return getFlying().a();
    }

    @Override
    public double getY() {
        return getFlying().b();
    }

    @Override
    public double getZ() {
        return getFlying().c();
    }

    @Override
    public float getYaw() {
        return getFlying().d();
    }

    @Override
    public float getPitch() {
        return getFlying().e();
    }

    @Override
    public boolean isOnGround() {
        return getFlying().f();
    }

    @Override
    public boolean isPos() {
        return getFlying().g();
    }

    @Override
    public boolean isLook() {
        return getFlying().h();
    }

    private PacketPlayInFlying getFlying() {
        return (PacketPlayInFlying) packet;
    }
}
