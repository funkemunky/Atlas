package cc.funkemunky.bungee.utils.asm;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
    Priority priority() default Priority.NORMAL;
    boolean commands() default false;
    String[] requirePlugins() default {};

    RequireType requireType() default RequireType.ALL;

    enum RequireType {
        ALL,
        ONE
    }
}