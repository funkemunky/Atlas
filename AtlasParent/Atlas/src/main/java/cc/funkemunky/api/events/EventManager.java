package cc.funkemunky.api.events;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.exceptions.ListenParamaterException;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.Comparator;
import java.util.SortedSet;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;

@Getter
public class EventManager {
    private final SortedSet<ListenerMethod> listenerMethods = new ConcurrentSkipListSet<>(Comparator.comparing(method -> method.getListenerPriority().getPriority(), Comparator.reverseOrder()));
    private boolean paused = false;

    public void registerListener(Method method, AtlasListener listener, Plugin plugin) throws ListenParamaterException {
        if(method.getParameterTypes().length == 1) {
            if(method.getParameterTypes()[0].getSuperclass().equals(AtlasEvent.class)) {
                Listen listen = method.getAnnotation(Listen.class);
                ListenerMethod lm = new ListenerMethod(plugin, method, listener, listen.priority());

                if(!listen.priority().equals(ListenerPriority.NONE)) {
                    lm.setListenerPriority(listen.priority());
                }

                listenerMethods.add(lm);
            } else {
                throw new ListenParamaterException("Method " + method.getDeclaringClass().getName() + "#" + method.getName() + "'s paramater: " + method.getParameterTypes()[0].getName() + " is not an instanceof " + AtlasEvent.class.getSimpleName() + "!");
            }
        } else {
            throw new ListenParamaterException("Method " + method.getDeclaringClass().getName() + "#" + method.getName() + " has an invalid amount of paramters (count=" + method.getParameterTypes().length + ")!");
        }
    }

    public void clearAllRegistered() {
        listenerMethods.clear();
    }

    public void unregisterAll(Plugin plugin) {
        listenerMethods.stream().filter(lm -> lm.getPlugin().equals(plugin)).forEach(listenerMethods::remove);
    }

    public void unregisterListener(AtlasListener listener) {
        listenerMethods.stream().filter(lm -> lm.getListener().equals(listener)).forEach(listenerMethods::remove);
    }

    public void registerListeners(AtlasListener listener, Plugin plugin) {
        Arrays.stream(listener.getClass().getMethods()).filter(method -> method.isAnnotationPresent(Listen.class)).forEach(method -> {
            try {
                registerListener(method, listener, plugin);
            } catch(ListenParamaterException e) {
                e.printStackTrace();
            }
        });
    }


    public boolean callEvent(AtlasEvent event) {
        if(!paused) {
            FutureTask<Boolean> callTask = new FutureTask<>(() -> {
                if(event instanceof Cancellable) {
                    String className = event.getClass().getName();
                    AtomicBoolean atomic = new AtomicBoolean(true);
                    listenerMethods.parallelStream().filter(lm -> lm.getClassName().equals(className)).forEach(lm -> {
                        try {
                            lm.getMethod().invoke(lm.getListener(), event);

                            if(((Cancellable) event).isCancelled()) {
                                atomic.set(false);
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                    return atomic.get();
                } else {
                    String className = event.getClass().getName();
                    listenerMethods.parallelStream().filter(lm -> lm.getClassName().equals(className)).forEach(lm -> {
                        try {
                            lm.getMethod().invoke(lm.getListener(), event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    });
                }

                return true;
            });

            Atlas.getInstance().getThreadPool().submit(callTask);

            if(event instanceof Cancellable) {
                try {
                    return callTask.get(5, TimeUnit.MILLISECONDS);
                } catch (InterruptedException | ExecutionException | TimeoutException e) {
                    return true;
                }
            }
        }
        return true;
    }
}
