/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
public class WrappedOutPositionPacket extends NMSObject {
    private static final String packet = Server.POSITION;

    // Fields
    private static FieldAccessor<Double> fieldX = fetchField(packet, double.class, 0);
    private static FieldAccessor<Double> fieldY = fetchField(packet, double.class, 1);
    private static FieldAccessor<Double> fieldZ = fetchField(packet, double.class, 2);
    private static FieldAccessor<Float> fieldYaw = fetchField(packet, float.class, 0);
    private static FieldAccessor<Float> fieldPitch = fetchField(packet, float.class, 1);
    private static FieldAccessor<Set> fieldFlags = fetchField(packet, Set.class, 0);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;

    public WrappedOutPositionPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        boolean X = true,
                Y = true,
                Z = true,
                Y_ROT = true,
                X_ROT = true;
        if (version.isAbove(ProtocolVersion.V1_7_10)) {
            List<Integer> ordinals = toOrdinal(fetch(fieldFlags));
            X = ordinals.contains(0);
            Y = ordinals.contains(1);
            Z = ordinals.contains(2);
            Y_ROT = ordinals.contains(3);
            X_ROT = ordinals.contains(4);
        }
        x = X ? fetch(fieldX) : 0;
        y = Y ? fetch(fieldY) : 0;
        z = Z ? fetch(fieldZ) : 0;
        yaw = Y_ROT ? fetch(fieldYaw) : 0;
        pitch = X_ROT ? fetch(fieldPitch) : 0;
    }

    private List<Integer> toOrdinal(Set<Enum> enums) {
        List<Integer> ordinals = new ArrayList<>();
        enums.forEach(e -> ordinals.add(e.ordinal()));
        return ordinals;
    }
}
