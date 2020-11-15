package com.poiji.parser;

import java.math.BigDecimal;
import java.text.NumberFormat;

/**
 * @see <a href="https://www.ibm.com/developerworks/library/j-numberformat/index.html">Resolving NumberFormat's parsing issues</a>
 */
public class Parsers {

    private static final Parser<Boolean> BOOLEAN_PARSER = new BooleanParser();

    private Parsers() {
        // static factory
    }

    public static Parser<Number> longs() {
        return integers();
    }

    public static Parser<Number> integers() {
        final NumberFormat format = NumberFormat.getInstance();
        format.setParseIntegerOnly(true);
        return new NumberParser(format);
    }

    public static Parser<BigDecimal> bigDecimals() {
        return new BigDecimalParser();
    }

    public static Parser<Number> numbers() {
        return new NumberParser(NumberFormat.getInstance());
    }

    public static Parser<Boolean> booleans() {
        return BOOLEAN_PARSER;
    }

}
