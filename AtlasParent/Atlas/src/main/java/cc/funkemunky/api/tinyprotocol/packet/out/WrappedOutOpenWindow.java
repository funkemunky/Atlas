package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedChatComponent;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedChatMessage;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedOutOpenWindow extends NMSObject {

    private static final String packet = Server.OPEN_WINDOW;

    public WrappedOutOpenWindow(Object object, Player player) {
        super(object, player);
    }

    public WrappedOutOpenWindow(int id, String name, WrappedChatMessage msg, int size) {
        setPacket(packet, id, name, msg.getObject(), size);
    }

    private static final FieldAccessor<Integer> idField = fetchField(packet, int.class, 0);
    private static FieldAccessor<String> nameField;
    private static FieldAccessor<Object> chatCompField;
    private static final FieldAccessor<Integer> inventorySize = fetchField(packet, int.class, 1);

    private int id;
    private String name; //Not a thing in 1.14 and above.
    private WrappedChatComponent chatComponent;
    private int size;

    @Override
    public void process(Player player, ProtocolVersion version) {
        id = fetch(idField);
        size = fetch(inventorySize);

        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_13_2)) {
            name = fetch(nameField);
        }
    }

    @Override
    public void updateObject() {

    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            chatCompField = fetchField(packet,
                    MinecraftReflection.iChatBaseComponent.getParent(), 0);
        }
        if(ProtocolVersion.getGameVersion().isOrBelow(ProtocolVersion.V1_13_2)) {
            nameField = fetchField(packet, String.class, 0);
        }
    }
}
