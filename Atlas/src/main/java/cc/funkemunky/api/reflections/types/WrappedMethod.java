/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.api.reflections.types;

import cc.funkemunky.api.reflections.Reflections;
import cc.funkemunky.api.utils.objects.MethodFunction;
import cc.funkemunky.api.utils.objects.QuadFunction;
import cc.funkemunky.api.utils.objects.TriFunction;
import lombok.Getter;

import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;
import java.util.function.BiFunction;
import java.util.function.Function;

@Getter
public class WrappedMethod {
    private final WrappedClass parent;
    private Method method;
    private final String name;
    private MethodFunction mfunc;
    private final List<Class<?>> parameters;
    private boolean isVoid;

    public WrappedMethod(WrappedClass parent, Method method) {
        this.name = method.getName();
        this.method = method;
        this.parent = parent;
        this.parameters = Arrays.asList(method.getParameterTypes());

        try {
            int length = method.getParameterCount();
            switch(length) {
                case 0:
                    Function func = Reflections.createMethodLambda(method);
                    mfunc = new MethodFunction(method, func);
                    break;
                case 1:
                    BiFunction bifunc = Reflections.createMethodLambda(method);
                    mfunc = new MethodFunction(method, bifunc);
                    break;
                case 2:
                    TriFunction trifunc = Reflections.createMethodLambda(method);
                    mfunc = new MethodFunction(method, trifunc);
                    break;
                case 3:
                    QuadFunction quadFunc = Reflections.createMethodLambda(method);
                    mfunc = new MethodFunction(method, quadFunc);
                    break;
                default:
                    method.setAccessible(true);
                    mfunc = new MethodFunction(method);
                    break;
            }
        } catch (Throwable throwable) {
            throwable.printStackTrace();
        }
        isVoid = method.getReturnType().equals(void.class);
    }

    public <T> T invoke(Object object, Object... args) {
        return mfunc.invokeMethod(object, args);
    }

    public int getModifiers() {
        return this.method.getModifiers();
    }
}
