package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.tinyprotocol.reflection.Reflection;
import lombok.Getter;
import org.bukkit.entity.Player;

public class WrappedIn13KeepAlive extends NMSObject {
    private static final String packet = Client.KEEP_ALIVE;
    @Getter
    private long ping;
    private FieldAccessor<Long> pingField = Reflection.getFieldSafe(packet, long.class, 0);

    public WrappedIn13KeepAlive(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        ping = fetch(pingField);
    }
}