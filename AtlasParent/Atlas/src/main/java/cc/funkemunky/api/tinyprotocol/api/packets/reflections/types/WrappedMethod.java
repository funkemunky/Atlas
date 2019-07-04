/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.api.tinyprotocol.api.packets.reflections.types;

import com.google.common.collect.Sets;
import lombok.Getter;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Set;

@Getter
public class WrappedMethod {
    private final WrappedClass parent;
    private final Method method;
    private final Set<Class> parameters;

    WrappedMethod(WrappedClass parent, Method method) {
        this.parent = parent;
        this.method = method;
        this.parameters = Sets.newHashSet(method.getParameterTypes());
    }

    public <T> T invoke(Object object, Object... args) {
        try {
            return (T) this.method.invoke(object, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
        return null;
    }
}
