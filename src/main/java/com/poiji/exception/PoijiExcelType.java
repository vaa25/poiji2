package com.poiji.exception;

import static com.poiji.util.PoijiConstants.CSV_EXTENSION;
import static com.poiji.util.PoijiConstants.XLSX_EXTENSION;
import static com.poiji.util.PoijiConstants.XLS_EXTENSION;

/**
 * Created by hakan on 08/03/2018
 */
public enum PoijiExcelType {
    XLS, XLSX, CSV;

    public static PoijiExcelType fromFileName(final String fileName){
        if (fileName.endsWith(XLSX_EXTENSION)){
            return XLSX;
        } else if (fileName.endsWith(XLS_EXTENSION)) {
            return XLS;
        } else if (fileName.endsWith(CSV_EXTENSION)) {
            return CSV;
        } else {
            throw new InvalidExcelFileExtension("Unsupported file extension. Supported are: xlsx, xls, csv");
        }
    }
}
