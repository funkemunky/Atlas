package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import javax.annotation.Nullable;

@Getter
public class WrappedOutAttachEntity extends NMSObject {

    //Not tested in all versions, just a basically wrapped packet.

    private static final WrappedClass packet = Reflections.getNMSClass(Server.ATTACH);
    private static final WrappedField fieldA = fetchField(packet, int.class, 0),
            fieldB = fetchField(packet, int.class, 1);
    private static WrappedField fieldC;
    private static final WrappedConstructor packetConst = packet.getConstructorAtIndex(ProtocolVersion.getGameVersion()
            .isOrAbove(ProtocolVersion.v1_17) ? 0 : 1);

    private int attachedEntityId, holdingEntityId;
    private boolean isLeashModifier = true;


    public WrappedOutAttachEntity(Object packet, Player player) {
        super(packet, player);
    }

    public WrappedOutAttachEntity(Entity holder, @Nullable Entity toAttach, boolean attachLeadNotRideVehicle) {
        final Object vanillaHolder = CraftReflection.getEntity(holder),
                vanillaToAttach = toAttach != null ? CraftReflection.getEntity(toAttach) : null;
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            setObject(packetConst.newInstance(vanillaToAttach, vanillaHolder));
        } else {
            setObject(packetConst.newInstance(attachLeadNotRideVehicle ? 1 : 0, vanillaToAttach, vanillaHolder));
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            set(fieldA, attachedEntityId);
            set(fieldB, holdingEntityId);
        } else {
            set(fieldA, isLeashModifier ? 1 : 0);
            set(fieldB, attachedEntityId);
            set(fieldC, holdingEntityId);
        }
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            attachedEntityId = fetch(fieldA);
            holdingEntityId = fetch(fieldB);
        } else {
            isLeashModifier = (int)fetch(fieldA) == 1;
            attachedEntityId = fetch(fieldB);
            holdingEntityId = fetch(fieldC);
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            fieldC = fetchField(packet, int.class, 2);
        }
    }
}