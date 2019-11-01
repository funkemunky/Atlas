package cc.funkemunky.api.events;

import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.reflections.types.WrappedMethod;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

class ListenerMethod {
    public Plugin plugin;
    public WrappedMethod method;
    public WrappedClass listenerClass;
    public AtlasListener listener;
    public ListenerPriority listenerPriority;
    public String className;
    public boolean ignoreCancelled;

    public ListenerMethod(Plugin plugin, Method method, AtlasListener listener, ListenerPriority listenerPriority) {
        this.plugin = plugin;
        this.listenerClass = new WrappedClass(listener.getClass());
        this.method = new WrappedMethod(listenerClass, method);
        this.listener = listener;
        this.listenerPriority = listenerPriority;
        this.ignoreCancelled = method.getAnnotation(Listen.class).ignoreCancelled();
        this.className = method.getParameterTypes()[0].getName();
    }
}