package com.poiji.bind.mapping;

import com.poiji.bind.PoijiFile;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.xml.sax.SAXException;

import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.apache.poi.openxml4j.opc.PackageAccess.READ;
import static org.apache.poi.openxml4j.opc.PackageAccess.READ_WRITE;

/**
 * Created by hakan on 22/10/2017
 */
final class XSSFUnmarshallerFile extends XSSFUnmarshaller {

    private final PoijiFile<?> poijiFile;

    XSSFUnmarshallerFile(PoijiFile<?> poijiFile, PoijiOptions options) {
        super(options);
        this.poijiFile = poijiFile;
    }



    protected void openFileAndExecute(Consumer<OPCPackage> process) {
        if (options.getPassword() != null) {
            applyInFileSystem(fileSystem-> applyInEncryptedOpcPackage(process, fileSystem));
        } else {
            applyInOpcPackage(process);
        }
    }

    @Override
    public <T> Stream<T> stream(final Class<T> type) {
        try {
            if (options.getPassword() != null) {
                POIFSFileSystem fs = new POIFSFileSystem(poijiFile.file(), true);
                return streamOfEncryptedItems(type, fs);
            } else {
                final PackageAccess packageAccess = options.getTransposed() ? READ_WRITE : READ;
                OPCPackage open = OPCPackage.open(poijiFile.file(), packageAccess);
                return stream0(type, open);
            }
        } catch (ParserConfigurationException | SAXException | IOException | OpenXML4JException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

    private void applyInOpcPackage(Consumer<OPCPackage> process) {
        final PackageAccess packageAccess = options.getTransposed() ? READ_WRITE : READ;
        try (OPCPackage open = OPCPackage.open(poijiFile.file(), packageAccess)) {
            process.accept(open);
        } catch (IOException | OpenXML4JException e) {
            throw new PoijiException("Problem occurred while reading data: " + e.getMessage(), e);
        }
    }

    private void applyInFileSystem(Consumer<POIFSFileSystem> process) {
        try (POIFSFileSystem fs = new POIFSFileSystem(poijiFile.file(), true)) {
            process.accept(fs);
        } catch (IOException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

}
