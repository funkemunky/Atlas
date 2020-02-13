package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.Atlas;
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
    private static String packet = Client.USE_ENTITY;

    private static FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Enum> fieldAction = fetchField(packet, Enum.class, 0);

    private int id;
    private EnumEntityUseAction action;
    private Entity entity;

    public WrappedInUseEntityPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = Objects.requireNonNull(fetch(fieldId));
        Enum fieldAct = Objects.nonNull(fetch(fieldAction)) ? fetch(fieldAction) : null;
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