package cc.funkemunky.api.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

@Retention(RetentionPolicy.RUNTIME)
public @interface Init {
    Priority priority() default Priority.NORMAL;

}