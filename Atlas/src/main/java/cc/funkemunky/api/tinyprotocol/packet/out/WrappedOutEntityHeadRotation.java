package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutEntityHeadRotation extends NMSObject {
    private static WrappedClass headRotationClass = Reflections.getNMSClass(Server.ENTITY_HEAD_ROTATION);
    private static WrappedField entityIdField = headRotationClass.getFirstFieldByType(int.class);
    private static WrappedField yawField = headRotationClass.getFirstFieldByType(byte.class);

    private int entityId;
    private byte yaw;

    public WrappedOutEntityHeadRotation(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutEntityHeadRotation(int entityId, byte byteThing) {
        Object headRotation = headRotationClass.getConstructor().newInstance();
        entityIdField.set(headRotation, entityId);
        yawField.set(headRotation, byteThing);

        setObject(headRotation);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        entityId = entityIdField.get(getObject());
        yaw = yawField.get(getObject());
    }
}
