package cc.funkemunky.api.events.impl;

import cc.funkemunky.api.events.AtlasEvent;
import cc.funkemunky.api.events.Cancellable;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import java.net.SocketAddress;

@RequiredArgsConstructor
@Getter
public class PacketLoginEvent extends AtlasEvent implements Cancellable {
    @Setter
    private boolean cancelled;

    public final SocketAddress address;
    @Setter
    private final Object packet; // Setters cannot be generated for final fields.
    private final String packetType;
}
