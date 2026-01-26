package cc.funkemunky.api.tinyprotocol.packet.out;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedChatMessageType;
import cc.funkemunky.api.utils.MiscUtils;
import lombok.Getter;
import lombok.Setter;
import net.md_5.bungee.api.chat.BaseComponent;
import net.md_5.bungee.api.chat.TextComponent;
import net.md_5.bungee.chat.ComponentSerializer;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_14_R1.inventory.CraftBlastingRecipe;
import org.bukkit.entity.Player;

import java.util.UUID;
import java.util.logging.Level;

@Getter
@Setter
public class WrappedOutChatPacket extends NMSObject {
    private final static String packet = Server.CHAT;
    public static UUID b = new UUID(0L, 0L);

    public WrappedOutChatPacket(Object packetObj, Player player) {
        super(packetObj, player);
    }

    //Constructor (ichatbase, chattype);
    public WrappedOutChatPacket(TextComponent message, WrappedChatMessageType type) {
        setObject(constructor.newInstance());
        this.message = message;
        this.chatType = type;
        updateObject();
    }

    //1.16+ only
    public WrappedOutChatPacket(TextComponent message, WrappedChatMessageType chatType, UUID uuid) {
        setObject(constructor.newInstance());
        this.message = message;
        this.chatType = chatType;
        this.uuid = uuid;
        updateObject();
    }

    //Saving your fingers from the most common use for using this wrapper.
    public WrappedOutChatPacket(TextComponent message) {
        this(message, WrappedChatMessageType.CHAT);
    }

    private static WrappedClass chatBaseComp = Reflections.getNMSClass("IChatBaseComponent");
    private static WrappedClass outChatClass = Reflections.getNMSClass(packet);
    private static WrappedConstructor constructor = outChatClass.getConstructor();
    private static WrappedClass chatSerialClass = Reflections.getNMSClass("IChatBaseComponent$ChatSerializer");
    private static WrappedMethod stcToComponent = chatSerialClass.getMethod("a", String.class);
    private static WrappedMethod getTextMethod = chatBaseComp.getMethod("getText");
    private static WrappedField chatTypeField, chatCompField, fieldUUID, fieldComponents;

    private TextComponent message;
    private WrappedChatMessageType chatType;
    private UUID uuid = b;

    @Override
    public void process(Player player, ProtocolVersion version) {
        //Getting the message

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            message = new TextComponent(ComponentSerializer.parse(getTextMethod.invoke(getObject())));
        } else {
            BaseComponent[] components = fetch(fieldComponents);

            if (components != null) {
                message = new TextComponent(components);
            } else {
                Object chatComp = fetch(chatCompField);

                if (chatComp != null) {
                    message = new TextComponent(CraftReflection.getMessageFromComp(chatComp, "WHITE"));
                }
            }
        }


        //Getting the chat type.
        chatType = WrappedChatMessageType.fromNMS(fetch(chatTypeField));

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
            uuid = fetch(fieldUUID);
        }
    }

    @Override
    public void updateObject() {
        set(fieldComponents, MiscUtils.toComponentArray(message));
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_12)) {
            set(chatTypeField, chatType.toNMS());
        } else if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            set(chatTypeField, chatType.getTypeAsByte());
        } else set(chatTypeField, chatType.getType());

        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16))
            set(fieldUUID, b);
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_12)) {
            chatTypeField = outChatClass.getFieldByType(Reflections.getNMSClass("ChatMessageType").getParent(), 0);
        } else if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_7_10)) {
            chatTypeField = outChatClass.getFieldByType(byte.class, 0);
        } else {
            chatTypeField = outChatClass.getFieldByType(int.class, 0);
        }
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.v1_16)) {
            fieldUUID = fetchField(outChatClass, UUID.class, 0);
        }
        chatCompField = outChatClass.getFieldByType(chatBaseComp.getParent(), 0);
        fieldComponents = fetchField(outChatClass, BaseComponent[].class, 0);
    }
}
