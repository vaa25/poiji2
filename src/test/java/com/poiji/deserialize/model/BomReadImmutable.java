package com.poiji.deserialize.model;

import com.poiji.annotation.*;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.Map;
import java.util.Objects;
import java.util.StringJoiner;
import java.util.concurrent.ConcurrentHashMap;

@ExcelSheet("test")
public final class BomReadImmutable {

    @ExcelCellName(value = "primitiveLong")
    private final long primitiveLong;
    @ExcelCellName(value = "TexT", order = 5)
    private final String text;
    @ExcelCell(4)
    private final Float wrappedFloat;
    @ExcelCellName("float")
    private final float primitiveFloat;
    @ExcelUnknownCells
    private final Map<String, String> unknown = new ConcurrentHashMap<>();
    @ExcelUnknownCells
    private final Map<String, String> anotherUnknown = new ConcurrentHashMap<>();
    @ExcelCellName("double")
    private final double primitiveDouble;
    @ExcelCellName(value = "Double", order = 10)
    private final Double wrappedDouble;
    @ExcelCellName("boolean")
    private final boolean primitiveBoolean;
    @ExcelCellName("Boolean")
    private final Boolean wrappedBoolean;
    @ExcelCellName("Date")
    private final Date date;
    @ExcelCellName("LocalDate")
    private final LocalDate localDate;
    @ExcelCellName("LocalDateTime")
    private final LocalDateTime localDateTime;
    @ExcelCellName("BigDecimal")
    private final BigDecimal bigDecimal;
    @ExcelCellName("byte")
    private final byte primitiveByte;
    @ExcelCellName("Byte")
    private final Byte wrappedByte;
    @ExcelCellName("short")
    private final short primitiveShort;
    @ExcelCellName("Short")
    private final Short wrappedShort;
    @ExcelParseExceptions
    private final Map<String, Exception> exceptions;

    public BomReadImmutable(long primitiveLong, String text, Float wrappedFloat, float primitiveFloat, double primitiveDouble, Double wrappedDouble, boolean primitiveBoolean, Boolean wrappedBoolean, Date date, LocalDate localDate, LocalDateTime localDateTime, BigDecimal bigDecimal, byte primitiveByte, Byte wrappedByte, short primitiveShort, Short wrappedShort, Map<String, Exception> exceptions) {
        this.primitiveLong = primitiveLong;
        this.text = text;
        this.wrappedFloat = wrappedFloat;
        this.primitiveFloat = primitiveFloat;
        this.primitiveDouble = primitiveDouble;
        this.wrappedDouble = wrappedDouble;
        this.primitiveBoolean = primitiveBoolean;
        this.wrappedBoolean = wrappedBoolean;
        this.date = date;
        this.localDate = localDate;
        this.localDateTime = localDateTime;
        this.bigDecimal = bigDecimal;
        this.primitiveByte = primitiveByte;
        this.wrappedByte = wrappedByte;
        this.primitiveShort = primitiveShort;
        this.wrappedShort = wrappedShort;
        this.exceptions = exceptions;
    }

    public Map<String, String> getUnknown() {
        return unknown;
    }

    public Map<String, String> getAnotherUnknown() {
        return anotherUnknown;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final BomReadImmutable that = (BomReadImmutable) o;
        return primitiveLong == that.primitiveLong && Float.compare(that.primitiveFloat,
            primitiveFloat
        ) == 0 && Double.compare(that.primitiveDouble,
            primitiveDouble
        ) == 0 && primitiveBoolean == that.primitiveBoolean && primitiveByte == that.primitiveByte && primitiveShort == that.primitiveShort && Objects
            .equals(text, that.text) && Objects.equals(wrappedFloat, that.wrappedFloat) && Objects.equals(unknown,
            that.unknown
        ) && Objects.equals(anotherUnknown, that.anotherUnknown) && Objects.equals(wrappedDouble,
            that.wrappedDouble
        ) && Objects.equals(wrappedBoolean, that.wrappedBoolean) && Objects.equals(
            date,
            that.date
        ) && Objects.equals(localDate, that.localDate) && Objects.equals(
            localDateTime,
            that.localDateTime
        ) && Objects.equals(bigDecimal, that.bigDecimal) && Objects.equals(
            wrappedByte,
            that.wrappedByte
        ) && Objects.equals(wrappedShort, that.wrappedShort);
    }

    @Override
    public int hashCode() {
        return Objects.hash(primitiveLong,
            text,
            wrappedFloat,
            primitiveFloat,
            unknown,
            anotherUnknown,
            primitiveDouble,
            wrappedDouble,
            primitiveBoolean,
            wrappedBoolean,
            date,
            localDate,
            localDateTime,
            bigDecimal,
            primitiveByte,
            wrappedByte,
            primitiveShort,
            wrappedShort
        );
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", BomReadImmutable.class.getSimpleName() + "[", "]")
            .add("primitiveLong=" + primitiveLong)
            .add("text='" + text + "'")
            .add("wrappedFloat=" + wrappedFloat)
            .add("primitiveFloat=" + primitiveFloat)
            .add("unknown=" + unknown)
            .add("anotherUnknown=" + anotherUnknown)
            .add("primitiveDouble=" + primitiveDouble)
            .add("wrappedDouble=" + wrappedDouble)
            .add("primitiveBoolean=" + primitiveBoolean)
            .add("wrappedBoolean=" + wrappedBoolean)
            .add("date=" + date)
            .add("localDate=" + localDate)
            .add("localDateTime=" + localDateTime)
            .add("bigDecimal=" + bigDecimal)
            .add("primitiveByte=" + primitiveByte)
            .add("wrappedByte=" + wrappedByte)
            .add("primitiveShort=" + primitiveShort)
            .add("wrappedShort=" + wrappedShort)
            .toString();
    }
}
