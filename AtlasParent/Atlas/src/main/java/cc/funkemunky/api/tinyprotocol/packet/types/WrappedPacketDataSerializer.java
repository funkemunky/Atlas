package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

@Getter
public class WrappedPacketDataSerializer extends NMSObject {

    public static WrappedClass vanillaClass = Reflections.getNMSClass("PacketDataSerializer");
    private static WrappedMethod readBytesMethod = vanillaClass.getMethod("array");
    private static WrappedMethod hasArray = vanillaClass.getMethod("hasArray");
    private static WrappedConstructor byteConst = vanillaClass.getConstructor(ByteBuf.class);

    private byte[] data;

    public WrappedPacketDataSerializer(Object object) {
        super(object);

        boolean hasArray = WrappedPacketDataSerializer.hasArray.invoke(object);

        if(hasArray)
        data = readBytesMethod.invoke(object);
        else data = new byte[0];
    }

    public WrappedPacketDataSerializer(ByteBuf buf) {
        setObject(byteConst.newInstance(buf));
    }

    @Override
    public void updateObject() {
        //Empty method
    }

    public WrappedPacketDataSerializer(byte[] data) {
        Object pds = byteConst.newInstance(Unpooled.wrappedBuffer(data));

        this.data = data;
        setObject(pds);
    }

    public void d(int dint) {
        vanillaClass.getMethod("d", int.class).invoke(getObject(), dint);
    }

    public void writeInt(int integer) {
        vanillaClass.getMethod("writeInt", int.class).invoke(getObject(), integer);
    }

    public void writeDouble(double doubleFloat) {
        vanillaClass.getMethod("writeDouble", double.class).invoke(getObject(), doubleFloat);
    }

    public void writeFloat(float floating) {
        vanillaClass.getMethod("writeFloat", float.class).invoke(getObject(), floating);
    }

    public void writeBoolean(boolean bool) {
        vanillaClass.getMethod("writeBoolean", boolean.class).invoke(getObject(), bool);
    }

    public ItemStack getItemStack() {
        return MinecraftReflection.toBukkitItemStack(vanillaClass
                .getMethodByType(MinecraftReflection.itemStack.getParent(), 0).invoke(getObject()));
    }

    public void writeItemStack(ItemStack stack) {
        vanillaClass.getMethod("a", MinecraftReflection.itemStack.getParent()).invoke(getObject(),
                CraftReflection.getVanillaItemStack(stack));
    }
}
