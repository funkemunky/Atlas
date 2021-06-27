/*
 * Copyright (c) 2018 NGXDEV.COM. Licensed under MIT.
 */

package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.optimized.flying.AtlasPacketPlayInFlying;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInFlyingPacket extends NMSObject {
    private static final WrappedClass packet = Reflections.getNMSClass(Client.FLYING);

    // Decoded data
    private double x, y, z;
    private float yaw, pitch;
    private boolean look, pos, ground;

    public WrappedInFlyingPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        AtlasPacketPlayInFlying flying = AtlasPacketPlayInFlying.getFlying(getObject());
        x = flying.getX();
        y = flying.getY();
        z = flying.getZ();
        yaw = flying.getYaw();
        pitch = flying.getPitch();
        ground = flying.isOnGround();
        pos = flying.isPos();
        look = flying.isLook();
    }

    @Override
    public void updateObject() {

    }
}
