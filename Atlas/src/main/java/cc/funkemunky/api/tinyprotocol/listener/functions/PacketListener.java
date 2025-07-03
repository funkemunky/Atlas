package cc.funkemunky.api.tinyprotocol.listener.functions;

import cc.funkemunky.api.tinyprotocol.listener.PacketInfo;

@FunctionalInterface
public interface PacketListener {
    void onEvent(PacketInfo info);
}
