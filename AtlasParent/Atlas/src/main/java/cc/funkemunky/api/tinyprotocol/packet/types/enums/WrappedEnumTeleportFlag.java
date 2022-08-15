package cc.funkemunky.api.tinyprotocol.packet.types.enums;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import net.minecraft.server.v1_8_R3.PacketPlayOutPosition;

import java.util.EnumSet;
import java.util.Iterator;
import java.util.Set;

public enum WrappedEnumTeleportFlag {
    X(0),
    Y(1),
    Z(2),
    Y_ROT(3),
    X_ROT(4);

    private int f;
    private static final WrappedClass enumTeleportFlag = Reflections.getNMSClass("PacketPlayOutPosition$EnumPlayerTeleportFlags");

    WrappedEnumTeleportFlag(int var3) {
        this.f = var3;
    }

    private int a() {
        return 1 << this.f;
    }

    private boolean b(int var1) {
        return (var1 & this.a()) == this.a();
    }

    public static Set<WrappedEnumTeleportFlag> a(int var0) {
        EnumSet var1 = EnumSet.noneOf(PacketPlayOutPosition.EnumPlayerTeleportFlags.class);
        WrappedEnumTeleportFlag[] var2 = values();
        int var3 = var2.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            WrappedEnumTeleportFlag var5 = var2[var4];
            if (var5.b(var0)) {
                var1.add(var5);
            }
        }

        return var1;
    }

    public static int a(Set<WrappedEnumTeleportFlag> var0) {
        int var1 = 0;

        WrappedEnumTeleportFlag var3;
        for(Iterator<WrappedEnumTeleportFlag> var2 = var0.iterator(); var2.hasNext(); var1 |= var3.a()) {
            var3 = var2.next();
        }

        return var1;
    }

    public static WrappedEnumTeleportFlag fromObject(Enum var) {
        return values()[var.ordinal()];
    }

    public <T> T getObject() {
        return (T) enumTeleportFlag.getEnum(this.name());
    }
}
