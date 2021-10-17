package com.poiji.option;

import com.poiji.annotation.ExcelCellName;
import com.poiji.bind.mapping.PoijiLogCellFormat;
import com.poiji.bind.mapping.PoijiNumberFormat;
import com.poiji.config.Casting;
import com.poiji.config.DefaultCasting;
import com.poiji.config.DefaultFormatting;
import com.poiji.config.Formatting;
import com.poiji.exception.PoijiException;
import com.poiji.save.ToCellCasting;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

import static com.poiji.util.PoijiConstants.DEFAULT_DATE_FORMATTER;
import static com.poiji.util.PoijiConstants.DEFAULT_DATE_PATTERN;
import static com.poiji.util.PoijiConstants.DEFAULT_DATE_TIME_FORMATTER;
import static com.poiji.util.PoijiConstants.DEFAULT_DATE_TIME_PATTERN;

/**
 * Created by hakan on 17/01/2017.
 */
public final class PoijiOptions {

    private int skip;
    private int limit;
    private int sheetIndex;
    private String password;
    private String dateRegex;
    private String dateTimeRegex;
    private String datePattern;
    private String localDatePattern;
    private String localDateTimePattern;
    private boolean dateLenient;
    private boolean trimCellValue;
    private boolean ignoreHiddenSheets;
    private boolean preferNullOverDefault;
    private DateTimeFormatter dateFormatter;
    private DateTimeFormatter dateTimeFormatter;
    private Casting casting;
    private ToCellCasting toCellCasting;
    private int headerStart;
    private String sheetName;
    private boolean caseInsensitive;
    private boolean ignoreWhitespaces;
    private boolean namedHeaderMandatory;
    private PoijiLogCellFormat poijiLogCellFormat;
    private PoijiNumberFormat numberFormat;
    private boolean disableXLSXNumberCellFormat;
    private char csvDelimiter;
    private boolean transposed;
    private String charset;
    private String listDelimiter;
    private Formatting formatting;

    public boolean getIgnoreWhitespaces() {
        return ignoreWhitespaces;
    }

    private PoijiOptions setIgnoreWhitespaces(final boolean ignoreWhitespaces) {
        this.ignoreWhitespaces = ignoreWhitespaces;
        return this;
    }

    public Formatting getFormatting() {
        return formatting;
    }

    private PoijiOptions setFormatting(final Formatting formatting) {
        this.formatting = formatting;
        return this;
    }

    public String getListDelimiter() {
        return listDelimiter;
    }

    private PoijiOptions setListDelimiter(final String listDelimiter) {
        this.listDelimiter = listDelimiter;
        return this;
    }

    public String getCharset() {
        return charset;
    }

    public PoijiOptions setCharset(final String charset) {
        this.charset = charset;
        return this;
    }

    public boolean getTransposed() {
        return transposed;
    }

    private PoijiOptions setTransposed(final boolean transposed) {
        this.transposed = transposed;
        return this;
    }

    public char getCsvDelimiter() {
        return csvDelimiter;
    }

    private PoijiOptions setCsvDelimiter(final char csvDelimiter) {
        this.csvDelimiter = csvDelimiter;
        return this;
    }

    private PoijiOptions disableXLSXNumberCellFormat(boolean disableXLSXNumberCellFormat) {
        this.disableXLSXNumberCellFormat = disableXLSXNumberCellFormat;
        return this;
    }

    public boolean isDisableXLSXNumberCellFormat() {
        return disableXLSXNumberCellFormat;
    }

    public PoijiNumberFormat getPoijiNumberFormat() {
        return numberFormat;
    }

    private PoijiOptions setPoijiNumberFormat(PoijiNumberFormat numberFormat) {
        this.numberFormat = numberFormat;
        return this;
    }

    public PoijiLogCellFormat getPoijiCellFormat() {
        return poijiLogCellFormat;
    }

    private PoijiOptions setPoijiLogCellFormat(PoijiLogCellFormat poijiLogCellFormat) {
        this.poijiLogCellFormat = poijiLogCellFormat;
        return this;
    }

    private PoijiOptions() {
        super();
    }

    private PoijiOptions setSkip(int skip) {
        this.skip = skip;
        return this;
    }

    public int getLimit() {
        return limit;
    }

    private PoijiOptions setLimit(int limit) {
        this.limit = limit;
        return this;
    }

    private PoijiOptions setDatePattern(String datePattern) {
        this.datePattern = datePattern;
        return this;
    }

    public String getLocalDatePattern() {
        return localDatePattern;
    }

    private PoijiOptions setLocalDatePattern(final String localDatePattern) {
        this.localDatePattern = localDatePattern;
        return this;
    }

    private PoijiOptions setDateFormatter(DateTimeFormatter dateFormatter) {
        this.dateFormatter = dateFormatter;
        return this;
    }

    private PoijiOptions setDateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
        this.dateTimeFormatter = dateTimeFormatter;
        return this;
    }

    private PoijiOptions setPreferNullOverDefault(boolean preferNullOverDefault) {
        this.preferNullOverDefault = preferNullOverDefault;
        return this;
    }

    public String getPassword() {
        return password;
    }

    private PoijiOptions setPassword(String password) {
        this.password = password;
        return this;
    }

    public String datePattern() {
        return datePattern;
    }

    public String getLocalDateTimePattern() {
        return localDateTimePattern;
    }

    private PoijiOptions setLocalDateTimePattern(final String localDateTimePattern) {
        this.localDateTimePattern = localDateTimePattern;
        return this;
    }

    public DateTimeFormatter dateFormatter() {
        return dateFormatter;
    }

    public DateTimeFormatter dateTimeFormatter() {
        return dateTimeFormatter;
    }

    public boolean preferNullOverDefault() {
        return preferNullOverDefault;
    }

    /**
     * the number of skipped rows
     *
     * @return n rows skipped
     */
    public int skip() {
        return skip;
    }

    public boolean ignoreHiddenSheets() {
        return ignoreHiddenSheets;
    }

    private PoijiOptions setIgnoreHiddenSheets(boolean ignoreHiddenSheets) {
        this.ignoreHiddenSheets = ignoreHiddenSheets;
        return this;
    }

    public boolean trimCellValue() {
        return trimCellValue;
    }

    private PoijiOptions setTrimCellValue(boolean trimCellValue) {
        this.trimCellValue = trimCellValue;
        return this;
    }

    public Casting getCasting() {
        return casting;
    }

    private PoijiOptions setCasting(Casting casting) {
        this.casting = casting;
        return this;
    }

    public ToCellCasting getToCellCasting() {
        return toCellCasting;
    }

    private PoijiOptions setToCellCasting(final ToCellCasting toCellCasting) {
        this.toCellCasting = toCellCasting;
        return this;
    }

    private PoijiOptions setSheetIndex(int sheetIndex) {
        this.sheetIndex = sheetIndex;
        return this;
    }

    public int sheetIndex() {
        return sheetIndex;
    }

    public String getDateRegex() {
        return dateRegex;
    }

    private PoijiOptions setDateRegex(String dateRegex) {
        this.dateRegex = dateRegex;
        return this;
    }

    public String getDateTimeRegex() {
        return dateTimeRegex;
    }

    private PoijiOptions setDateTimeRegex(String dateTimeRegex) {
        this.dateTimeRegex = dateTimeRegex;
        return this;
    }

    public boolean getDateLenient() {
        return dateLenient;
    }

    private PoijiOptions setDateLenient(boolean dateLenient) {
        this.dateLenient = dateLenient;
        return this;
    }

    public int getHeaderStart() {
        return headerStart;
    }

    private PoijiOptions setHeaderStart(int headerStart) {
        this.headerStart = headerStart;
        return this;
    }

    private PoijiOptions setSheetName(String sheetName) {
        this.sheetName = sheetName;
        return this;
    }

    public String getSheetName() {
        return sheetName;
    }

    public boolean getCaseInsensitive() {
        return caseInsensitive;
    }

    private PoijiOptions setCaseInsensitive(final boolean caseInsensitive) {
        this.caseInsensitive = caseInsensitive;
        return this;
    }

    public boolean getNamedHeaderMandatory() {
        return namedHeaderMandatory;
    }

    private PoijiOptions setNamedHeaderMandatory(boolean namedHeaderMandatory) {
        this.namedHeaderMandatory = namedHeaderMandatory;
        return this;
    }

    public static class PoijiOptionsBuilder {

        private int sheetIndex;
        private String password;
        private String dateRegex;
        private String dateTimeRegex;
        private boolean dateLenient;
        private boolean trimCellValue;
        private boolean ignoreHiddenSheets;
        private boolean preferNullOverDefault;
        private String datePattern = DEFAULT_DATE_PATTERN;
        private DateTimeFormatter dateFormatter = DEFAULT_DATE_FORMATTER;
        private String localDatePattern = DEFAULT_DATE_PATTERN;
        private String localDateTimePattern = DEFAULT_DATE_TIME_PATTERN;
        private DateTimeFormatter dateTimeFormatter = DEFAULT_DATE_TIME_FORMATTER;
        private Casting casting = new DefaultCasting();
        private ToCellCasting toCellCasting = new ToCellCasting();
        private PoijiLogCellFormat cellFormat;
        private PoijiNumberFormat numberFormat;
        private boolean disabledXLSXNumberCellFormat;
        private int headerStart = 0;
        private int skip = 0;
        private int limit = 0;
        private String sheetName;
        private boolean caseInsensitive;
        private boolean ignoreWhitespaces;
        private boolean namedHeaderMandatory;
        private boolean transposed;
        private String charset = "UTF-8";
        private char csvDelimiter = ',';
        private String listDelimiter = ",";
        private Formatting formatting = new DefaultFormatting();

        private PoijiOptionsBuilder() {
        }

        private PoijiOptionsBuilder(int skip) {
            this.skip = skip;
        }

        /**
         * Ignore white space before and after column names for annotation {@link ExcelCellName}.
         * Default - false.
         *
         * @param ignoreWhitespaces true or false
         */
        public PoijiOptionsBuilder ignoreWhitespaces(final boolean ignoreWhitespaces) {
            this.ignoreWhitespaces = ignoreWhitespaces;
            return this;
        }

        /**
         * Use a custom excel header format implementation
         *
         * @param formatting custom header format implementation
         * @return this
         */
        public PoijiOptionsBuilder withFormatting(Formatting formatting) {
            Objects.requireNonNull(formatting);

            this.formatting = formatting;
            return this;
        }

        public PoijiOptionsBuilder setCharset(final String charset) {
            this.charset = charset;
            return this;
        }

        /**
         * Swaps rows and columns
         */
        public PoijiOptionsBuilder setTransposed(final boolean transposed) {
            this.transposed = transposed;
            return this;
        }

        /**
         * Disable the cell format of all the number cells of an excel file ending with xlsx
         *
         * @return this
         */
        public PoijiOptionsBuilder disableXLSXNumberCellFormat() {
            this.disabledXLSXNumberCellFormat = true;
            return this;
        }

        /**
         * Skip a number of rows after the header row. The header row is not counted.
         *
         * @param skip ignored row number after the header row
         * @return builder itself
         */
        public static PoijiOptionsBuilder settings(int skip) {
            if (skip < 0) {
                throw new PoijiException("Skip index must be greater than or equal to 0");
            }
            return new PoijiOptionsBuilder(skip);
        }

        public static PoijiOptionsBuilder settings() {
            return new PoijiOptionsBuilder();
        }

        /**
         * set a date formatter, default date time formatter is "dd/M/yyyy"
         * for java.time.LocalDate
         *
         * @param dateFormatter date time formatter
         * @return this
         */
        public PoijiOptionsBuilder dateFormatter(DateTimeFormatter dateFormatter) {
            this.dateFormatter = dateFormatter;
            return this;
        }

        /**
         * set a date time formatter, default date time formatter is "dd/M/yyyy HH:mm:ss"
         * for java.time.LocalDateTime
         *
         * @param dateTimeFormatter date time formatter
         * @return this
         */
        public PoijiOptionsBuilder dateTimeFormatter(DateTimeFormatter dateTimeFormatter) {
            this.dateTimeFormatter = dateTimeFormatter;
            return this;
        }

        /**
         * set date pattern, default date format is "dd/M/yyyy" for
         * java.util.Date
         *
         * @param datePattern date time formatter
         * @return this
         */
        public PoijiOptionsBuilder datePattern(String datePattern) {
            this.datePattern = datePattern;
            return this;
        }

        /**
         * set date time pattern, default date time format is "dd/M/yyyy HH:mm:ss" for
         * writing java.time.LocalDate into excel
         *
         * @param localDatePattern date time pattern
         * @return this
         */
        public PoijiOptionsBuilder localDatePattern(String localDatePattern) {
            this.localDatePattern = localDatePattern;
            return this;
        }

        /**
         * set date time pattern, default date time format is "dd/M/yyyy HH:mm:ss" for
         * writing java.time.LocalDateTime into excel
         *
         * @param localDateTimePattern date time pattern
         * @return this
         */
        public PoijiOptionsBuilder localDateTimePattern(String localDateTimePattern) {
            this.localDateTimePattern = localDateTimePattern;
            return this;
        }

        /**
         * set whether or not to use null instead of default values for Integer,
         * Double, Float, Long, String and java.util.Date types.
         *
         * @param preferNullOverDefault boolean
         * @return this
         */
        public PoijiOptionsBuilder preferNullOverDefault(boolean preferNullOverDefault) {
            this.preferNullOverDefault = preferNullOverDefault;
            return this;
        }

        public PoijiOptions build() {
            return new PoijiOptions()
                .setSkip(skip + headerStart + 1)
                .setPassword(password)
                .setPreferNullOverDefault(preferNullOverDefault)
                .setDatePattern(datePattern)
                .setLocalDatePattern(localDatePattern)
                .setLocalDateTimePattern(localDateTimePattern)
                .setDateFormatter(dateFormatter)
                .setDateTimeFormatter(dateTimeFormatter)
                .setSheetIndex(sheetIndex)
                .setSheetName(sheetName)
                .setIgnoreHiddenSheets(ignoreHiddenSheets)
                .setTrimCellValue(trimCellValue)
                .setDateRegex(dateRegex)
                .setDateTimeRegex(dateTimeRegex)
                .setDateLenient(dateLenient)
                .setHeaderStart(headerStart)
                .setCasting(casting)
                .setToCellCasting(toCellCasting)
                .setLimit(limit)
                .setPoijiLogCellFormat(cellFormat)
                .setPoijiNumberFormat(numberFormat)
                .setCaseInsensitive(caseInsensitive)
                .setIgnoreWhitespaces(ignoreWhitespaces)
                .setCsvDelimiter(csvDelimiter)
                .setListDelimiter(listDelimiter)
                .disableXLSXNumberCellFormat(disabledXLSXNumberCellFormat)
                .setTransposed(transposed)
                .setCharset(charset)
                .setFormatting(formatting)
                .setNamedHeaderMandatory(namedHeaderMandatory);
        }


        /**
         * set sheet index, default is 0
         *
         * @param sheetIndex number
         * @return this
         */
        public PoijiOptionsBuilder sheetIndex(int sheetIndex) {
            if (sheetIndex < 0) {
                throw new PoijiException("Sheet index must be greater than or equal to 0");
            }
            this.sheetIndex = sheetIndex;
            return this;
        }

        /**
         * Set the sheet Name
         *
         * @param sheetName excel sheet name
         * @return this
         */
        public PoijiOptionsBuilder sheetName(String sheetName) {
            this.sheetName = sheetName;
            return this;
        }

        /**
         * skip a number of rows after the header row. The header row is not counted.
         *
         * @param skip number
         * @return this
         */
        public PoijiOptionsBuilder skip(int skip) {
            if (skip < 0) {
                throw new PoijiException("Skip index must be greater than or equal to 0");
            }
            this.skip = skip;
            return this;
        }

        /**
         * limit a number of rows after the header and skipped rows row. The header and skipped rows are not counted.
         *
         * @param limit number
         * @return this
         */
        public PoijiOptionsBuilder limit(int limit) {
            if (limit < 1) {
                throw new PoijiException("limit must be greater than 0");
            }
            this.limit = limit;
            return this;
        }

        /**
         * set password for encrypted excel file, Default is null
         *
         * @param password
         * @return this
         */
        public PoijiOptionsBuilder password(String password) {
            this.password = password;
            return this;
        }

        /**
         * Ignore hidden sheets
         *
         * @param ignoreHiddenSheets whether or not to ignore any hidden sheets
         *                           in the work book.
         * @return this
         */
        public PoijiOptionsBuilder ignoreHiddenSheets(boolean ignoreHiddenSheets) {
            this.ignoreHiddenSheets = ignoreHiddenSheets;
            return this;
        }

        /**
         * Trim cell value
         *
         * @param trimCellValue trim the cell value before processing work book.
         * @return this
         */
        public PoijiOptionsBuilder trimCellValue(boolean trimCellValue) {
            this.trimCellValue = trimCellValue;
            return this;
        }

        /**
         * Date regex, if would like to specify a regex patter the date must be
         * in, e.g.\\d{2}/\\d{1}/\\d{4}.
         *
         * @param dateRegex date regex pattern
         * @return this
         */
        public PoijiOptionsBuilder dateRegex(String dateRegex) {
            this.dateRegex = dateRegex;
            return this;
        }

        /**
         * DateTime regex, if would like to specify a regex patter the date time must be
         * in, e.g.\\d{2}/\\d{1}/\\d{4} \\d{2}:\\d{2}:\\d{2}.
         *
         * @param dateTimeRegex date regex pattern
         * @return this
         */
        public PoijiOptionsBuilder dateTimeRegex(String dateTimeRegex) {
            this.dateTimeRegex = dateTimeRegex;
            return this;
        }

        /**
         * If the simple date format is lenient, use to
         * set how strict the date formatting must be, defaults to lenient false.
         * It works only for java.util.Date.
         *
         * @param dateLenient true or false
         * @return this
         */
        public PoijiOptionsBuilder dateLenient(boolean dateLenient) {
            this.dateLenient = dateLenient;
            return this;
        }

        /**
         * Use a custom casting implementation
         *
         * @param casting custom casting implementation
         * @return this
         */
        public PoijiOptionsBuilder withCasting(Casting casting) {
            Objects.requireNonNull(casting);

            this.casting = casting;
            return this;
        }

        /**
         * Use a modified cell casting implementation
         *
         * @param toCellCasting modified cell casting implementation
         * @return this
         */
        public PoijiOptionsBuilder withCellCasting(ToCellCasting toCellCasting) {
            Objects.requireNonNull(toCellCasting);

            this.toCellCasting = toCellCasting;
            return this;
        }

        /**
         * This is to set the row which the unmarshall will
         * use to start reading header titles, incase the
         * header is not in row 0.
         *
         * @param headerStart an index number of the excel header to start reading header
         * @return this
         */
        public PoijiOptionsBuilder headerStart(int headerStart) {
            if (headerStart < 0) {
                throw new PoijiException("Header index must be greater than or equal to 0");
            }
            this.headerStart = headerStart;
            return this;
        }

        /**
         * Permits case insensitive column names mapping for annotation {@link ExcelCellName}.
         * Default - false.
         *
         * @param caseInsensitive true or false
         * @return this builder
         */
        public PoijiOptionsBuilder caseInsensitive(final boolean caseInsensitive) {
            this.caseInsensitive = caseInsensitive;
            return this;
        }

        /**
         * Set CSV delimiter. Default is ','
         *
         * @param csvDelimiter fields are mandatory or not
         */
        public PoijiOptionsBuilder csvDelimiter(char csvDelimiter) {
            this.csvDelimiter = csvDelimiter;
            return this;
        }

        /**
         * Set true if all headers named in {@link ExcelCellName} are mandatory, otherwise false
         *
         * @param namedHeaderMandatory fields are mandatory or not
         */
        public PoijiOptionsBuilder namedHeaderMandatory(boolean namedHeaderMandatory) {
            this.namedHeaderMandatory = namedHeaderMandatory;
            return this;
        }

        /**
         * Add cell format option to see each internal cell's excel format for files ending with xlsx format.
         * This option should be enabled for debugging purpose.
         *
         * @param cellFormat poiji cell format instance
         */
        public PoijiOptionsBuilder poijiLogCellFormat(final PoijiLogCellFormat cellFormat) {
            this.cellFormat = cellFormat;
            return this;
        }

        /**
         * Change the default cell formats of an excel file by overriding
         *
         * @param numberFormat poiji number format instance
         */
        public PoijiOptionsBuilder poijiNumberFormat(final PoijiNumberFormat numberFormat) {
            this.numberFormat = numberFormat;
            return this;
        }

        /**
         * Use different delimiter to split the list of items of a cell
         *
         * @param delimiter by default delimiter is ','
         * @return this
         */
        public PoijiOptionsBuilder addListDelimiter(String delimiter) {
            this.listDelimiter = delimiter;
            return this;
        }
    }

}
