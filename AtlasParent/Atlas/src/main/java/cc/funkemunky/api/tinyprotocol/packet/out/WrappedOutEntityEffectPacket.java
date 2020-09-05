package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

public class WrappedOutEntityEffectPacket extends NMSObject {

    public int entityId;
    public byte effectId;
    public byte amplifier;
    public int duration;
    public byte flags;

    private static WrappedClass wrapped = Reflections.getNMSClass(Server.ENTITY_EFFECT);
    private static WrappedField fieldEntityId,
            fieldEffectId,
            fieldAmplifier,
            fieldDuration, fieldFlags;

    public WrappedOutEntityEffectPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void updateObject() {
        fieldEntityId.set(getObject(), entityId);
        fieldEffectId.set(getObject(), effectId);
        fieldAmplifier.set(getObject(), amplifier);
        fieldDuration.set(getObject(), duration);
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) fieldFlags.set(getObject(), flags);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        entityId = fieldEntityId.get(getObject());
        effectId = fieldEffectId.get(getObject());
        amplifier = fieldAmplifier.get(getObject());
        duration = ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)
                ? fieldDuration.get(getObject()) : (short) fieldDuration.get(getObject());
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10))flags = fieldFlags.get(getObject());
    }

    static {
        fieldEntityId = wrapped.getFieldByType(int.class, 0);
        fieldEffectId = wrapped.getFieldByType(byte.class, 0);
        fieldAmplifier = wrapped.getFieldByType(byte.class, 1);
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            fieldDuration = wrapped.getFieldByType(int.class, 1);
            fieldFlags = wrapped.getFieldByType(byte.class, 2);
        } else  fieldDuration = wrapped.getFieldByType(short.class, 0);
    }
}
