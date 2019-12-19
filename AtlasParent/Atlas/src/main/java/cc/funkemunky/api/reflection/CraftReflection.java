package cc.funkemunky.api.reflection;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class CraftReflection {
    public static WrappedClass craftHumanEntity = Reflections.getCBClass("entity.CraftHumanEntity");
    public static WrappedClass craftEntity = Reflections.getCBClass("entity.CraftEntity");
    public static WrappedClass craftItemStack = Reflections.getCBClass("inventory.CraftItemStack");
    public static WrappedClass craftBlock = Reflections.getCBClass("block.CraftBlock");
    public static WrappedClass craftWorld = Reflections.getCBClass("CraftWorld");
    public static WrappedClass craftInventoryPlayer = Reflections.getCBClass("inventory.CraftInventoryPlayer");

    //Vanilla Instances
    public static WrappedMethod itemStackInstance = craftItemStack.getMethod("asNMSCopy", ItemStack.class);
    public static WrappedMethod humanEntityInstance = craftHumanEntity.getMethod("getHandle");
    public static WrappedMethod entityInstance = craftEntity.getMethod("getHandle");
    public static WrappedMethod blockInstance = craftBlock.getMethod("getNMSBlock");
    public static WrappedMethod worldInstance = craftWorld.getMethod("getHandle");
    public static WrappedMethod bukkitEntity = MinecraftReflection.entity.getMethod("getBukkitEntity");
    public static WrappedMethod getInventory = craftInventoryPlayer.getMethod("getInventory");

    public static <T> T getVanillaItemStack(ItemStack stack) {
        return itemStackInstance.invoke(null, stack);
    }

    public static <T> T getEntityHuman(HumanEntity entity) {
        return humanEntityInstance.invoke(entity);
    }

    public static <T> T getEntity(Entity entity) {
        return entityInstance.invoke(entity);
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
}