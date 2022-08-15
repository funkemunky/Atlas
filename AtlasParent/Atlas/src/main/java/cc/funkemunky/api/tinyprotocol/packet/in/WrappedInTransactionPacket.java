package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.optimized.incoming.transaction.AtlasPacketPlayInTransaction;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedInTransactionPacket extends NMSObject {
    private static final String packet = Client.TRANSACTION;

    private static final FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Short> fieldAction;
    private static FieldAccessor<Boolean> fieldAccepted;

    private int id, idRaw = -1;
    private short action;
    private boolean accept;

    public WrappedInTransactionPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            AtlasPacketPlayInTransaction transaction = AtlasPacketPlayInTransaction.getTransaction(getObject());
            id = transaction.getId();
            action = transaction.getAction();
            accept = transaction.isAccepted();
        } else {
            idRaw = fetch(fieldId);
            id = (short) ((idRaw >> 16) & 0xFF);
            action = (short) (idRaw & 0xFFFF);
            accept = (idRaw & (1 << 30)) != 0;
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            set(fieldId, id);
            set(fieldAction, action);
            set(fieldAccepted, accept);
        } else {
            set(fieldId, ((accept ? 1 : 0) << 30) | (id << 16) | (action & 0xFFFF));
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            fieldAccepted = fetchField(packet, boolean.class, 0);
            fieldAction = fetchField(packet, short.class, 0);
        }
    }
}
