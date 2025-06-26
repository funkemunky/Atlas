package cc.funkemunky.api.tinyprotocol.packet.types.enums;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;

public enum WrappedEnumAnimation {
    NONE,
    EAT,
    DRINK,
    BLOCK,
    BOW,
    SPEAR,
    CROSSBOW;

    private static WrappedClass enumAnimation;

    public static WrappedEnumAnimation fromNMS(Object vanillaObject) {
        Enum vanilla = (Enum) vanillaObject;

        return valueOf(vanilla.name().toUpperCase());
    }

    public Enum toVanilla() {
        return enumAnimation.getEnum(name().toUpperCase());
    }

    static {
        enumAnimation = Reflections.getNMSClass("EnumAnimation");
    }
}
