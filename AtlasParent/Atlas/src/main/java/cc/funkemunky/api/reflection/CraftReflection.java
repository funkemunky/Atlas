package cc.funkemunky.api.reflection;

import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.types.WrappedMethod;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.HumanEntity;
import org.bukkit.inventory.ItemStack;

public class CraftReflection {
    public static WrappedClass craftHumanEntity = Reflections.getCBClass("CraftHumanEntity");
    public static WrappedClass craftItemStack = Reflections.getCBClass("CraftItemStack");
    public static WrappedClass craftBlock = Reflections.getCBClass("CraftBlock");
    public static WrappedClass craftWorld = Reflections.getCBClass("CraftWorld");

    //Vanilla Instances
    public static WrappedMethod itemStackInstance = craftItemStack.getMethodByType(MinecraftReflection.itemStack.getParent(), 0);
    public static WrappedMethod humanEntityInstance = craftHumanEntity.getMethodByType(MinecraftReflection.entityHuman.getParent(), 0);
    public static WrappedMethod blockInstance = craftBlock.getMethodByType(MinecraftReflection.block.getParent(), 0);
    public static WrappedMethod worldInstance = craftWorld.getMethodByType(MinecraftReflection.worldServer.getParent(), 0);

    public static Object getVanillaItemStack(ItemStack stack) {
        return itemStackInstance.invoke(stack);
    }

    public static Object getEntityHuman(HumanEntity entity) {
        return humanEntityInstance.invoke(entity);
    }

    public static Object getVanillaBlock(Block block) {
        return blockInstance.invoke(block);
    }

    public static Object getVanillaWorld(World world) {
        return worldInstance.invoke(world);
    }
}
