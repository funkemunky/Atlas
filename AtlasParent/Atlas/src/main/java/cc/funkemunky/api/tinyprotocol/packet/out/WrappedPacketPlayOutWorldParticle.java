package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.PacketType;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumParticle;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedPacketPlayOutWorldParticle extends NMSObject {

    private WrappedEnumParticle type;
    private static WrappedClass craftParticle, packet, particleClass;
    private static WrappedMethod toNMS;
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

    private static WrappedField fieldType, fieldBoolean, fieldX, fieldY, fieldZ,
            fieldXOffset, fieldYOffset, fieldZOffset, fieldSpeed, fieldAmount;

    static {
        packet = Reflections.getNMSClass(PacketType.Server.WORLD_PARTICLE.vanillaName);

        fieldX = fetchField(packet, float.class, 0);
        fieldY = fetchField(packet, float.class, 1);
        fieldZ = fetchField(packet, float.class, 2);
        fieldXOffset = fetchField(packet, float.class, 3);
        fieldYOffset = fetchField(packet, float.class, 4);
        fieldZOffset = fetchField(packet, float.class, 5);
        fieldSpeed = fetchField(packet, float.class, 6);
        fieldAmount = fetchField(packet, int.class, 0);
        fieldBoolean = fetchField(packet, boolean.class, 0);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldType = fetchField(packet, String.class, 0);
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            particleClass = Reflections.getNMSClass("EnumParticle");
            fieldType = fetchField(packet, particleClass.getParent(), 0);
        } else {
            particleClass = Reflections.getNMSClass("Particle");
            fieldType = fetchField(packet, particleClass.getParent(), 0);
        }
    }

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

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            setPacket(packetPlayOutWorldParticle, type.getName().toLowerCase(), x, y, z, xOffset, yOffset, zOffset, speed, amount);
        } else if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            setPacket(packetPlayOutWorldParticle, type.toNMS(), var2, x, y, z, xOffset, yOffset, zOffset, speed, amount, data);
        } else {
            setPacket(packetPlayOutWorldParticle, x, y, z, xOffset, yOffset, zOffset, speed, amount,
                    var2, type.toNMS());
        }
    }

    public WrappedPacketPlayOutWorldParticle(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        type = WrappedEnumParticle.getByName(((Enum)fetch(fieldType)).name());
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        xOffset = fetch(fieldXOffset);
        yOffset = fetch(fieldYOffset);
        zOffset = fetch(fieldZOffset);
        speed = fetch(fieldSpeed);
        amount = fetch(fieldAmount);
        j = fetch(fieldBoolean);
    }

    @Override
    public void updateObject() {

    }
}