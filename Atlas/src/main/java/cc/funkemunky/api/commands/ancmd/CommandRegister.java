package cc.funkemunky.api.commands.ancmd;

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
    private Method method;
    private Object object;
}
