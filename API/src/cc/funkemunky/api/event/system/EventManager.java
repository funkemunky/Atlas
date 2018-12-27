package cc.funkemunky.api.event.system;

import cc.funkemunky.api.Atlas;
import org.bukkit.scheduler.BukkitRunnable;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.*;

public class EventManager {
    private static final Map<Listener, List<Method>> registered = new HashMap<>();

    public static void register(Listener listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(EventMethod.class)) {
                List<Method> methods = registered.getOrDefault(listener, new ArrayList<>());
                methods.add(method);
                registered.put(listener, methods);
            }
        }
    }


    public static void unregister(Listener listener) {
        registered.remove(listener);
    }

    public static Set<Listener> getRegistered() {
        return registered.keySet();
    }

    public static void clearRegistered() {
        registered.clear();
    }

    public static void callEvent(Event event) {
        Atlas.getInstance().getThreadPool().execute(() -> call(event));
    }

    private static void call(Event event) {
        for (Listener listener : registered.keySet()) {
            for (Method method : registered.get(listener)) {
                if (method.getParameterTypes()[0] == event.getClass()) {
                    try {
                        method.invoke(listener, event);
                    } catch (Exception e) {
                        new BukkitRunnable() {
                            public void run() {
                                try {
                                    method.invoke(listener, event);
                                } catch (IllegalAccessException | InvocationTargetException e1) {
                                    e1.printStackTrace();
                                } catch (Exception e) {
                                    //Empty catch block.
                                }
                            }
                        }.runTask(Atlas.getInstance());
                    }
                }
            }
        }
    }
}

