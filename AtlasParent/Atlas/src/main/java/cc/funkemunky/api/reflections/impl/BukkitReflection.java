package cc.funkemunky.api.reflections.impl;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import org.bukkit.Chunk;
import org.bukkit.inventory.ItemStack;

public class BukkitReflection {
    public static WrappedField bukkitChunkField = MinecraftReflection.chunk.getFieldByType(Chunk.class, 0);
    public static WrappedClass spigotConfig;
    private static final WrappedMethod asBukkitCopyItemStack = CraftReflection.craftItemStack
            .getMethod("asBukkitCopy", MinecraftReflection.itemStack.getParent());

    public static boolean isBungeeMode() {
       if(spigotConfig == null) return false;

       return spigotConfig.getFieldByName("bungee").get(null);
    }

    public static Chunk getChunkFromVanilla(Object vanillaChunk) {
        return bukkitChunkField.get(vanillaChunk);
    }

    public static ItemStack getBukkitStackFromVanilla(Object object) {
        return asBukkitCopyItemStack.invoke(null, object);
    }

    static {
        if(Reflections.classExists("org.spigotmc.SpigotConfig")) {
            spigotConfig = Reflections.getClass("org.spigotmc.SpigotConfig");
        }
    }
}
