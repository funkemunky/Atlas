package cc.funkemunky.api.bungee.events;

import cc.funkemunky.api.events.AtlasEvent;
import lombok.AllArgsConstructor;

@AllArgsConstructor
public class BungeeReceiveEvent extends AtlasEvent {
    public Object[] objects;
    public String channel;

}
