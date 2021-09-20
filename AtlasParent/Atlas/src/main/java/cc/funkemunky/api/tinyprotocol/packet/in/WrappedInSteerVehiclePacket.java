package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInSteerVehiclePacket extends NMSObject {
    private static final WrappedClass packetClass = Reflections.getNMSClass(Client.STEER_VEHICLE);

    // Fields
    private float sideways, forward;
    private boolean jump, unmount;

    // Decoded data
    private static WrappedField sidewaysField = packetClass.getFieldByType(float.class, 0),
            forwardField = packetClass.getFieldByType(float.class, 0);
    private static WrappedField jumpField = packetClass.getFieldByType(boolean.class, 0),
            unmountField = packetClass.getFieldByType(boolean.class, 1);

    public WrappedInSteerVehiclePacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        sideways = sidewaysField.get(getObject());
        forward = forwardField.get(getObject());

        jump = jumpField.get(getObject());
        unmount = unmountField.get(getObject());
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Client.STEER_VEHICLE, sideways, forward, jump, unmount));
    }
}
