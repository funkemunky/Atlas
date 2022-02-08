package cc.funkemunky.api.utils;

import org.simpleyaml.configuration.comments.CommentType;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;


@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.FIELD, ElementType.TYPE})
public @interface ConfigSetting {
    String path() default "";
    String name() default "";
    String comment() default "";
    CommentType commentType() default CommentType.SIDE;
    String configName() default "";
    boolean hide() default false;
}
