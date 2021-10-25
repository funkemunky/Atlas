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

import java.lang.annotation.Annotation;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;

@Getter
public class WrappedField {
    private final WrappedClass parent;
    private final Field field;
    private final Class<?> type;

    public WrappedField(WrappedClass parent, Field field) {
        this.parent = parent;
        this.field = field;
        this.type = field.getType();
        this.field.setAccessible(true);
    }

    public <T> T get(Object parent) {
        try {
            return (T) this.field.get(parent);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
            return null;
        }
    }

    public void set(Object parent, Object value) {
        try {
            this.field.setAccessible(true);
            assert !Modifier.isFinal(field.getModifiers()) : "Field " + field.getName() + " is final and cannot be set";
            this.field.set(parent, value);
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        }
    }

    public boolean isAnnotationPresent(Class<? extends Annotation> annClass) {
        return field.isAnnotationPresent(annClass);
    }

    public <T> T getAnnotation(Class<? extends Annotation> annClass) {
        return (T) field.getAnnotation(annClass);
    }

    public int getModifiers() {
        return this.field.getModifiers();
    }
}
