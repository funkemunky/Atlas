package cc.funkemunky.api.utils;

import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class PotionEffectBuilder {
    private int amplifier;
    private int duration;
    private PotionEffectType type;
    private boolean ambient;
    private boolean particles;

    private PotionEffectBuilder() {
        duration = 100;
        amplifier = 0;
        type = PotionEffectType.SPEED;
        ambient = false;
        particles = true;
    }

    public PotionEffectBuilder amplifier(int amplifier) {
        this.amplifier = amplifier;
        return this;
    }

    public PotionEffectBuilder duration(int duration) {
        this.duration = duration;
        return this;
    }

    public PotionEffectBuilder type(PotionEffectType type) {
        this.type = type;
        return this;
    }

    public PotionEffectBuilder ambient(boolean ambient) {
        this.ambient = ambient;
        return this;
    }

    public PotionEffectBuilder particles(boolean particles) {
        this.particles = particles;
        return this;
    }

    public PotionEffect build() {
        return new PotionEffect(type, duration, amplifier, ambient, particles);
    }


    public static PotionEffectBuilder builder() {
        return new PotionEffectBuilder();
    }
}
