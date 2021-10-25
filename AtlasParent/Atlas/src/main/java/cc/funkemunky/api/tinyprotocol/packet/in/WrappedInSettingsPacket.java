package cc.funkemunky.api.tinyprotocol.packet.in;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.WrappedEnumMainHand;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;

public class WrappedInSettingsPacket extends NMSObject {

    //Reflection fields
    private static WrappedClass packet = Reflections.getNMSClass(Client.SETTINGS);

    private static WrappedField fieldLocale, fieldView, fieldChatVisibility, fieldChatColors;
    //1.7.10 only
    private static WrappedField fieldVersion, fieldFlags, fieldShowCape;
    //1.9+ only
    private static WrappedField fieldMainHand;
    //1.8+ only
    private static WrappedField fieldSkinParts;

    public String locale;
    public int viewDistance;
    public WrappedChatVisibility chatVisibility = WrappedChatVisibility.FULL;
    public boolean chatColors;
    //1.7.10 only
    public int version = -1, flags = -1;
    public boolean showCape;
    //1.8+ only
    public int displayedSkinParts;
    //1.9+ only
    public WrappedEnumMainHand hand = WrappedEnumMainHand.RIGHT;

    public WrappedInSettingsPacket(Object object, Player player) {
        super(object, player);
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        locale = fetch(fieldLocale);
        viewDistance = fetch(fieldView);
        chatVisibility = WrappedChatVisibility.fromVanilla(fetch(fieldChatVisibility));
        chatColors = fetch(fieldChatColors);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            this.version = fetch(fieldVersion);
            flags = fetch(fieldFlags);
            showCape = fetch(fieldShowCape);
        } else {
            displayedSkinParts = fetch(fieldSkinParts);
            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9))
                hand = WrappedEnumMainHand.fromVanilla(fetch(fieldMainHand));
        }
    }

    @Override
    public void updateObject() {
        set(fieldLocale, locale);
        set(fieldView, viewDistance);
        set(fieldChatVisibility, chatVisibility);
        set(fieldChatColors, chatColors);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            set(fieldVersion, version);
            set(fieldFlags, flags);
            set(fieldShowCape, showCape);
        } else {
            set(fieldSkinParts, displayedSkinParts);
            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9))
                set(fieldMainHand, hand.toVanilla());
        }
    }

    @Getter
    @RequiredArgsConstructor
    public enum WrappedChatVisibility {
        FULL(0, "options.chat.visibility.full"),
        SYSTEM(1, "options.chat.visibility.system"),
        HIDDEN(2, "options.chat.visibility.hidden");

        static WrappedClass chatVisibilityClass = Reflections.getNMSClass(ProtocolVersion.getGameVersion()
                .isOrBelow(ProtocolVersion.V1_7_10)
                || ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_14)
                ? "EnumChatVisibility" : "EntityHuman$EnumChatVisibility");

        private final int id;
        private final String path;

        public <T> T toVanilla() {
            return (T) chatVisibilityClass.getEnum(toString());
        }

        public static WrappedChatVisibility fromVanilla(Object o) {
            if(o instanceof Enum) valueOf(o.toString());

            return FULL;
        }
    }

    static {
        fieldLocale = packet.getFieldByType(String.class, 0);
        fieldView = packet.getFieldByType(int.class, 0);
        fieldChatVisibility = packet.getFieldByType(WrappedChatVisibility.chatVisibilityClass.getParent(), 0);
        fieldChatColors = packet.getFieldByType(boolean.class, 0);

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)) {
            fieldVersion = packet.getFieldByType(int.class, 1);
            fieldFlags = packet.getFieldByType(int.class, 2);
            fieldShowCape = packet.getFieldByType(boolean.class, 1);
        } else {
            fieldSkinParts = packet.getFieldByType(int.class, 1);

            if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9))
                fieldMainHand = packet.getFieldByType(WrappedEnumMainHand.vanillaClass.getParent(), 0);
        }
    }
}
