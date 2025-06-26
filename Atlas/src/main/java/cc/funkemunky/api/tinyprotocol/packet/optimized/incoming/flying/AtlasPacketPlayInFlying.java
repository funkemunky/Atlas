package cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying.versions.*;

public abstract class AtlasPacketPlayInFlying {

    protected final Object packet;

    public AtlasPacketPlayInFlying(Object packet) {
        this.packet = packet;
    }

    public abstract double getX();

    public abstract double getY();

    public abstract double getZ();

    public abstract float getYaw();

    public abstract float getPitch();

    public abstract boolean isOnGround();

    public abstract boolean isPos();

    public abstract boolean isLook();

    public static AtlasPacketPlayInFlying getFlying(Object packet) {
        switch(ProtocolVersion.getGameVersion()) {
            case V1_7_10: {
                return new v1_7R4(packet);
            }
            case V1_8:
                return new v1_8R1(packet);
            case V1_8_5:
                return new v1_8R2(packet);
            case V1_8_9:
                return new v1_8R3(packet);
            case V1_9:
            case V1_9_1:
                return new v1_9R1(packet);
            case V1_9_2:
            case V1_9_4:
                return new v1_9R2(packet);
            case V1_10:
            case V1_10_2:
                return new v1_10R1(packet);
            case V1_11:
                return new v1_11R1(packet);
            case V1_12:
            case V1_12_1:
            case V1_12_2:
                return new v1_12R1(packet);
            case V1_13:
                return new v1_13R1(packet);
            case V1_13_1:
            case V1_13_2:
                return new v1_13R2(packet);
            case V1_14:
            case V1_14_1:
            case v1_14_2:
            case v1_14_3:
            case v1_14_4:
                return new v1_14R1(packet);
            case v1_15:
            case v1_15_1:
            case v1_15_2:
                return new v1_15R1(packet);
            case v1_16:
            case v1_16_1:
                return new v1_16R1(packet);
            case v1_16_2:
            case v1_16_3:
                return new v1_16R2(packet);
            case v1_16_4:
            case v1_16_5:
                return new v1_16R3(packet);
            case v1_17:
            case v1_17_1:
            case v1_18: {
                return new v1_17_R1(packet);
            }
            default: {
                return new vReflection(packet);
            }
        }
    }
}
