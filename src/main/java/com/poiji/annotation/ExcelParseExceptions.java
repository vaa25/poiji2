package com.poiji.annotation;

import java.lang.annotation.Documented;
import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Must be set on {@code Map<String, ExcelParseException>},
 * where key - excel column name, value - exception thrown while parsing cell value to java type.
 * Problem java property will be set to default value.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
@Documented
public @interface ExcelParseExceptions {
}
