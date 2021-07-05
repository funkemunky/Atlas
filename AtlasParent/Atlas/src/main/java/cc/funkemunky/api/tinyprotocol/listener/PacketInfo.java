package cc.funkemunky.api.tinyprotocol.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
@Getter
public class PacketInfo {
    private final Object packet;
    private final String type;
    private final long timestamp;
}
