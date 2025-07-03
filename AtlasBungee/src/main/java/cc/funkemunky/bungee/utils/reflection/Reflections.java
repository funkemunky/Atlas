/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.bungee.utils.reflection;

import cc.funkemunky.bungee.utils.reflection.types.WrappedClass;

public class Reflections {

    public static boolean classExists(String name) {
        try {
            Class.forName(name);
            return true;
        } catch (ClassNotFoundException e) {
            return false;
        }
    }

    public static WrappedClass getClass(String name) {
        try {
            return new WrappedClass(Class.forName(name));
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static WrappedClass getClass(Class clazz) {
        return new WrappedClass(clazz);
    }
}
