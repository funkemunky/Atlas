package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.reflection.FieldAccessor;
import cc.funkemunky.api.utils.ReflectionsUtil;
import lombok.Getter;

@Getter
public class WrappedChatMessage extends NMSObject {
    private static String type = Type.CHATMESSAGE;

    private String chatMessage;
    private Object[] objects;

    private static FieldAccessor<String> messageField = fetchField(type, String.class, 0);
    private static FieldAccessor<Object[]> objectsField = fetchField(type, Object[].class, 0);
    private static Class<?> chatMsgClass = ReflectionsUtil.getClass(type);

    public WrappedChatMessage(String chatMessage, Object... object) {
        this.chatMessage = chatMessage;
        this.objects = object;
    }

    public WrappedChatMessage(String chatMessage) {
        this(chatMessage, new Object[]{});
    }

    public void setPacket(String packet, Object... args) {

        Object o = ReflectionsUtil.newInstance(chatMsgClass, packet, args);

        setObject(o);
    }

    public WrappedChatMessage(Object object) {
        super(object);

        chatMessage = fetch(messageField);
        objects = fetch(objectsField);
    }

    @Override
    public void updateObject() {

    }
}
