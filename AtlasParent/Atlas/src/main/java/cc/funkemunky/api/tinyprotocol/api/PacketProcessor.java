package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.tinyprotocol.api.packets.ChannelListener;
import cc.funkemunky.api.tinyprotocol.api.packets.impl.ChannelNew;
import cc.funkemunky.api.utils.objects.listmap.ConcurrentListMap;
import cc.funkemunky.api.utils.objects.listmap.ListMap;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.val;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    An asynchronous processor for packets.
 */
@NoArgsConstructor
public class PacketProcessor implements Listener {
    private ListMap<EventPriority, Task> processors = new ConcurrentListMap<>();
    private List<AsyncTask> asyncProcessors = new ArrayList<>();
    @Getter
    private final Map<UUID, ExecutorService> playerThreads = new ConcurrentHashMap<>();
    @Getter
    private final ChannelListener channelListener;

    public PacketProcessor() {
        channelListener = new ChannelNew();
    }

    public void process(EventPriority priority, Task task) {
        processors.add(priority, task);
    }

    public void process(Task task) {
        process(EventPriority.NORMAL, task);
    }

    public void processAsync(AsyncTask task) {
        asyncProcessors.add(task);
    }

    public boolean call(NMSObject packet, PacketType type) {

        boolean cancel = false;
        for (EventPriority value : EventPriority.values()) {
            val tasks = processors.getList(value);

            if(tasks.parallelStream().anyMatch(task -> task.run(packet, type))) {
                cancel = true;
                break;
            }
        }

        return cancel;
    }

    public void shutdown() {
        processors.clear();
        asyncProcessors.clear();

        //nullifying everything
        processors = null;
        asyncProcessors = null;
    }

    @EventHandler(priority = EventPriority.LOWEST) //So this is one of the very first things run.
    public void onEvent(PlayerJoinEvent event) {

    }

    @EventHandler(priority = EventPriority.HIGHEST) //So this is one of the very last things run
    public void onEvent(PlayerQuitEvent event) {
        playerThreads.remove(event.getPlayer().getUniqueId());
    }

    @FunctionalInterface
    public interface AsyncTask {

        void run(NMSObject packet);
    }

    @FunctionalInterface
    public interface Task {

        boolean run(NMSObject packet);
    }
}
