/*
 * Decompiled with CFR 0.152.
 */
package br.com.dragonmc.core.common.utils.reflection;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.Arrays;

public final class Reflection {
    private Reflection() {
    }

    public static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType) {
        return Reflection.getField(target, name, fieldType, 0);
    }

    public static <T> FieldAccessor<T> getField(Class<?> target, Class<T> fieldType, int index) {
        return Reflection.getField(target, null, fieldType, index);
    }

    private static <T> FieldAccessor<T> getField(Class<?> target, String name, Class<T> fieldType, int index) {
        for (final Field field : target.getDeclaredFields()) {
            if (name != null && !field.getName().equals(name) || !fieldType.isAssignableFrom(field.getType()) || index-- > 0) continue;
            field.setAccessible(true);
            return new FieldAccessor<T>(){

                @Override
                public T get(Object target) {
                    try {
                        return (T) field.get(target);
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access reflection.", e);
                    }
                }

                @Override
                public void set(Object target, Object value) {
                    try {
                        field.set(target, value);
                    }
                    catch (IllegalAccessException e) {
                        throw new RuntimeException("Cannot access reflection.", e);
                    }
                }

                @Override
                public boolean hasField(Object target) {
                    return field.getDeclaringClass().isAssignableFrom(target.getClass());
                }
            };
        }
        if (target.getSuperclass() != null) {
            return Reflection.getField(target.getSuperclass(), name, fieldType, index);
        }
        throw new IllegalArgumentException("Cannot contains field with type " + fieldType);
    }

    public static MethodInvoker getMethod(Class<?> clazz, String methodName, Class<?> ... params) {
        return Reflection.getTypedMethod(clazz, methodName, null, params);
    }

    public static MethodInvoker getTypedMethod(Class<?> clazz, String methodName, Class<?> returnType, Class<?> ... params) {
        for (final Method method : clazz.getDeclaredMethods()) {
            if ((methodName != null && !method.getName().equals(methodName) || returnType != null) && (!method.getReturnType().equals(returnType) || !Arrays.equals(method.getParameterTypes(), params))) continue;
            method.setAccessible(true);
            return new MethodInvoker(){

                @Override
                public Object invoke(Object target, Object ... arguments) {
                    try {
                        return method.invoke(target, arguments);
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Cannot invoke method " + method, e);
                    }
                }
            };
        }
        if (clazz.getSuperclass() != null) {
            return Reflection.getMethod(clazz.getSuperclass(), methodName, params);
        }
        throw new IllegalStateException(String.format("Unable to contains method %s (%s).", methodName, Arrays.asList(params)));
    }

    public static ConstructorInvoker getConstructor(Class<?> clazz, Class<?> ... params) {
        for (final Constructor<?> constructor : clazz.getDeclaredConstructors()) {
            if (!Arrays.equals(constructor.getParameterTypes(), params)) continue;
            constructor.setAccessible(true);
            return new ConstructorInvoker(){

                @Override
                public Object invoke(Object ... arguments) {
                    try {
                        return constructor.newInstance(arguments);
                    }
                    catch (Exception e) {
                        throw new RuntimeException("Cannot invoke constructor " + constructor, e);
                    }
                }
            };
        }
        throw new IllegalStateException(String.format("Unable to contains constructor for %s (%s).", clazz, Arrays.asList(params)));
    }

    public static Object getHandle(Object obj) {
        try {
            return Reflection.getMethod(obj.getClass(), "getHandle", new Class[0]).invoke(obj, new Object[0]);
        }
        catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void setField(Object object, String fieldName, Object finalObject) {
        try {
            Field field = object.getClass().getDeclaredField(fieldName);
            field.setAccessible(true);
            field.set(fieldName, finalObject);
        }
        catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException e) {
            e.printStackTrace();
        }
    }

    public static Field getField(Class<?> clazz, String fieldName) {
        if (clazz != null && clazz != Object.class) {
            try {
                Field field = clazz.getDeclaredField(fieldName);
                field.setAccessible(true);
                return field;
            }
            catch (NoSuchFieldException e) {
                clazz = clazz.getSuperclass();
            }
        }
        return null;
    }

    public static Field getFieldWithException(Class<?> clazz, String name) throws Exception {
        Field field = clazz.getDeclaredField(name);
        field.setAccessible(true);
        return field;
    }

    public static boolean ClassListEqual(Class<?>[] l1, Class<?>[] l2) {
        boolean equal = true;
        if (l1.length != l2.length) {
            return false;
        }
        for (int i = 0; i < l1.length; ++i) {
            if (l1[i] == l2[i]) continue;
            equal = false;
            break;
        }
        return equal;
    }

    public static interface FieldAccessor<T> {
        public T get(Object var1);

        public void set(Object var1, Object var2);

        public boolean hasField(Object var1);
    }

    public static interface MethodInvoker {
        public Object invoke(Object var1, Object ... var2);
    }

    public static interface ConstructorInvoker {
        public Object invoke(Object ... var1);
    }
}

