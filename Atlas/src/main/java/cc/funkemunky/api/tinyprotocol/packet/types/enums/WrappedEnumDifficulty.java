package cc.funkemunky.api.tinyprotocol.packet.types.enums;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;

public enum WrappedEnumDifficulty {
    PEACEFUL(0, "options.difficulty.peaceful"),
    EASY(1, "options.difficulty.easy"),
    NORMAL(2, "options.difficulty.normal"),
    HARD(3, "options.difficulty.hard");

    private static final WrappedEnumDifficulty[] e = new WrappedEnumDifficulty[values().length];
    private final int f;
    private final String g;
    private static final WrappedClass enumDifficulty = Reflections.getNMSClass("EnumDifficulty");

    private WrappedEnumDifficulty(int var3, String var4) {
        this.f = var3;
        this.g = var4;
    }

    public int a() {
        return this.f;
    }

    public static WrappedEnumDifficulty getById(int var0) {
        return e[var0 % e.length];
    }

    public String b() {
        return this.g;
    }

    static {
        WrappedEnumDifficulty[] var0 = values();
        int var1 = var0.length;

        for(int var2 = 0; var2 < var1; ++var2) {
            WrappedEnumDifficulty var3 = var0[var2];
            e[var3.f] = var3;
        }

    }

    public static WrappedEnumDifficulty getByName(String name) {
        for(WrappedEnumDifficulty var : values()) {
            if(!var.name().equals(name)) continue;
            return var;
        }
        return PEACEFUL;
    }

    public Object getObject() {
        return enumDifficulty.getEnum(this.name());
    }

    public static WrappedEnumDifficulty fromObject(Enum var) {
        return getByName(var.name());
    }
}
