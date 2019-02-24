package cc.funkemunky.example.event;

import cc.funkemunky.api.event.system.Cancellable;
import cc.funkemunky.api.event.system.Event;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.entity.Player;

@Getter
@Setter
public class CustomMoveEvent extends Event implements Cancellable {
    private Player player;
    private Location to, from;
    private boolean cancelled;

    public CustomMoveEvent(Player player, Location to, Location from) {
        this.player = player;
        this.to = to;
        this.from = from;
    }
}
