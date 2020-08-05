package cc.funkemunky.api.tinyprotocol.packet.in.impl;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.api.PacketType;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.in.ClientPacket;
import org.bukkit.entity.Player;

public class WrappedInBlockPlace1_9 extends ClientPacket {

    private static WrappedClass blockPlace = Reflections.getNMSClass(PacketType.Client.BLOCK_PLACE), enumHandClass;
    private static WrappedField enumHand, timeStampField;

    public WrappedInBlockPlace1_9(Object object, Player player) {
        super(object, player);
    }

    public boolean mainHand;
    public long timeStamp;

    @Override
    public void process(Player player, ProtocolVersion version) {
        Enum obj = enumHand.get(getObject());

        mainHand = obj.name().equals("MAIN_HAND");
        timeStamp = timeStampField.get(getObject());
    }

    @Override
    public void updateObject() {
        set(enumHand, mainHand ? enumHandClass.getEnum("MAIN_HAND") : enumHandClass.getEnum("OFF_HAND"));
        set(timeStampField, timeStamp);
    }

    @Override
    public PacketType.Client getType() {
        return PacketType.Client.BLOCK_PLACE;
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            enumHand = blockPlace.getFieldByType(Enum.class, 0);
            timeStampField = blockPlace.getFieldByType(long.class, 0);
            enumHandClass = Reflections.getNMSClass("EnumHand");
        }
    }
}