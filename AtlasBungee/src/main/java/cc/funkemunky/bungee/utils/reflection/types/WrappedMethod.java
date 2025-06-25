/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.bungee.utils.reflection.types;

import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

@Getter
public class WrappedMethod {
    private final WrappedClass parent;
    private final Method method;
    private final String name;
    private final List<Class<?>> parameters;

    public WrappedMethod(WrappedClass parent, Method method) {
        this.parent = parent;
        this.method = method;
        this.name = method.getName();
        this.parameters = Arrays.asList(method.getParameterTypes());
    }

    public <T> T invoke(Object object, Object... args) {
        try {
            return (T) this.method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }

    public int getModifiers() {
        return this.method.getModifiers();
    }
}
