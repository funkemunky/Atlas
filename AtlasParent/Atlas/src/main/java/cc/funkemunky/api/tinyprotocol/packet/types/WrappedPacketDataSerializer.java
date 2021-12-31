package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.impl.CraftReflection;
import cc.funkemunky.api.reflections.impl.MinecraftReflection;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedField;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import lombok.Getter;
import org.bukkit.inventory.ItemStack;

import java.nio.charset.Charset;

@Getter
public class WrappedPacketDataSerializer extends NMSObject {

    public static WrappedClass vanillaClass = Reflections.getNMSClass("PacketDataSerializer"),
            byteBufClass = Reflections.getUtilClass("io.netty.buffer.ByteBuf"),
            unpooledClass = Reflections.getUtilClass("io.netty.buffer.Unpooled"),
            emptyByteBuf = Reflections.getUtilClass("io.netty.buffer.EmptyByteBuf");
    private static final WrappedMethod readBytesMethod = vanillaClass.getMethod("array"),
            hasArray = vanillaClass.getMethod("hasArray"),
            readableBytes = vanillaClass.getMethod("readableBytes"),
            copyMethod = byteBufClass.getMethod("copy");
    private static final WrappedField fieldByteBuf = vanillaClass.getFieldByType(byteBufClass.getParent(), 0);
    private static WrappedConstructor byteConst = vanillaClass.getConstructor(byteBufClass.getParent());

    private boolean empty;

    public WrappedPacketDataSerializer(Object object) {
        super(!vanillaClass.getParent().isInstance(object) ? byteConst.newInstance(object)
                : object);
    }

    @Override
    public void updateObject() {
        //Empty method
    }

    public WrappedPacketDataSerializer(byte[] data) {
        Object pds = byteConst.newInstance((Object)unpooledClass.getMethod("wrappedBuffer", byte[].class)
                .invoke(null, data));

        setObject(pds);
    }

    public void copy() {
        setObject(byteConst.newInstance(
                byteBufClass.getParent().cast(fetch(copyMethod))));
    }

    public int readableBytes() {
        if(empty) return 0;
        return fetch(readableBytes);
    }

    public byte[] getData() {
        byte[] bytes = new byte[readableBytes()];
        if(bytes.length > 0)
        vanillaClass.getMethod("readBytes", byte[].class).invoke(getObject(), bytes);

        return bytes;
    }

    public int getRefCount() {
        if(empty) return 0;
        return fetch(vanillaClass.getMethod("refCnt"));
    }

    public void d(int dint) {
        if(empty) return;
        vanillaClass.getMethod("d", int.class).invoke(getObject(), dint);
    }

    public void writeInt(int integer) {
        if(empty) return;
        vanillaClass.getMethod("writeInt", int.class).invoke(getObject(), integer);
    }

    public void writeDouble(double doubleFloat) {
        if(empty) return;
        vanillaClass.getMethod("writeDouble", double.class).invoke(getObject(), doubleFloat);
    }

    public void writeFloat(float floating) {
        if(empty) return;
        vanillaClass.getMethod("writeFloat", float.class).invoke(getObject(), floating);
    }

    public void writeBoolean(boolean bool) {
        if(empty) return;
        vanillaClass.getMethod("writeBoolean", boolean.class).invoke(getObject(), bool);
    }

    public String toString(Charset set) {
        if(empty) return "";
       return vanillaClass.getMethod("toString", Charset.class).invoke(getObject(), set);
    }
    public ItemStack getItemStack() {
        if(empty) return null;
        return MinecraftReflection.toBukkitItemStack(vanillaClass
                .getMethodByType(MinecraftReflection.itemStack.getParent(), 0).invoke(getObject()));
    }

    public void writeItemStack(ItemStack stack) {
        if(empty) return;
        vanillaClass.getMethod("a", MinecraftReflection.itemStack.getParent()).invoke(getObject(),
                CraftReflection.getVanillaItemStack(stack));
    }
}
