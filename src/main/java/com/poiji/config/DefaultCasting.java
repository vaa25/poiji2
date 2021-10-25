package com.poiji.config;

import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import com.poiji.parser.BooleanParser;
import com.poiji.parser.Parsers;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.math.BigDecimal;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import static java.util.Collections.emptyList;
import static java.util.Collections.emptySet;
import static java.util.stream.Collectors.toList;
import static java.util.stream.Collectors.toSet;

/**
 * Created by hakan on 22/01/2017.
 */
public class DefaultCasting implements Casting {
    private final boolean errorLoggingEnabled;
    private Exception exception;

    private final List<DefaultCastingError> errors = new ArrayList<>();

    public DefaultCasting() {
        this(false);
    }

    public DefaultCasting(boolean errorLoggingEnabled) {
        this.errorLoggingEnabled = errorLoggingEnabled;
    }

    private <T> T onError(String value, String sheetName, int row, int col, Exception exception, T defaultValue) {
        logError(value, defaultValue, sheetName, row, col, exception);
        return defaultValue;
    }

    private void logError(String value, Object defaultValue, String sheetName, int row, int col, Exception exception) {
        this.exception = exception;
        if (errorLoggingEnabled) {
            errors.add(new DefaultCastingError(value, defaultValue, sheetName, row, col, exception));
        }
    }

    private boolean primitiveBooleanValue(String value, String sheetName, int row, int col) {
        try {
            return !value.isEmpty() && Parsers.booleans().parse(value);
        } catch (BooleanParser.BooleanParseException bpe) {
            return onError(value, sheetName, row, col, bpe, false);
        }
    }

    private Boolean booleanValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : false;
            }
            return Parsers.booleans().parse(value);
        } catch (BooleanParser.BooleanParseException bpe) {
            return onError(value, sheetName, row, col, bpe, options.preferNullOverDefault() ? null : false);
        }
    }

    private Byte byteValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : (byte) 0;
            }
            return Byte.valueOf(trimDecimal(value));
        } catch (Exception e) {
            return onError(value, sheetName, row, col, e, options.preferNullOverDefault() ? null : (byte) 0);
        }
    }

    private byte primitiveByteValue(String value, String sheetName, int row, int col) {
        try {
            return value.isEmpty() ? (byte) 0 : Byte.parseByte(trimDecimal(value));
        } catch (Exception e) {
            return onError(value, sheetName, row, col, e, (byte)0);
        }
    }

    private Short shortValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : (short) 0;
            }
            return Short.valueOf(trimDecimal(value));
        } catch (Exception e) {
            return onError(value, sheetName, row, col, e, options.preferNullOverDefault() ? null : (short) 0);
        }
    }

    private short primitiveShortValue(String value, String sheetName, int row, int col) {
        try {
            return value.isEmpty() ? (short) 0 : Short.parseShort(trimDecimal(value));
        } catch (Exception e) {
            return onError(value, sheetName, row, col, e, (short)0);
        }
    }

    private int primitiveIntegerValue(String value, String sheetName, int row, int col) {
        try {
            return value.isEmpty() ? 0 : Parsers.integers().parse(value).intValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, 0);
        }
    }

    private Integer integerValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : 0;
            }
            return Parsers.integers().parse(value).intValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, options.preferNullOverDefault() ? null : 0);
        }
    }

    private long primitiveLongValue(String value, String sheetName, int row, int col) {
        try {
            return value.isEmpty() ? 0 : Parsers.longs().parse(value).longValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, 0L);
        }
    }

    private Long longValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : 0L;
            }
            return Parsers.longs().parse(value).longValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, options.preferNullOverDefault() ? null : 0L);
        }
    }

    private double primitiveDoubleValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            return value.isEmpty() ? 0d : Parsers.numbers(options.getLocale()).parse(value).doubleValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, 0d);
        }
    }

    private Double doubleValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : 0d;
            }
            return Parsers.numbers(options.getLocale()).parse(value).doubleValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, options.preferNullOverDefault() ? null : 0d);
        }
    }

    private float primitiveFloatValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            return value.isEmpty() ? 0f : Parsers.numbers(options.getLocale()).parse(value).floatValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, 0f);
        }
    }

    private Float floatValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : 0f;
            }
            return Parsers.numbers(options.getLocale()).parse(value).floatValue();
        } catch (NumberFormatException nfe) {
            return onError(value, sheetName, row, col, nfe, options.preferNullOverDefault() ? null : 0f);
        }
    }

    private BigDecimal bigDecimalValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        try {
            if (value.isEmpty()) {
                return options.preferNullOverDefault() ? null : BigDecimal.ZERO;
            }
            return Parsers.bigDecimals().parse(value);
        } catch (NumberFormatException | IllegalStateException e) {
            return onError(value, sheetName, row, col, e, options.preferNullOverDefault() ? null : BigDecimal.ZERO);
        }
    }

    private Date dateValue(String value, String sheetName, int row, int col, PoijiOptions options) {

        //ISSUE #57
        //if a date regex has been specified then it wont be null
        //so then make sure the string matches the pattern
        //if it doesn't, fall back to default
        //else continue to turn string into java date

        //the reason for this is sometime Java will manage to parse a string to a date object
        //without any exceptions but since the string was not an exact match you get a very strange date
        if (options.getDateRegex() != null && !value.matches(options.getDateRegex())) {
            return options.preferNullOverDefault() ? null : Calendar.getInstance().getTime();
        } else {
            try {
                final SimpleDateFormat sdf = new SimpleDateFormat(options.datePattern());
                sdf.setLenient(options.getDateLenient());
                return sdf.parse(value);
            } catch (ParseException e) {
                return onError(value, sheetName, row, col, e, options.preferNullOverDefault() ? null : Calendar.getInstance().getTime());
            }
        }
    }

    private LocalDate localDateValue(String value, String sheetName, int row, int col, PoijiOptions options) {

        //ISSUE #57
        //if a date regex has been specified then it wont be null
        //so then make sure the string matches the pattern
        //if it doesn't, fall back to default
        //else continue to turn string into java date

        //the reason for this is sometime java will manage to parse a string to a date object
        //without any exceptions but since the string was not an exact match you get a very strange date
        if (options.getDateRegex() != null && !value.matches(options.getDateRegex())) {
            return options.preferNullOverDefault() ? null : LocalDate.now();
        } else {
            try {
                return LocalDate.parse(value, options.dateFormatter());
            } catch (DateTimeParseException e) {
                return onError(value, sheetName, row, col, e, options.preferNullOverDefault() ? null : LocalDate.now());
            }
        }
    }

    private LocalDateTime localDateTimeValue(String value, String sheetName, int row, int col, PoijiOptions options) {
        if (options.getDateTimeRegex() != null && !value.matches(options.getDateTimeRegex())) {
            return options.preferNullOverDefault() ? null : LocalDateTime.now();
        } else {
            try {
                return LocalDateTime.parse(value, options.dateTimeFormatter());
            } catch (DateTimeParseException e) {
                return onError(value, sheetName, row, col, e, options.preferNullOverDefault() ? null : LocalDateTime.now());
            }
        }
    }


    private Object enumValue(String value, String sheetName, int row, int col, Class type) {
        return Arrays.stream(type.getEnumConstants())
                .filter(o -> ((Enum<?>) o).name().equals(value))
                .findFirst()
                .orElseGet(() -> {
                    IllegalArgumentException e = new IllegalArgumentException("No enumeration " + type.getSimpleName() + "." + value);
                    return onError(value, sheetName, row, col, e, null);
                });
    }

    @Override
    public Object castValue(Field field, String rawValue, int row, int col, PoijiOptions options) {
        Class<?> fieldType = field.getType();
        return getValueObject(field, row, col, options, rawValue, fieldType);
    }

    protected Object getValueObject(Field field, int row, int col, PoijiOptions options, String rawValue, Class<?> fieldType) {
        this.exception = null;
        String sheetName = options.getSheetName();

        String value = options.trimCellValue() ? rawValue.trim() : rawValue;

        if (fieldType == int.class) {
            return primitiveIntegerValue(value, sheetName, row, col);

        } else if (fieldType == Integer.class) {
            return integerValue(trimDecimal(value), sheetName, row, col, options);

        } else if (fieldType == BigDecimal.class) {
            return bigDecimalValue(value, sheetName, row, col, options);

        } else if (fieldType == long.class) {
            return primitiveLongValue(trimDecimal(value), sheetName, row, col);

        } else if (fieldType == Long.class) {
            return longValue(trimDecimal(value), sheetName, row, col, options);

        } else if (fieldType == double.class) {
            return primitiveDoubleValue(value, sheetName, row, col, options);

        } else if (fieldType == Double.class) {
            return doubleValue(value, sheetName, row, col, options);

        } else if (fieldType == float.class) {
            return primitiveFloatValue(value, sheetName, row, col, options);

        } else if (fieldType == Float.class) {
            return floatValue(value, sheetName, row, col, options);

        } else if (fieldType == boolean.class) {
            return primitiveBooleanValue(value, sheetName, row, col);

        } else if (fieldType == Boolean.class) {
            return booleanValue(value, sheetName, row, col, options);

        } else if (fieldType == byte.class) {
            return primitiveByteValue(value, sheetName, row, col);

        } else if (fieldType == Byte.class) {
            return byteValue(value, sheetName, row, col, options);

        } else if (fieldType == short.class) {
            return primitiveShortValue(value, sheetName, row, col);

        } else if (fieldType == Short.class) {
            return shortValue(value, sheetName, row, col, options);

        } else if (fieldType == Date.class) {
            return dateValue(value, sheetName, row, col, options);

        } else if (fieldType == LocalDate.class) {
            return localDateValue(value, sheetName, row, col, options);

        } else if (fieldType == LocalDateTime.class) {
            return localDateTimeValue(value, sheetName, row, col, options);

        } else if (fieldType.isEnum()) {
            return enumValue(value, sheetName, row, col, fieldType);

        } else if (fieldType == List.class || fieldType == Collection.class) {
            return castListValue(value, sheetName, row, col, field, options);

        } else if (fieldType == Set.class) {
            return castSetValue(value, sheetName, row, col, field, options);

        } else if (value.isEmpty()) {
            return options.preferNullOverDefault() ? null : value;

        } else {
            return value;

        }
    }

    @Override
    public Exception getException() {
        return this.exception;
    }

    private String trimDecimal(final String string){
        int i = string.lastIndexOf('.');
        if (i == -1){
            i = string.lastIndexOf(',');
        }
        if (i == -1){
            return string;
        } else {
            return string.substring(0, i);
        }
    }

    public boolean isErrorLoggingEnabled() {
        return errorLoggingEnabled;
    }

    public List<DefaultCastingError> getErrors() {
        if (errorLoggingEnabled) {
            return Collections.unmodifiableList(errors);
        } else {
            throw new PoijiException("logging not enabled");
        }
    }

    private Object castListValue(String value, String sheetName, int row, int col, Field field, PoijiOptions options) {
        if (value.isEmpty()) {
            return options.preferNullOverDefault() ? null : emptyList();
        }
        final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        final Type fieldType = genericType.getActualTypeArguments()[0];
        final String[] valueList = value.split(options.getListDelimiter());

        if (fieldType == Integer.class) {
            return Stream.of(valueList).map(rv -> primitiveIntegerValue(rv, sheetName, row, col)).collect(toList());
        } else if (fieldType == BigDecimal.class) {
            return Stream.of(valueList).map(rv -> bigDecimalValue(rv, sheetName, row, col, options)).collect(toList());
        } else if (fieldType == Long.class) {
            return Stream.of(valueList).map(rv -> longValue(rv, sheetName, row, col, options)).collect(toList());
        } else if (fieldType == Double.class) {
            return Stream.of(valueList).map(rv -> doubleValue(rv, sheetName, row, col, options)).collect(toList());
        } else if (fieldType == Boolean.class) {
            return Stream.of(valueList).map(rv -> booleanValue(rv, sheetName, row, col, options)).collect(toList());
        } else if (fieldType == Float.class) {
            return Stream.of(valueList).map(rv -> floatValue(rv, sheetName, row, col, options)).collect(toList());
        } else {
            return Arrays.asList(valueList);
        }
    }

    private Object castSetValue(String value, String sheetName, int row, int col, Field field, PoijiOptions options) {
        if (value.isEmpty()) {
            return options.preferNullOverDefault() ? null : emptySet();
        }
        final ParameterizedType genericType = (ParameterizedType) field.getGenericType();
        final Type fieldType = genericType.getActualTypeArguments()[0];
        final String[] valueList = value.split(options.getListDelimiter());

        if (fieldType == Integer.class) {
            return Stream.of(valueList).map(rv -> primitiveIntegerValue(rv, sheetName, row, col)).collect(toSet());
        } else if (fieldType == BigDecimal.class) {
            return Stream.of(valueList).map(rv -> bigDecimalValue(rv, sheetName, row, col, options)).collect(toSet());
        } else if (fieldType == Long.class) {
            return Stream.of(valueList).map(rv -> longValue(rv, sheetName, row, col, options)).collect(toSet());
        } else if (fieldType == Double.class) {
            return Stream.of(valueList).map(rv -> doubleValue(rv, sheetName, row, col, options)).collect(toSet());
        } else if (fieldType == Boolean.class) {
            return Stream.of(valueList).map(rv -> booleanValue(rv, sheetName, row, col, options)).collect(toSet());
        } else if (fieldType == Float.class) {
            return Stream.of(valueList).map(rv -> floatValue(rv, sheetName, row, col, options)).collect(toSet());
        } else {
            return new HashSet<>(Arrays.asList(valueList));
        }
    }

}
