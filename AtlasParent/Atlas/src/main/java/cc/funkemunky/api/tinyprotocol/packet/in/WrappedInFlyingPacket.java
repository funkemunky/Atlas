/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInFlyingPacket extends NMSObject {
    private static final WrappedClass packet = Reflections.getNMSClass(Client.FLYING);

    // Fields
    private static WrappedField fieldX = fetchField(packet, double.class, 0),
            fieldY = fetchField(packet, double.class, 1),
            fieldZ = fetchField(packet, double.class, 2),
            fieldYaw = fetchField(packet, float.class, 0),
            fieldPitch = fetchField(packet, float.class, 1),
            fieldGround = fetchField(packet, boolean.class, 0),
            hasPos = fetchField(packet, boolean.class, 1),
            hasLook = fetchField(packet, boolean.class, 2);

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
        set(fieldX, x);
        set(fieldY, y);
        set(fieldZ, z);
        set(fieldYaw, yaw);
        set(fieldPitch, pitch);
        set(fieldGround, ground);
        set(hasPos, pos);
        set(hasLook, look);
    }
}
