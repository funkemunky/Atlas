package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class WrappedInUseEntityPacket extends NMSObject {
    private static WrappedClass packet = Reflections.getNMSClass(Client.USE_ENTITY);

    private static WrappedField idField = packet.getFieldByType(int.class, 0),
            actionField = packet.getFieldByType(Enum.class, 0);

    private static WrappedClass enumAction = new WrappedClass(actionField.getType());

    private int id;
    private EnumEntityUseAction action;
    private Entity entity;

    public WrappedInUseEntityPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = Objects.requireNonNull(fetch(idField));
        Object enumObj = fetch(actionField);
        Enum fieldAct = enumObj != null ? (Enum) enumObj : null;
        action = fieldAct == null ? EnumEntityUseAction.INTERACT_AT : EnumEntityUseAction.valueOf(fieldAct.name());

        List<Entity> entities = Atlas.getInstance().getEntities()
                .getOrDefault(player.getWorld().getUID(), new ArrayList<>());

        for (Entity ent : entities) {
            if(id == ent.getEntityId()) {
                entity = ent;
                break;
            }
        }
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Client.USE_ENTITY, id, enumAction.getEnum(action.name)));
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
}
