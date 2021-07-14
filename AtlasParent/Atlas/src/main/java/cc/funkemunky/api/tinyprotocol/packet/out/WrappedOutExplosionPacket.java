package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

/**
 * PacketPlayOutExplosion
 * Compatible with 1.7.10-1.17
 **/
@Getter
@Setter
public class WrappedOutExplosionPacket extends NMSObject {

    private static final WrappedClass packetClass = Reflections.getNMSClass(Packet.Server.EXPLOSION);
    private static final WrappedField fieldX = fetchField(packetClass, double.class, 0),
            fieldY = fetchField(packetClass, double.class, 1),
            fieldZ = fetchField(packetClass, double.class, 2),
            fieldRadius = fetchField(packetClass, float.class, 0),
            fieldBlockRecords = fetchField(packetClass, List.class, 0),
            fieldMotionX = fetchField(packetClass, float.class, 1),
            fieldMotionY = fetchField(packetClass, float.class, 2),
            fieldMotionZ = fetchField(packetClass, float.class, 3);

    private double x, y, z;
    private final List<BaseBlockPosition> blockRecords = new ArrayList<>();
    private float radius, motionX, motionY, motionZ;

    public WrappedOutExplosionPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        radius = fetch(fieldRadius);
        motionX = fetch(fieldMotionX);
        motionY = fetch(fieldMotionY);
        motionZ = fetch(fieldMotionZ);

        final List<Object> blockRecordObjects = fetch(fieldBlockRecords);

        for (Object blockRecordObject : blockRecordObjects) {
            blockRecords.add(new BaseBlockPosition(blockRecordObject));
        }
    }

    @Override
    public void updateObject() {
        set(fieldX, x);
        set(fieldY, y);
        set(fieldZ, z);
        set(fieldRadius, radius);
        set(fieldMotionX, fieldMotionX);
        set(fieldMotionY, fieldMotionY);
        set(fieldMotionZ, fieldMotionZ);

        final List<Object> objects = new ArrayList<>();

        for (BaseBlockPosition blockRecord : blockRecords) {
            objects.add(blockRecord.getAsBlockPosition());
        }

        set(fieldBlockRecords, objects);
    }
}
