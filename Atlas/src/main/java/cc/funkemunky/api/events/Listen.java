package cc.funkemunky.api.events;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(value = RetentionPolicy.RUNTIME)
@Target(value = {ElementType.METHOD})
public @interface Listen {
    ListenerPriority priority() default ListenerPriority.NORMAL;
    boolean ignoreCancelled() default false;
}
