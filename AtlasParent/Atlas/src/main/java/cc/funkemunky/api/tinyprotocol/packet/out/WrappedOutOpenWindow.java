package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedChatMessage;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutOpenWindow extends NMSObject {

    private static String packet = Server.OPEN_WINDOW;

    public WrappedOutOpenWindow(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutOpenWindow(int id, String name, WrappedChatMessage msg, int size) {
        setPacket(packet, id, name, msg.getObject(), size);
    }

    private static FieldAccessor<Integer> idField = fetchField(packet, int.class, 0);
    private static FieldAccessor<String> nameField = fetchField(packet, String.class, 0);
    private static WrappedMethod getText = MinecraftReflection.iChatBaseComponent.getMethod("getText");
    private static FieldAccessor<Integer> inventorySize = fetchField(packet, int.class, 1);

    private int id;
    private String name;
    private int size;

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(idField);
        name = fetch(nameField);
        size = fetch(inventorySize);
    }

    @Override
    public void updateObject() {

    }
}
