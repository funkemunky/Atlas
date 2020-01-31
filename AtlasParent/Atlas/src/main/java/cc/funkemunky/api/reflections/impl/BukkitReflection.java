package cc.funkemunky.api.reflections.impl;

import cc.funkemunky.api.reflections.types.WrappedField;
import org.bukkit.Chunk;

public class BukkitReflection {
    public static WrappedField bukkitChunkField = MinecraftReflection.chunk.getFieldByType(Chunk.class, 0);

    public static Chunk getChunkFromVanilla(Object vanillaChunk) {
        return bukkitChunkField.get(vanillaChunk);
    }
}
