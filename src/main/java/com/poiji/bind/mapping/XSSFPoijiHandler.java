package com.poiji.bind.mapping;

import com.poiji.option.PoijiOptions;

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

    private Data data;
    protected Consumer<? super T> consumer;
    private int internalRow;
    private int internalCount;

    private final PoijiOptions options;

    private final ReadMappedFields mappedFields;

    XSSFPoijiHandler(
        PoijiOptions options, Consumer<? super T> consumer, final ReadMappedFields mappedFields
    ) {
        this.options = options;
        this.consumer = consumer;
        this.mappedFields = mappedFields;
    }

    XSSFPoijiHandler(
        PoijiOptions options,  final ReadMappedFields mappedFields
    ) {
        this.options = options;
        this.mappedFields = mappedFields;
    }

    @Override
    public void startRow(int rowNum) {
        if (isContentRow(rowNum)) {
            internalCount += 1;
            data = mappedFields.createInstanceData();
        }
    }

    @Override
    public void endRow(int rowNum) {

        if (internalRow != rowNum || isHeaderRow(rowNum))
			return;

        if (isContentRow(rowNum)) {
            consumer.accept((T) mappedFields.createNewInstance(data));
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
            mappedFields.setCellInData(internalRow, column, formattedValue, data);
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
