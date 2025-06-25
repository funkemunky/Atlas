package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutTransaction extends NMSObject {
    private static final String packet = Server.TRANSACTION;
    private static WrappedConstructor rawIntConstructor;
    private static final FieldAccessor<Integer> fieldId = fetchField(packet, int.class, 0);
    private static FieldAccessor<Short> fieldAction;
    private static FieldAccessor<Boolean> fieldAccepted;
    private int id;
    private short action;
    private boolean accept;
    private int idRaw = -1;

    public WrappedOutTransaction(int id, short action, boolean accept) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)) {
            setObject(rawIntConstructor
                    .newInstance((int)((accept ? 1 : 0) << 30) | (id << 16) | (action & 0xFFFF)));
        } else {
            setPacket(packet);
            this.id = id;
            this.action = action;
            this.accept = accept;
            updateObject();
        }
    }

    public WrappedOutTransaction(Object packet, Player player) {
        super(packet, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            id = fetch(fieldId);
            action = fetch(fieldAction);
            accept = fetch(fieldAccepted);
        } else {
            idRaw = fetch(fieldId);

            id = (short) ((idRaw >> 16) & 0xFF);
            action = (short) (idRaw & 0xFFFF);
            accept = (idRaw & (1 << 30)) != 0;
        }
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17)) {
            set(fieldId, ((accept ? 1 : 0) << 30) | (id << 16) | (action & 0xFFFF));
        } else {
            set(fieldId, id);
            set(fieldAction, action);
            set(fieldAccepted, accept);
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_17)) {
            fieldAccepted = fetchField(packet, boolean.class, 0);
            fieldAction = fetchField(packet, short.class, 0);
        } else rawIntConstructor = Reflections.getNMSClass(packet).getConstructor(int.class);
    }
}
