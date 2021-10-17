package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCellName;
import java.util.Objects;

public final class CsvRules {

    @ExcelCellName("quoted")
    private String quoted;
    @ExcelCellName("number")
    private String number;
    @ExcelCellName("with delimiter")
    private String withDelimiter;
    @ExcelCellName("with quote")
    private String withQuote;
    @ExcelCellName("with quote in quoted")
    private String withQuoteInQuoted;

    public String getQuoted() {
        return quoted;
    }

    public CsvRules setQuoted(final String quoted) {
        this.quoted = quoted;
        return this;
    }

    public String getNumber() {
        return number;
    }

    public CsvRules setNumber(final String number) {
        this.number = number;
        return this;
    }

    public String getWithDelimiter() {
        return withDelimiter;
    }

    public CsvRules setWithDelimiter(final String withDelimiter) {
        this.withDelimiter = withDelimiter;
        return this;
    }

    public String getWithQuote() {
        return withQuote;
    }

    public CsvRules setWithQuote(final String withQuote) {
        this.withQuote = withQuote;
        return this;
    }

    public String getWithQuoteInQuoted() {
        return withQuoteInQuoted;
    }

    public CsvRules setWithQuoteInQuoted(final String withQuoteInQuoted) {
        this.withQuoteInQuoted = withQuoteInQuoted;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final CsvRules csvRules = (CsvRules) o;
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
