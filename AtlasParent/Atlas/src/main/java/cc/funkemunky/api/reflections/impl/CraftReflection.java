package cc.funkemunky.api.reflections.impl;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftReflection {
    public static WrappedClass craftHumanEntity = Reflections.getCBClass("entity.CraftHumanEntity"); //1.7-1.14
    public static WrappedClass craftEntity = Reflections.getCBClass("entity.CraftEntity"); //1.7-1.14
    public static WrappedClass craftItemStack = Reflections.getCBClass("inventory.CraftItemStack"); //1.7-1.14
    public static WrappedClass craftBlock = Reflections.getCBClass("block.CraftBlock"); //1.7-1.14
    public static WrappedClass craftPlayer = Reflections.getCBClass("entity.CraftPlayer");
    public static WrappedClass craftWorld = Reflections.getCBClass("CraftWorld"); //1.7-1.14
    public static WrappedClass craftInventoryPlayer = Reflections.getCBClass("inventory.CraftInventoryPlayer"); //1.7-1.14
    public static WrappedClass craftServer = Reflections.getCBClass("CraftServer"); //1.7-1.14\
    public static WrappedClass craftChunk = Reflections.getCBClass("CraftChunk");
    public static WrappedClass craftMagicNumbers = Reflections.getCBClass("util.CraftMagicNumbers");
    public static WrappedClass craftChatMessage = Reflections.getCBClass("util.CraftChatMessage");

    //Vanilla Instances
    private static WrappedMethod itemStackInstance = craftItemStack.getMethod("asNMSCopy", ItemStack.class); //1.7-1.14
    private static WrappedMethod humanEntityInstance = craftHumanEntity.getMethod("getHandle"); //1.7-1.14
    private static WrappedMethod entityInstance = craftEntity.getMethod("getHandle"); //1.7-1.14
    private static WrappedMethod blockInstance = craftBlock.getMethod(ProtocolVersion.getGameVersion()
            .isOrAbove(ProtocolVersion.v1_17_1) ? "getNMS" : "getNMSBlock"); //1.7-1.14
    private static WrappedMethod worldInstance = craftWorld.getMethod("getHandle"); //1.7-1.14
    private static WrappedMethod bukkitEntity = MinecraftReflection.entity.getMethod("getBukkitEntity"); //1.7-1.14
    private static WrappedMethod getInventory = craftInventoryPlayer.getMethod("getInventory"); //1.7-1.14
    private static WrappedMethod mcServerInstance = craftServer.getMethod("getServer"); //1.7-1.14
    private static WrappedMethod entityPlayerInstance = craftPlayer.getMethod("getHandle");
    private static WrappedMethod chunkInstance = craftChunk.getMethod("getHandle");
    private static WrappedMethod methodGetBlockFromMaterial = ProtocolVersion.getGameVersion()
            .isOrAbove(ProtocolVersion.V1_13) ? craftMagicNumbers.getMethod("getBlock", Material.class)
            : craftMagicNumbers.getMethod("getBlock", int.class);
    private static WrappedMethod fromComponent;

    public static <T> T getVanillaItemStack(ItemStack stack) {
        return itemStackInstance.invoke(null, stack);
    }

    public static <T> T getEntityHuman(HumanEntity entity) {
        return humanEntityInstance.invoke(entity);
    }

    public static <T> T getEntity(Entity entity) {
        return entityInstance.invoke(entity);
    }

    public static <T> T getEntityPlayer(Player player) {
        return entityPlayerInstance.invoke(player);
    }

    public static <T> T getVanillaBlock(Block block) {
        return blockInstance.invoke(block);
    }

    public static <T> T getVanillaWorld(World world) {
        return worldInstance.invoke(world);
    }

    public static Entity getBukkitEntity(Object vanillaEntity) {
        return bukkitEntity.invoke(vanillaEntity);
    }

    public static <T> T getVanillaInventory(Player player) {
        return getInventory.invoke(player.getInventory());
    }

    public static <T> T getMinecraftServer() {
        return mcServerInstance.invoke(Bukkit.getServer());
    }

    public static <T> T getVanillaChunk(Chunk chunk) {
        return chunkInstance.invoke(chunk);
    }

    public static <T> T getVanillaBlock(Material material) {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_13)) {
            return methodGetBlockFromMaterial.invoke(null, material);
        } else {
            return methodGetBlockFromMaterial.invoke(null, material.getId());
        }
    }

    public static String getMessageFromComp(Object ichatcomp, String defaultColor) {
        if(fromComponent == null) return "Not a usable version (1.8+ only)";
        return fromComponent.invoke(null, ichatcomp);
    }

    static {
        if(ProtocolVersion.getGameVersion().isOrAbove(ProtocolVersion.V1_8)) {
            fromComponent = craftChatMessage.getMethod("fromComponent",
                    MinecraftReflection.iChatBaseComponent.getParent());
        }
    }
}