package cc.funkemunky.api.tinyprotocol.packet.in.impl;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.PacketType;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.in.ClientPacket;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
//TODO Test 1.15
public class WrappedInArmAnimationPacket extends ClientPacket {

    public WrappedInArmAnimationPacket(Object object, Player player) {
        super(object, player);
    }

    public boolean mainHand;

    private static WrappedClass enumHand, armAnimation = Reflections.getNMSClass(PacketType.Client.ARM_ANIMATION);
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
            set(mainHandField, enumHand.getEnum(mainHand ? "MAIN_HAND" : "OFF_HAND"));
        }
    }

    @Override
    public PacketType.Client getType() {
        return PacketType.Client.ARM_ANIMATION;
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            enumHand = Reflections.getNMSClass("EnumHand");
            mainHandField = armAnimation.getFieldByType(Enum.class, 0);
        }
    }
}
