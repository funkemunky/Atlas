/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Getter
@NoArgsConstructor
public class WrappedOutPositionPacket extends NMSObject {
    private static final String packet = Server.POSITION;

    // Fields
    private static FieldAccessor<Double> fieldX = fetchField(packet, double.class, 0);
    private static FieldAccessor<Double> fieldY = fetchField(packet, double.class, 1);
    private static FieldAccessor<Double> fieldZ = fetchField(packet, double.class, 2);
    private static FieldAccessor<Float> fieldYaw = fetchField(packet, float.class, 0);
    private static FieldAccessor<Float> fieldPitch = fetchField(packet, float.class, 1);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;

    public WrappedOutPositionPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        x = fetch(fieldX);
        y = fetch(fieldY);
        z = fetch(fieldZ);
        yaw = fetch(fieldYaw);
        pitch = fetch(fieldPitch);
    }

    private List<Integer> toOrdinal(Set<Enum> enums) {
        List<Integer> ordinals = new ArrayList<>();
        enums.forEach(e -> ordinals.add(e.ordinal()));
        return ordinals;
    }
}
