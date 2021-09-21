package cc.funkemunky.api.tinyprotocol.listener;

import cc.funkemunky.api.tinyprotocol.listener.functions.AsyncPacketListener;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

@RequiredArgsConstructor
@Getter
class AsyncListenerEntry {
    private final Plugin plugin;
    private final EventPriority priority;
    private final AsyncPacketListener listener;
}
