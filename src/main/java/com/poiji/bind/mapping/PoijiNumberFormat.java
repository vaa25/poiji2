package com.poiji.bind.mapping;

import java.util.SortedMap;
import java.util.TreeMap;
import org.apache.poi.xssf.model.StylesTable;

/**
 * Created by hakan on 26.04.2020
 */
public final class PoijiNumberFormat {

    private final SortedMap<Short, String> numberFormats = new TreeMap<>();

    public PoijiNumberFormat() {
    }

    public void putNumberFormat(short index, String fmt) {
        numberFormats.put(index, fmt);
    }

    public String getNumberFormatAt(short fmtId) {
        return numberFormats.get(fmtId);
    }

    void overrideExcelNumberFormats(final StylesTable styles) {
        for (Short fmtId : numberFormats.keySet()) {
            String format = numberFormats.get(fmtId);
            styles.putNumberFormat(fmtId, format);
        }
    }
}
