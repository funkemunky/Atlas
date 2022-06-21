package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;

public enum WrappedEnumMainHand {
    LEFT("options.mainHand.left"),
    RIGHT("options.mainHand.right");

    private final String c;
    public static WrappedClass vanillaClass;

    WrappedEnumMainHand(String var2) {
        this.c = var2;
    }

    public String toString() {
        return c;
    }

    public <T> T toVanilla() {
        return (T) vanillaClass.getEnum(name());
    }

    public static WrappedEnumMainHand fromVanilla(Object object) {
        if(object instanceof Enum) {
            return valueOf(((Enum)object).name());
        }
        return null;
    }

    static {
        if(ProtocolVersion.getGameVersion().isAbove(ProtocolVersion.V1_8_9))
            vanillaClass = Reflections.getNMSClass("EnumMainHand");
    }
}
