package cc.funkemunky.api.events.impl;

import cc.funkemunky.api.events.AtlasEvent;
import cc.funkemunky.api.events.Cancellable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Deprecated
public class PacketSendEvent extends AtlasEvent implements Cancellable {
    private final Player player;
    @Setter
    private Object packet;
    @Setter
    private boolean cancelled;
    private final String type;
    private final long timeStamp;

    public PacketSendEvent(Player player, Object packet, String type) {
        this.player = player;
        this.packet = packet;
        this.type = type;

        timeStamp = System.currentTimeMillis();
    }
}
