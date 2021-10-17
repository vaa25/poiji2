package com.poiji.save;

import com.poiji.option.PoijiOptions;
import java.lang.reflect.Field;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collection;
import java.util.Date;
import java.util.Map;
import java.util.stream.Stream;

public final class CsvFileSaver implements FileSaver {

    private final PoijiOptions options;
    private final MappedFields mappedFields;
    private final CsvWriter csvWriter;

    public CsvFileSaver(
        final MappedFields mappedFields, final PoijiOptions options, final CsvWriter csvWriter
    ) {
        this.options = options;
        this.mappedFields = mappedFields;
        this.csvWriter = csvWriter;
    }

    @Override
    public <T> void save(final Collection<T> data) {
        mappedFields.addUnknownColumnNamesFromData(data);
        save(data.stream());
    }

    @Override
    public <T> void save(final Stream<T> data) {
        try{
            final Map<Field, Integer> orders = mappedFields.getOrders();
            final Map<Field, String> names = mappedFields.getNames();
            final Map<String, Integer> unknownOrders = mappedFields.getUnknownOrders();
            final Map<Field, Collection<String>> unknownFieldsToNames = mappedFields.getUnknownFieldsToNames();
            final int maxColumn = orders.values().stream().mapToInt(value -> value).max().orElse(-1) + 1 + unknownOrders.size();
            final Field[] fields = new Field[maxColumn];
            orders.forEach((field, column) -> fields[column] = field);
            unknownOrders.forEach((unknownName, unknownColumn) -> {
                unknownFieldsToNames.forEach((field, strings) -> {
                    if (strings.contains(unknownName)){
                        fields[unknownColumn] = field;
                    }
                });
            });
            final String[] headers = new String[maxColumn];
            for (int i = 0; i < fields.length; i++) {
                final Field field = fields[i];
                headers[i] = wrapRules(names.getOrDefault(field, ""));
            }
            unknownOrders.forEach((unknownName, unknownColumn) -> headers[unknownColumn] = unknownName);
            csvWriter.writeHeader(String.join(String.valueOf(options.getCsvDelimiter()), headers));
            data.forEach(entity -> {
                final String[] row = new String[maxColumn];
                for (int i = 0; i < fields.length; i++) {
                    final Field field = fields[i];
                    if (field != null) {
                        try {
                            if (unknownOrders.containsValue(i)){
                                final Map<String, String> unknownCell = (Map<String, String>) field.get(entity);
                                row[i] = wrapRules(unknownCell.getOrDefault(headers[i], ""));
                            } else {
                                row[i] = wrapRules(toString(field.get(entity)));
                            }
                        } catch (IllegalAccessException e) {
                            e.printStackTrace();
                        }
                    } else {
                        row[i] = "";
                    }
                }
                csvWriter.writeRow(String.join(String.valueOf(options.getCsvDelimiter()), row));
            });
        } finally {
            csvWriter.close();
        }
    }

    private String wrapRules(String value){
        if (value.contains("\"")){
            value = value.replace("\"", "\"\"");
        }
        if (value.contains(String.valueOf(options.getCsvDelimiter())) || value.contains("\"")){
            value = "\"" + value + "\"";
        }
        return value;
    }

    private String toString(final Object o) {
        if (o == null) {
            return "";
        } else if (o instanceof Date) {
            final Date date = (Date) o;
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(options.datePattern());
            return date.toInstant().atZone(ZoneId.systemDefault()).format(dateTimeFormatter);
        } else if (o instanceof LocalDate) {
            final LocalDate localDate = (LocalDate) o;
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(options.getLocalDatePattern());
            return localDate.format(dateTimeFormatter);
        } else if (o instanceof LocalDateTime) {
            final LocalDateTime localDateTime = (LocalDateTime) o;
            final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern(options.getLocalDateTimePattern());
            return localDateTime.format(dateTimeFormatter);
        } else {
            return o.toString();
        }
    }

}
