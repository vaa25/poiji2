package com.poiji.bind.mapping;

import com.poiji.annotation.DisableCellFormatXLS;
import com.poiji.option.PoijiOptions;

import java.lang.reflect.Field;
import java.util.Collection;
import java.util.HashSet;
import java.util.Map;
import org.apache.poi.ss.formula.BaseFormulaEvaluator;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;

import static java.util.stream.Collectors.toList;

public final class HSSFReadMappedFields extends ReadMappedFields{

    private final DataFormatter dataFormatter;
    private final Collection<Short> disabledCellFormat;
    private final BaseFormulaEvaluator baseFormulaEvaluator;

    public HSSFReadMappedFields(
        final Class<?> entity, final BaseFormulaEvaluator baseFormulaEvaluator, final PoijiOptions options
    ) {
        super(entity, options);
        this.baseFormulaEvaluator = baseFormulaEvaluator;
        dataFormatter = new DataFormatter();
        disabledCellFormat = new HashSet<>();
    }

    @Override
    public HSSFReadMappedFields parseEntity(){
        final Class<?> superclass = entity.getSuperclass();
        if (!superclass.isInterface() && superclass != Object.class) {
            superClassFields = new HSSFReadMappedFields(superclass, baseFormulaEvaluator, options).parseEntity();
        }
        super.parseEntity();
        return this;
    }

    private void parseDisableCellFormatColumns(){
        final Collection<Field> fields = parseDisableCellFormatFields();
        for (final Map.Entry<Integer, Field> entry : orderedFields.entrySet()) {
            if (fields.contains(entry.getValue())){
                disabledCellFormat.add(entry.getKey().shortValue());
            }
        }
        disabledCellFormat.addAll(unknownColumns.keySet().stream().map(Integer::shortValue).collect(toList()));
    }

    private Collection<Field> parseDisableCellFormatFields() {
        final Field[] fields = entity.getDeclaredFields();
        final Collection<Field> result = new HashSet<>();
        for (final Field field : fields) {
            final DisableCellFormatXLS annotation = field.getAnnotation(DisableCellFormatXLS.class);
            if (annotation != null && annotation.value()) {
                result.add(field);
            }
        }
        return result;
    }

    public <T> T parseRow(final Row row) {
        final Data data = createInstanceData();
        for (short columnOrder = row.getFirstCellNum(); columnOrder < row.getLastCellNum(); columnOrder++) {
            final Cell cell = row.getCell(columnOrder);
            if (disabledCellFormat.contains(columnOrder)){
                cell.setCellStyle(null);
            }
            final String cellValue = dataFormatter.formatCellValue(cell, baseFormulaEvaluator);
            setCellInData(row.getRowNum(), columnOrder, cellValue, data);
        }
        return (T) createNewInstance(data);
    }

    public void parseColumnNames(final Row row) {
        for (short columnOrder = row.getFirstCellNum(); columnOrder < row.getLastCellNum(); columnOrder++) {
            final Cell cell = row.getCell(columnOrder);
            if (cell != null){
                parseColumnName(columnOrder, cell.getStringCellValue());
            }
        }
        validateMandatoryNameColumns();
        parseDisableCellFormatColumns();
    }

}
