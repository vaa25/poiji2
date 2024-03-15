package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCellName;

import java.util.Objects;

public final class CsvRulesImmutable {

    @ExcelCellName("quoted")
    private final String quoted;
    @ExcelCellName("number")
    private final String number;
    @ExcelCellName("with delimiter")
    private final String withDelimiter;
    @ExcelCellName("with quote")
    private final String withQuote;
    @ExcelCellName("with quote in quoted")
    private final String withQuoteInQuoted;

    public CsvRulesImmutable(String quoted, String number, String withDelimiter, String withQuote, String withQuoteInQuoted) {
        this.quoted = quoted;
        this.number = number;
        this.withDelimiter = withDelimiter;
        this.withQuote = withQuote;
        this.withQuoteInQuoted = withQuoteInQuoted;
    }

    public String getQuoted() {
        return quoted;
    }

    public String getNumber() {
        return number;
    }

    public String getWithDelimiter() {
        return withDelimiter;
    }

    public String getWithQuote() {
        return withQuote;
    }

    public String getWithQuoteInQuoted() {
        return withQuoteInQuoted;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CsvRulesImmutable csvRules = (CsvRulesImmutable) o;
        return Objects.equals(quoted, csvRules.quoted) && Objects.equals(number, csvRules.number) &&
            Objects.equals(withDelimiter, csvRules.withDelimiter) && Objects.equals(withQuote, csvRules.withQuote) &&
            Objects.equals(withQuoteInQuoted, csvRules.withQuoteInQuoted);
    }

    @Override
    public int hashCode() {
        return Objects.hash(quoted, number, withDelimiter, withQuote, withQuoteInQuoted);
    }

    @Override
    public String toString() {
        return "CsvRules{" + "quoted='" + quoted + '\'' + ", number='" + number + '\'' + ", withDelimiter='" +
            withDelimiter + '\'' + ", withQuote='" + withQuote + '\'' + ", withQuoteInQuoted='" + withQuoteInQuoted +
            '\'' + '}';
    }
}
