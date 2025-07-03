package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.entity.Player;

public class WrappedOutCloseWindowPacket extends NMSObject {

    private static final WrappedClass packet = Reflections.getNMSClass(Server.CLOSE_WINDOW);
    private static final WrappedField idField = packet.getFieldByType(int.class, 0);

    public WrappedOutCloseWindowPacket(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutCloseWindowPacket(int id) {
        this.id = id;
        updateObject();
    }

    public int id;

    @Override
    public void updateObject() {
        setObject(NMSObject.construct(getObject(), Server.CLOSE_WINDOW, id));
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(idField);
    }
}
