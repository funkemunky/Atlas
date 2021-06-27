package cc.funkemunky.api.packet;

import io.netty.buffer.ByteBuf;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.function.Consumer;

public class PacketHandler {

    private static final Map<String, Consumer<ByteBuf>>
            incoming = Collections.synchronizedMap(new HashMap<>()),
            outgoing = Collections.synchronizedMap(new HashMap<>());

    public PacketListener incomingDirect(Consumer<ByteBuf> listen) {
        PacketListener listener = new PacketListener();

        incoming.put(listener.id, listen);

        return listener;
    }

    public PacketListener outgoingDirect(Consumer<ByteBuf> listen) {
        PacketListener listener = new PacketListener();

        outgoing.put(listener.id, listen);

        return listener;
    }

    public static class PacketListener {
        private final String id;

        protected PacketListener(String id) {
            this.id = id;
        }

        protected PacketListener() {
            this.id = UUID.randomUUID().toString();
        }

        public void removeListener() {
            incoming.remove(id);
        }

        public String getId() {
            return id;
        }
    }
}
