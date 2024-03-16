package com.poiji.util;

import com.poiji.annotation.ExcelCellRange;
import com.poiji.bind.mapping.Data;
import com.poiji.exception.IllegalCastException;
import com.poiji.exception.PoijiInstantiationException;
import java.lang.annotation.Annotation;
import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

public class ReflectUtil {
    public static <T> T newInstanceOf(Class<T> type) {
        T obj;
        try {
            Constructor<T> constructor = type.getDeclaredConstructor();
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            obj = constructor.newInstance();
        } catch (Exception ex) {
            throw new PoijiInstantiationException("Cannot create a new instance of " + type.getName(), ex);
        }

        return obj;
    }

    public static <T> T newInstanceOf(Data data) {
        final Class<?> type = data.getDeclaringClass();
        if (type == null) {
            return null;
        }
        return newInstanceOf(type, data);
    }

    public static <T> T newInstanceOf(Class<?> type, Data data) {
        try {
            final Constructor<?> constructor = type.getDeclaredConstructors()[0];
            if (!constructor.isAccessible()) {
                constructor.setAccessible(true);
            }
            if (constructor.getParameterCount() == 0) {
                return createInstanceUsingDefaultConstructor(data, constructor);
            } else {
                return createInstanceUsingParameterizedConstructor(data, constructor);
            }
        } catch (Exception ex) {
            throw new PoijiInstantiationException("Cannot create a new instance of " + type.getName(), ex);
        }
    }

    private static <T> T createInstanceUsingParameterizedConstructor(Data data, Constructor<?> constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final Object[] parameters = defineConstructorParameters(data, constructor);
        final T instance = (T) constructor.newInstance(parameters);
        fillPreCreatedProperties(data, instance);
        return instance;
    }

    private static <T> void fillPreCreatedProperties(Data data, T instance) throws IllegalAccessException {
        for (Map.Entry<Field, Object> entry : data.entrySet()) {
            final Field field = entry.getKey();
            final Class<?> fieldType = field.getType();
            if (fieldType.isAssignableFrom(Map.class)) {
                final Map property = (Map) field.get(instance);
                property.putAll((Map) entry.getValue());
            } else if (fieldType.isAssignableFrom(Collection.class)) {
                final Collection property = (Collection) field.get(instance);
                property.addAll((Collection) entry.getValue());
            }
        }
    }

    private static Object[] defineConstructorParameters(Data data, Constructor<?> constructor) {
        final Object[] constructorFields = ConstructorFieldMapper.getConstructorFields(constructor);
        final int count = constructorFields.length;
        final Object[] parameters = new Object[count];
        for (int i = 0; i < count; i++) {
            final Object parameter = constructorFields[i];
            if (parameter != null && parameter.getClass()  == Field.class) {
                final Field field = (Field) parameter;
                final Object value = data.remove(field);
                if (value instanceof List) {
                    instantiateListElements((List) value);
                }
                parameters[i] = value;
            } else {
                parameters[i] = parameter;
            }
        }
        return parameters;
    }

    private static void instantiateListElements(List list) {
        for (int j = 0; j < list.size(); j++) {
            final Object element = list.get(j);
            if (element.getClass() == Data.class) {
                list.set(j, newInstanceOf((Data) element));
            }
        }
    }

    private static <T> T createInstanceUsingDefaultConstructor(Data data, Constructor<?> constructor) throws InstantiationException, IllegalAccessException, InvocationTargetException {
        final T instance = (T) constructor.newInstance();
        for (Map.Entry<Field, Object> entry : data.entrySet()) {
            entry.getKey().set(instance, getValue(entry));
        }
        return instance;
    }

    private static Object getValue(Map.Entry<Field, Object> entry) {
        final Object value = entry.getValue();
        if (value != null && value.getClass() == Data.class) {
            final Class<?> type = entry.getKey().getType();
            return newInstanceOf(type, (Data) value);
        } else if (value instanceof Collection){
            final Collection<?> valueCollection = (Collection<?>) value;
            if (valueCollection.iterator().next().getClass() == Data.class) {
                final Collection<?> result = (Collection<?>) newInstanceOf(value.getClass());
                final Collection<Data> dataCollection = (Collection<Data>) valueCollection;
                for (Data data : dataCollection) {
                    result.add(newInstanceOf(data));
                }
                return result;
            } else {
                return valueCollection;
            }
        }
        return value;
    }

    /**
     * Finds a particular annotation on a class and checks subtypes marked with ExcelCellRange recursively.
     * <p>
     * Recursively does not refer to super classes.
     */
    public static <T, A extends Annotation> Collection<A> findRecursivePoijiAnnotations(Class<T> typeToInspect,
        Class<A> annotationType) {
        List<A> annotations = new ArrayList<>();

        for (Field field : typeToInspect.getDeclaredFields()) {
            Annotation excelCellRange = field.getAnnotation(ExcelCellRange.class);
            if (excelCellRange != null) {
                annotations.addAll(findRecursivePoijiAnnotations(field.getType(), annotationType));
            } else {
                A fieldAnnotation = field.getAnnotation(annotationType);
                if (fieldAnnotation != null) {
                    annotations.add(fieldAnnotation);
                }
            }
        }

        return annotations;
    }

    public static void setFieldData(Field field, Object o, Object instance) {
        try {
            setAccessible(field);
            field.set(instance, o);
        } catch (IllegalAccessException e) {
            throw new IllegalCastException("Unexpected cast type {" + o + "} of field" + field.getName());
        }
    }

    public static void setAccessible(Field field) {
        if (!field.isAccessible()) {
            field.setAccessible(true);
        }
    }
}
