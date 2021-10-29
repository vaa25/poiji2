package com.poiji.bind;

import com.poiji.bind.mapping.PoijiPropertyHelper;
import com.poiji.exception.InvalidExcelFileExtension;
import com.poiji.exception.PoijiExcelType;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.InputStream;

import static com.poiji.exception.PoijiExcelType.XLSX;

class FromExcelProperties<T> {

    private Source source;
    private PoijiOptions options;
    private Class<T> javaType;

    private void validate() {
        if (source == null) {
            throw new PoijiException("Source must be set");
        }
        if (javaType == null) {
            throw new PoijiException("Class must be set");
        }
        if (!source.hasProperties()){
            throw new InvalidExcelFileExtension("Reading metadata from this excel type is not supported");
        }
        if (options == null){
            options = PoijiOptions.PoijiOptionsBuilder.settings().build();
        }
    }

    public FromExcelProperties<T> withJavaType(final Class<T> javaType) {
        this.javaType = javaType;
        return this;
    }

    public FromExcelProperties<T> withOptions(final PoijiOptions options) {
        this.options = options;
        return this;
    }

    public FromExcelProperties<T> withSource(final File file) {
        this.source = new FileSource(file);
        return this;
    }

    public FromExcelProperties<T> withSource(final InputStream inputStream, final PoijiExcelType excelType) {
        this.source = new InputStreamSource(inputStream, excelType);
        return this;
    }

    public T get() {
        validate();
        return source.getPropertyUnmarshaller(options).unmarshal(javaType);
    }

    private interface Source{
        PropertyUnmarshaller getPropertyUnmarshaller(final PoijiOptions options);
        boolean hasProperties();
    }

    private static class FileSource implements Source{

        private final File file;
        private final PoijiExcelType excelType;

        public FileSource(final File file) {
            this.file = file;
            this.excelType = PoijiExcelType.fromFileName(file.toString());
        }

        @Override
        public PropertyUnmarshaller getPropertyUnmarshaller(final PoijiOptions options){
            return PoijiPropertyHelper.createPoijiPropertyFile(file, options);
        }

        @Override
        public boolean hasProperties() {
            return XLSX == excelType;
        }

    }

    private static class InputStreamSource implements Source{

        private final InputStream inputStream;
        private final PoijiExcelType excelType;

        public InputStreamSource(final InputStream inputStream, final PoijiExcelType excelType) {
            this.inputStream = inputStream;
            this.excelType = excelType;
        }

        @Override
        public PropertyUnmarshaller getPropertyUnmarshaller(final PoijiOptions options){
            return PoijiPropertyHelper.createPoijiPropertyStream(inputStream, options);
        }

        @Override
        public boolean hasProperties() {
            return XLSX == excelType;
        }

    }
}