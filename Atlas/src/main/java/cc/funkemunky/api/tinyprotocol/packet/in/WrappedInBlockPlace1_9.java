package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

public class WrappedInBlockPlace1_9 extends NMSObject {

    private static final String packet = Client.BLOCK_PLACE_1_9;

    private static WrappedClass blockPlace = Reflections.getNMSClass(packet), enumHandClass;
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
        setObject(NMSObject.construct(getObject(), packet, mainHand
                ? enumHandClass.getEnum("MAIN_HAND") : enumHandClass.getEnum("OFF_HAND"),
                timeStamp));
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            enumHand = blockPlace.getFieldByType(Enum.class, 0);
            timeStampField = blockPlace.getFieldByType(long.class, 0);
            enumHandClass = Reflections.getNMSClass("EnumHand");
        }
    }
}