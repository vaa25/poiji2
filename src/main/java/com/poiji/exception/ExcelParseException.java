package com.poiji.exception;

public final class ExcelParseException extends PoijiException {

    private final String cellValue;
    private final String fieldName;

    public ExcelParseException(final Throwable cause, final String cellValue, final String fieldName) {
        super(cause.getMessage(), cause);
        this.cellValue = cellValue;
        this.fieldName = fieldName;
    }

    public String getCellValue() {
        return cellValue;
    }

    public String getFieldName() {
        return fieldName;
    }
}
