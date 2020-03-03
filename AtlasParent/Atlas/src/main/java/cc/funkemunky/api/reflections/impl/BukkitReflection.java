package cc.funkemunky.api.reflections.impl;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import org.bukkit.Chunk;

public class BukkitReflection {
    public static WrappedField bukkitChunkField = MinecraftReflection.chunk.getFieldByType(Chunk.class, 0);
    public static WrappedClass spigotConfig;

    public static boolean isBungeeMode() {
       if(spigotConfig == null) return false;

       return spigotConfig.getFieldByName("bungee").get(null);
    }

    public static Chunk getChunkFromVanilla(Object vanillaChunk) {
        return bukkitChunkField.get(vanillaChunk);
    }

    static {
        if(Reflections.classExists("org.spigotmc.SpigotConfig")) {
            spigotConfig = Reflections.getClass("org.spigotmc.SpigotConfig");
        }
    }
}
