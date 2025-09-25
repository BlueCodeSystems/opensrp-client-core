package org.powermock.reflect;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.lang.reflect.Modifier;
import java.util.Arrays;

/**
 * Minimal reflection helper that mimics the subset of the PowerMock Whitebox API used in tests.
 * This avoids the heavy PowerMock dependency while keeping existing tests functional on JDK 17.
 */
public final class Whitebox {

    private Whitebox() {
        // no instances
    }

    public static void setInternalState(Object target, String fieldName, Object value) {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        setField(target.getClass(), target, fieldName, value);
    }

    public static void setInternalState(Class<?> targetClass, String fieldName, Object value) {
        if (targetClass == null) {
            throw new IllegalArgumentException("targetClass must not be null");
        }
        Field field = resolveField(targetClass, fieldName);
        if (!Modifier.isStatic(field.getModifiers())) {
            throw new IllegalArgumentException("Field '" + fieldName + "' is not static; provide an instance instead");
        }
        setField(field, null, value);
    }

    private static void setField(Class<?> type, Object instance, String fieldName, Object value) {
        Field field = resolveField(type, fieldName);
        setField(field, instance, value);
    }

    private static void setField(Field field, Object instance, Object value) {
        try {
            field.setAccessible(true);
            removeFinalModifier(field);
            field.set(instance, value);
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to set field '" + field.getName() + "'", e);
        }
    }

    public static <T> T getInternalState(Object target, String fieldName) {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        Field field = resolveField(target.getClass(), fieldName);
        try {
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            T value = (T) field.get(target);
            return value;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to read field '" + fieldName + "'", e);
        }
    }

    public static <T> T getInternalState(Class<?> targetClass, String fieldName) {
        Field field = resolveField(targetClass, fieldName);
        try {
            field.setAccessible(true);
            @SuppressWarnings("unchecked")
            T value = (T) field.get(null);
            return value;
        } catch (IllegalAccessException e) {
            throw new IllegalStateException("Unable to read static field '" + fieldName + "'", e);
        }
    }

    public static <T> T invokeMethod(Object target, String methodName, Object... args) {
        if (target == null) {
            throw new IllegalArgumentException("target must not be null");
        }
        Method method = resolveMethod(target.getClass(), methodName, args);
        try {
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(target, args);
            return result;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to invoke method '" + methodName + "'", e);
        }
    }

    public static <T> T invokeMethod(Class<?> targetClass, String methodName, Object... args) {
        Method method = resolveMethod(targetClass, methodName, args);
        try {
            method.setAccessible(true);
            @SuppressWarnings("unchecked")
            T result = (T) method.invoke(null, args);
            return result;
        } catch (ReflectiveOperationException e) {
            throw new IllegalStateException("Unable to invoke static method '" + methodName + "'", e);
        }
    }

    private static Field resolveField(Class<?> type, String name) {
        Class<?> current = type;
        while (current != null) {
            try {
                return current.getDeclaredField(name);
            } catch (NoSuchFieldException ignored) {
                current = current.getSuperclass();
            }
        }
        throw new IllegalStateException("Field '" + name + "' not found on " + type.getName());
    }

    private static Method resolveMethod(Class<?> type, String name, Object[] args) {
        Class<?> current = type;
        while (current != null) {
            for (Method method : current.getDeclaredMethods()) {
                if (!method.getName().equals(name)) {
                    continue;
                }
                if (parametersMatch(method.getParameterTypes(), args)) {
                    return method;
                }
            }
            current = current.getSuperclass();
        }
        throw new IllegalStateException("Method '" + name + "' with parameters " + Arrays.toString(argumentTypes(args)) + " not found on " + type.getName());
    }

    private static boolean parametersMatch(Class<?>[] parameterTypes, Object[] args) {
        if (parameterTypes.length != (args == null ? 0 : args.length)) {
            return false;
        }
        for (int i = 0; i < parameterTypes.length; i++) {
            Object arg = args[i];
            Class<?> paramType = parameterTypes[i];
            if (arg == null) {
                if (paramType.isPrimitive()) {
                    return false;
                }
                continue;
            }
            if (!wrap(paramType).isAssignableFrom(arg.getClass())) {
                return false;
            }
        }
        return true;
    }

    private static Class<?>[] argumentTypes(Object[] args) {
        if (args == null) {
            return new Class<?>[0];
        }
        Class<?>[] types = new Class<?>[args.length];
        for (int i = 0; i < args.length; i++) {
            types[i] = args[i] == null ? Object.class : args[i].getClass();
        }
        return types;
    }

    private static Class<?> wrap(Class<?> type) {
        if (!type.isPrimitive()) {
            return type;
        }
        if (type == boolean.class) return Boolean.class;
        if (type == byte.class) return Byte.class;
        if (type == char.class) return Character.class;
        if (type == short.class) return Short.class;
        if (type == int.class) return Integer.class;
        if (type == long.class) return Long.class;
        if (type == float.class) return Float.class;
        if (type == double.class) return Double.class;
        if (type == void.class) return Void.class;
        return type;
    }

    private static void removeFinalModifier(Field field) {
        if (!Modifier.isFinal(field.getModifiers())) {
            return;
        }
        try {
            Field modifiersField = Field.class.getDeclaredField("modifiers");
            modifiersField.setAccessible(true);
            modifiersField.setInt(field, field.getModifiers() & ~Modifier.FINAL);
        } catch (NoSuchFieldException | IllegalAccessException ignored) {
            // best effort; if we cannot remove the modifier the subsequent set() may still succeed for non-inlined fields
        }
    }

}
