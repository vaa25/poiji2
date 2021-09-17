package com.poiji.exception;

/**
 * Created by hakan on 17/01/2017.
 */
@SuppressWarnings("serial")
public final class IllegalCastException extends PoijiException {

    private String cellValue;

    public IllegalCastException(String message) {
        super(message);
    }

    public IllegalCastException(final Throwable cause, final String cellValue) {
        super(cause.getMessage(), cause);
        this.cellValue = cellValue;
    }

    public String getCellValue() {
        return cellValue;
    }
}
