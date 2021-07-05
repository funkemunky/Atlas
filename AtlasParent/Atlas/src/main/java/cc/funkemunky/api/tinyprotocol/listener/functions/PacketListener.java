package cc.funkemunky.api.tinyprotocol.listener.functions;

import cc.funkemunky.api.tinyprotocol.api.NMSObject;

@FunctionalInterface
public interface PacketListener {
    boolean onEvent(NMSObject object, String type);
}
