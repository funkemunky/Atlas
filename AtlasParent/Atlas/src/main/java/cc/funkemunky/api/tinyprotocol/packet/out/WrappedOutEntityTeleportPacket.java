package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedPacketDataSerializer;
import cc.funkemunky.api.utils.MathHelper;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufAllocator;
import io.netty.buffer.PooledByteBufAllocator;
import io.netty.buffer.Unpooled;
import org.bukkit.entity.Player;

@Deprecated
public class WrappedOutEntityTeleportPacket extends NMSObject {

    private static WrappedField fieldEntityId, fieldX, fieldY, fieldZ, fieldYaw, fieldPitch, fieldOnGround;
    private static WrappedClass classEntityTeleport = Reflections.getNMSClass(Packet.Server.ENTITY_TELEPORT);
    private static WrappedConstructor emptyConstructor;

    public int entityId;
    public double x, y, z;
    public float yaw, pitch;
    public boolean onGround;

    public WrappedOutEntityTeleportPacket(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutEntityTeleportPacket(int entityId, double x, double y, double z, float yaw, float pitch,
                                          boolean onGround) {
        this.entityId = entityId;
        this.x = x;
        this.y = y;
        this.z = z;
        this.yaw = yaw;
        this.pitch = pitch;
        this.onGround = onGround;

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            setObject(emptyConstructor.newInstance());
            updateObject();
        } else {
            WrappedPacketDataSerializer buf = new WrappedPacketDataSerializer(Unpooled.buffer());
            buf.d(entityId);
            buf.writeDouble(x);
            buf.writeDouble(y);
            buf.writeDouble(z);
            buf.writeFloat(yaw);
            buf.writeFloat(pitch);
            buf.writeBoolean(onGround);

            setObject(classEntityTeleport.getConstructor(WrappedPacketDataSerializer.vanillaClass.getParent())
                    .newInstance(buf.getObject()));
        }
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        entityId = fetch(fieldEntityId);
        onGround = fetch(fieldOnGround);
        yaw = ((byte)fetch(fieldYaw)) / 256.f * 360.f;
        pitch = ((byte)fetch(fieldPitch)) / 256.f * 360.f;

        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_8_9)) {
            x = ((int)fetch(fieldX)) / 32.;
            y = ((int)fetch(fieldY)) / 32.;
            z = ((int)fetch(fieldZ)) / 32.;
        } else {
            x = fetch(fieldX);
            y = fetch(fieldY);
            z = fetch(fieldZ);
        }
    }

    @Override
    public void updateObject() {
        set(fieldEntityId, entityId);
        set(fieldYaw, (byte)((int)(yaw * 256.0F / 360.0F)));
        set(fieldPitch, (byte)((int)(pitch * 256.0F / 360.0F)));
        set(fieldOnGround, onGround);

        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_8_9)) {
            set(fieldX, MathHelper.floor_double(x * 32.));
            set(fieldY, MathHelper.floor_double(y * 32.));
            set(fieldZ, MathHelper.floor_double(z * 32.));
        } else {
            set(fieldX, x);
            set(fieldY, y);
            set(fieldZ, z);
        }
    }

    static {
        fieldEntityId = fetchField(classEntityTeleport, int.class, 0);
        fieldYaw = fetchField(classEntityTeleport, byte.class, 0);
        fieldPitch = fetchField(classEntityTeleport, byte.class, 1);
        fieldOnGround = fetchField(classEntityTeleport, boolean.class, 0);

        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_8_9)) {
            fieldX = fetchField(classEntityTeleport, int.class, 1);
            fieldY = fetchField(classEntityTeleport, int.class, 2);
            fieldZ = fetchField(classEntityTeleport, int.class, 3);
        } else {
            fieldX = fetchField(classEntityTeleport, double.class, 0);
            fieldY = fetchField(classEntityTeleport, double.class, 1);
            fieldZ = fetchField(classEntityTeleport, double.class, 2);
        }
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            emptyConstructor = classEntityTeleport.getConstructor();
        }
    }
}
