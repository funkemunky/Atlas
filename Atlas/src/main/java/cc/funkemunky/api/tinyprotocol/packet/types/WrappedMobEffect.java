package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import lombok.Getter;
import org.bukkit.craftbukkit.libs.org.eclipse.sisu.bean.IgnoreSetters;
import org.bukkit.entity.Player;

@Getter
@IgnoreSetters
public class WrappedMobEffect extends NMSObject {

    public WrappedMobEffect(Object object) {
        super(object);
    }

    private static final WrappedClass classMobEffect = Reflections.getNMSClass("MobEffect");

    private int effectId, amplifier, duration;
    private boolean splash, ambient, particles;

    public WrappedMobEffect(int effectId, int amplifier, int duration, boolean splash, boolean ambient, boolean particles) {
        super((Object)classMobEffect.getConstructor(int.class, int.class, int.class, boolean.class, boolean.class)
                .newInstance(effectId, amplifier, duration, ambient, particles));
        this.effectId = effectId;
        this.amplifier = amplifier;
        this.duration = duration;
        this.splash = splash;
        this.ambient = ambient;
        this.particles = particles;
    }

    @Override
    public void process(Player player, ProtocolVersion version) {
        effectId = fetch(classMobEffect.getFieldByName("effectId"));
        amplifier = fetch(classMobEffect.getFieldByName("amplifier"));
        duration = fetch(classMobEffect.getFieldByName("duration"));
        splash = fetch(classMobEffect.getFieldByName("splash"));
        ambient = fetch(classMobEffect.getFieldByName("ambient"));
        particles = fetch(classMobEffect.getFieldByName("particles"));
    }

    @Override
    public void updateObject() {
        set(classMobEffect.getFieldByName("effectId"), effectId);
        set(classMobEffect.getFieldByName("amplifier"), amplifier);
        set(classMobEffect.getFieldByName("duration"), duration);
        set(classMobEffect.getFieldByName("splash"), splash);
        set(classMobEffect.getFieldByName("ambient"), ambient);
        set(classMobEffect.getFieldByName("particles"), particles);
    }
}
