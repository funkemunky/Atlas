package cc.funkemunky.api.event.system;

import cc.funkemunky.api.Atlas;
import lombok.val;
import org.bukkit.plugin.Plugin;

import javax.annotation.Nullable;
import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

public class EventManager {
    private static final Map<Map.Entry<Plugin, Listener>, List<Method>> registered = new ConcurrentHashMap<>();
    public static boolean enabled = true;

    public static void register(Plugin plugin, Listener listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(EventMethod.class)) {
                Map.Entry<Plugin, Listener> entry = new AbstractMap.SimpleEntry<>(plugin, listener);
                List<Method> methods = registered.getOrDefault(entry, new ArrayList<>());
                methods.add(method);

                methods.sort(Comparator.comparingInt(m -> m.getAnnotation(EventMethod.class).priority().getPriority()));
                Collections.reverse(methods);

                registered.put(entry, methods);
            }
        }
    }

    public static void unregister(Listener listener) {
        unregister(null, listener);
    }

    public static void unregisterAll(Plugin plugin) {
        registered.keySet().stream().filter(entry -> entry.getKey().equals(plugin)).forEach(registered::remove);
    }

    public static void unregister(@Nullable Plugin plugin, Listener listener) {
        if(plugin != null) {
            Map.Entry<Plugin, Listener> entry = new AbstractMap.SimpleEntry<>(plugin, listener);
            if(registered.containsKey(entry)) {
                registered.put(entry, new ArrayList<>());
            }
            registered.remove(entry);
        } else {
            val opEntry = registered.keySet().stream().filter(entry -> entry.getValue().equals(listener)).findFirst();

            if(opEntry.isPresent()) {
                val entry = opEntry.get();

                registered.remove(entry);
            }
        }
    }

    public static Set<Map.Entry<Plugin, Listener>> getRegistered() {
        return registered.keySet();
    }

    public static void clearRegistered() {
        registered.clear();
    }

    public static Event callEvent(Event event) {
        FutureTask<Event> futureTask = new FutureTask<>(() -> call(event));

        Atlas.getInstance().getThreadPool().submit(futureTask);

        try {
            return futureTask.get();
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
        return event;
    }

    private static Event call(Event event) {
        if(enabled) {
            Atlas.getInstance().getProfile().start("event:" + event.getClass().getName());
            for (Map.Entry<Plugin, Listener> entry : registered.keySet()) {
                for (Method method : registered.get(entry)) {
                    if (method.getParameterTypes()[0] == event.getClass()) {
                        try {
                            Atlas.getInstance().getProfile().start("event:" + event.getClass().getName() + ":" + method.getClass().getName() + "#" + method.getName() + "()");
                            method.invoke(entry.getValue(), event);
                            Atlas.getInstance().getProfile().stop("event:" + event.getClass().getName() + ":" + method.getClass().getName() + "#" + method.getName() + "()");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
            Atlas.getInstance().getProfile().stop("event:" + event.getClass().getName());
        }
        return event;
    }
}

