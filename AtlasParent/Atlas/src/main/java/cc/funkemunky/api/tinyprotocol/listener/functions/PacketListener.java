package cc.funkemunky.api.tinyprotocol.listener.functions;

import cc.funkemunky.api.tinyprotocol.listener.PacketInfo;

@FunctionalInterface
public interface PacketListener {
    boolean onEvent(PacketInfo info);
}
