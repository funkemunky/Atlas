package cc.funkemunky.api.tinyprotocol.packet.login;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

public class WrappedStatusInPing extends NMSObject {

    public WrappedStatusInPing(Object object) {
        super(object);
    }

    private static final WrappedClass statusInPing = Reflections.getNMSClass(Login.PING);
    private static final WrappedField pingField = statusInPing.getFieldByType(long.class, 0);

    public long ping;

    @Override
    public void process(Player player, ProtocolVersion version) {
        ping = pingField.get(getObject());
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Login.PING, ping));
    }
}
