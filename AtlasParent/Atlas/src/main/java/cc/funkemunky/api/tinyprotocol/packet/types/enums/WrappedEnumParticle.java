package cc.funkemunky.api.tinyprotocol.packet.types.enums;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.Particle;

import java.util.Arrays;

@Getter
public enum WrappedEnumParticle {
    EXPLOSION_NORMAL,
    EXPLOSION_LARGE,
    EXPLOSION_HUGE,
    FIREWORKS_SPARK,
    WATER_BUBBLE,
    WATER_SPLASH,
    WATER_WAKE,
    SUSPENDED,
    SUSPENDED_DEPTH,
    CRIT,
    CRIT_MAGIC,
    SMOKE_NORMAL,
    SMOKE_LARGE,
    SPELL,
    SPELL_INSTANT,
    SPELL_MOB,
    SPELL_MOB_AMBIENT,
    SPELL_WITCH,
    DRIP_WATER,
    DRIP_LAVA,
    VILLAGER_ANGRY,
    VILLAGER_HAPPY,
    TOWN_AURA,
    NOTE,
    PORTAL,
    ENCHANTMENT_TABLE,
    FLAME,
    LAVA,
    CLOUD,
    REDSTONE,
    SNOWBALL,
    SNOW_SHOVEL,
    SLIME,
    HEART,
    BARRIER,
    ITEM_CRACK,
    BLOCK_CRACK,
    BLOCK_DUST,
    WATER_DROP,
    MOB_APPEARANCE,
    DRAGON_BREATH,
    END_ROD,
    DAMAGE_INDICATOR,
    SWEEP_ATTACK,
    FALLING_DUST,
    TOTEM,
    SPIT,
    SQUID_INK,
    BUBBLE_POP,
    CURRENT_DOWN,
    BUBBLE_COLUMN_UP,
    NAUTILUS,
    DOLPHIN,
    LEGACY_BLOCK_CRACK,
    LEGACY_BLOCK_DUST,
    LEGACY_FALLING_DUST;

    private static WrappedClass particle, craftParticle, nmsParticle;
    private static WrappedMethod toNMS;

    public static WrappedEnumParticle getByName(String name) {
        return Arrays.stream(values()).filter(val -> val.getName().equalsIgnoreCase(name))
                .findFirst().orElse(null);
    }

    public <T> T toNMS() {
        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_9)) {
            return (T) Reflections.getNMSClass("EnumParticle").getEnum(name());
        }
        return toNMS.invoke(null, Particle.valueOf(getName()));
    }

    public String getName() {
        String name = this.name();

        if(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)) {
            name = name.replace("LEGACY_", "");
        }

        return name;
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_9)) {
            particle = Reflections.getClass("org.bukkit.Particle");
            craftParticle = Reflections.getCBClass("CraftParticle");
            nmsParticle = Reflections.getNMSClass(ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_13)
                    ? "EnumParticle" : "Particle");
            toNMS = craftParticle.getMethod("toNMS", Particle.class);
        }
    }
}
