package com.poiji.util;

import com.poiji.annotation.ExcelConstructor;
import com.poiji.exception.PoijiInstantiationException;

import java.lang.reflect.Constructor;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ConstructorSelector {

    private static final Map<Class<?>, Constructor<?>> fieldDefaultsMapping = new ConcurrentHashMap<>();

    public static Constructor<?> selectConstructor(Class<?> type) {
        return fieldDefaultsMapping.computeIfAbsent(type, ignored -> setAccessible(defineConstructor(type)));
    }

    private static Constructor<?> setAccessible(Constructor<?> constructor) {
        if (!constructor.isAccessible()) {
            constructor.setAccessible(true);
        }
        return constructor;
    }

    private static Constructor<?> defineConstructor(Class<?> type) {
        final Constructor<?>[] constructors = type.getDeclaredConstructors();
        if (constructors.length > 1) {
            final Constructor<?> constructor = getMarkedConstructor(type, constructors);
            if (constructor == null) {
                final String annotation = ExcelConstructor.class.getSimpleName();
                final String message = String.format("Several constructors were found in %s. Mark one of it with @%s please.", type.getName(), annotation);
                throw new PoijiInstantiationException(message, null);
            }
            return constructor;
        }
        return constructors[0];
    }

    private static Constructor<?> getMarkedConstructor(Class<?> type, Constructor<?>[] constructors) {
        Constructor<?> result = null;
        for (Constructor<?> constructor : constructors) {
            if (constructor.isAnnotationPresent(ExcelConstructor.class)) {
                if (result != null) {
                    final String annotation = ExcelConstructor.class.getSimpleName();
                    final String message = String.format("Several constructors are marked with @%s in %s. Mark only one of it please.", annotation, type.getName());
                    throw new PoijiInstantiationException(message, null);
                }
                result = constructor;
            }
        }
        return result;
    }
}
