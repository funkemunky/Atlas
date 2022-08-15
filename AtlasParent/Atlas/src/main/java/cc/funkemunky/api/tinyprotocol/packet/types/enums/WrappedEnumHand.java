package cc.funkemunky.api.tinyprotocol.packet.types.enums;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;

public enum WrappedEnumHand {
    MAIN_HAND,
    OFF_HAND;

    public static WrappedClass enumHandClass;

    public static WrappedEnumHand getFromVanilla(Object object) {
        if(enumHandClass == null) return WrappedEnumHand.MAIN_HAND;

        if(object instanceof Enum)
            return WrappedEnumHand.values()[((Enum)object).ordinal()];

        return WrappedEnumHand.MAIN_HAND;
    }

    public <T> T toEnumHand() {
        return (T) enumHandClass.getEnum(name());
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            enumHandClass = Reflections.getNMSClass("EnumHand");
        }
    }
}
