package cc.funkemunky.api.tinyprotocol.packet.types.v1_13;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.tinyprotocol.GeneralObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;

public class WrappedStringRange extends GeneralObject {

    public static WrappedClass srClass;
    private static WrappedField startField;
    private static WrappedField endField;
    private static boolean canUse;

    public int start, end;

    public WrappedStringRange(int start, int end) {
        super(srClass);
        this.start = start;
        this.end = end;

        if(canUse) wrap(start, end);
    }

    public WrappedStringRange(Object object) {
        super(srClass);

        if(canUse) {
            start = startField.get(object);
            end = endField.get(object);
        }
    }

    static {
        if(canUse = ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            srClass = Reflections.getClass("com.mojang.brigadier.context.StringRange");
            startField = srClass.getFieldByName("start");
            endField = srClass.getFieldByName("end");
        }
    }
}
