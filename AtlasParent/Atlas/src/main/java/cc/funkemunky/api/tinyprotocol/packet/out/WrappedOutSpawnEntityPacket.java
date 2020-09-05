package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.GeneralWrapper;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.MathHelper;
import cc.funkemunky.api.utils.MiscUtils;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import java.util.Optional;
import java.util.UUID;

//TODO 1.15+ compatibility.
public class WrappedOutSpawnEntityPacket extends NMSObject {

    public Optional<Entity> entity = Optional.empty();
    public int entityId, type;
    public UUID uuid; //1.9+ only
    public double x, y, z; //an integer versions below 1.9
    public int pitch, yaw, data, velocityX, velocityY, velocityZ;

    private static final WrappedClass packet = Reflections.getNMSClass(Packet.Server.SPAWN_ENTITY);
    private static WrappedField fieldEntityId, fieldType, fieldUuid, fieldX, fieldY, fieldZ, fieldYaw,
            fieldPitch, fieldData, fieldVelocityX, fieldVelocityY, fieldVelocityZ;


    public WrappedOutSpawnEntityPacket(Object object, Player player) {
        super(object, player);
    }

    //TODO Make this so people can create this packet to send.
    public WrappedOutSpawnEntityPacket(Entity entity) {
        setObject(packet.getConstructor().newInstance());
        this.entityId = entity.getEntityId();
        this.entity = Optional.of(entity);
        this.uuid = entity.getUniqueId();
        this.type = entity.getType().getTypeId();
        this.x = entity.getLocation().getX();
        this.y = entity.getLocation().getY();
        this.z = entity.getLocation().getZ();
        this.pitch = MathHelper.d(entity.getLocation().getPitch() * 256.f / 360.f);
        this.yaw = MathHelper.d(entity.getLocation().getYaw() * 256.f / 360.f);

        updateObject();
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        entityId = fetch(fieldEntityId);

        //if this packet is being sent to this player, the entity will be in the same world.
        entity = player.getWorld().getEntities().stream().filter(ent -> ent.getEntityId() == entityId).findFirst();
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            x = (int)fetch(fieldX) / 32.;
            y = (int)fetch(fieldY) / 32.;
            z = (int)fetch(fieldZ) / 32.;
            entity.ifPresent(ent -> uuid = ent.getUniqueId());
        } else {
            x = fetch(fieldX);
            y = fetch(fieldY);
            z = fetch(fieldZ);
            uuid = fetch(fieldUuid);
        }
        velocityX = fetch(fieldVelocityX);
        velocityY = fetch(fieldVelocityY);
        velocityZ = fetch(fieldVelocityZ);
        pitch = fetch(fieldPitch);
        yaw = fetch(fieldYaw);
        type = fetch(fieldType);
        data = fetch(fieldData);
    }

    @Override
    public void updateObject() {
        set(fieldEntityId, entityId);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            set(fieldX, MathHelper.floor(x * 32.));
            set(fieldY, MathHelper.floor(y * 32.));
            set(fieldZ, MathHelper.floor(z * 32.));
        } else {
            set(fieldX, x);
            set(fieldY, y);
            set(fieldZ, z);
            set(fieldUuid, uuid);
        }

        set(fieldVelocityX, velocityX);
        set(fieldVelocityY, velocityY);
        set(fieldVelocityZ, velocityZ);
        set(fieldPitch, pitch);
        set(fieldYaw, yaw);
        set(fieldType, type);
        set(fieldData, data);
    }

    static {
        fieldEntityId = packet.getFieldByType(int.class, 0);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            fieldX = packet.getFieldByType(int.class, 1);
            fieldY = packet.getFieldByType(int.class, 2);
            fieldZ = packet.getFieldByType(int.class, 3);
            fieldVelocityX = packet.getFieldByType(int.class, 4);
            fieldVelocityY = packet.getFieldByType(int.class, 5);
            fieldVelocityZ = packet.getFieldByType(int.class, 6);
            fieldPitch = packet.getFieldByType(int.class, 7);
            fieldYaw = packet.getFieldByType(int.class, 8);
            fieldType = packet.getFieldByType(int.class, 9);
            fieldData = packet.getFieldByType(int.class, 10);
        } else {
            fieldUuid = packet.getFieldByType(UUID.class, 0);
            fieldX = packet.getFieldByType(double.class, 0);
            fieldY = packet.getFieldByType(double.class, 1);
            fieldZ = packet.getFieldByType(double.class, 2);
            fieldVelocityX = packet.getFieldByType(int.class, 1);
            fieldVelocityY = packet.getFieldByType(int.class, 2);
            fieldVelocityZ = packet.getFieldByType(int.class, 3);
            fieldPitch = packet.getFieldByType(int.class, 4);
            fieldYaw = packet.getFieldByType(int.class, 5);
            fieldType = packet.getFieldByType(int.class, 6);
            fieldData = packet.getFieldByType(int.class, 7);
        }
    }
}
