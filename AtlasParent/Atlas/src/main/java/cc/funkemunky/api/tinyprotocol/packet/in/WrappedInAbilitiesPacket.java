package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

public class WrappedInAbilitiesPacket extends NMSObject {
    private static final String packet = Client.ABILITIES;
    private static FieldAccessor<Boolean>
            invulnerableField,
            flyingField,
            allowedFlightField,
            creativeModeField;
    private static FieldAccessor<Float>
            flySpeedField,
            walkSpeedField;

    @Getter
    private boolean invulnerable, flying, allowedFlight, creativeMode;
    @Getter
    private float flySpeed, walkSpeed;


    public WrappedInAbilitiesPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        //In 1.17+, this is the only thing the client sends in this packet serverbound. The rest is only updated from
        //clientbound Abilities packets
        flying = fetch(flyingField);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)) {
            invulnerable = fetch(invulnerableField);
            allowedFlight = fetch(allowedFlightField);
            creativeMode = fetch(creativeModeField);
            flySpeed = fetch(flySpeedField);
            walkSpeed = fetch(walkSpeedField);
        }
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(),
                packet, invulnerable, flying, allowedFlight, creativeMode, flySpeed, walkSpeed));
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_16)) {
            invulnerableField = fetchField(packet, boolean.class, 0);
            flyingField = fetchField(packet, boolean.class, 1);
            allowedFlightField = fetchField(packet, boolean.class, 2);
            creativeModeField = fetchField(packet, boolean.class, 3);
            flySpeedField = fetchField(packet, float.class, 0);
            walkSpeedField = fetchField(packet, float.class, 1);
        } else {
            flyingField = fetchField(packet, boolean.class, 0);
        }
    }
}