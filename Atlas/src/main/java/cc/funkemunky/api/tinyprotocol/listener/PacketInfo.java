package cc.funkemunky.api.tinyprotocol.listener;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import org.bukkit.entity.Player;

@RequiredArgsConstructor
@Getter
public class PacketInfo {
    private final Player player;
    private final Object packet;
    private final String type;
    private final long timestamp;
    @Setter
    private boolean cancelled;
}
