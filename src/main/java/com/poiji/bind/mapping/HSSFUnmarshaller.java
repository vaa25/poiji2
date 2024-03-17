package com.poiji.bind.mapping;

import com.poiji.bind.Unmarshaller;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import com.poiji.save.TransposeUtil;
import org.apache.poi.hssf.usermodel.HSSFFormulaEvaluator;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.usermodel.*;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

/**
 * This is the main class that converts the excel sheet fromExcel Java object
 * Created by hakan on 16/01/2017.
 */
abstract class HSSFUnmarshaller implements Unmarshaller {

    protected final PoijiOptions options;
    private final int limit;
    private int internalCount;
    protected BaseFormulaEvaluator baseFormulaEvaluator;

    HSSFUnmarshaller(final PoijiOptions options) {
        this.options = options;
        this.limit = options.getLimit();
    }

    @Override
    public <T> void unmarshal(Class<T> type, Consumer<? super T> consumer) {
        try (final HSSFWorkbook workbook = (HSSFWorkbook) workbook()) {
            final Sheet sheet = getSheet(type, workbook);
            processRowsToObjects(sheet, type, consumer);
        } catch (final IOException e) {
            throw new PoijiException("Problem occurred while closing HSSFWorkbook", e);
        }
    }

    @Override
    public List<String> readSheetNames() {
        try (final HSSFWorkbook workbook = (HSSFWorkbook) workbook()) {
            final List<String> result = new ArrayList<>();
            workbook.forEach(sheet-> result.add(sheet.getSheetName()));
            return result;
        } catch (final IOException e) {
            throw new PoijiException("Problem occurred while closing HSSFWorkbook", e);
        }
    }

    private <T> Sheet getSheet(final Class<T> type, final HSSFWorkbook workbook) {
        if (options.getTransposed()){
            TransposeUtil.transpose(workbook);
        }
        final Optional<String> maybeSheetName = SheetNameExtractor.getSheetName(type, options);
        baseFormulaEvaluator = HSSFFormulaEvaluator.create(workbook, null, null);
        return this.getSheetToProcess(workbook, options, maybeSheetName.orElse(null));
    }

    @Override
    public <T> Stream<T> stream(Class<T> type){
        try (final HSSFWorkbook workbook = (HSSFWorkbook) workbook()) {
            final Sheet sheet = getSheet(type, workbook);
            return processRowsToStream(sheet, type);
        } catch (final IOException e) {
            throw new PoijiException("Problem occurred while closing HSSFWorkbook", e);
        }
    }

    protected  <T> Stream<T> processRowsToStream(final Sheet sheet, final Class<T> type) {
        final int skip = options.skip();
        final int maxPhysicalNumberOfRows = sheet.getPhysicalNumberOfRows() + 1 - skip;
        final HSSFReadMappedFields readMappedFields = loadColumnTitles(sheet, maxPhysicalNumberOfRows, type);
        final Iterator<T> iterator = new HSSFStreamIterator<>(sheet.iterator(), readMappedFields, options);
        final Spliterator<T> spliterator =
            Spliterators.spliteratorUnknownSize(iterator, Spliterator.ORDERED | Spliterator.IMMUTABLE);
        return StreamSupport.stream(spliterator, false);
    }

    protected <T> void processRowsToObjects(final Sheet sheet, final Class<T> type, final Consumer<? super T> consumer) {
        final int skip = options.skip();
        final int maxPhysicalNumberOfRows = sheet.getPhysicalNumberOfRows() + 1 - skip;

        final HSSFReadMappedFields readMappedFields = loadColumnTitles(sheet, maxPhysicalNumberOfRows, type);

        for (final Row currentRow : sheet) {
            if (!skip(currentRow) && !isRowEmpty(currentRow)) {
                internalCount += 1;

                if (limit != 0 && internalCount > limit) {
                    return;
                }

                consumer.accept(readMappedFields.parseRow(currentRow));
            }
        }
    }

    private Sheet getSheetToProcess(Workbook workbook, PoijiOptions options, String sheetName) {
        int nonHiddenSheetIndex = 0;
        int requestedIndex = options.sheetIndex();
        Sheet sheet = null;
        if (options.ignoreHiddenSheets()) {
            for (int i = 0; i < workbook.getNumberOfSheets(); i++) {
                if (!workbook.isSheetHidden(i) && !workbook.isSheetVeryHidden(i)) {
                    if (sheetName == null) {
                        if (nonHiddenSheetIndex == requestedIndex) {
                            return workbook.getSheetAt(i);
                        }
                    } else {
                        if (workbook.getSheetName(i).equalsIgnoreCase(sheetName)) {
                            return workbook.getSheetAt(i);
                        }
                    }
                    nonHiddenSheetIndex++;
                }
            }
        } else {
            if (sheetName == null) {
                sheet = workbook.getSheetAt(requestedIndex);
            } else {
                sheet = workbook.getSheet(sheetName);
            }
        }
        return sheet;
    }

    private HSSFReadMappedFields loadColumnTitles(Sheet sheet, int maxPhysicalNumberOfRows, final Class<?> type) {
        final HSSFReadMappedFields readMappedFields = new HSSFReadMappedFields(type, baseFormulaEvaluator,  options).parseEntity();
        if (maxPhysicalNumberOfRows > 0 && options.getHeaderCount() > 0) {
            readMappedFields.parseColumnNames(sheet.getRow(options.getHeaderStart() + options.getHeaderCount() - 1));
        }
        return readMappedFields;
    }

    private boolean skip(final Row currentRow) {
        return currentRow.getRowNum() < options.getHeaderStart() + options.getHeaderCount() + options.skip();
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

    protected abstract Workbook workbook();
}
