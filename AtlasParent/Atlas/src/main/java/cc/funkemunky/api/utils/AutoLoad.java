package cc.funkemunky.api.utils;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.concurrent.FutureTask;

@Retention(RetentionPolicy.RUNTIME)
public @interface AutoLoad {
    boolean commands() default true;
    boolean listeners() default true;
}
