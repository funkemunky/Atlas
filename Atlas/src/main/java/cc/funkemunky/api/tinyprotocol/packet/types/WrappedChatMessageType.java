package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import lombok.RequiredArgsConstructor;

import java.util.Arrays;

@RequiredArgsConstructor
@Getter
public enum WrappedChatMessageType {
    CHAT(0),
    SYSTEM(1),
    GAME_INFO(2);

    private final int type;

    public byte getTypeAsByte() {
        return (byte)type;
    }

    public <T> T toNMS() {
        return (T) chatMsgTypeClass.getEnum(name());
    }

    private static WrappedClass chatMsgTypeClass;
    private static WrappedField chatMsgByteField;

    public static WrappedChatMessageType fromNMS(Object object) {
        if(object instanceof Byte) {
            return fromByte((byte)object);
        } else if(object instanceof Integer) {
            return fromByte((byte)(int)object);
        } else {
            return fromByte(chatMsgByteField.get(object));
        }
    }

    public static WrappedChatMessageType fromByte(byte b) {
        return Arrays.stream(values())
                .filter(type -> type.getTypeAsByte() == b).findFirst().orElse(WrappedChatMessageType.CHAT);
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_12)) {
            chatMsgTypeClass = Reflections.getNMSClass("ChatMessageType");
            chatMsgByteField = chatMsgTypeClass.getFieldByType(byte.class, 0);
        }
    }
}
