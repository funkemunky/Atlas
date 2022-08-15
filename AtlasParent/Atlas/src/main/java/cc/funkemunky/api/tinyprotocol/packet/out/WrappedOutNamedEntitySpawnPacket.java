package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.MathHelper;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedGameProfile;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.UUID;

//TODO Add the ability to use this wrapper to spawn a fake entity and implement other field support.
public class WrappedOutNamedEntitySpawnPacket extends NMSObject {

    public WrappedOutNamedEntitySpawnPacket(Object object, Player player) {
        super(object, player);
    }

    private static WrappedClass packet = Reflections.getNMSClass(Server.NAMED_ENTITY_SPAWN);

    public int entityId;
    public UUID uuid;
    public double x, y, z;
    public byte yaw, pitch;
    public int currentItem;

    private static final WrappedField fieldEntityId, fieldUuid, fieldX, fieldY, fieldZ, fieldYaw, fieldPitch,
            fieldCurrentItem;


    @Override
    public void process(Player player, ProtocolVersion version) {
        entityId = fetch(fieldEntityId);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            uuid = new WrappedGameProfile(fetch(fieldUuid)).id;
        } else uuid = fetch(fieldUuid);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            currentItem = fetch(fieldCurrentItem);
            x = (int)fetch(fieldX) / 32.;
            y = (int)fetch(fieldY) / 32.;
            z = (int)fetch(fieldZ) / 32.;
        } else {
            x = fetch(fieldX);
            y = fetch(fieldY);
            z = fetch(fieldZ);
        }
    }

    @Override
    public void updateObject() {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            Player player = Bukkit.getPlayer(uuid);
            set(fieldUuid, player != null ? new WrappedGameProfile(player) : null);
        } else set(fieldUuid, uuid);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            set(fieldCurrentItem, currentItem);
            set(fieldX, MathHelper.floor(x * 32.));
            set(fieldY, MathHelper.floor(y * 32.));
            set(fieldZ, MathHelper.floor(z * 32.));
        } else {
            set(fieldX, x);
            set(fieldY, y);
            set(fieldZ, z);
        }
    }

    static {
        fieldEntityId = fetchField(packet, int.class, 0);
        fieldYaw = fetchField(packet, byte.class, 0);
        fieldPitch = fetchField(packet, byte.class, 1);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldUuid = fetchField(packet, MinecraftReflection.gameProfile.getParent(), 0);
        } else fieldUuid = fetchField(packet, UUID.class, 0);

        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            fieldX = fetchField(packet, int.class, 1);
            fieldY = fetchField(packet, int.class, 2);
            fieldZ = fetchField(packet, int.class, 3);
            fieldCurrentItem = fetchField(packet, int.class, 4);
        } else {
            fieldX = fetchField(packet, double.class, 0);
            fieldY = fetchField(packet, double.class, 1);
            fieldZ = fetchField(packet, double.class, 2);
            fieldCurrentItem = null;
        }
    }
}