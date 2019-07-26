package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import lombok.Getter;

import java.util.Arrays;

@Getter
public enum WrappedEnumParticle {
    EXPLOSION_NORMAL("explode", 0, true),
    EXPLOSION_LARGE("largeexplode", 1, true),
    EXPLOSION_HUGE("hugeexplosion", 2, true),
    FIREWORKS_SPARK("fireworksSpark", 3, false),
    WATER_BUBBLE("bubble", 4, false),
    WATER_SPLASH("splash", 5, false),
    WATER_WAKE("wake", 6, false),
    SUSPENDED("suspended", 7, false),
    SUSPENDED_DEPTH("depthsuspend", 8, false),
    CRIT("crit", 9, false),
    CRIT_MAGIC("magicCrit", 10, false),
    SMOKE_NORMAL("smoke", 11, false),
    SMOKE_LARGE("largesmoke", 12, false),
    SPELL("spell", 13, false),
    SPELL_INSTANT("instantSpell", 14, false),
    SPELL_MOB("mobSpell", 15, false),
    SPELL_MOB_AMBIENT("mobSpellAmbient", 16, false),
    SPELL_WITCH("witchMagic", 17, false),
    DRIP_WATER("dripWater", 18, false),
    DRIP_LAVA("dripLava", 19, false),
    VILLAGER_ANGRY("angryVillager", 20, false),
    VILLAGER_HAPPY("happyVillager", 21, false),
    TOWN_AURA("townaura", 22, false),
    NOTE("note", 23, false),
    PORTAL("portal", 24, false),
    ENCHANTMENT_TABLE("enchantmenttable", 25, false),
    FLAME("flame", 26, false),
    LAVA("lava", 27, false),
    FOOTSTEP("footstep", 28, false),
    CLOUD("cloud", 29, false),
    REDSTONE("reddust", 30, false),
    SNOWBALL("snowballpoof", 31, false),
    SNOW_SHOVEL("snowshovel", 32, false),
    SLIME("slime", 33, false),
    HEART("heart", 34, false),
    BARRIER("barrier", 35, false),
    ITEM_CRACK("iconcrack_", 36, false, 2),
    BLOCK_CRACK("blockcrack_", 37, false, 1),
    BLOCK_DUST("blockdust_", 38, false, 1),
    WATER_DROP("droplet", 39, false),
    ITEM_TAKE("take", 40, false),
    MOB_APPEARANCE("mobappearance", 41, true);

    private String name;
    private int value;
    private boolean something;
    private int data;

    WrappedEnumParticle(String name, int value, boolean something) {
        this.name = name;
        this.value = value;
        this.something = something;
    }

    WrappedEnumParticle(String name, int value, boolean something, int data) {
        this.name = name;
        this.value = value;
        this.something = something;
        this.data = data;
    }

    public static WrappedEnumParticle getByName(String name) {
        return Arrays.stream(values()).filter(val -> val.getName().equalsIgnoreCase(name)).findFirst().orElse(null);
    }

    public Object toNMS() {
        return Reflections.getNMSClass("EnumParticle").getEnum(getByName(name).name());
    }
}
