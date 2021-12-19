package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.Vec3D;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumHand;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.Objects;

@Getter
public class WrappedInUseEntityPacket extends NMSObject {

    private static final WrappedClass packetClass = Reflections.getNMSClass(Client.USE_ENTITY),
            enumEntityUseAction = Reflections.getNMSClass((ProtocolVersion.getGameVersion()
                    .isAbove(ProtocolVersion.V1_8) ? "PacketPlayInUseEntity$" : "") + "EnumEntityUseAction");
    private static WrappedClass actionOne, actionTwo;
    private static final WrappedField fieldId = fetchField(packetClass, int.class, 0),
            fieldAction = fetchField(packetClass, enumEntityUseAction.getParent(), 0);
    private static WrappedField fieldVec, fieldHand, fieldSneaking;

    //1.17 fields
    private static WrappedField fieldHandTwo;

    private int id;
    private EnumEntityUseAction action;
    private Entity entity;
    private Vec3D vec = new Vec3D(-1,-1,-1);
    private WrappedEnumHand enumHand = WrappedEnumHand.MAIN_HAND;
    private boolean sneaking;

    public WrappedInUseEntityPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = Objects.requireNonNull(fetch(fieldId));
        //We cache the entities so we dont have to loop every single packet for the same entity.
        entity = Atlas.getInstance().getWorldInfo(player.getWorld())
                .getEntityOrLock(id).orElse(null);
        //This will lock if the entity doesn't exist for some reaosn.

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            Enum fieldAct = fetch(fieldAction);
            action = fieldAct == null ? EnumEntityUseAction.INTERACT_AT : EnumEntityUseAction.valueOf(fieldAct.name());

            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
                Object vec = fetch(fieldVec);
                if(vec != null)
                    this.vec = new Vec3D(vec);

                if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                    enumHand = WrappedEnumHand.getFromVanilla(fetch(fieldHand));
                }
            }
        } else { //1.17 specific code
            Object actionField = fetch(fieldAction);

            action = EnumEntityUseAction.ATTACK;

            if(actionField.getClass().isAssignableFrom(actionOne.getParent())) {
                enumHand = WrappedEnumHand.getFromVanilla(fieldHand.get(actionField));
                action = EnumEntityUseAction.INTERACT;
            } else if(actionField.getClass().isAssignableFrom(actionTwo.getParent())) {
                enumHand = WrappedEnumHand.getFromVanilla(fieldHandTwo.get(actionField));
                vec = new Vec3D((Object)fieldVec.get(actionField));

                action = EnumEntityUseAction.INTERACT_AT;
            }
        }
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
            sneaking = fetch(fieldSneaking);
        } else sneaking = player.isSneaking();
    }

    @Override
    public void updateObject() {
        set(fieldId, id);
        set(fieldAction, enumEntityUseAction.getEnum(action.toString()));

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            set(fieldVec, vec.getObject());

            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                set(fieldHand, enumHand.toEnumHand());

                if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
                    set(fieldSneaking, sneaking);
                }
            }
        }
    }

    public enum EnumEntityUseAction {
        INTERACT("INTERACT"),
        ATTACK("ATTACK"),
        INTERACT_AT("INTERACT_AT");

        @Getter
        private String name;

        EnumEntityUseAction(String name) {
            this.name = name;
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
                fieldVec = packetClass.getFieldByType(MinecraftReflection.vec3D.getParent(), 0);

                if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
                    fieldHand = packetClass.getFieldByType(WrappedEnumHand.enumHandClass.getParent(), 0);
                }
            } else {
                actionOne = Reflections.getNMSClass("PacketPlayInUseEntity$d");
                actionTwo = Reflections.getNMSClass("PacketPlayInUseEntity$e");

                fieldHand = fetchField(actionOne, WrappedEnumHand.enumHandClass.getParent(), 0);
                fieldHandTwo = fetchField(actionTwo, WrappedEnumHand.enumHandClass.getParent(), 0);
                fieldVec = fetchField(actionTwo, "b");
            }
            if (ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
                fieldSneaking = fetchField(packetClass, boolean.class, 0);
            }
        }
    }
}