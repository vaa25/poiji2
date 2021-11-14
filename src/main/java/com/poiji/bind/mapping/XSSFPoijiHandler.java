package com.poiji.bind.mapping;

import com.poiji.option.PoijiOptions;
import com.poiji.util.ReflectUtil;
import java.util.function.Consumer;
import org.apache.poi.ss.util.CellAddress;
import org.apache.poi.xssf.eventusermodel.XSSFSheetXMLHandler.SheetContentsHandler;
import org.apache.poi.xssf.usermodel.XSSFComment;

/**
 * This class handles the processing of a .xlsx file,
 * and generates a list of instances of a given type
 * <p>
 * Created by hakan on 22/10/2017
 */
class XSSFPoijiHandler<T> implements SheetContentsHandler {

    private T instance;
    protected Consumer<? super T> consumer;
    private int internalRow;
    private int internalCount;

    private final Class<T> type;
    private final PoijiOptions options;

    private final ReadMappedFields mappedFields;

    XSSFPoijiHandler(
        Class<T> type, PoijiOptions options, Consumer<? super T> consumer, final ReadMappedFields mappedFields
    ) {
        this.type = type;
        this.options = options;
        this.consumer = consumer;

        this.mappedFields = mappedFields;
    }

    XSSFPoijiHandler(
        Class<T> type, PoijiOptions options,  final ReadMappedFields mappedFields
    ) {
        this.type = type;
        this.options = options;
        this.mappedFields = mappedFields;
    }

    @Override
    public void startRow(int rowNum) {
        if (isContentRow(rowNum)) {
            internalCount += 1;
            instance = ReflectUtil.newInstanceOf(type);
        }
    }

    @Override
    public void endRow(int rowNum) {

        if (internalRow != rowNum || isHeaderRow(rowNum))
			return;

        if (isContentRow(rowNum)) {
            consumer.accept(instance);
        }

        if (rowNum <= options.getHeaderStart() + options.getHeaderCount()) {
            mappedFields.validateMandatoryNameColumns();
        }
    }

    @Override
    public void cell(String cellReference, String formattedValue, XSSFComment comment) {
        CellAddress cellAddress = new CellAddress(cellReference);
        int row = cellAddress.getRow();

        int column = cellAddress.getColumn();
        if (isHeaderRow(row)) {
            mappedFields.parseColumnName(column, formattedValue);
            return;
        }

        if (isContentRow(row)){
            internalRow = row;
            mappedFields.setCellInInstance(internalRow, column, formattedValue, instance);
        }

    }

    private boolean isContentRow(final int rowNum) {
        return rowNum > options.skip() + options.getHeaderStart() + options.getHeaderCount() - 1 && (options.getLimit() == 0 || internalCount <= options.getLimit());
    }

    private boolean isHeaderRow(final int row) {
        int headerStart = options.getHeaderStart();
        int headerCount = options.getHeaderCount();
        return row >= headerStart && row < headerStart + headerCount;
    }

    @Override
    public void headerFooter(String text, boolean isHeader, String tagName) {
        //no-op
    }
}
