package cc.funkemunky.api.events;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.exceptions.ListenParamaterException;
import cc.funkemunky.api.events.impl.PacketReceiveEvent;
import cc.funkemunky.api.events.impl.PacketSendEvent;
import cc.funkemunky.api.tinyprotocol.api.TinyProtocolHandler;
import lombok.Getter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;

@Getter
public class EventManager {
    private final Map<Class<?>, List<ListenerMethod>> listenerMethods = Collections.synchronizedMap(new HashMap<>());
    public boolean paused = false;

    public void registerListener(Method method, AtlasListener listener, Plugin plugin) throws ListenParamaterException {
        if(method.getParameterTypes().length == 1) {
            Class<?> param = method.getParameterTypes()[0];
            if(param.getSuperclass().equals(AtlasEvent.class)) {
                Listen listen = method.getAnnotation(Listen.class);
                ListenerMethod lm = new ListenerMethod(plugin, method, listener, listen.priority());

                if(param == PacketReceiveEvent.class || param == PacketSendEvent.class) {
                    TinyProtocolHandler.legacyListeners = true;
                    Atlas.getInstance().getLogger().warning("Legacy packet listeners are being used by plugin " +
                            "" + plugin.getName() + " and therefore have been enabled. This can reduce performance," +
                            " so please update your plugin or tell the author to use the new packet listeners");
                }
                if(!listen.priority().equals(ListenerPriority.NONE)) {
                    lm.listenerPriority = listen.priority();
                }

                listenerMethods.compute(param, (key, list) -> {
                    if(list == null) list = new ArrayList<>();

                    list.add(lm);
                    list.sort(Comparator
                            .comparing(mth -> mth.listenerPriority.getPriority(), Comparator.reverseOrder()));

                    return list;
                });
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
        for (Class<?> aClass : listenerMethods.keySet()) {
            listenerMethods.compute(aClass, (key, list) -> {
                if(list != null)
                list.removeIf(listenerMethod -> listenerMethod.plugin == plugin);

                return list;
            });
        }
    }

    public void unregisterListener(AtlasListener listener) {
        for (Class<?> aClass : listenerMethods.keySet()) {
            listenerMethods.compute(aClass, (key, list) -> {
                if(list != null)
                list.removeIf(listenerMethod -> listenerMethod.listener == listener);

                return list;
            });
        }
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

    public void callEvent(AtlasEvent event) {
        if(event == null) return;
        Class<?> eventClass = event.getClass();
        if(!paused && listenerMethods.containsKey(eventClass)) {
            List<ListenerMethod> methods = listenerMethods.get(eventClass);

            if(event instanceof Cancellable) {
                Cancellable cancellable = (Cancellable) event;
                for (ListenerMethod lm : methods) {
                    if(!cancellable.isCancelled() || !lm.ignoreCancelled) {
                        lm.method.invoke(lm.listener, cancellable);
                    }
                }
            } else {
                for(ListenerMethod lm : methods) {
                    lm.method.invoke(lm.listener, event);
                }
            }
        }
    }

    public void callEventAsync(AtlasEvent event) {
        Atlas.getInstance().getService().execute(() -> callEvent(event));
    }
}