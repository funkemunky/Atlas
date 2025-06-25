package cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.transaction;

import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.transaction.versions.*;

public abstract class AtlasPacketPlayInTransaction {

    protected final Object packet;

    public AtlasPacketPlayInTransaction(Object packet) {
        this.packet = packet;
    }

    public abstract int getId();

    public abstract short getAction();

    public abstract boolean isAccepted();

    public static AtlasPacketPlayInTransaction getTransaction(Object packet) {
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
            default: {
                return new vReflection(packet);
            }
        }
    }

}
