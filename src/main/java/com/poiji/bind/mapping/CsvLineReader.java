package com.poiji.bind.mapping;

import com.poiji.option.PoijiOptions;
import com.poiji.util.ReflectUtil;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;

public final class CsvLineReader<T> {

    private final ReadMappedFields readMappedFields;
    private final Class<T> entity;
    private final PoijiOptions options;
    private boolean isFirstLine;
    private final Collection<Integer> usedColumns;
    private int row = 1;

    public CsvLineReader(final Class<T> entity, final PoijiOptions options) {
        this.readMappedFields = new ReadMappedFields(entity, options).parseEntity();
        this.isFirstLine = true;
        this.options = options;
        this.entity = entity;
        this.usedColumns = new HashSet<>();

    }

    public T readLine(final String line) {
        if (line.isEmpty()){
            return null;
        } else if (isFirstLine){
            isFirstLine = false;
            final String[] columnNames = parseLine(line);
            for (int i = 0; i < columnNames.length; i++) {
                final String columnName = unwrap(columnNames[i]);
                if (!columnName.isEmpty()){
                    readMappedFields.parseColumnName(i, columnName);
                    usedColumns.add(i);
                }
            }
            readMappedFields.validateMandatoryNameColumns();
            return null;
        } else {
            final String[] values = parseLine(line);
            if (areValuesHaveData(values)){
                final T instance = ReflectUtil.newInstanceOf(entity);
                for (int column = 0; column < values.length; column++) {
                    if (usedColumns.contains(column)){
                        readMappedFields.setCellInInstance(row, column, unwrap(values[column]), instance);
                    }
                }
                row++;
                return instance;
            } else {
                row++;
                return null;
            }
        }
    }

    private String[] parseLine(final String line) {
        final char[] chars = line.toCharArray();
        State state = State.BEGIN;
        final ArrayList<String> words = new ArrayList<>();
        StringBuilder word = new StringBuilder();
        final char delimiter = options.getCsvDelimiter();
        for (char aChar : chars) {
            if (state == State.BEGIN && word.length() == 0 && aChar == '\"') {
                state = State.MIDDLE;
            } else if ((state == State.MIDDLE || (state == State.BEGIN && word.length() > 0)) && aChar == '\"') {
                state = State.QUOTE;
            } else if (state == State.QUOTE && aChar == '\"') {
                state = State.BEGIN;
                word.append('\"');
            } else if ((state == State.QUOTE || state == State.BEGIN) && aChar == delimiter) {
                words.add(word.toString());
                word = new StringBuilder();
                state = State.BEGIN;
            } else {
                word.append(aChar);
            }
        }
        if (word.length() > 0) {
            words.add(word.toString());
        }
        final String[] result = new String[words.size()];
        words.toArray(result);
        return result;
    }

    private enum State {
        BEGIN, MIDDLE, QUOTE
    }

    private boolean areValuesHaveData(final String[] values) {
        for (final String value : values) {
            if (!value.isEmpty()){
                return true;
            }
        }
        return false;
    }

    private String unwrap(final String value) {
        if (!value.isEmpty() && value.charAt(0) == '"' && value.charAt(value.length() - 1) == '"'){
            return value.substring(1, value.length() - 1);
        } else {
            return value;
        }
    }
}
