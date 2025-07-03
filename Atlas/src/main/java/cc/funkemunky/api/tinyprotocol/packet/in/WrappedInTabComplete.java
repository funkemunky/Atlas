package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.BaseBlockPosition;
import cc.funkemunky.api.tinyprotocol.packet.types.v1_13.DontImportIfNotLatestThanks;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInTabComplete extends NMSObject {

    private static final String packet = Client.TAB_COMPLETE;

    private static DontImportIfNotLatestThanks stuff;

    public WrappedInTabComplete(Object object, Player player) {
        super(object, player);
    }

    private static FieldAccessor<String> messageAccessor = fetchField(packet, String.class, 0);
    private static FieldAccessor<Boolean> hasToolTipAccessor;
    private static FieldAccessor<Object> blockPositionAcessor;
    private static FieldAccessor<Integer> idAccessor;

    private String message;
    private int id = -1;
    private BaseBlockPosition blockPosition; //1.8 and up only.
    private boolean hasToolTip; //1.9 and up only.

    @Override
    public void process(Player player, ProtocolVersion version) {
        message = fetch(messageAccessor);

        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_13)) {
            id = fetch(idAccessor);
        } else {
            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)) {
                hasToolTip = fetch(hasToolTipAccessor);
            }
            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8)) {
                Object blockPos = fetch(blockPositionAcessor);
                if(blockPos != null)
                    blockPosition = new BaseBlockPosition(fetch(blockPositionAcessor));
            }
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            setPacket(packet, message, hasToolTip, blockPosition.getAsBlockPosition());
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            setPacket(packet, message, blockPosition.getAsBlockPosition());
        } else setPacket(packet, message);
    }

    static {

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            idAccessor = fetchField(packet, int.class, 0);
            stuff = new DontImportIfNotLatestThanks();
        } else {
            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9)) {
                hasToolTipAccessor = fetchField(packet, boolean.class, 0);
            }
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
                blockPositionAcessor = fetchField(packet, Object.class, 1);
            }
        }
    }
}