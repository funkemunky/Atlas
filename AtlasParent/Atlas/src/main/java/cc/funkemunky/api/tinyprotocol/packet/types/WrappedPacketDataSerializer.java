package cc.funkemunky.api.tinyprotocol.packet.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedConstructor;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import cc.funkemunky.api.tinyprotocol.api.NMSObject;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.Unpooled;
import lombok.Getter;

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

    @Override
    public void updateObject() {
        //Empty method
    }

    public WrappedPacketDataSerializer(byte[] data) {
        Object pds = byteConst.newInstance(Unpooled.wrappedBuffer(data));

        this.data = data;
        setObject(pds);
    }
}
