package cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying.versions;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.flying.AtlasPacketPlayInFlying;

public class vReflection extends AtlasPacketPlayInFlying {
    public vReflection(Object packet) {
        super(packet);
    }

    private final static WrappedClass classFlying = Reflections.getNMSClass("PacketPlayInFlying");
    private final static WrappedField fieldX = classFlying.getFieldByType(double.class, 0),
            fieldY = classFlying.getFieldByType(double.class, 1), fieldZ = classFlying.getFieldByType(double.class, 2),
            fieldYaw = classFlying.getFieldByType(float.class, 0), fieldPitch = classFlying.getFieldByType(float.class, 1),
            fieldGround = classFlying.getFieldByType(boolean.class, 0),
            fieldHasPos = classFlying.getFieldByType(boolean.class, 1),
            fieldHasLook = classFlying.getFieldByType(boolean.class, 2);

    @Override
    public double getX() {
        return fieldX.get(packet);
    }

    @Override
    public double getY() {
        return fieldY.get(packet);
    }

    @Override
    public double getZ() {
        return fieldZ.get(packet);
    }

    @Override
    public float getYaw() {
        return fieldYaw.get(packet);
    }

    @Override
    public float getPitch() {
        return fieldPitch.get(packet);
    }

    @Override
    public boolean isOnGround() {
        return fieldGround.get(packet);
    }

    @Override
    public boolean isPos() {
        return fieldHasPos.get(packet);
    }

    @Override
    public boolean isLook() {
        return fieldHasLook.get(packet);
    }
}
