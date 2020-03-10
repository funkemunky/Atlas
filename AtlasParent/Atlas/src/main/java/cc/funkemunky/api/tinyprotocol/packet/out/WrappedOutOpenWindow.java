package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.impl.MinecraftReflection;
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
    private static FieldAccessor<Object> chatCompField;
    private static FieldAccessor<Integer> inventorySize = fetchField(packet, int.class, 1);

    private int id;
    private String name;
    private WrappedChatMessage chatComponent;
    private int size;

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(idField);
        name = fetch(nameField);
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8))
            chatComponent = new WrappedChatMessage(fetch(chatCompField));
        size = fetch(inventorySize);
    }

    @Override
    public void updateObject() {

    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            fetchField(packet,
                    MinecraftReflection.iChatBaseComponent.getParent(), 0);
        }
    }
}
