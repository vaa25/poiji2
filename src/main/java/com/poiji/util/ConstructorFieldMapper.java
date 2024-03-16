package com.poiji.util;

import com.poiji.exception.PoijiInstantiationException;

import java.beans.ConstructorProperties;
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
        final Object[] constructorMapping = new Object[constructor.getParameterCount()];
        mapFieldsWithConstructorProperties(constructor, constructorMapping);
        findNotMappedFieldsWithExamining(constructor, constructorMapping);
        fillNotMappedParametersWithDefaults(constructor, constructorMapping);
        return constructorMapping;
    }

    /**
     * ConstructorProperties is used by lombok. It allows to map any custom fields easy without examining.
     */
    private static Object[] mapFieldsWithConstructorProperties(Constructor<?> constructor, Object[] constructorMapping) {
        final ConstructorProperties constructorProperties = constructor.getAnnotation(ConstructorProperties.class);
        if (constructorProperties != null) {
            final Field[] fields = constructor.getDeclaringClass().getDeclaredFields();
            final String[] parameterNames = constructorProperties.value();
            final Class<?>[] parameterTypes = constructor.getParameterTypes();
            for (int i = 0; i < parameterNames.length; i++) {
                final String parameterName = parameterNames[i];
                for (Field field : fields) {
                    if (field.getName().equals(parameterName) && field.getType() == parameterTypes[i]) {
                        ReflectUtil.setAccessible(field);
                        constructorMapping[i] = field;
                        break;
                    }
                }
            }
        }
        return constructorMapping;
    }

    /**
     * The only way to find mapping between constructor parameters and instance fields is to pass special value
     * into constructor and look it up in every field in instance.
     * Knowing what parameter was passed and what field was found in we can define one mapping.
     */
    private static Object[] findNotMappedFieldsWithExamining(Constructor<?> constructor, Object[] constructorMapping) {
        final Class<?> entity = constructor.getDeclaringClass();
        final Class<?>[] parameterTypes = constructor.getParameterTypes();
        final Object[] defaultConstructorParameters = fieldDefaultsMapping.computeIfAbsent(constructor, ignored -> getDefaultValues(parameterTypes));
        final Field[] fields = entity.getDeclaredFields();
        for (int i = 0; i < parameterTypes.length; i++) {
            if (constructorMapping[i] == null) {
                final Object[] instanceConstructorParameters = defaultConstructorParameters.clone();
                final Class<?> parameterType = parameterTypes[i];
                final Object parameterToExamine = ImmutableInstanceRegistrar.getEmptyInstance(parameterType);
                instanceConstructorParameters[i] = parameterToExamine;
                try {
                    final Object instance = constructor.newInstance(instanceConstructorParameters);
                    for (Field field : fields) {
                        ReflectUtil.setAccessible(field);
                        try {
                            if (isFieldHasExampleValue(field, instance, parameterToExamine)) {
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
            }
        }
        return constructorMapping;
    }

    /**
     * Sometimes constructor can have parameter that not corresponds to any field.
     * We have to fill it anyway to construct instance successfully.
     */
    private static Object[] fillNotMappedParametersWithDefaults(Constructor<?> constructor, Object[] constructorMapping) {
        final Object[] defaultConstructorParameters = fieldDefaultsMapping.computeIfAbsent(constructor, ignored -> getDefaultValues(constructor.getParameterTypes()));
        for (int i = 0; i < constructorMapping.length; i++) {
            if (constructorMapping[i] == null) {
                constructorMapping[i] = defaultConstructorParameters[i];
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
