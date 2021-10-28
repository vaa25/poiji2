package com.poiji.bind.mapping;

import com.poiji.bind.PoijiInputStream;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;
import javax.xml.parsers.ParserConfigurationException;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.xml.sax.SAXException;

/**
 * Created by hakan on 22/10/2017
 */
final class XSSFUnmarshallerStream extends XSSFUnmarshaller {

    private final PoijiInputStream<?> poijiInputStream;

    XSSFUnmarshallerStream(PoijiInputStream<?> poijiInputStream, PoijiOptions options) {
        super(options);
        this.poijiInputStream = poijiInputStream;
    }

    @Override
    public <T> void unmarshal(Class<T> type, Consumer<? super T> consumer) {

        if (options.getPassword() != null) {
            returnFromEncryptedFile(type, consumer);
        } else {
            returnFromExcelFile(type, consumer);
        }
    }

    @Override
    public <T> Stream<T> stream(final Class<T> type) {
        try {
            if (options.getPassword() != null) {
                POIFSFileSystem fs = new POIFSFileSystem(poijiInputStream.stream());
                return streamOfEncryptedItems(type, fs);
            } else {
                OPCPackage open = OPCPackage.open(poijiInputStream.stream());
                return stream0(type, open);
            }
        } catch (ParserConfigurationException | SAXException | IOException | OpenXML4JException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

    private  <T> void returnFromExcelFile(Class<T> type, Consumer<? super T> consumer) {
        try (OPCPackage open = OPCPackage.open(poijiInputStream.stream())) {
            unmarshal0(type, consumer, open);
        } catch (ParserConfigurationException | SAXException | IOException | OpenXML4JException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

    private  <T> void returnFromEncryptedFile(Class<T> type, Consumer<? super T> consumer) {
        try (POIFSFileSystem fs = new POIFSFileSystem(poijiInputStream.stream())) {
            listOfEncryptedItems(type, consumer, fs);
        } catch (IOException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

}
