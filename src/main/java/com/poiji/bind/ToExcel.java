package com.poiji.bind;

import com.poiji.exception.PoijiExcelType;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import com.poiji.save.FileSaver;
import com.poiji.save.FileSaverFactory;
import java.io.File;
import java.io.OutputStream;
import java.util.Collection;
import java.util.stream.Stream;

public class ToExcel<T> {

    private Source source;
    private Destination destination;
    private Class<T> javaType;
    private PoijiOptions options;

    public void save() {
        validate();
        final FileSaverFactory<T> factory = new FileSaverFactory<>(javaType, options);
        final FileSaver fileSaver = destination.getFileSaver(factory);
        source.save(fileSaver);
    }

    private void validate() {
        if (javaType == null) {
            throw new PoijiException("Java type must be installed");
        }
        if (source == null) {
            throw new PoijiException("Source must be installed");
        }
        if (destination == null) {
            throw new PoijiException("Destination must be installed");
        }
        if (options == null){
            options = PoijiOptions.PoijiOptionsBuilder.settings().build();
        }
    }

    public ToExcel<T> withSource(final Stream<T> stream) {
        this.source = new StreamSource<>(stream);
        return this;
    }

    public ToExcel<T> withSource(final Collection<T> collection) {
        this.source = new CollectionSource<>(collection);
        return this;
    }

    public ToExcel<T> withJavaType(final Class<T> javaType) {
        this.javaType = javaType;
        return this;
    }

    public ToExcel<T> withOptions(final PoijiOptions options) {
        this.options = options;
        return this;
    }

    public ToExcel<T> withDestination(final File file) {
        this.destination = new FileDestination(file);
        return this;
    }

    public ToExcel<T> withDestination(final OutputStream outputStream, final PoijiExcelType excelType) {
        this.destination = new OutputStreamDestination(outputStream, excelType);
        return this;
    }

    private interface Destination {
        <T> FileSaver getFileSaver(final FileSaverFactory<T> factory);
    }

    private static class FileDestination implements Destination{

        private final File file;
        private final PoijiExcelType excelType;

        public FileDestination(final File file) {
            this.file = file;
            this.excelType = PoijiExcelType.fromFileName(file.getName());
        }

        @Override
        public <T> FileSaver getFileSaver(final FileSaverFactory<T> factory) {
            return factory.toFile(file, excelType);
        }
    }

    private static class OutputStreamDestination implements Destination{

        private final OutputStream outputStream;
        private final PoijiExcelType excelType;

        public OutputStreamDestination(final OutputStream outputStream, final PoijiExcelType excelType) {
            this.outputStream = outputStream;
            this.excelType = excelType;
        }

        @Override
        public <T> FileSaver getFileSaver(final FileSaverFactory<T> factory) {
            return factory.toOutputStream(outputStream, excelType);
        }
    }

    private interface Source {

        void save(final FileSaver fileSaver);

    }

    private static class CollectionSource<T> implements Source {

        private final Collection<T> collection;

        public CollectionSource(final Collection<T> collection) {
            this.collection = collection;
        }

        @Override
        public void save(final FileSaver fileSaver) {
            fileSaver.save(collection);
        }
    }

    private static class StreamSource<T> implements Source {

        private final Stream<T> stream;

        public StreamSource(final Stream<T> stream) {
            this.stream = stream;
        }

        @Override
        public void save(final FileSaver fileSaver) {
            fileSaver.save(stream);
        }
    }

}