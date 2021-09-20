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
    private static final FieldAccessor<Short> fieldAction = fetchField(packet, short.class, 0);
    private static final FieldAccessor<Boolean> fieldAccepted = fetchField(packet, boolean.class, 0);

    private int id;
    private short action;
    private boolean accept;

    public WrappedInTransactionPacket(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        AtlasPacketPlayInTransaction transaction = AtlasPacketPlayInTransaction.getTransaction(getObject());
        id = transaction.getId();
        action = transaction.getAction();
        accept = transaction.isAccepted();
    }

    @Override
    public void updateObject() {
        set(fieldId, id);
        set(fieldAction, action);
        set(fieldAccepted, accept);
    }
}
