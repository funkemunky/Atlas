package cc.funkemunky.example.listeners.atlas;

import cc.funkemunky.api.event.system.EventMethod;
import cc.funkemunky.api.event.system.Listener;
import cc.funkemunky.api.utils.Init;
import cc.funkemunky.example.event.CustomMoveEvent;

@Init
public class CustomMoveListeners implements Listener {

    @EventMethod
    public void onEvent(CustomMoveEvent event) {
        event.getPlayer().sendMessage("Custom move event.");
    }
}
