package cc.funkemunky.api.reflection;

import cc.funkemunky.api.tinyprotocol.api.packets.reflections.Reflections;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.packets.reflections.types.WrappedField;
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
    public static WrappedField itemStackInstance = craftItemStack.getFieldByName("handle");
    public static WrappedMethod humanEntityInstance = craftHumanEntity.getMethod("getHandle");
    public static WrappedMethod blockInstance = craftBlock.getMethod("getNMSBlock");
    public static WrappedMethod worldInstance = craftWorld.getMethod("getHandle");

    public static Object getVanillaItemStack(ItemStack stack) {
        return itemStackInstance.get(stack);
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