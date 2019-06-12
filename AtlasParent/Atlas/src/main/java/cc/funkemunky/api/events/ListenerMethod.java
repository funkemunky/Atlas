package cc.funkemunky.api.events;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
@Setter
class ListenerMethod {
    private Plugin plugin;
    private Method method;
    private AtlasListener listener;
    public ListenerPriority listenerPriority;
}
