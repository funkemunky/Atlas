package cc.funkemunky.api.tinyprotocol.api;

import cc.funkemunky.api.utils.objects.listmap.ConcurrentListMap;
import cc.funkemunky.api.utils.objects.listmap.ListMap;
import lombok.val;
import org.bukkit.event.EventPriority;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/*
    An asynchronous processor for packets.
 */
public class PacketProcessor {
    private ListMap<EventPriority, Task> processors = new ConcurrentListMap<>();
    private List<AsyncTask> asyncProcessors = new ArrayList<>();
    private ExecutorService asyncThread;

    public PacketProcessor() {
        asyncThread = Executors.newSingleThreadExecutor();
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

    public boolean call(Object packet, String type) {
        asyncThread.execute(() -> asyncProcessors.forEach(con -> con.run(packet, type)));
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
        asyncThread.shutdown();

        //nullifying everything
        processors = null;
        asyncProcessors = null;
        asyncThread = null;
    }

    @FunctionalInterface
    public interface AsyncTask {

        void run(Object packet, String type);
    }

    @FunctionalInterface
    public interface Task {

        boolean run(Object packet, String type);
    }
}
