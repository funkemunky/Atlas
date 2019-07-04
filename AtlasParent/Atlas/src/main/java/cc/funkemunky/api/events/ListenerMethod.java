package cc.funkemunky.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

@Getter
@Setter
class ListenerMethod {
    private Plugin plugin;
    private Method method;
    private AtlasListener listener;
    public ListenerPriority listenerPriority;
    private String className;

    public ListenerMethod(Plugin plugin, Method method, AtlasListener listener, ListenerPriority listenerPriority) {
        this.plugin = plugin;
        this.method = method;
        this.listener = listener;
        this.listenerPriority = listenerPriority;
        this.className = method.getParameterTypes()[0].getName();
    }
}
