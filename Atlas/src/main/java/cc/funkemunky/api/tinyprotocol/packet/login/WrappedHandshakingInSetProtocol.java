package cc.funkemunky.api.tinyprotocol.packet.login;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.enums.WrappedEnumProtocol;
import org.bukkit.entity.Player;

public class WrappedHandshakingInSetProtocol extends NMSObject {
    private static WrappedClass packet = Reflections.getNMSClass(Login.HANDSHAKE);
    public WrappedHandshakingInSetProtocol(Object object) {
        super(object);
    }

    public int a, port;
    public String hostname;
    public WrappedEnumProtocol enumProtocol;

    private static WrappedField aField = packet.getFieldByType(int.class, ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17) ? 1 :0),
            hostField = packet.getFieldByType(String.class, 0),
            portField = packet.getFieldByType(int.class, ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_17) ? 2 : 1),
            protocolField = packet.getFieldByType(WrappedEnumProtocol.enumProtocol.getParent(), 0);

    @Override
    public void process(Player player, ProtocolVersion version) {
        a = aField.get(getObject());
        hostname = hostField.get(getObject());
        port = portField.get(getObject());
        enumProtocol = WrappedEnumProtocol.fromVanilla(protocolField.get(getObject()));
    }

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Login.HANDSHAKE, a, hostname, port, enumProtocol.toVanilla()));
    }
}
