package cc.funkemunky.api.tinyprotocol.packet.optimized.flying.versions;

import cc.funkemunky.api.tinyprotocol.packet.optimized.flying.AtlasPacketPlayInFlying;
import net.minecraft.server.v1_14_R1.PacketPlayInFlying;

public class v1_14R1 extends AtlasPacketPlayInFlying {

    public v1_14R1(Object packet) {
        super(packet);
    }

    @Override
    public double getX() {
        return getFlying().a(0D);
    }

    @Override
    public double getY() {
        return getFlying().b(0D);
    }

    @Override
    public double getZ() {
        return getFlying().c(0D);
    }

    @Override
    public float getYaw() {
        return getFlying().a(0f);
    }

    @Override
    public float getPitch() {
        return getFlying().b(0f);
    }

    @Override
    public boolean isOnGround() {
        return getFlying().b();
    }

    @Override
    public boolean isPos() {
        return getFlying() instanceof PacketPlayInFlying.PacketPlayInPositionLook
                || getFlying() instanceof PacketPlayInFlying.PacketPlayInPosition;
    }

    @Override
    public boolean isLook() {
        return getFlying() instanceof PacketPlayInFlying.PacketPlayInLook
                || getFlying() instanceof PacketPlayInFlying.PacketPlayInPositionLook;
    }

    private PacketPlayInFlying getFlying() {
        return (PacketPlayInFlying) packet;
    }
}
