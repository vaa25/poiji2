package com.poiji.bind.mapping;

import java.lang.reflect.Field;
import java.util.HashMap;

public class Data extends HashMap<Field, Object> {

    public Class<?> getDeclaringClass() {
        if (isEmpty()) {
            return null;
        }
        return super.entrySet().iterator().next().getKey().getDeclaringClass();
    }
}
