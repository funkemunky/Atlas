package cc.funkemunky.api.commands.ancmd;

import cc.funkemunky.api.reflections.types.WrappedMethod;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.bukkit.plugin.Plugin;

import java.lang.reflect.Method;

@AllArgsConstructor
@Getter
@Setter
public class CommandRegister {
    private Plugin plugin;
    private WrappedMethod method;
    private Object object;
}
