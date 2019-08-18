/*
 * Created by Justin Heflin on 4/19/18 8:21 PM
 * Copyright (c) 2018.
 *
 * Can be redistributed non commercially as long as credit is given to original copyright owner.
 *
 * last modified: 4/19/18 7:22 PM
 */
package cc.funkemunky.api.tinyprotocol.api.packets.reflections.types;

import lombok.Getter;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

@Getter
public class WrappedClass {
    private final Class parent;

    public WrappedClass(Class parent) {
        this.parent = parent;
    }

    public WrappedField getFieldByName(String name) {
        Field tempField = null;
        for (Field field : this.parent.getDeclaredFields()) {
            if (field.getName().equals(name)) {
                tempField = field;
                break;
            }
        }
        if (tempField != null) {
            tempField.setAccessible(true);
            return new WrappedField(this, tempField);
        }
        return null;
    }

    public WrappedConstructor getConstructor(Class... types) {
        try {
            return new WrappedConstructor(this, this.parent.getDeclaredConstructor(types));
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
            return null;
        }
    }

    public WrappedConstructor getConstructor() {
        if(Arrays.stream(this.parent.getConstructors()).anyMatch(cons -> cons.getParameterCount() == 0)) {
            return new WrappedConstructor(this);
        }
        return null;
    }

    public WrappedConstructor getConstructorAtIndex(int index) {
        return new WrappedConstructor(this, this.parent.getConstructors()[index]);
    }

    public WrappedField getFieldByType(Class<?> type, int index) {
        for (Field field : this.parent.getDeclaredFields()) {
            if (field.getType().equals(type) && index-- <= 0) {
                return new WrappedField(this, field);
            }
        }
        throw new NullPointerException("Could not find field with type " + type.getSimpleName() + " at index " + index);
    }

    public WrappedField getFirstFieldByType(Class<?> type) {
        return this.getFieldByType(type, 0);
    }

    public WrappedMethod getMethod(String name, Class... parameters) {
        for (Method method : this.parent.getDeclaredMethods()) {
            if (!method.getName().equals(name) || parameters.length != method.getParameterTypes().length) {
                continue;
            }
            boolean same = true;
            for (int x = 0; x < method.getParameterTypes().length; x++) {
                if (method.getParameterTypes()[x] != parameters[x]) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return new WrappedMethod(this, method);
            }
        }
        for (Method method : this.parent.getMethods()) {
            if (!method.getName().equals(name) || parameters.length != method.getParameterTypes().length) {
                continue;
            }
            boolean same = true;
            for (int x = 0; x < method.getParameterTypes().length; x++) {
                if (method.getParameterTypes()[x] != parameters[x]) {
                    same = false;
                    break;
                }
            }
            if (same) {
                return new WrappedMethod(this, method);
            }
        }
        return null;
    }

    public WrappedMethod getDeclaredMethodByType(Class<?> type, int index) {
        for (Method method : this.parent.getDeclaredMethods()) {
            if(method.getReturnType().equals(type) && index-- <= 0) {
                return new WrappedMethod(this, method);
            }
        }
        throw new NullPointerException("Could not find method with return type " + type.getSimpleName() + " at index " + index);
    }

    public WrappedMethod getMethodByType(Class<?> type, int index) {
        for (Method method : this.parent.getMethods()) {
            if(method.getReturnType().equals(type) && index-- <= 0) {
                return new WrappedMethod(this, method);
            }
        }
        throw new NullPointerException("Could not find method with return type " + type.getSimpleName() + " at index " + index);
    }

    public Enum getEnum(String name) {
        return Enum.valueOf(this.parent, name);
    }
}
