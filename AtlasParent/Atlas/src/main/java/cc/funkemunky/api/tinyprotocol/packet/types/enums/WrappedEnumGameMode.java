package cc.funkemunky.api.tinyprotocol.packet.types.enums;

import cc.funkemunky.api.reflections.Reflections;

public enum WrappedEnumGameMode {
    NOT_SET(-1, ""),
    SURVIVAL(0, "survival"),
    CREATIVE(1, "creative"),
    ADVENTURE(2, "adventure"),
    SPECTATOR(3, "spectator");

    int f;
    String g;

    WrappedEnumGameMode(int var3, String var4) {
        this.f = var3;
        this.g = var4;
    }

    public int getId() {
        return this.f;
    }

    public String b() {
        return this.g;
    }

    public boolean c() {
        return this == ADVENTURE || this == SPECTATOR;
    }

    public boolean d() {
        return this == CREATIVE;
    }

    public boolean e() {
        return this == SURVIVAL || this == ADVENTURE;
    }

    public static WrappedEnumGameMode getById(int var0) {
        WrappedEnumGameMode[] var1 = values();
        int var2 = var1.length;

        for(int var3 = 0; var3 < var2; ++var3) {
            WrappedEnumGameMode var4 = var1[var3];
            if (var4.f == var0) {
                return var4;
            }
        }

        return SURVIVAL;
    }

    public static WrappedEnumGameMode getByName(String name) {
        for(WrappedEnumGameMode var : values()) {
            if(!var.name().equals(name)) continue;
            return var;
        }
        return SURVIVAL;
    }

    public Object getObject(WrappedEnumGameMode gamemode) {
        return Reflections.getNMSClass("EnumGameMode").getEnum(gamemode.name());
    }

    public Object getObject() {
        return getObject(getById(f));
    }

    public static WrappedEnumGameMode fromObject(Enum var) {
        return getByName(var.name());
    }
}
