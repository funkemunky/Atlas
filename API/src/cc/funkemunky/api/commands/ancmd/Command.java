package cc.funkemunky.api.commands.ancmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
public @interface Command {
    String name() default "";
    boolean playerOnly = false;
    boolean consoleOnly = false;
    String[] permission() default "";
    String description() default "";
    String usage() default "";
    String noPermissionMessage() default "&cNo permission.";
    String[] aliases() default "";
}
