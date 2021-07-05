package cc.funkemunky.api.tinyprotocol.listener.functions;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;

@FunctionalInterface
public interface AsyncPacketListener {
    void onEvent(NMSObject object, String type);
}
