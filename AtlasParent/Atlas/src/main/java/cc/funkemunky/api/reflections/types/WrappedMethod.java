/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.api.reflections.types;

import cc.funkemunky.api.utils.objects.MultiFunction;
import com.hervian.lambda.Lambda;
import com.hervian.lambda.LambdaFactory;
import lombok.Getter;

import java.lang.invoke.*;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;
import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;

@Getter
public class WrappedMethod {
    private final WrappedClass parent;
    private Method method;
    private Lambda lambda;
    private final String name;
    private final List<Class<?>> parameters;
    private boolean isVoid;

    public WrappedMethod(WrappedClass parent, Method method) {
        boolean isStatic = Modifier.isStatic(method.getModifiers());

        this.name = method.getName();
        this.method = method;
        this.parent = parent;
        this.parameters = Arrays.asList(method.getParameterTypes());

        try {
            lambda = LambdaFactory.create(method);
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        isVoid = method.getReturnType().equals(void.class);
    }

    public <T> T invoke(Object object, Object... args) {
        if(args.length > 2) {
            try {
                return (T) method.invoke(object, args);
            } catch (IllegalAccessException | InvocationTargetException e) {
                e.printStackTrace();
            }
        } else {
            if(isVoid) {
                if(args.length == 1) {
                    lambda.invoke_for_void(object, args[0]);
                } else {
                    lambda.invoke_for_void(object, args[0], args[1]);
                }
                return null;
            } else {
                if(args.length == 1) {
                    return (T) lambda.invoke_for_Object(object, args[0]);
                } else return (T) lambda.invoke_for_Object(object, args[0], args[1]);
            }
        }
        return null;
    }

    public int getModifiers() {
        return this.method.getModifiers();
    }
}
