package cc.funkemunky.api.tinyprotocol.packet.types.enums;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.tinyprotocol.packet.types.MathHelper;
import cc.funkemunky.api.utils.math.IntVector;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;

public enum WrappedEnumDirection {
    DOWN(1, -1, "down", WrappedEnumDirection.EnumAxisDirection.NEGATIVE, WrappedEnumDirection.EnumAxis.Y, new IntVector(0, -1, 0)),
    UP(0, -1, "up", WrappedEnumDirection.EnumAxisDirection.POSITIVE, WrappedEnumDirection.EnumAxis.Y, new IntVector(0, 1, 0)),
    NORTH(3, 2, "north", WrappedEnumDirection.EnumAxisDirection.NEGATIVE, WrappedEnumDirection.EnumAxis.Z, new IntVector(0, 0, -1)),
    SOUTH(2, 0, "south", WrappedEnumDirection.EnumAxisDirection.POSITIVE, WrappedEnumDirection.EnumAxis.Z, new IntVector(0, 0, 1)),
    WEST(5, 1, "west", WrappedEnumDirection.EnumAxisDirection.NEGATIVE, WrappedEnumDirection.EnumAxis.X, new IntVector(-1, 0, 0)),
    EAST(4, 3, "east", WrappedEnumDirection.EnumAxisDirection.POSITIVE, WrappedEnumDirection.EnumAxis.X, new IntVector(1, 0, 0));

    private static final WrappedEnumDirection[] n = new WrappedEnumDirection[6];
    private static final WrappedEnumDirection[] o = new WrappedEnumDirection[4];
    private static final Map<String, WrappedEnumDirection> p = new HashMap<>();

    static {
        WrappedEnumDirection[] var0 = values();

        for (WrappedEnumDirection var3 : var0) {
            n[var3.ordinal()] = var3;
            if (var3.k().c()) {
                o[var3.i] = var3;
            }

            p.put(var3.j().toLowerCase(), var3);
        }

    }

    private final int h;
    private final int i;
    private final String j;
    private final WrappedEnumDirection.EnumAxis k;
    private final WrappedEnumDirection.EnumAxisDirection l;
    private final IntVector m;
    public static WrappedClass enumDirection;

    private WrappedEnumDirection(int order, int offset, String direction, WrappedEnumDirection.EnumAxisDirection axisDirection, WrappedEnumDirection.EnumAxis axis, IntVector offsetPosition) {
        this.i = offset;
        this.h = order;
        this.j = direction;
        this.k = axis;
        this.l = axisDirection;
        this.m = offsetPosition;
    }

    public static WrappedEnumDirection fromType1(int var0) {
        return n[MathHelper.a(var0 % n.length)];
    }

    public static WrappedEnumDirection fromType2(int var0) {
        return o[MathHelper.a(var0 % o.length)];
    }

    public static WrappedEnumDirection fromAngle(double var0) {
        return fromType2(MathHelper.floor(var0 / 90.0D + 0.5D) & 3);
    }

    public static WrappedEnumDirection a(Random var0) {
        return values()[var0.nextInt(values().length)];
    }

    public static WrappedEnumDirection a(WrappedEnumDirection.EnumAxisDirection var0, WrappedEnumDirection.EnumAxis var1) {
        WrappedEnumDirection[] var2 = values();
        int var3 = var2.length;

        for (int var4 = 0; var4 < var3; ++var4) {
            WrappedEnumDirection var5 = var2[var4];
            if (var5.c() == var0 && var5.k() == var1) {
                return var5;
            }
        }

        throw new IllegalArgumentException("No such direction: " + var0 + " " + var1);
    }

    public int b() {
        return this.i;
    }

    public WrappedEnumDirection.EnumAxisDirection c() {
        return this.l;
    }

    public WrappedEnumDirection opposite() {
        return fromType1(this.h);
    }

    public int getAdjacentX() {
        return this.k == WrappedEnumDirection.EnumAxis.X ? this.l.a() : 0;
    }

    public int getAdjacentY() {
        return this.k == WrappedEnumDirection.EnumAxis.Y ? this.l.a() : 0;
    }

    public int getAdjacentZ() {
        return this.k == WrappedEnumDirection.EnumAxis.Z ? this.l.a() : 0;
    }

    public String j() {
        return this.j;
    }

    public WrappedEnumDirection.EnumAxis k() {
        return this.k;
    }

    public String toString() {
        return this.j;
    }

    public String getName() {
        return this.j;
    }

    public static WrappedEnumDirection fromVanilla(Enum object) {
        return Arrays.stream(values()).filter(val -> val.name().equals(object.name())).findFirst()
                .orElse(WrappedEnumDirection.UP);
    }

    public <T> T toVanilla() {
        return (T) enumDirection.getEnum(name());
    }

    public static enum EnumDirectionLimit {
        HORIZONTAL,
        VERTICAL;

        public boolean a(WrappedEnumDirection var1) {
            return var1 != null && var1.k().d() == this;
        }
    }

    public static enum EnumAxisDirection {
        POSITIVE(1, "Towards positive"),
        NEGATIVE(-1, "Towards negative");

        private final int c;
        private final String d;

        private EnumAxisDirection(int var3, String var4) {
            this.c = var3;
            this.d = var4;
        }

        public int a() {
            return this.c;
        }

        public String toString() {
            return this.d;
        }
    }

    public static enum EnumAxis {
        X("x", WrappedEnumDirection.EnumDirectionLimit.HORIZONTAL),
        Y("y", WrappedEnumDirection.EnumDirectionLimit.VERTICAL),
        Z("z", WrappedEnumDirection.EnumDirectionLimit.HORIZONTAL);

        private static final Map<String, EnumAxis> d = new HashMap<>();

        static {
            WrappedEnumDirection.EnumAxis[] var0 = values();
            int var1 = var0.length;

            for (int var2 = 0; var2 < var1; ++var2) {
                WrappedEnumDirection.EnumAxis var3 = var0[var2];
                d.put(var3.a().toLowerCase(), var3);
            }
        }

        private final String e;
        private final WrappedEnumDirection.EnumDirectionLimit f;

        private EnumAxis(String var3, WrappedEnumDirection.EnumDirectionLimit var4) {
            this.e = var3;
            this.f = var4;
        }

        public String a() {
            return this.e;
        }

        public boolean b() {
            return this.f == WrappedEnumDirection.EnumDirectionLimit.VERTICAL;
        }

        public boolean c() {
            return this.f == WrappedEnumDirection.EnumDirectionLimit.HORIZONTAL;
        }

        public String toString() {
            return this.e;
        }

        public boolean a(WrappedEnumDirection var1) {
            return var1 != null && var1.k() == this;
        }

        public WrappedEnumDirection.EnumDirectionLimit d() {
            return this.f;
        }

        public String getName() {
            return this.e;
        }
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            enumDirection = Reflections.getNMSClass("EnumDirection");
        }
    }
}
