package cc.funkemunky.api.tinyprotocol.listener;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.tinyprotocol.listener.functions.AsyncPacketListener;
import cc.funkemunky.api.tinyprotocol.listener.functions.PacketListener;
import cc.funkemunky.api.utils.Tuple;
import lombok.val;
import org.bukkit.event.EventPriority;

import java.util.*;

/*
    An asynchronous processor for packets.
 */
public class PacketProcessor {
    private final Map<String, List<Tuple<EventPriority, PacketListener>>>
            processors = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, List<Tuple<EventPriority, AsyncPacketListener>>>
            asyncProcessors = Collections.synchronizedMap(new HashMap<>());

    public void process(EventPriority priority, PacketListener listener, String... types) {
        Tuple<EventPriority, PacketListener> tuple = new Tuple<>(priority, listener);
        synchronized (processors) {
            for (String type : types) {
                processors.compute(type, (key, list) -> {
                    if(list == null) list = new ArrayList<>();

                    list.add(tuple);
                    list.sort(Comparator.comparing(t -> t.one.getSlot()));

                    return list;
                });
            }
        }
    }

    public void process(EventPriority priority, PacketListener listener) {
        process(priority, listener, "*");
    }

    public void process(PacketListener listener, String... types) {
        process(EventPriority.NORMAL, listener, types);
    }

    public void processAsync(AsyncPacketListener listener, String... types) {
        processAsync(EventPriority.NORMAL, listener, types);
    }

    public void processAsync(EventPriority priority, AsyncPacketListener listener) {
        processAsync(priority, listener, "*");
    }

    public void processAsync(EventPriority priority, AsyncPacketListener listener, String... types) {
        Tuple<EventPriority, AsyncPacketListener> tuple = new Tuple<>(priority, listener);
        synchronized (asyncProcessors) {
            for (String type : types) {
                asyncProcessors.compute(type, (key, list) -> {
                    if(list == null) list = new ArrayList<>();

                    list.add(tuple);
                    list.sort(Comparator.comparing(t -> t.one.getSlot()));

                    return list;
                });
            }
        }
    }

    public boolean call(Object packet, String type) {
        PacketInfo info = new PacketInfo(packet, type, System.currentTimeMillis());
        if(asyncProcessors.containsKey(type))
        Atlas.getInstance().getSchedular().execute(() -> {
            val list = asyncProcessors.get(type);

            list.addAll(asyncProcessors.getOrDefault("*", Collections.emptyList()));

            for (Tuple<EventPriority, AsyncPacketListener> tuple : list) {
                tuple.two.onEvent(info);
            }
        });

        if(!processors.containsKey(type)) return true;

        val list = processors.get(type);

        list.addAll(processors.getOrDefault("*", Collections.emptyList()));

        for (Tuple<EventPriority, PacketListener> tuple : list) {
            if(tuple.two.onEvent(info)) {
                return false;
            }
        }
        return true;
    }

    public void shutdown() {
        processors.clear();
        asyncProcessors.clear();
    }
}
