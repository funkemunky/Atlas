package cc.funkemunky.api.commands.ancmd;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target({ElementType.METHOD})
@Deprecated
public @interface Command {
    String name() default "";
    String display() default "";
    boolean playerOnly() default false;
    boolean consoleOnly() default false;
    boolean opOnly() default false;
    boolean async() default false;
    String[] permission() default {};
    String[] tabCompletions() default {}; //Format label::result
    String description() default "";
    String usage() default "";
    String noPermissionMessage() default "&cNo permission.";
    String[] aliases() default {};
}
