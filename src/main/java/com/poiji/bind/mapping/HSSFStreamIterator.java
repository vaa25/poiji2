package com.poiji.bind.mapping;

import com.poiji.option.PoijiOptions;
import java.util.Iterator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;

public final class HSSFStreamIterator<T> implements Iterator<T> {

    private final int limit;
    private int internalCount;
    private final Iterator<Row> iter;
    private final HSSFReadMappedFields readMappedFields;
    private final PoijiOptions options;
    private T next;

    public HSSFStreamIterator(
        final Iterator<Row> rowIterator, final HSSFReadMappedFields readMappedFields, final PoijiOptions options
    ) {
        this.readMappedFields = readMappedFields;
        this.iter = rowIterator;
        this.options = options;
        this.limit = options.getLimit();
    }

    @Override
    public boolean hasNext() {
        while (iter.hasNext()){
            final Row currentRow = iter.next();
            if (!skip(currentRow) && !isRowEmpty(currentRow)) {
                internalCount += 1;

                if (limit != 0 && internalCount > limit) {
                    continue;
                }
                this.next = readMappedFields.parseRow(currentRow);
                return true;
            }
        }
        return false;
    }

    private boolean skip(final Row currentRow) {
        return currentRow.getRowNum() + 1 <= options.skip();
    }

    private boolean isRowEmpty(Row row) {
        for (int c = row.getFirstCellNum(); c < row.getLastCellNum(); c++) {
            Cell cell = row.getCell(c, Row.MissingCellPolicy.CREATE_NULL_AS_BLANK);
            if (cell != null && cell.getCellType() != CellType.BLANK) {
                return false;
            }
        }
        return true;
    }

    @Override
    public T next() {
        return next;
    }
}
