package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedEnumParticle;
import cc.funkemunky.api.utils.ReflectionsUtil;
import lombok.Getter;
import org.bukkit.Particle;
import org.bukkit.entity.Player;

import java.lang.reflect.InvocationTargetException;

@Getter
public class WrappedPacketPlayOutWorldParticle extends NMSObject {

    private WrappedEnumParticle type;
    private static String packetPlayOutWorldParticle = Packet.Server.WORLD_PARTICLE;
    private boolean j;
    private float x;
    private float y;
    private float z;
    private float xOffset;
    private float yOffset;
    private float zOffset;
    private float speed;
    private int amount;
    private int[] data;

    public WrappedPacketPlayOutWorldParticle(WrappedEnumParticle type, boolean var2, float x, float y, float z, float xOffset, float yOffset, float zOffset, float speed, int amount, int... data) {
        this.type = type;
        this.j = var2;
        this.x = x;
        this.y = y;
        this.z = z;
        this.xOffset = xOffset;
        this.yOffset = yOffset;
        this.zOffset = zOffset;
        this.speed = speed;
        this.amount = amount;
        this.data = data;

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            setPacket(packetPlayOutWorldParticle, type.getName(), x, y, z, xOffset, yOffset, zOffset, speed, amount);
        } else {
            setPacket(packetPlayOutWorldParticle, type.toNMS(), var2, x, y, z, xOffset, yOffset, zOffset, speed, amount, data);
        }
    }
}