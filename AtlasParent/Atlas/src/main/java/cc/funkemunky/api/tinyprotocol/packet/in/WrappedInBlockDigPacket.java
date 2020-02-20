package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumDirection;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInBlockDigPacket extends NMSObject {
    private static final String packet = Client.BLOCK_DIG;

    // Fields
    private static FieldAccessor<Object> fieldBlockPosition;
    private static FieldAccessor<Integer> fieldPosX;
    private static FieldAccessor<Integer> fieldPosY;
    private static FieldAccessor<Integer> fieldPosZ;
    private static FieldAccessor<Object> fieldDirection;
    private static FieldAccessor<Object> fieldDigType;
    private static FieldAccessor<Integer> face;
    private static FieldAccessor<Integer> intAction;


    // Decoded data
    private BaseBlockPosition position;
    private WrappedEnumDirection direction;
    private EnumPlayerDigType action;


    public WrappedInBlockDigPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void updateObject() {

    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if (ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldPosX = fetchField(packet, int.class, 0);
            fieldPosY = fetchField(packet, int.class, 1);
            fieldPosZ = fetchField(packet, int.class, 2);
            face = fetchField(packet, int.class, 3);
            intAction = fetchField(packet, int.class, 4);
            position = new BaseBlockPosition(fetch(fieldPosX), fetch(fieldPosY), fetch(fieldPosZ));
            direction = WrappedEnumDirection.values()[Math.min(fetch(face), 5)];
            action = EnumPlayerDigType.values()[fetch(intAction)];
        } else {
            fieldBlockPosition = fetchField(packet, Object.class, 0);
            fieldDirection = fetchField(packet, Object.class, 1);
            fieldDigType = fetchField(packet, Object.class, 2);
            position = new BaseBlockPosition(fetch(fieldBlockPosition));
            direction = WrappedEnumDirection.values()[((Enum) fetch(fieldDirection)).ordinal()];
            action = EnumPlayerDigType.values()[((Enum) fetch(fieldDigType)).ordinal()];
        }
    }

    public enum EnumPlayerDigType {
        START_DESTROY_BLOCK,
        ABORT_DESTROY_BLOCK,
        STOP_DESTROY_BLOCK,
        DROP_ALL_ITEMS,
        DROP_ITEM,
        RELEASE_USE_ITEM,
        SWAP_HELD_ITEMS;
    }
}