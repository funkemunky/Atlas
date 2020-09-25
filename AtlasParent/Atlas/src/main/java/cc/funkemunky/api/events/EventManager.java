package cc.funkemunky.api.events;

import cc.funkemunky.api.Atlas;
import cc.funkemunky.api.events.exceptions.AsyncEventException;
import cc.funkemunky.api.events.exceptions.ListenParamaterException;
import dev.brighten.db.utils.Pair;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;
import java.util.*;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.stream.Collectors;

@Getter
public class EventManager {
    private final Map<String, List<ListenerMethod>>
            listenerMethods = Collections.synchronizedMap(new HashMap<>());
    private final Map<String, List<Pair<String, Consumer<AtlasEvent>>>>
            events = Collections.synchronizedMap(new HashMap<>());

    public boolean paused = false;

    public void registerListener(Method method, AtlasListener listener, Plugin plugin) throws ListenParamaterException {
        if (method.getParameterTypes().length >= 1) {
            if (AtlasEvent.class.isAssignableFrom(method.getParameterTypes()[0])) {
                Listen listen = method.getAnnotation(Listen.class);
                ListenerMethod lm = new ListenerMethod(plugin, method, listener, listen.priority());

                if (!listen.priority().equals(ListenerPriority.NONE)) {
                    lm.listenerPriority = listen.priority();
                }

                synchronized (listenerMethods) {
                    listenerMethods.compute(method.getParameterTypes()[0].getSimpleName(),
                            (eventClass, methods) -> {
                        List<ListenerMethod> list = methods == null ? new ArrayList<>() : new ArrayList<>(methods);

                        list.add(lm);
                        list.sort(Comparator
                                .comparing(mth -> mth.listenerPriority.getPriority(),
                                        Comparator.reverseOrder()));

                        return list;
                    });
                }
            } else {
                throw new ListenParamaterException("Method " + method.getDeclaringClass().getName()
                        + "#" + method.getName() + "'s paramater: " + method.getParameterTypes()[0].getName()
                        + " is not an instanceof " + AtlasEvent.class.getSimpleName() + "!");
            }
        } else {
            throw new ListenParamaterException("Method " + method.getDeclaringClass().getName()
                    + "#" + method.getName() + " has an invalid amount of paramters (count="
                    + method.getParameterTypes().length + ")!");
        }
    }

    public void clearAllRegistered() {
        listenerMethods.clear();
    }

    public void unregisterAll(Plugin plugin) {
        synchronized (listenerMethods) {
            listenerMethods.forEach((key, list) -> {
                List<ListenerMethod> methods = list.stream()
                        .filter(lm -> !lm.plugin.equals(plugin))
                        .collect(Collectors.toList()); //This will remove any listeners from the plugin.

                if(list.size() != methods.size()) //This is just so we don't update the map unnecessarily.
                    listenerMethods.put(key, methods);
            });
        }
        synchronized (events) {
            events.forEach((key, list) -> {
                List<Pair<String, Consumer<AtlasEvent>>> lambdas = list.stream()
                        .filter(p -> !p.key.startsWith(plugin.getName()))
                        .collect(Collectors.toList());

                if(lambdas.size() != list.size())
                    events.put(key, lambdas);
            });
        }
    }

    public void unregisterListener(AtlasListener listener) {
        synchronized (listenerMethods) {
            listenerMethods.forEach((key, list) -> {
                List<ListenerMethod> methods = list.stream()
                        .filter(lm -> !lm.listener.equals(listener))
                        .collect(Collectors.toList()); //This will remove any listeners from this listener class.

                if(list.size() != methods.size()) //This is just so we don't update the map unnecessarily.
                    listenerMethods.put(key, methods);
            });
        }
    }

    public void unregisterLambda(String id) {
        synchronized (events) {
            events.forEach((key, list) -> {
                List<Pair<String, Consumer<AtlasEvent>>> lambdas = list.stream()
                        .filter(p -> !p.key.equals(id))
                        .collect(Collectors.toList());

                if(lambdas.size() != list.size())
                    events.put(key, lambdas);
            });
        }
    }

    public LambdaBuilder registerLambda(Plugin plugin, Class<? extends AtlasEvent> clazz) {
        return new LambdaBuilder(clazz, plugin);
    }

    @RequiredArgsConstructor
    public class LambdaBuilder<T extends AtlasEvent> {
        private final Class<T> clazz;
        private final Plugin plugin;

        public String build(Consumer<T> consumer) {
            String id = plugin.getName() + "-@-" + UUID.randomUUID();
            synchronized (events) {
                events.compute(clazz.getSimpleName(), (key, list) -> {
                    List<Pair<String, Consumer<AtlasEvent>>> methods = list == null
                            ? new ArrayList<>() : new ArrayList<>(list);

                    methods.add(new Pair<>(id, (Consumer<AtlasEvent>) consumer));

                    return methods;
                });
            }

            return id;
        }
    }

    public void registerListeners(AtlasListener listener, Plugin plugin) {
        Arrays.stream(listener.getClass().getMethods())
                .filter(method -> method.isAnnotationPresent(Listen.class))
                .forEach(method -> {
                    try {
                        registerListener(method, listener, plugin);
                    } catch (ListenParamaterException e) {
                        e.printStackTrace();
                    }
                });
    }

    public void callEvent(AtlasEvent event) {
        callEvent(event, false);
    }

    public void callEventAsync(AtlasEvent event) {
        callEventAsync(event, false);
    }

    public void callEvent(AtlasEvent event, boolean lambdaOnly) {
        if (!paused && event != null) {
            String name = event.getClass().getSimpleName();
            Atlas.getInstance().getProfile().start("event:" + name);

            if (!lambdaOnly) {
                listenerMethods.computeIfPresent(name, (eventClass, list) -> {
                    if (event instanceof Cancellable) {
                        Cancellable cancellable = (Cancellable) event;
                        for (ListenerMethod lm : list) {
                            if (!cancellable.isCancelled() || !lm.ignoreCancelled) {
                                lm.method.invoke(lm.listener, cancellable);
                            }
                        }
                    } else {
                        for (ListenerMethod lm : list) {
                            lm.method.invoke(lm.listener, event);
                        }
                    }
                    return list;
                });
            }

            events.computeIfPresent(name, (eventClass, consumerList) -> {
                for (Pair<String, Consumer<AtlasEvent>> atlasEventConsumer : consumerList) {
                    atlasEventConsumer.value.accept(event);
                }
                return consumerList;
            });


            Atlas.getInstance().getProfile().stop("event:" + name);
        }
    }

    @SneakyThrows
    public void callEventAsync(AtlasEvent event, boolean lambdaOnly) {
        if(event instanceof Cancellable) {
            throw new AsyncEventException(event);
        }
        Atlas.getInstance().getService().execute(() -> callEvent(event, lambdaOnly));
    }
}