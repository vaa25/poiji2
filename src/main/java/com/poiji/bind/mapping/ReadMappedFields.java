package com.poiji.bind.mapping;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelCellRange;
import com.poiji.annotation.ExcelList;
import com.poiji.annotation.ExcelParseExceptions;
import com.poiji.annotation.ExcelRow;
import com.poiji.annotation.ExcelUnknownCells;
import com.poiji.annotation.ExcelWriteOnly;
import com.poiji.config.Casting;
import com.poiji.exception.ExcelParseException;
import com.poiji.option.PoijiOptions;
import com.poiji.util.AnnotationUtil;
import com.poiji.util.ReflectUtil;
import java.lang.reflect.Field;
import java.lang.reflect.ParameterizedType;
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
    private final Map<Field, ReadMappedList> listFields;
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
        listFields = new HashMap<>();
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
        final List<Field> withoutExcelList = parseExcelList(withoutExcelRow);
        parseExcelError(withoutExcelList);
        return this;
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
                boolean inList = false;
                if (!listFields.isEmpty()){
                    for (final ReadMappedList readMappedList : listFields.values()) {
                        if (readMappedList.parseColumnName(columnOrder, columnName)){
                            inList = true;
                        }
                    }
                }
                if (!inList){
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
    }

    public void validateMandatoryNameColumns(){
        AnnotationUtil.validateMandatoryNameColumns(options, entity, columnNames);
        listFields.values().forEach(ReadMappedList::validateMandatoryNameColumns);
    }

    public Object createNewInstance(Data data){
        return ReflectUtil.newInstanceOf(entity, data);
    }

    public Data createInstanceData() {
        return new Data();
    }

    public void setCellInData(final int row, final int column, final String content, final Data data) {
        setExcelRow(row, data);
        if (superClassFields != null) {
            superClassFields.setCellInData(row, column, content, data);
        }
        if (!unknownFields.isEmpty() && unknownColumns.containsKey(column) && !content.isEmpty()) {
            for (final Field unknownField : unknownFields) {
                final Object unknownData = data.get(unknownField);
                if (unknownData == null) {
                    final Map<String, String> map = new HashMap<>();
                    data.put(unknownField, map);
                    map.put(unknownColumns.get(column), content);
                } else {
                    final Map map = (Map) unknownData;
                    map.put(unknownColumns.get(column), content);
                }

            }
        } else if (orderedFields.containsKey(column)) {
            final Field field = orderedFields.get(column);
            final Casting casting = options.getCasting();
            final Object o = casting.castValue(field, content, row, column, options);
            final Exception exception = casting.getException();
            if (exception != null && !excelParseException.isEmpty()){
                setExcelError(column, content, data, field, exception);
            }
            data.put(field, o);
        } else {
            for (final Map.Entry<Field, ReadMappedFields> entry : rangeFields.entrySet()) {
                final Field rangeField = entry.getKey();
                Data rangeFieldData = (Data) data.get(rangeField);
                if (rangeFieldData == null) {
                    rangeFieldData = createInstanceData();
                    data.put(rangeField, rangeFieldData);
                }
                entry.getValue().setCellInData(row, column, content, rangeFieldData);
            }
            for (final Map.Entry<Field, ReadMappedList> entry : listFields.entrySet()) {
                final Field listField = entry.getKey();
                List<Data> listFieldData = (List<Data>) data.get(listField);
                if (listFieldData == null) {
                    listFieldData = new ArrayList<>();
                    data.put(listField, listFieldData);
                }
                entry.getValue().setCellInList(row, column, content, listFieldData);
            }
        }
    }

    private List<Field> parseExcelWriteOnly(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            if (field.getAnnotation(ExcelWriteOnly.class) == null){
                rest.add(field);
            }
        }
        return rest;
    }

    private List<Field> parseExcelRow(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            final ExcelRow annotation = field.getAnnotation(ExcelRow.class);
            if (annotation != null) {
                this.excelRow.add(field);
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
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
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
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
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
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
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
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
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    private List<Field> parseExcelList(final List<Field> fields) {
        final List<Field> rest = new ArrayList<>(fields.size());
        for (final Field field : fields) {
            final ExcelList annotation = field.getAnnotation(ExcelList.class);
            if (field.getType().isAssignableFrom(List.class) && annotation != null) {
                final Class entity = (Class) ((ParameterizedType) field.getGenericType()).getActualTypeArguments()[0];
                listFields.put(field, new ReadMappedList(annotation, entity, options));
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
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
                if (!field.isAccessible()) {
                    field.setAccessible(true);
                }
            } else {
                rest.add(field);
            }
        }
        return rest;
    }

    private String getUniqueColumnName(final int columnIndex, final String columnName) {
        return columnNames.contains(columnName) || columnName.isEmpty()
            ? columnName + "@" + columnIndex
            : columnName;
    }

    private void setExcelRow(final int column, final Data data) {
        for (final Field field : excelRow) {
            data.put(field, column);
        }
    }

    private void setExcelError(
        final int column, final String content, final Data data, final Field field, final Exception exception
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
            Map<String, ExcelParseException> map = (Map<String, ExcelParseException>) data.get(errorField);
            if (map == null){
                map = new HashMap<>();
                data.put(errorField, map);
            }
            map.put(key, new ExcelParseException(exception, content, field.getName()));
        }
    }

}
