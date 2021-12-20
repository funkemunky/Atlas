/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.api
        .reflections;

import cc.funkemunky.api.reflections.types.WrappedClass;
import cc.funkemunky.api.tinyprotocol.api.ProtocolVersion;
import cc.funkemunky.api.utils.ClassScanner;
import cc.funkemunky.api.utils.objects.QuadFunction;
import cc.funkemunky.api.utils.objects.TriFunction;
import lombok.Getter;
import lombok.SneakyThrows;
import lombok.val;
import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.Main;

import java.lang.invoke.LambdaMetafactory;
import java.lang.invoke.MethodHandles;
import java.lang.invoke.MethodType;
import java.lang.reflect.Method;
import java.util.Set;
import java.util.function.BiFunction;
import java.util.function.Function;
import java.util.regex.Pattern;

@Getter
public class Reflections {
    private static final String craftBukkitString;
    private static final String netMinecraftServerString;
    private static MethodHandles.Lookup lookup = MethodHandles.lookup();
    private static Set<String> classNames;

    static {
        String version = Bukkit.getServer().getClass().getPackage().getName().split("\\.")[3];
        craftBukkitString = "org.bukkit.craftbukkit." + version + ".";
        netMinecraftServerString = "net.minecraft.server." + version + ".";

        classNames = ClassScanner.scanFile2(null, Main.class);
    }

    public static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static WrappedClass getCBClass(String name) {
        return getClass(craftBukkitString + name);
    }

    @SneakyThrows
    public static WrappedClass getNMSClass(String name) {
        try {
            return getClass(netMinecraftServerString + name);
        } catch(Exception e) {
            Pattern toTest = Pattern.compile("\\." + name.replace("$", ".") + "$");
            for (String className : classNames) {
                if(!className.startsWith("net.minecraft")) continue;

                if(toTest.matcher(className).find()) {
                    return getClass(className);
                }
            }
            throw new ClassNotFoundException(name);
        }
    }

    public static WrappedClass getClass(String name) {
        try {
            return new WrappedClass(Class.forName(name));
        } catch (ClassNotFoundException | NoClassDefFoundError e) {
            throw new NullPointerException("Class" + name + " could not be found!");
        }
    }

    public static WrappedClass getUtilClass(String name) {
        return getClass((ProtocolVersion.getGameVersion().isBelow(ProtocolVersion.V1_8)
                ? "net.minecraft.util." : "") + name.replace("cc.funkemunky.api.utils.", ""));
    }

    @SneakyThrows
    public static <T> T createMethodLambda(Method method) {
        if(!method.isAccessible()) return null;
        val handle = lookup.unreflect(method);
        Class<?> functionType;
        switch(method.getParameterCount()) {
            case 0:
                functionType = Function.class;
                break;
            case 1:
                functionType = BiFunction.class;
                break;
            case 2:
                functionType = TriFunction.class;
                break;
            case 3:
                functionType = QuadFunction.class;
            default:
                functionType = null;
                break;
        }

        if(functionType != null) {
            return (T) LambdaMetafactory.metafactory(lookup, "apply",
                    MethodType.methodType(functionType),
                    MethodType.methodType(method.getReturnType(), handle.type().parameterArray()),
                    handle, handle.type()).getTarget().invoke();
        }

        return null;
    }

    public static WrappedClass getClass(Class clazz) {
        return new WrappedClass(clazz);
    }
}
