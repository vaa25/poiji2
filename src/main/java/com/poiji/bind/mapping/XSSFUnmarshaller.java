package com.poiji.bind.mapping;

import com.poiji.bind.Unmarshaller;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import com.poiji.save.TransposeUtil;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.IOUtils;
import org.apache.poi.util.LocaleUtil;
import org.apache.poi.util.XMLHelper;
import org.apache.poi.xssf.eventusermodel.ReadOnlySharedStringsTable;
import org.apache.poi.xssf.eventusermodel.XSSFReader;
import org.apache.poi.xssf.eventusermodel.XSSFReader.SheetIterator;
import org.apache.poi.xssf.model.StylesTable;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.xml.sax.ContentHandler;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.*;
import java.util.function.Consumer;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static org.apache.poi.openxml4j.opc.PackageAccess.READ_WRITE;

/**
 * Created by hakan on 22/10/2017
 */
abstract class XSSFUnmarshaller implements Unmarshaller {

    protected final PoijiOptions options;

    XSSFUnmarshaller(PoijiOptions options) {
        this.options = options;
    }

    @Override
    public <T> void unmarshal(Class<T> type, Consumer<? super T> consumer) {
        openFileAndExecute(opcPackage -> unmarshal0(type, consumer, opcPackage));
    }

    protected <T> void unmarshal0(Class<T> type, Consumer<? super T> consumer, OPCPackage open) {
        try {
            if (options.getTransposed()) {
                if (open.getPackageAccess() == READ_WRITE) {
                    final XSSFWorkbook workbook = new XSSFWorkbook(open);
                    TransposeUtil.transpose(workbook);
                    workbook.write(new OutputStream() {
                        @Override
                        public void write(final int b) {
                        }
                    });
                } else {
                    throw new UnsupportedOperationException("Can't apply transposition for streamed XLSX source");
                }
            }
            ReadOnlySharedStringsTable readOnlySharedStringsTable = new ReadOnlySharedStringsTable(open);
            XSSFReader workbookReader = new XSSFReader(open);
            StylesTable styles = workbookReader.getStylesTable();
            XMLReader reader = XMLHelper.newXMLReader();

            InputSource is = new InputSource(workbookReader.getWorkbookData());

            reader.setContentHandler(new WorkBookContentHandler(options));
            reader.parse(is);

            WorkBookContentHandler wbch = (WorkBookContentHandler) reader.getContentHandler();
            List<WorkBookSheet> sheets = wbch.getSheets();
            if (sheets.isEmpty()) {
                throw new PoijiException("no excel sheets found");
            }
            PoijiNumberFormat poijiNumberFormat = options.getPoijiNumberFormat();
            if (poijiNumberFormat != null) {
                poijiNumberFormat.overrideExcelNumberFormats(styles);
            }

            SheetIterator iter = (SheetIterator) workbookReader.getSheetsData();
            int sheetCounter = 0;

            Optional<String> maybeSheetName = SheetNameExtractor.getSheetName(type, options);

            if (!maybeSheetName.isPresent()) {
                int requestedIndex = options.sheetIndex();
                int nonHiddenSheetIndex = 0;
                while (iter.hasNext()) {
                    try (InputStream stream = iter.next()) {
                        WorkBookSheet wbs = sheets.get(sheetCounter);
                        if (wbs.getState().equals("visible")) {
                            if (nonHiddenSheetIndex == requestedIndex) {
                                processSheet(styles, reader, readOnlySharedStringsTable, type, stream, consumer);
                                return;
                            }
                            nonHiddenSheetIndex++;
                        }
                    }
                    sheetCounter++;
                }
            } else {
                String sheetName = maybeSheetName.get();
                while (iter.hasNext()) {
                    try (InputStream stream = iter.next()) {
                        WorkBookSheet wbs = sheets.get(sheetCounter);
                        if (wbs.getState().equals("visible")) {
                            if (iter.getSheetName().equalsIgnoreCase(sheetName)) {
                                processSheet(styles, reader, readOnlySharedStringsTable, type, stream, consumer);
                                return;
                            }
                        }
                    }
                    sheetCounter++;
                }
            }
        } catch (IOException | OpenXML4JException | ParserConfigurationException | SAXException e) {
            throw new PoijiException("Problem occurred while reading data: " + e.getMessage(), e);
        }
    }

    protected <T> Stream<T> stream0(Class<T> type, OPCPackage open)
        throws ParserConfigurationException, IOException, SAXException, OpenXML4JException
    {
        if (options.getTransposed()) {
            if (open.getPackageAccess() == READ_WRITE) {
                final XSSFWorkbook workbook = new XSSFWorkbook(open);
                TransposeUtil.transpose(workbook);
                workbook.write(new OutputStream() {
                    @Override
                    public void write(final int b) {
                    }
                });
            } else {
                throw new UnsupportedOperationException("Can't apply transposition for streamed XLSX source");
            }
        }

        ReadOnlySharedStringsTable readOnlySharedStringsTable = new ReadOnlySharedStringsTable(open);
        XSSFReader workbookReader = new XSSFReader(open);
        StylesTable styles = workbookReader.getStylesTable();
        XMLReader reader = XMLHelper.newXMLReader();

        InputSource is = new InputSource(workbookReader.getWorkbookData());

        reader.setContentHandler(new WorkBookContentHandler(options));
        reader.parse(is);

        WorkBookContentHandler wbch = (WorkBookContentHandler) reader.getContentHandler();
        List<WorkBookSheet> sheets = wbch.getSheets();
        if (sheets.isEmpty()) {
            throw new PoijiException("no excel sheets found");
        }
        PoijiNumberFormat poijiNumberFormat = options.getPoijiNumberFormat();
        if (poijiNumberFormat != null) {
            poijiNumberFormat.overrideExcelNumberFormats(styles);
        }

        SheetIterator iter = (SheetIterator) workbookReader.getSheetsData();
        int sheetCounter = 0;

        Optional<String> maybeSheetName = SheetNameExtractor.getSheetName(type, options);

        if (!maybeSheetName.isPresent()) {
            int requestedIndex = options.sheetIndex();
            int nonHiddenSheetIndex = 0;
            while (iter.hasNext()) {
                InputStream stream = iter.next();
                WorkBookSheet wbs = sheets.get(sheetCounter);
                if (wbs.getState().equals("visible")) {
                    if (nonHiddenSheetIndex == requestedIndex) {
                        return streamSheet(styles, reader, readOnlySharedStringsTable, type, stream, open);
                    }
                    nonHiddenSheetIndex++;
                }
                sheetCounter++;
            }
        } else {
            String sheetName = maybeSheetName.get();
            while (iter.hasNext()) {
                InputStream stream = iter.next();
                WorkBookSheet wbs = sheets.get(sheetCounter);
                if (wbs.getState().equals("visible")) {
                    if (iter.getSheetName().equalsIgnoreCase(sheetName)) {
                        return streamSheet(styles, reader, readOnlySharedStringsTable, type, stream, open);
                    }
                }
                sheetCounter++;
            }
        }
        return Stream.empty();
    }

    private <T> void processSheet(StylesTable styles,
                                  XMLReader reader,
                                  ReadOnlySharedStringsTable readOnlySharedStringsTable,
                                  Class<T> type,
                                  InputStream sheetInputStream,
                                  Consumer<? super T> consumer
    ) {
        final Locale oldLocale = LocaleUtil.getUserLocale();
        try {
            DataFormatter formatter = new DataFormatter();
            InputSource sheetSource = new InputSource(sheetInputStream);
            LocaleUtil.setUserLocale(options.getLocale());
            final ReadMappedFields mappedFields = new ReadMappedFields(type, options).parseEntity();
            final XSSFPoijiHandler<T> poijiHandler = new XSSFPoijiHandler<>(options, consumer, mappedFields);
            ContentHandler contentHandler = new XSSFSheetXMLPoijiHandler(
                styles,
                null,
                readOnlySharedStringsTable,
                poijiHandler,
                formatter,
                false,
                options
            );
            reader.setContentHandler(contentHandler);
            reader.parse(sheetSource);
		} catch (SAXException | IOException e) {
            IOUtils.closeQuietly(sheetInputStream);
            throw new PoijiException("Problem occurred while reading data", e);
        } finally {
            LocaleUtil.setUserLocale(oldLocale);
        }
    }

    private <T> Stream<T> streamSheet(
        StylesTable styles, XMLReader reader, ReadOnlySharedStringsTable readOnlySharedStringsTable, Class<T> type,
        InputStream sheetInputStream, final OPCPackage open
    ) {

        final ReadMappedFields mappedFields = new ReadMappedFields(type, options).parseEntity();
        final XSSFStreamIterator<T> poijiHandler = new XSSFStreamIterator<>(options, mappedFields);

        new Thread(() -> {
            LocaleUtil.setUserLocale(options.getLocale());
            DataFormatter formatter = new DataFormatter();
            ContentHandler contentHandler = new XSSFSheetXMLPoijiHandler(
                styles,
                null,
                readOnlySharedStringsTable,
                poijiHandler,
                formatter,
                false,
                options
            );
            try {
                reader.setContentHandler(contentHandler);
                reader.parse(new InputSource(sheetInputStream));
            } catch (SAXException | IOException e) {
                IOUtils.closeQuietly(sheetInputStream);
                throw new PoijiException("Problem occurred while reading data", e);
            }
        }).start();

        final Spliterator<T> spliterator =
            Spliterators.spliteratorUnknownSize(poijiHandler, Spliterator.ORDERED | Spliterator.IMMUTABLE);
        return StreamSupport.stream(spliterator, false).onClose(() -> {
            try {
                open.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

    }

    protected final void applyInEncryptedOpcPackage(Consumer<OPCPackage> process, POIFSFileSystem fileSystem) {
        try (InputStream stream = DocumentFactoryHelper.getDecryptedStream(fileSystem, options.getPassword());
             OPCPackage open = OPCPackage.open(stream)) {
            process.accept(open);
        } catch (IOException | OpenXML4JException e) {
            IOUtils.closeQuietly(fileSystem);
            throw new PoijiException("Problem occurred while reading data: " + e.getMessage(), e);
        }
    }

    protected final <T> Stream<T> streamOfEncryptedItems(Class<T> type, POIFSFileSystem fs) throws IOException {
        InputStream stream = DocumentFactoryHelper.getDecryptedStream(fs, options.getPassword());

        try (OPCPackage open = OPCPackage.open(stream)) {
            return stream0(type, open);

        } catch (ParserConfigurationException | SAXException | IOException | OpenXML4JException e) {
            IOUtils.closeQuietly(fs);
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

    protected abstract void openFileAndExecute(Consumer<OPCPackage> process);

    @Override
    public List<String> readSheetNames() {
        final List<String> result = new ArrayList<>();
        openFileAndExecute(opcPackage -> readSheetNames(opcPackage, result));
        return result;
    }

    private void readSheetNames(OPCPackage opcPackage, List<String> result) {
        try {
            final XSSFWorkbook workbook = new XSSFWorkbook(opcPackage);
            workbook.forEach(sheet -> result.add(sheet.getSheetName()));
        } catch (IOException e) {
            throw new PoijiException("Problem occurred while reading data: " + e.getMessage(), e);
        }
    }

}
