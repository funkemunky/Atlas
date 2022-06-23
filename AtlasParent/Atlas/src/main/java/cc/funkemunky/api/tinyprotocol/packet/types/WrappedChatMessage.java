package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.entity.Player;

@Getter
@Deprecated
public class WrappedChatMessage extends NMSObject {
    private static String type = Type.CHATMESSAGE;

    private String chatMessage;
    private Object[] objects;

    private static WrappedClass chatMessageClass;
    private static WrappedField messageField;
    private static WrappedField objectsField;

    public WrappedChatMessage(String chatMessage, Object... object) {
        this.chatMessage = chatMessage;
        this.objects = object;

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_19)) {
            if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
                if(object.length > 0)
                    setObject(chatMessageClass.getConstructorAtIndex(1).newInstance(chatMessage, new Object[]{object}));
                else setObject(chatMessageClass.getConstructorAtIndex(0).newInstance(chatMessage));
            } else {
                setObject(chatMessageClass.getConstructorAtIndex(0).newInstance(chatMessage, new Object[]{object}));
            }
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
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_19))
            return;

        chatMessage = fetch(messageField);
        objects = fetch(objectsField);
    }

    @Override
    public void updateObject() {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_19))
            return;

        messageField.set(getObject(), chatMessage);
        objectsField.set(getObject(), objects);
    }

    static {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.v1_19)) {
            chatMessageClass = Reflections.getNMSClass("ChatMessage");
            messageField = chatMessageClass.getFieldByType(String.class, 0);
            objectsField = chatMessageClass.getFieldByType(Object[].class, 0);
        }
    }
}
