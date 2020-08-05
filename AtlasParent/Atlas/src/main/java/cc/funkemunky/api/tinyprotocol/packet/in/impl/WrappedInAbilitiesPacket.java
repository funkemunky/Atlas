package cc.funkemunky.api.tinyprotocol.packet.in.impl;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.PacketType;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.in.ClientPacket;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

//TODO Test 1.15
public class WrappedInAbilitiesPacket extends ClientPacket {
    private static final String packet = PacketType.Client.ABILITIES.vanillaName;
    private static FieldAccessor<Boolean>
            invulnerableField = fetchField(packet, boolean.class, 0),
            flyingField = fetchField(packet, boolean.class, 1),
            allowedFlightField = fetchField(packet, boolean.class, 2),
            creativeModeField = fetchField(packet, boolean.class, 3);
    private static FieldAccessor<Float>
            flySpeedField = fetchField(packet, float.class, 0),
            walkSpeedField = fetchField(packet, float.class, 1);

    @Getter
    private boolean invulnerable, flying, allowedFlight, creativeMode;
    @Getter
    private float flySpeed, walkSpeed;


    public WrappedInAbilitiesPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        invulnerable = fetch(invulnerableField);
        flying = fetch(flyingField);
        allowedFlight = fetch(allowedFlightField);
        creativeMode = fetch(creativeModeField);
        flySpeed = fetch(flySpeedField);
        walkSpeed = fetch(walkSpeedField);
    }

    @Override
    public void updateObject() {
        set(invulnerableField, invulnerable);
        set(flyingField, flying);
        set(allowedFlightField, allowedFlight);
        set(creativeModeField, creativeMode);
        set(flySpeedField, flySpeed);
        set(walkSpeedField, walkSpeed);
    }

    @Override
    public PacketType.Client getType() {
        return PacketType.Client.ABILITIES;
    }
}