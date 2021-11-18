package com.poiji.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelList {

    /**
     * Count of neighbour cells in row that belongs to one list element
     */
    int elementSize();

    /**
     * First column number that belongs to list
     */
    int listStart();

    /**
     * Last column number that belongs to list
     */
    int listEnd() default Integer.MAX_VALUE;
}
