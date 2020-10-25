package com.poiji.util;

import com.poiji.config.DefaultCasting;
import com.poiji.config.DefaultCastingError;
import com.poiji.option.PoijiOptions;
import com.poiji.option.PoijiOptions.PoijiOptionsBuilder;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.math.BigDecimal;
import java.text.ParseException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.Arrays;
import java.util.Collection;
import java.util.Date;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;

@RunWith(Parameterized.class)
public class DefaultCastingWithErrorLoggingTest {
    private static final String EMPTY_SHEET_NAME = null;

    private static final int EMPTY_ROW = -1;

    private static final int EMPTY_COL = -1;

    @Parameterized.Parameter
    public String sheetName;

    @Parameterized.Parameter(1)
    public int row;

    @Parameterized.Parameter(2)
    public int col;

    private DefaultCasting casting;

    @Parameterized.Parameters
    public static Collection<Object[]> data() {

        return Arrays.asList(new Object[][]{
                {EMPTY_SHEET_NAME, EMPTY_ROW, EMPTY_COL},
                {"Sheet 1", -1, -1},
                {"Sheet 2", 2, 5}
        });
    }

    @Before
    public void setUp() {

        casting = new DefaultCasting(true);
    }

    @Test
    public void getInstance() {

        assertNotNull(casting);
        assertTrue(casting.isErrorLoggingEnabled());
    }

    // Integer
    @Test
    public void castPrimitiveIntegerExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an int";

        Integer expectedDefault = 0;

        Integer testVal = (Integer) casting.castValue(int.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castIntegerDefaultExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Integer";

        Integer expectedDefault = 0;

        Integer testVal = (Integer) casting.castValue(Integer.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castIntegerNullExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Integer";

        Integer testVal = (Integer) casting.castValue(Integer.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, NumberFormatException.class);
    }

    @Test
    public void castPrimitiveIntegerExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an int";

        Integer expectedDefault = 0;

        Integer testVal = (Integer) casting.castValue(int.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castIntegerDefaultExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Integer";

        Integer expectedDefault = 0;

        Integer testVal = (Integer) casting.castValue(Integer.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castIntegerNullExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not an Integer";

        Integer testVal = (Integer) casting.castValue(Integer.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, NumberFormatException.class);
    }

    // Long
    @Test
    public void castPrimitiveLongExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an int";

        Long expectedDefault = 0L;

        Long testVal = (Long) casting.castValue(long.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castLongDefaultExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Long";

        Long expectedDefault = 0L;

        Long testVal = (Long) casting.castValue(Long.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castLongNullExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Long";

        Long testVal = (Long) casting.castValue(Long.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, NumberFormatException.class);
    }

    @Test
    public void castPrimitiveLongExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an int";

        Long expectedDefault = 0L;

        Long testVal = (Long) casting.castValue(long.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castLongDefaultExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not a Long";

        Long expectedDefault = 0L;

        Long testVal = (Long) casting.castValue(Long.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castLongNullExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Long";

        Long testVal = (Long) casting.castValue(Long.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, NumberFormatException.class);
    }

    // Double
    @Test
    public void castPrimitiveDoubleExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not a double";

        Double expectedDefault = 0d;

        Double testVal = (Double) casting.castValue(double.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castDoubleDefaultExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Double";

        Double expectedDefault = 0d;

        Double testVal = (Double) casting.castValue(Double.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castDoubleNullExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Double";

        Double testVal = (Double) casting.castValue(Double.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, NumberFormatException.class);
    }

    @Test
    public void castPrimitiveDoubleExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an double";

        Double expectedDefault = 0d;

        Double testVal = (Double) casting.castValue(double.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castDoubleDefaultExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Double";

        Double expectedDefault = 0d;

        Double testVal = (Double) casting.castValue(Double.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castDoubleNullExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Double";

        Double testVal = (Double) casting.castValue(Double.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, NumberFormatException.class);
    }

    // Float
    @Test
    public void castPrimitiveFloatExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an float";

        Float expectedDefault = 0f;

        Float testVal = (Float) casting.castValue(float.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castFloatDefaultExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Float";

        Float expectedDefault = 0f;

        Float testVal = (Float) casting.castValue(Float.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castFloatNullExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Float";

        Float testVal = (Float) casting.castValue(Float.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, NumberFormatException.class);
    }

    @Test
    public void castPrimitiveFloatExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an float";

        Float expectedDefault = 0f;

        Float testVal = (Float) casting.castValue(float.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castFloatDefaultExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Float";

        Float expectedDefault = 0f;

        Float testVal = (Float) casting.castValue(Float.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castFloatNullExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Float";

        Float testVal = (Float) casting.castValue(Float.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, NumberFormatException.class);
    }

    // BigDecimal
    @Test
    public void castBigDecimalDefaultExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an BigDecimal";

        BigDecimal expectedDefault = BigDecimal.ZERO;

        BigDecimal testVal = (BigDecimal) casting.castValue(BigDecimal.class, value, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castBigDecimalNullExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a BigDecimal";

        BigDecimal testVal = (BigDecimal) casting.castValue(BigDecimal.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, NumberFormatException.class);
    }

    @Test
    public void castBigDecimalDefaultExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an BigDecimal";

        BigDecimal expectedDefault = BigDecimal.ZERO;

        BigDecimal testVal = (BigDecimal) casting.castValue(BigDecimal.class, value, row, col, options);

        assertEquals(expectedDefault, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedDefault, NumberFormatException.class);
    }

    @Test
    public void castBigDecimalNullExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a BigDecimal";

        BigDecimal testVal = (BigDecimal) casting.castValue(BigDecimal.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, NumberFormatException.class);
    }

    // Date
    // TODO: Impossible to test default casting of date without abstracting Calendar.getInstance()
    //  into something that can be mocked
    @Test
    public void castDateNullExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Date";

        Date testVal = (Date) casting.castValue(Date.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, ParseException.class);
    }

    @Test
    public void castDateNullExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a Date";

        Date testVal = (Date) casting.castValue(Date.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, ParseException.class);
    }

    // LocalDate
    @Test
    public void castLocalDateDefaultExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not a LocalDate";

        LocalDate expectedValue = LocalDate.now();

        LocalDate testVal = (LocalDate) casting.castValue(LocalDate.class, value, options);

        assertEquals(expectedValue, testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, expectedValue, DateTimeParseException.class);
    }

    @Test
    public void castLocalDateNullExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a LocalDate";

        LocalDate testVal = (LocalDate) casting.castValue(LocalDate.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, DateTimeParseException.class);
    }

    @Test
    public void castLocalDateDefaultExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not a LocalDate";

        LocalDate expectedValue = LocalDate.now();

        LocalDate testVal = (LocalDate) casting.castValue(LocalDate.class, value, row, col, options);

        assertEquals(expectedValue, testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, expectedValue, DateTimeParseException.class);
    }

    @Test
    public void castLocalDateNullExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not a LocalDate";

        LocalDate testVal = (LocalDate) casting.castValue(LocalDate.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, DateTimeParseException.class);
    }

    // Enum
    @Test
    public void castEnumExceptionWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .build();

        String value = "not an Enum";

        TestEnum testVal = (TestEnum) casting.castValue(TestEnum.class, value, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, EMPTY_ROW, EMPTY_COL, value, null, IllegalArgumentException.class);
    }

    @Test
    public void castEnumExceptionWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        String value = "not an Enum";

        TestEnum testVal = (TestEnum) casting.castValue(TestEnum.class, value, row, col, options);

        assertNull(testVal);
        assertSingleCastingErrorPresent(sheetName, row, col, value, null, IllegalArgumentException.class);
    }

    // Multiple Casting Errors
    @Test
    public void multipleCastingErrorsOnSingleSheetWithoutLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        // Error One
        String valueOne = "not an Enum One";

        TestEnum testValOne = (TestEnum) casting.castValue(TestEnum.class, valueOne, options);

        assertNull(testValOne);

        // Error Two
        String valueTwo = "not an Enum Two";

        TestEnum testValTwo = (TestEnum) casting.castValue(TestEnum.class, valueTwo, options);

        assertNull(testValTwo);


        assertEquals(casting.getErrors().size(), 2);

        DefaultCastingError errorOne = casting.getErrors().get(0);
        assertCastingErrorEquals(errorOne, sheetName, EMPTY_ROW, EMPTY_COL, valueOne, null, IllegalArgumentException.class);

        DefaultCastingError errorTwo = casting.getErrors().get(1);
        assertCastingErrorEquals(errorTwo, sheetName, EMPTY_ROW, EMPTY_COL, valueTwo, null, IllegalArgumentException.class);
    }

    @Test
    public void multipleCastingErrorsOnSingleSheetWithLogging() {

        PoijiOptions options = PoijiOptionsBuilder.settings()
                .sheetName(sheetName)
                .preferNullOverDefault(true)
                .build();

        // Error One
        String valueOne = "not an Enum One";

        TestEnum testValOne = (TestEnum) casting.castValue(TestEnum.class, valueOne, row, col, options);

        assertNull(testValOne);

        // Error Two
        String valueTwo = "not an Enum Two";

        TestEnum testValTwo = (TestEnum) casting.castValue(TestEnum.class, valueTwo, row, col, options);

        assertNull(testValTwo);


        assertEquals(casting.getErrors().size(), 2);

        DefaultCastingError errorOne = casting.getErrors().get(0);
        assertCastingErrorEquals(errorOne, sheetName, row, col, valueOne, null, IllegalArgumentException.class);

        DefaultCastingError errorTwo = casting.getErrors().get(1);
        assertCastingErrorEquals(errorTwo, sheetName, row, col, valueTwo, null, IllegalArgumentException.class);
    }

    @Test
    public void multipleCastingErrorsOnSeparateSheetsWithoutLogging() {
        // Error One
        String sheetNameOne = "Sheet One";

        PoijiOptions optionsOne = PoijiOptionsBuilder.settings()
                .sheetName(sheetNameOne)
                .preferNullOverDefault(true)
                .build();


        String valueOne = "not an Enum One";

        TestEnum testValOne = (TestEnum) casting.castValue(TestEnum.class, valueOne, optionsOne);

        assertNull(testValOne);

        // Error Two
        String sheetNameTwo = "Sheet Two";

        PoijiOptions optionsTwo = PoijiOptionsBuilder.settings()
                .sheetName(sheetNameTwo)
                .preferNullOverDefault(true)
                .build();


        String valueTwo = "not an Enum Two";

        TestEnum testValTwo = (TestEnum) casting.castValue(TestEnum.class, valueTwo, optionsTwo);

        assertNull(testValTwo);


        assertEquals(casting.getErrors().size(), 2);

        DefaultCastingError errorOne = casting.getErrors().get(0);
        assertCastingErrorEquals(errorOne, sheetNameOne, EMPTY_ROW, EMPTY_COL, valueOne, null, IllegalArgumentException.class);

        DefaultCastingError errorTwo = casting.getErrors().get(1);
        assertCastingErrorEquals(errorTwo, sheetNameTwo, EMPTY_ROW, EMPTY_COL, valueTwo, null, IllegalArgumentException.class);
    }

    @Test
    public void multipleCastingErrorsOnSeparateSheetsWithLogging() {
        // Error One
        String sheetNameOne = "Sheet One";

        PoijiOptions optionsOne = PoijiOptionsBuilder.settings()
                .sheetName(sheetNameOne)
                .preferNullOverDefault(true)
                .build();


        String valueOne = "not an Enum One";

        TestEnum testValOne = (TestEnum) casting.castValue(TestEnum.class, valueOne, row, col, optionsOne);

        assertNull(testValOne);

        // Error Two
        String sheetNameTwo = "Sheet Two";

        PoijiOptions optionsTwo = PoijiOptionsBuilder.settings()
                .sheetName(sheetNameTwo)
                .preferNullOverDefault(true)
                .build();


        String valueTwo = "not an Enum Two";

        TestEnum testValTwo = (TestEnum) casting.castValue(TestEnum.class, valueTwo, row, col, optionsTwo);

        assertNull(testValTwo);


        assertEquals(casting.getErrors().size(), 2);

        DefaultCastingError errorOne = casting.getErrors().get(0);
        assertCastingErrorEquals(errorOne, sheetNameOne, row, col, valueOne, null, IllegalArgumentException.class);

        DefaultCastingError errorTwo = casting.getErrors().get(1);
        assertCastingErrorEquals(errorTwo, sheetNameTwo, row, col, valueTwo, null, IllegalArgumentException.class);
    }

    // Convenience Methods
    private void assertSingleCastingErrorPresent(String sheetName, int row, int col, String value, Object defaultValue, Class<?> exClass) {
        assertEquals(casting.getErrors().size(), 1);

        assertCastingErrorEquals(casting.getErrors().get(0), sheetName, row, col, value, defaultValue, exClass);
    }

    private void assertCastingErrorEquals(DefaultCastingError castingError,
                                          String sheetName,
                                          int row,
                                          int col,
                                          String value,
                                          Object defaultValue,
                                          Class<?> exClass) {
        assertEquals(sheetName, castingError.getSheetName());
        assertEquals(row, castingError.getRow());
        assertEquals(col, castingError.getColumn());
        assertEquals(value, castingError.getValue());
        assertEquals(defaultValue, castingError.getDefaultValue());
        assertEquals(exClass, castingError.getException().getClass());
    }

    private enum TestEnum {
        ITEM1, ITEM2
    }
}
