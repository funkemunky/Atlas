package cc.funkemunky.api.tinyprotocol.listener;

import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
@Getter
class ListenerEntry {
    private final Plugin plugin;
    private final EventPriority priority;
    private final PacketListener listener;
}
