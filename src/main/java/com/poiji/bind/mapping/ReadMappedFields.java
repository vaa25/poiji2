package com.poiji.bind.mapping;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelCellRange;
import com.poiji.annotation.ExcelParseExceptions;
import com.poiji.annotation.ExcelRow;
import com.poiji.annotation.ExcelUnknownCells;
import com.poiji.annotation.ExcelWriteOnly;
import com.poiji.config.Casting;
import com.poiji.exception.ExcelParseException;
import com.poiji.exception.IllegalCastException;
import com.poiji.option.PoijiOptions;
import com.poiji.util.AnnotationUtil;
import com.poiji.util.ReflectUtil;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import static com.poiji.annotation.ExcelCellName.ABSENT_ORDER;
import static java.util.Arrays.asList;
import static java.util.Collections.singletonList;

public class ReadMappedFields {

    protected final Class<?> entity;
    protected final Map<Integer, Field> orderedFields;
    private final Map<String, Field> namedFields;
    private final List<Field> unknownFields;
    protected final PoijiOptions options;
    protected final Map<Integer, String> unknownColumns;
    private final Map<Field, ReadMappedFields> rangeFields;
    private final List<Field> excelRow;
    private final List<Field> excelParseException;
    protected ReadMappedFields superClassFields;
    private final Set<String> columnNames;

    public ReadMappedFields(final Class<?> entity, final PoijiOptions options) {
        this.entity = entity;
        orderedFields = new HashMap<>();
        namedFields = new HashMap<>();
        unknownFields = new ArrayList<>();
        this.options = options;
        unknownColumns = new HashMap<>();
        rangeFields = new HashMap<>();
        this.excelRow = new ArrayList<>();
        this.excelParseException = new ArrayList<>();
        columnNames = new HashSet<>();
    }

    public ReadMappedFields parseEntity() {
        final Class<?> superclass = entity.getSuperclass();
        if (!superclass.isInterface() && superclass != Object.class) {
            superClassFields = new ReadMappedFields(superclass, options).parseEntity();
        }
        final List<Field> declaredFields = asList(entity.getDeclaredFields());
        final List<Field> withoutWriteOnly = parseExcelWriteOnly(declaredFields);
        final List<Field> withoutExcelRange = parseExcelCellRange(withoutWriteOnly);
        final List<Field> withoutExcelCell = parseExcelCell(withoutExcelRange);
        final List<Field> withoutUnknownCells = parseUnknownCells(withoutExcelCell);
        final List<Field> withoutExcelCellName = parseExcelCellName(withoutUnknownCells);
        final List<Field> withoutExcelRow = parseExcelRow(withoutExcelCellName);
        parseExcelError(withoutExcelRow);
        return this;
    }

    public void validateMandatoryNameColumns(){
        AnnotationUtil.validateMandatoryNameColumns(options, entity, columnNames);
    }

    private List<Field> parseExcelWriteOnly(final List<Field> fields) {
        final List<Field> withoutWriteOnly = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            if (field.getAnnotation(ExcelWriteOnly.class) == null){
                withoutWriteOnly.add(field);
            }
        }
        return withoutWriteOnly;
    }

    private List<Field> parseExcelRow(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            final ExcelRow annotation = field.getAnnotation(ExcelRow.class);
            if (annotation != null) {
                this.excelRow.add(field);
                field.setAccessible(true);
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    private List<Field> parseExcelError(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            final ExcelParseExceptions annotation = field.getAnnotation(ExcelParseExceptions.class);
            if (annotation != null) {
                this.excelParseException.add(field);
                field.setAccessible(true);
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    private List<Field> parseExcelCellName(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            final ExcelCellName annotation = field.getAnnotation(ExcelCellName.class);
            if (annotation != null) {
                final List<String> possibleFieldNames = getPossibleFieldNames(annotation);
                for (final String possibleFieldName : possibleFieldNames) {
                    final int order = annotation.order();
                    if (order != ABSENT_ORDER && !orderedFields.containsKey(order)) {
                        orderedFields.put(order, field);
                    }
                    final String name = options.getFormatting().transform(options, possibleFieldName);
                    namedFields.put(name, field);
                }
                field.setAccessible(true);
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    private List<String> getPossibleFieldNames(final ExcelCellName annotation) {
        final String delimeter = annotation.columnNameDelimiter();
        return delimeter.isEmpty() ? singletonList(annotation.value()) : asList(annotation.value().split(delimeter));
    }

    private List<Field> parseUnknownCells(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            if (field.getAnnotation(ExcelUnknownCells.class) != null && field.getType().isAssignableFrom(Map.class)) {
                unknownFields.add(field);
                field.setAccessible(true);
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    private List<Field> parseExcelCellRange(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            if (field.getAnnotation(ExcelCellRange.class) != null) {
                rangeFields.put(field, new ReadMappedFields(field.getType(), options).parseEntity());
                field.setAccessible(true);
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    private List<Field> parseExcelCell(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            if (field.getAnnotation(ExcelCell.class) != null) {
                final Integer excelOrder = field.getAnnotation(ExcelCell.class).value();
                orderedFields.put(excelOrder, field);
                field.setAccessible(true);
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    public void parseColumnName(final int columnOrder, final String columnName) {
        if (superClassFields != null) {
            superClassFields.parseColumnName(columnOrder, columnName);
        }
        final String transformedColumnName = options.getFormatting().transform(options, columnName);
        final String uniqueColumnName = getUniqueColumnName(columnOrder, transformedColumnName);
        columnNames.add(uniqueColumnName);

        if (!orderedFields.containsKey(columnOrder)) {
            if (namedFields.containsKey(uniqueColumnName)) {
                orderedFields.put(columnOrder, namedFields.get(uniqueColumnName));
            } else {
                if (unknownFields.isEmpty()) {
                    for (final ReadMappedFields rangeField : rangeFields.values()) {
                        rangeField.parseColumnName(columnOrder, uniqueColumnName);
                    }
                } else {
                    unknownColumns.put(columnOrder, uniqueColumnName);
                }
            }
        }
    }

    private String getUniqueColumnName(final int columnIndex, final String columnName) {
        return columnNames.contains(columnName) || columnName.isEmpty()
            ? columnName + "@" + columnIndex
            : columnName;
    }

    private void setFieldData(Field field, Object o, Object instance) {
        try {
            field.set(instance, o);
        } catch (IllegalAccessException e) {
            throw new IllegalCastException("Unexpected cast type {" + o + "} of field" + field.getName());
        }
    }

    public void setCellInInstance(final int row, final int column, final String content, final Object instance) {
        setExcelRow(row, instance);
        if (superClassFields != null) {
            superClassFields.setCellInInstance(row, column, content, instance);
        }
        if (!unknownFields.isEmpty() && unknownColumns.containsKey(column) && !content.isEmpty()) {
            for (final Field unknownField : unknownFields) {
                try {
                    final Object unknownInstance = unknownField.get(instance);
                    if (unknownInstance == null) {
                        final Map<String, String> map = new HashMap<>();
                        unknownField.set(instance, map);
                        map.put(unknownColumns.get(column), content);
                    } else {
                        final Map map = (Map) unknownInstance;
                        map.put(unknownColumns.get(column), content);
                    }

                } catch (IllegalAccessException e) {
                    throw new IllegalCastException("Could not read content of field " + unknownField.getName() + " on Object {" + instance + "}");
                }
            }
        } else if (orderedFields.containsKey(column)) {
            final Field field = orderedFields.get(column);
            final Casting casting = options.getCasting();
            final Object o = casting.castValue(field, content, row, column, options);
            final Exception exception = casting.getException();
            if (exception != null && !excelParseException.isEmpty()){
                setExcelError(column, content, instance, field, exception);
            }
            setFieldData(field, o, instance);
        } else {
            for (final Map.Entry<Field, ReadMappedFields> entry : rangeFields.entrySet()) {
                final Field rangeField = entry.getKey();
                try {
                    Object rangeFieldInstance = rangeField.get(instance);
                    if (rangeFieldInstance == null) {
                        rangeFieldInstance = ReflectUtil.newInstanceOf(rangeField.getType());
                        rangeField.set(instance, rangeFieldInstance);
                    }
                    entry.getValue().setCellInInstance(row, column, content, rangeFieldInstance);
                } catch (IllegalAccessException e) {
                    throw new IllegalCastException("Could not read content of field " + rangeField.getName() + " on Object {" + instance + "}");
                }
            }
        }
    }

    private <T> void setExcelRow(final int column, final T instance) {
        for (final Field field : excelRow) {
            try {
                field.setInt(instance, column);
            } catch (IllegalAccessException e) {
                throw new IllegalCastException("Could not set excel row number in field " + field.getName() + " on Object {" + instance + "}");
            }
        }
    }

    private void setExcelError(
        final int column, final String content, final Object instance, final Field field, final Exception exception
    ) {

        final ExcelCellName annotation = field.getAnnotation(ExcelCellName.class);
        final String key;
        if (annotation != null){
            key = annotation.value();
        } else if (orderedFields.containsKey(column)){
            key = "["+ column + "]";
        } else {
            return;
        }
        for (final Field errorField : excelParseException) {
            try {
                Map<String, ExcelParseException> map = (Map<String, ExcelParseException>) errorField.get(instance);
                if (map == null){
                    map = new HashMap<>();
                    errorField.set(instance, map);
                }
                map.put(key, new ExcelParseException(exception, content, field.getName()));
            } catch (IllegalAccessException e) {
                throw new IllegalCastException("Could not set excel parse error in field " + errorField.getName() + " on Object {" + instance + "}");
            }
        }
    }

}
