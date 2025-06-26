package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInArmAnimationPacket extends NMSObject {
    private static final String packet = Client.ARM_ANIMATION;

    public WrappedInArmAnimationPacket(Object object, Player player) {
        super(object, player);
    }

    public boolean mainHand;

    private static WrappedClass enumHand, armAnimation;
    private static WrappedField mainHandField;

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            Enum enumhand = mainHandField.get(getObject());

            mainHand = enumhand.name().equals("MAIN_HAND");
        } else mainHand = true;
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            setObject(NMSObject.construct(getObject(), packet, enumHand.getEnum(mainHand ? "MAIN_HAND" : "OFF_HAND")));
        }
    }

    static {
        armAnimation = Reflections.getNMSClass(packet);
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            enumHand = Reflections.getNMSClass("EnumHand");
            mainHandField = armAnimation.getFieldByType(enumHand.getParent(), 0);
        }
    }
}
