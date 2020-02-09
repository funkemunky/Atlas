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

    private static WrappedClass wrapper = Reflections.getNMSClass("PacketDataSerializer");
    private static WrappedMethod readBytesMethod = wrapper.getMethodByType(byte[].class, 0);
    private static WrappedConstructor byteConst = wrapper.getConstructor(ByteBuf.class);

    private byte[] data;

    public WrappedPacketDataSerializer(Object object) {
        super(object);

        data = readBytesMethod.invoke(object);
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
