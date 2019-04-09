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
import java.util.TreeSet;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;

@Getter
public class EventManager {
    private SortedSet<ListenerMethod> listenerMethods = new TreeSet<>(Comparator.comparingInt(method -> method.getListenerPriority().getPriority()));
    private boolean paused = false;

    public void registerListener(Method method, AtlasListener listener, Plugin plugin) throws ListenParamaterException {
        if(method.getParameterTypes().length == 1) {
            if(method.getParameterTypes()[0].getSuperclass().equals(AtlasEvent.class)) {
                Listen listen = method.getAnnotation(Listen.class);
                ListenerMethod lm = new ListenerMethod(plugin, method, listener, listen.priorty());

                if(!listen.priorty().equals(ListenerPriority.NONE)) {
                    lm.setListenerPriority(listen.priorty());
                }

                listenerMethods.add(lm);
            } else {
                throw new ListenParamaterException("Method " + method.getClass().getName() + "#" + method.getName() + "'s paramater: " + method.getParameterTypes()[0].getSimpleName() + " is not an instanceof " + AtlasEvent.class.getName() + "!");
            }
        } else {
            throw new ListenParamaterException("Method " + method.getClass().getName() + "#" + method.getName() + " has an invalid amount of paramters (count=" + method.getParameterTypes().length + ")!");
        }
    }

    public void clearAllRegistered() {
        listenerMethods.clear();
    }

    public void unregisterAll(Plugin plugin) {
        listenerMethods.stream().filter(lm -> lm.getPlugin().equals(plugin)).forEach(lm -> listenerMethods.remove(lm));
    }

    public void unregisterListener(AtlasListener listener) {
        listenerMethods.stream().filter(lm -> lm.getListener().equals(listener)).forEach(lm -> listenerMethods.remove(lm));
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
                    for (ListenerMethod lm : listenerMethods) {
                        if(!lm.getMethod().getParameterTypes()[0].equals(event.getClass())) continue;

                        try {
                            lm.getMethod().invoke(lm.getListener(), event);

                            if(((Cancellable) event).isCancelled()) {
                                return false;
                            }
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                } else {
                    for (ListenerMethod lm : listenerMethods) {
                        if(!lm.getMethod().getParameterTypes()[0].equals(event.getClass())) continue;

                        try {
                            lm.getMethod().invoke(lm.getListener(), event);
                        } catch (IllegalAccessException | InvocationTargetException e) {
                            e.printStackTrace();
                        }
                    }
                }

                return true;
            });

            Atlas.getInstance().getThreadPool().submit(callTask);

            try {
                return callTask.get();
            } catch (InterruptedException | ExecutionException e) {
                e.printStackTrace();
            }
        }
        return true;
    }
}
