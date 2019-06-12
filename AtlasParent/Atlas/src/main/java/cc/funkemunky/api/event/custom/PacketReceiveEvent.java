package cc.funkemunky.api.event.custom;

import cc.funkemunky.api.event.system.Cancellable;
import cc.funkemunky.api.event.system.Event;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.entity.Player;

@Deprecated
@Getter
public class PacketReceiveEvent extends Event implements Cancellable {
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
