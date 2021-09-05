package com.poiji.save;

import com.poiji.exception.InvalidExcelFileExtension;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.OutputStream;

import static com.poiji.util.PoijiConstants.CSV_EXTENSION;
import static com.poiji.util.PoijiConstants.XLSX_EXTENSION;
import static com.poiji.util.PoijiConstants.XLS_EXTENSION;

public final class FileSaverFactory<T> {

    private final Class<T> entity;
    private final PoijiOptions options;

    public FileSaverFactory(final Class<T> entity, final PoijiOptions options) {
        this.entity = entity;
        this.options = options;
    }

    public FileSaver toFile(final File file) {
        final MappedFields mappedFields = new MappedFields(entity, options).parseEntity();
        final WorkbookSaver workbookSaver = new FileWorkbookSaver(file, mappedFields, options);
        if (file.toString().endsWith(XLSX_EXTENSION)) {
            return new XlsxFileSaver(workbookSaver, options);
        } else if (file.toString().endsWith(XLS_EXTENSION)) {
            return new XlsFileSaver(workbookSaver, options);
        } else if (file.toString().endsWith(CSV_EXTENSION)) {
            return new CsvFileSaver(mappedFields, options, new CsvFileWriter(file));
        } else {
            throw new InvalidExcelFileExtension(file.getName() + " has unsupported extension. 'xlsx' and 'xls' and 'csv' are supported only.");
        }
    }

    public FileSaver toOutputStream(final OutputStream outputStream, final PoijiExcelType excelType) {
        final MappedFields mappedFields = new MappedFields(entity, options).parseEntity();
        final WorkbookSaver workbookSaver = new OutputStreamWorkbookSaver(outputStream, mappedFields, options);
        switch (excelType) {
            case XLSX: return new XlsxFileSaver(workbookSaver, options);
            case XLS: return new XlsFileSaver(workbookSaver, options);
            case CSV: return new CsvFileSaver(mappedFields, options, new CsvOutputStreamWriter(outputStream));
            default: throw new InvalidExcelFileExtension(
                    excelType + " is unsupported extension. 'xlsx' and 'xls' are supported only.");
        }
    }

}
