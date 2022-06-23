package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

@Deprecated
public class WrappedOutEntityEffectPacket extends NMSObject {

    public int entityId;
    public int effectId;
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
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_18_2)) {
            entityId = fieldEntityId.get(getObject());
            effectId = fieldEffectId.get(getObject());
        } else {
            entityId = fieldEntityId.get(getObject());
            effectId = ((byte)fieldEffectId.get(getObject()));
        }
        amplifier = fieldAmplifier.get(getObject());
        duration = ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)
                ? fieldDuration.get(getObject()) : (short) fieldDuration.get(getObject());
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10))
            flags = fieldFlags.get(getObject());
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_18_2)) {
            fieldEntityId = wrapped.getFieldByType(int.class, 3);
            fieldEffectId = wrapped.getFieldByType(int.class, 4);
            fieldDuration = wrapped.getFieldByType(int.class, 5);
            fieldAmplifier = wrapped.getFieldByType(byte.class, 0);
            fieldFlags = wrapped.getFieldByType(byte.class, 1);
        } else {
            fieldEntityId = wrapped.getFieldByType(int.class,
                    ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17) ? 3 : 0);
            fieldEffectId = wrapped.getFieldByType(byte.class, 0);
            fieldAmplifier = wrapped.getFieldByType(byte.class, 1);
            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
                fieldDuration = wrapped.getFieldByType(int.class, 1
                        + (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17) ? 3 : 0));
                fieldFlags = wrapped.getFieldByType(byte.class, 2);
            } else  fieldDuration = wrapped.getFieldByType(short.class, 0);
        }
    }
}
