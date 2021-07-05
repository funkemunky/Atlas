package cc.funkemunky.api.tinyprotocol.listener.functions;

@FunctionalInterface
public interface PacketListener {
    boolean onEvent(Object packet, String type);
}
