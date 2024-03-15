package com.poiji.util;

import com.poiji.exception.PoijiException;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

public class ImmutableInstanceRegistrar {

    private static final Map<Class<?>, Object> empties = new ConcurrentHashMap<>();

    static {
        register(char.class, (char) 1);
        register(Character.class, (char) 1);
        register(byte.class, (byte) 1);
        register(Byte.class, (byte) 1);
        register(boolean.class, true);
        register(Boolean.class, true);
        register(int.class, 1);
        register(Integer.class, 1);
        register(long.class, 1L);
        register(Long.class, 1L);
        register(short.class, (short) 1);
        register(Short.class, (short) 1);
        register(double.class, 1.0);
        register(Double.class, 1.0);
        register(float.class, 1f);
        register(Float.class, 1f);
        register(String.class, "");
        register(Date.class, new Date());
        register(LocalDate.class, LocalDate.now());
        register(LocalDateTime.class, LocalDateTime.now());
        register(BigDecimal.class, BigDecimal.ZERO);
        register(Map.class, Collections.emptyMap());
        register(List.class, Collections.emptyList());
    }

    public static <T> void register(Class<T> type, T instance) {
        empties.put(type, instance);
    }

    static <T> T getEmptyInstance(Class<T> type) {
        if (empties.containsKey(type)) {
            return (T) empties.get(type);
        }
        final String message = String.format("Use %s.register() to register empty instance for '%s' first",
                ImmutableInstanceRegistrar.class.getName(), type.getName());
        throw new PoijiException(message);
    }
}
