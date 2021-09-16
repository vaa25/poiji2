package com.poiji.bind.mapping;

import com.poiji.bind.Unmarshaller;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import com.poiji.save.TransposeUtil;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Optional;
import java.util.function.Consumer;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.util.IOUtils;
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

import static org.apache.poi.openxml4j.opc.PackageAccess.READ_WRITE;

/**
 * Created by hakan on 22/10/2017
 */
abstract class XSSFUnmarshaller implements Unmarshaller {

    protected final PoijiOptions options;

    XSSFUnmarshaller(PoijiOptions options) {
        this.options = options;
    }

    protected <T> void unmarshal0(Class<T> type, Consumer<? super T> consumer, OPCPackage open)
            throws ParserConfigurationException, IOException, SAXException, OpenXML4JException {
        if (options.getTransposed()){
            if (open.getPackageAccess() == READ_WRITE){
                final XSSFWorkbook workbook = new XSSFWorkbook(open);
                TransposeUtil.transpose(workbook);
                workbook.write(new OutputStream() {@Override public void write(final int b) {}});
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
        if (poijiNumberFormat != null)
            poijiNumberFormat.overrideExcelNumberFormats(styles);

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
	  }

    private <T> void processSheet(StylesTable styles,
                                  XMLReader reader,
                                  ReadOnlySharedStringsTable readOnlySharedStringsTable,
                                  Class<T> type,
                                  InputStream sheetInputStream,
                                  Consumer<? super T> consumer) {

        DataFormatter formatter = new DataFormatter();
        InputSource sheetSource = new InputSource(sheetInputStream);
        try {
            final ReadMappedFields mappedFields = new ReadMappedFields(type, options).parseEntity();
            PoijiHandler<T> poijiHandler = new PoijiHandler<>(type, options, consumer, mappedFields);
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
        }
    }

    protected <T> void listOfEncryptedItems(Class<T> type, Consumer<? super T> consumer, POIFSFileSystem fs) throws IOException {
        InputStream stream = DocumentFactoryHelper.getDecryptedStream(fs, options.getPassword());

        try (OPCPackage open = OPCPackage.open(stream)) {
            unmarshal0(type, consumer, open);

        } catch (ParserConfigurationException | SAXException | IOException | OpenXML4JException e) {
            IOUtils.closeQuietly(fs);
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

    protected abstract <T> void returnFromExcelFile(Class<T> type, Consumer<? super T> consumer);

    protected abstract <T> void returnFromEncryptedFile(Class<T> type, Consumer<? super T> consumer);
}
