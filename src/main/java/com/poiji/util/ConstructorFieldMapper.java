package com.poiji.util;

import com.poiji.exception.PoijiInstantiationException;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

class ConstructorFieldMapper {

    private static final Map<Constructor<?>, Object[]> fieldDefaultsMapping = new ConcurrentHashMap<>();
    private static final Map<Constructor<?>, Object[]> constructorMappings = new ConcurrentHashMap<>();

    static Object[] getConstructorFields(Constructor<?> constructor) {
        return constructorMappings.computeIfAbsent(constructor, ignored -> getConstructorMapping(constructor));
    }

    private static Object[] getConstructorMapping(Constructor<?> constructor) {
        final Class<?> entity = constructor.getDeclaringClass();
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] defaults = fieldDefaultsMapping.computeIfAbsent(constructor, ignored -> getDefaultValues(parameterTypes));
        final Object[] constructorMapping = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            final Object[] clone = defaults.clone();
            final Class<?> parameterType = parameterTypes[i];
            final Object example = ImmutableInstanceRegistrar.getEmptyInstance(parameterType);
            clone[i] = example;
            try {
                final Object instance = constructor.newInstance(clone);
                final Field[] fields = entity.getDeclaredFields();
                for (Field field : fields) {
                    if (!field.isAccessible()) {
                        field.setAccessible(true);
                    }
                    try {
                        if (isFieldHasExampleValue(field, instance, example)) {
                            constructorMapping[i] = field;
                            break;
                        }
                    } catch (IllegalAccessException e) {
                        throw new PoijiInstantiationException("Can't get field " + field, e);
                    }
                }
            } catch (InstantiationException | IllegalAccessException | InvocationTargetException e) {
                throw new PoijiInstantiationException("Can't create instance " + entity, e);
            }
            if (constructorMapping[i] == null) {
                constructorMapping[i] = defaults[i];
            }
        }
        return constructorMapping;

    }

    private static boolean isFieldHasExampleValue(Field field, Object instance, Object example) throws IllegalAccessException {
        final Object value = field.get(instance);
        return field.getType().isPrimitive() && example.equals(value) || value == example;
    }

    private static Object[] getDefaultValues(Class<?>[] parameterTypes) {
        final Object[] defaultValues = new Object[parameterTypes.length];
        for (int i = 0; i < parameterTypes.length; i++) {
            defaultValues[i] = getDefaultValue(parameterTypes[i]);
        }
        return defaultValues;
    }

    private static Object getDefaultValue(Class<?> parameterType) {
        if (parameterType.isPrimitive()) {
            return getDefaultPrimitive(parameterType);
        }
        return null;
    }

    private static Object getDefaultPrimitive(Class<?> parameterType) {
        switch (parameterType.getName()) {
            case "boolean":
                return false;
            case "int":
                return 0;
            case "long":
                return 0L;
            case "short":
                return (short) 0;
            case "double":
                return 0.0;
            case "float":
                return 0.0f;
            case "byte":
                return (byte) 0x0;
            case "char":
                return (char) 0x0;
            default:
                return null;
        }
    }
}
