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

import java.nio.charset.Charset;

@Getter
public class WrappedPacketDataSerializer extends NMSObject {

    public static WrappedClass vanillaClass = Reflections.getNMSClass("PacketDataSerializer");
    private static final WrappedMethod readBytesMethod = vanillaClass.getMethod("array"),
            hasArray = vanillaClass.getMethod("hasArray"),
            readableBytes = vanillaClass.getMethod("readableBytes");
    private static WrappedConstructor byteConst = vanillaClass.getConstructor(ByteBuf.class);

    public WrappedPacketDataSerializer(Object object) {
        super(object);
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

        setObject(pds);
    }

    public int readableBytes() {
        return fetch(readableBytes);
    }

    public byte[] getData() {
        return fetch(readBytesMethod);
    }

    public int getRefCount() {
        return fetch(vanillaClass.getMethod("refCnt"));
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

    public String toString(Charset set) {
       return vanillaClass.getMethod("toString", Charset.class).invoke(getObject(), set);
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
