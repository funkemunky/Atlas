/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInFlyingPacket extends NMSObject {
    private static final String packet = Client.FLYING;

    // Fields
    private static FieldAccessor<Double> fieldX = fetchField(packet, double.class, 0);
    private static FieldAccessor<Double> fieldY = fetchField(packet, double.class, 1);
    private static FieldAccessor<Double> fieldZ = fetchField(packet, double.class, 2);
    private static FieldAccessor<Float> fieldYaw = fetchField(packet, float.class, 0);
    private static FieldAccessor<Float> fieldPitch = fetchField(packet, float.class, 1);
    private static FieldAccessor<Boolean> fieldGround = fetchField(packet, boolean.class, 0);
    private static FieldAccessor<Boolean> hasPos = fetchField(packet, boolean.class, 1);
    private static FieldAccessor<Boolean> hasLook = fetchField(packet, boolean.class, 2);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;
    private boolean look, pos, ground;

    public WrappedInFlyingPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);
        ground = fetch(fieldGround);
        pos = fetch(hasPos);
        look = fetch(hasLook);
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), packet, x, y, z, yaw, pitch, ground, pos, look));
    }
}
