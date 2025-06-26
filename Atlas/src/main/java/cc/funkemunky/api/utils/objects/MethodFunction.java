package cc.funkemunky.api.utils.objects;

import cc.funkemunky.api.utils.MiscUtils;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.function.BiFunction;
import java.util.function.Function;

public class MethodFunction {
    private Function function;
    private BiFunction biFunc;
    private TriFunction triFunc;
    private QuadFunction quadFunc;
    private Method method;

    public MethodFunction(Method method, Function function) {
        this.function = function;
        this.method = method;
        method.setAccessible(true);
    }

    public MethodFunction(Method method, BiFunction biFunc) {
        this.biFunc = biFunc;
        this.method = method;
        method.setAccessible(true);
    }

    public MethodFunction(Method method, TriFunction triFunc) {
        this.triFunc = triFunc;
        this.method = method;
        method.setAccessible(true);
    }

    public MethodFunction(Method method, QuadFunction quadFunc) {
        this.quadFunc = quadFunc;
        this.method = method;
        method.setAccessible(true);
    }

    public MethodFunction(Method method) {
        this.method = method;
        method.setAccessible(true);
    }

    //Apparently not allowed to convert Function from Object. I guess it's because they're an interface :/.
    /*public MethodFunction(Method method, Object objFunc) {
        if(objFunc instanceof Function) {
            this.function = (Function) objFunc;
        } else if(objFunc instanceof BiFunction) {
            this.biFunc = (BiFunction) objFunc;
        } else if(objFunc instanceof TriFunction) {
            this.triFunc = (TriFunction) objFunc;
        } else if(objFunc instanceof QuadFunction) {
            this.quadFunc = (QuadFunction) objFunc;
        }
    }*/

    /** Maximum arguments of 3 **/
    public <T> T invokeMethod(Object instance, Object... args) {
        if(args.length <= 3) {
            if(function != null) {
                return (T) function.apply(instance);
            } else if(biFunc != null) {
                return (T) biFunc.apply(instance, MiscUtils.getArgOrNull(args, 0));
            } else if(triFunc != null) {
                return (T) triFunc.apply(instance, MiscUtils.getArgOrNull(args, 0),
                        MiscUtils.getArgOrNull(args, 1));
            } else if(quadFunc != null) {
                return (T) quadFunc.apply(instance, MiscUtils.getArgOrNull(args, 0),
                        MiscUtils.getArgOrNull(args, 1), MiscUtils.getArgOrNull(args, 2));
            }
        }

        try {
            return (T) method.invoke(instance, args);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
            return null;
        }
    }
}
