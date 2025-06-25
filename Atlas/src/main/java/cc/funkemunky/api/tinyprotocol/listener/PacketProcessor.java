package cc.funkemunky.api.tinyprotocol.listener;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.api.Packet;
import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import cc.funkemunky.api.utils.RunUtils;
import lombok.val;
import org.bukkit.Bukkit;
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
            processors = new HashMap<>();
    private final Map<String, List<ListenerEntry>>
            asyncProcessors = new HashMap<>();

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
        return process(plugin, priority, listener, Packet.allPackets.toArray(new String[0]));
    }

    public PacketListener processAsync(Plugin plugin, PacketListener listener, String... types) {
        return processAsync(plugin, EventPriority.NORMAL, listener, types);
    }

    public PacketListener processAsync(Plugin plugin, EventPriority priority, PacketListener listener) {
        return processAsync(plugin, priority, listener, Packet.allPackets.toArray(new String[0]));
    }

    public PacketListener processAsync(Plugin plugin, EventPriority priority, PacketListener listener,
                                            String... types) {
        ListenerEntry entry = new ListenerEntry(plugin, priority, listener);
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
        synchronized (asyncProcessors) {
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
                list.removeIf(entry -> entry.getPlugin().getName().equals(plugin.getName()));
            }
        }
        synchronized (asyncProcessors) {
            for (List<ListenerEntry> list : asyncProcessors.values()) {
                list.removeIf(entry -> entry.getPlugin().getName().equals(plugin.getName()));
            }
        }
    }

    public boolean call(Player player, Object packet, String type) {
        if(packet == null) return false;
        PacketInfo info = new PacketInfo(player, packet, type, System.currentTimeMillis());
        if(asyncProcessors.containsKey(type)) {
            RunUtils.taskAsync(() -> {
                val list = asyncProcessors.get(type);

                for (ListenerEntry tuple : list) {
                    tuple.getListener().onEvent(info);
                }
            });
        }

        if(processors.containsKey(type)) {
            val list = processors.get(type);

            boolean cancelled = false;
            for (ListenerEntry tuple : list) {
                try {
                    tuple.getListener().onEvent(info);

                    if(info.isCancelled()) {
                        cancelled = true;
                    }
                } catch(Exception e) {
                    e.printStackTrace();
                }
            }
            return !cancelled;
        } return true;
    }

    public void shutdown() {
        processors.clear();
        asyncProcessors.clear();
    }
}
