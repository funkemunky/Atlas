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
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.*;

@Getter
//TODO Test updateObject functionality and additions to the wrapper for 1.8 and 1.9+.
public class WrappedInUseEntityPacket extends NMSObject {
    private static String packet = Client.USE_ENTITY;

    private static FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Enum> fieldAction = fetchField(packet, Enum.class, 0);
    private static WrappedClass packetClass = Reflections.getNMSClass(packet),
            enumEntityUseAction = Reflections.getNMSClass(
                    (ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8)
                            ? "PacketPlayInUseEntity$" : "") + "EnumEntityUseAction");
    private static WrappedField vecField, handField;

    private int id;
    private EnumEntityUseAction action;
    private Entity entity;
    private Vec3D vec;
    private WrappedEnumHand enumHand;

    private static Map<Integer, Entity> cachedEntities = new HashMap<>();

    public WrappedInUseEntityPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = Objects.requireNonNull(fetch(fieldId));
        Enum fieldAct = Objects.nonNull(fetch(fieldAction)) ? fetch(fieldAction) : null;
        action = fieldAct == null ? EnumEntityUseAction.INTERACT_AT : EnumEntityUseAction.valueOf(fieldAct.name());

        //We cache the entities so we dont have to loop every single packet for the same entity.
        entity = cachedEntities.computeIfAbsent(id, key -> {
            List<Entity> entities = Atlas.getInstance().getEntities()
                    .getOrDefault(player.getWorld().getUID(), new ArrayList<>());

            for (Entity ent : entities) {
                if(ent == null) continue;
                if(id == ent.getEntityId()) {
                    cachedEntities.put(id, ent);
                    return ent;
                }
            }
            return null;
        });

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            Object vec = fetch(vecField);
            if(vec != null)
            this.vec = new Vec3D(vec);
        }
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            enumHand = WrappedEnumHand.getFromVanilla(fetch(handField));
        } else enumHand = WrappedEnumHand.MAIN_HAND;
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            vec.updateObject();
            setPacket(packet, id, enumEntityUseAction.getEnum(action.toString()),
                    vec.getObject(), enumHand.toEnumHand());
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            setPacket(packet, id, enumEntityUseAction.getEnum(action.toString()), vec.getObject());
        } else setPacket(packet, id, enumEntityUseAction.getEnum(action.toString()));
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
            vecField = packetClass.getFieldByType(MinecraftReflection.vec3D.getParent(), 0);
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            handField = packetClass.getFieldByType(WrappedEnumHand.enumHandClass.getParent(), 0);
        }
    }
}