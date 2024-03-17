package com.poiji.bind.mapping;

import com.poiji.bind.PoijiInputStream;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

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

    private void applyInOpcPackage(Consumer<OPCPackage> process) {
        try (OPCPackage open = OPCPackage.open(poijiInputStream.stream())) {
            process.accept(open);
        } catch (IOException | OpenXML4JException e) {
            throw new PoijiException("Problem occurred while reading data: " + e.getMessage(), e);
        }
    }

    private void applyInFileSystem(Consumer<OPCPackage> process) {
        try (POIFSFileSystem fileSystem = new POIFSFileSystem(poijiInputStream.stream())) {
            applyInEncryptedOpcPackage(process, fileSystem);
        } catch (IOException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

    @Override
    protected void openFileAndExecute(Consumer<OPCPackage> process) {
        if (options.getPassword() != null) {
            applyInFileSystem(process);
        } else {
            applyInOpcPackage(process);
        }
    }
}
