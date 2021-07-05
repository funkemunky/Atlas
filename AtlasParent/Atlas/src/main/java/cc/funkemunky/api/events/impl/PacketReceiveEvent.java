package cc.funkemunky.api.events.impl;

import cc.funkemunky.api.events.AtlasEvent;
import cc.funkemunky.api.events.Cancellable;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Getter
@Deprecated
public class PacketReceiveEvent extends AtlasEvent implements Cancellable {
    private Player player;
    @Setter
    private Object packet;
    @Setter
    private boolean cancelled;
    private String type;
    private long timeStamp;

    public PacketReceiveEvent(Player player, Object packet, String type) {
        this.player = player;
        this.packet = packet;
        this.type = type;

        timeStamp = System.currentTimeMillis();
    }
}
