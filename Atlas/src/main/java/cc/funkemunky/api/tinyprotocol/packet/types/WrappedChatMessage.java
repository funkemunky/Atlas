package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
public class WrappedChatMessage extends NMSObject {
    private static String type = Type.CHATMESSAGE;

    private String chatMessage;
    private Object[] objects;

    private static WrappedClass chatMessageClass = Reflections.getNMSClass("ChatMessage");
    private static WrappedField messageField = chatMessageClass.getFieldByType(String.class, 0);
    private static WrappedField objectsField = chatMessageClass.getFieldByType(Object[].class, 0);

    public WrappedChatMessage(String chatMessage, Object... object) {
        this.chatMessage = chatMessage;
        this.objects = object;

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
            if(object.length > 0)
            setObject(chatMessageClass.getConstructorAtIndex(1).newInstance(chatMessage, new Object[]{object}));
            else setObject(chatMessageClass.getConstructorAtIndex(0).newInstance(chatMessage));
        } else {
            setObject(chatMessageClass.getConstructorAtIndex(0).newInstance(chatMessage, new Object[]{object}));
        }
    }

    public WrappedChatMessage(String chatMessage) {
        this(chatMessage, new Object[0]);
    }

    public WrappedChatMessage(Object object) {
        super(object);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        chatMessage = fetch(messageField);
        objects = fetch(objectsField);
    }

    @Override
    public void updateObject() {
        messageField.set(getObject(), chatMessage);
        objectsField.set(getObject(), objects);
    }
}
