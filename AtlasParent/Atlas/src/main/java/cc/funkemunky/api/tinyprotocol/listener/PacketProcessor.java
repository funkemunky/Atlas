package cc.funkemunky.api.tinyprotocol.listener;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.listener.functions.AsyncPacketListener;
import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import lombok.val;
import org.bukkit.entity.Player;
import org.bukkit.event.EventPriority;
import org.bukkit.plugin.Plugin;

import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;

/*
    An asynchronous processor for packets.
 */
public class PacketProcessor {
    private final Map<String, List<ListenerEntry>>
            processors = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, List<AsyncListenerEntry>>
            asyncProcessors = Collections.synchronizedMap(new HashMap<>());

    public PacketListener process(Plugin plugin, PacketListener listener, String... types) {
        return process(plugin, EventPriority.NORMAL, listener, types);
    }

    public PacketListener process(Plugin plugin, EventPriority priority, PacketListener listener, String... types) {
        ListenerEntry entry = new ListenerEntry(plugin, priority, listener);
        synchronized (processors) {
            for (String type : types) {
                processors.compute(type, (key, list) -> {
                    if(list == null) list = new CopyOnWriteArrayList<>();

                    list.add(entry);
                    list.sort(Comparator.comparing(t -> t.getPriority().getSlot()));

                    return list;
                });
            }
        }

        return listener;
    }

    public PacketListener process(Plugin plugin, EventPriority priority, PacketListener listener) {
        return process(plugin, priority, listener, "*");
    }

    public AsyncPacketListener processAsync(Plugin plugin, AsyncPacketListener listener, String... types) {
        return processAsync(plugin, EventPriority.NORMAL, listener, types);
    }

    public AsyncPacketListener processAsync(Plugin plugin, EventPriority priority, AsyncPacketListener listener) {
        return processAsync(plugin, priority, listener, "*");
    }

    public AsyncPacketListener processAsync(Plugin plugin, EventPriority priority, AsyncPacketListener listener,
                                            String... types) {
        AsyncListenerEntry entry = new AsyncListenerEntry(plugin, priority, listener);
        synchronized (asyncProcessors) {
            for (String type : types) {
                asyncProcessors.compute(type, (key, list) -> {
                    if(list == null) list = new CopyOnWriteArrayList<>();

                    list.add(entry);
                    list.sort(Comparator.comparing(t -> t.getPriority().getSlot()));

                    return list;
                });
            }
        }

        return listener;
    }

    public boolean removeListener(PacketListener listener) {
        boolean removedListener = false;
        synchronized (processors) {
            int iterations = 0;
            for (List<ListenerEntry> list : processors.values()) {
                for (Iterator<ListenerEntry> it = list.iterator(); it.hasNext(); ) {
                    ListenerEntry entry = it.next();

                    iterations++;
                    if(entry.getListener() == listener) {
                        it.remove();
                        Atlas.getInstance().getLogger().info("Removed listener in " + iterations + " iterations.");
                        removedListener = true;
                        break;
                    }
                }
            }
        }

        return removedListener;
    }

    public void removeListeners(Plugin plugin) {
        synchronized (processors) {
            for (List<ListenerEntry> list : processors.values()) {
                list.removeIf(entry -> entry.getPlugin() == plugin);
            }
        }
    }

    public boolean call(Player player, Object packet, String type) {
        if(packet == null) return false;
        PacketInfo info = new PacketInfo(player, packet, type, System.currentTimeMillis());
        Atlas.getInstance().getService().execute(() -> {
            val list = asyncProcessors.getOrDefault("*", new ArrayList<>());

            list.addAll(asyncProcessors.getOrDefault(type, new ArrayList<>()));

            for (AsyncListenerEntry tuple : list) {
                tuple.getListener().onEvent(info);
            }
        });

        val list = processors.getOrDefault("*", new ArrayList<>());

        list.addAll(processors.getOrDefault(type, new CopyOnWriteArrayList<>()));

        boolean cancelled = false;
        for (ListenerEntry tuple : list) {
            if(!tuple.getListener().onEvent(info)) {
                cancelled = true;
            }
        }
        return !cancelled;
    }

    public void shutdown() {
        processors.clear();
        asyncProcessors.clear();
    }
}
