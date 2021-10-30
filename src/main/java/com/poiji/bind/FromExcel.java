package com.poiji.bind;

import com.poiji.bind.mapping.UnmarshallerHelper;
import com.poiji.exception.InvalidExcelFileExtension;
import com.poiji.exception.PoijiExcelType;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;
import java.util.stream.Stream;
import org.apache.poi.ss.usermodel.Sheet;

public class FromExcel<T> {

    private Source source;
    private PoijiOptions options;
    private Class<T> javaType;
    private Consumer<? super T> consumer;

    public void toConsume() {
        validate();
        if (consumer == null) {
            throw new PoijiException("Consumer must be set");
        }
        source.getDeserializer(options).unmarshal(javaType, consumer);
    }

    public List<T> toList() {
        validate();
        final List<T> result = new ArrayList<>();
        if (consumer == null) {
            consumer = result::add;
        } else {
            consumer = consumer.andThen(o -> result.add((T) o));
        }
        toConsume();
        return result;
    }

    public Stream<T> toStream() {
        validate();
        final Stream<T> stream = source.getDeserializer(options).stream(javaType);
        if (consumer != null) {
            return stream.peek(consumer);
        } else {
            return stream;
        }
    }

    private void validate() {
        if (source == null) {
            throw new PoijiException("Source must be set");
        }
        if (javaType == null) {
            throw new PoijiException("Class must be set");
        }
        if (options == null){
            options = PoijiOptions.PoijiOptionsBuilder.settings().build();
        }
    }

    public FromExcel<T> withConsumer(final Consumer<? super T> consumer) {
        this.consumer = consumer;
        return this;
    }

    public FromExcel<T> withJavaType(final Class<T> targetClass) {
        this.javaType = targetClass;
        return this;
    }

    public FromExcel<T> withOptions(final PoijiOptions options) {
        this.options = options;
        return this;
    }

    public FromExcel<T> withSource(final File file) {
        this.source = new FileSource(file);
        return this;
    }

    public FromExcel<T> withSource(final InputStream inputStream, final PoijiExcelType excelType) {
        this.source = new InputStreamSource(inputStream, excelType);
        return this;
    }

    public FromExcel<T> withSource(final Sheet sheet) {
        this.source = new SheetSource(sheet);
        return this;
    }

    private interface Source {
        Unmarshaller getDeserializer(final PoijiOptions options);
    }

    private static class SheetSource implements Source{

        private final Sheet sheet;

        public SheetSource(final Sheet sheet) {
            this.sheet = sheet;
        }

        @Override
        public Unmarshaller getDeserializer(final PoijiOptions options) {
            return UnmarshallerHelper.SheetInstance(sheet, options);
        }
    }

    private static class InputStreamSource implements Source {

        private final InputStream inputStream;
        private final PoijiExcelType excelType;

        public InputStreamSource(final InputStream inputStream, final PoijiExcelType excelType) {
            this.inputStream = inputStream;
            this.excelType = excelType;
        }

        @Override
        public Unmarshaller getDeserializer(final PoijiOptions options) {
            final PoijiInputStream<?> poijiInputStream = new PoijiInputStream<>(inputStream);

            switch (excelType) {
                case XLS:
                    return UnmarshallerHelper.HSSFInstance(poijiInputStream, options);
                case XLSX:
                    return UnmarshallerHelper.XSSFInstance(poijiInputStream, options);
                case CSV:
                    return UnmarshallerHelper.csvInstance(poijiInputStream, options);
                default:
                    throw new InvalidExcelFileExtension(
                        "Invalid file extension (" + excelType + "), excepted .xls or .xlsx or .csv");
            }
        }

    }

    private static class FileSource implements Source {

        private final File file;
        private final PoijiExcelType excelType;

        public FileSource(final File file) {
            this.file = file;
            this.excelType = PoijiExcelType.fromFileName(file.toString());
        }

        @Override
        public Unmarshaller getDeserializer(final PoijiOptions options) {
            final PoijiFile<?> poijiFile = new PoijiFile<>(file);
            try {
                switch (excelType) {
                    case XLS:
                        return UnmarshallerHelper.HSSFInstance(poijiFile, options);
                    case XLSX:
                        return UnmarshallerHelper.XSSFInstance(poijiFile, options);
                    case CSV:
                        return UnmarshallerHelper.csvInstance(
                            new PoijiInputStream<>(new FileInputStream(poijiFile.file())), options);
                    default:
                        throw new InvalidExcelFileExtension(
                            "Invalid file extension (" + excelType + "), excepted .xls or .xlsx or .csv");
                }
            } catch (FileNotFoundException e) {
                throw new PoijiException(e.getMessage(), e);
            }
        }

    }
}