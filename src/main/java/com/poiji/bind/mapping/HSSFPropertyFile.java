package com.poiji.bind.mapping;

import com.poiji.bind.PropertyUnmarshaller;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import org.apache.poi.openxml4j.exceptions.OpenXML4JException;
import org.apache.poi.openxml4j.opc.OPCPackage;
import org.apache.poi.openxml4j.opc.PackageAccess;
import org.apache.poi.poifs.filesystem.DocumentFactoryHelper;
import org.apache.poi.poifs.filesystem.POIFSFileSystem;
import org.apache.poi.util.IOUtils;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

/**
 * Created by hakan on 24.05.2020
 */
public final class HSSFPropertyFile implements PropertyUnmarshaller {

    private final File file;
    private final PoijiOptions options;

    HSSFPropertyFile(File file, PoijiOptions options) {
        this.file = file;
        this.options = options;
    }

    @Override
    public <T> T unmarshal(Class<T> type) {
        if (options.getPassword() != null) {
            return returnFromEncryptedFile(type);
        }
        return returnFromExcelFile(type);
    }

    private <T> T returnFromExcelFile(Class<T> type) {
        try (OPCPackage open = OPCPackage.open(file, PackageAccess.READ)) {
            XSSFWorkbook xssfWorkbook = new XSSFWorkbook(open);
            PropertyHandler propertyHandler = new PropertyHandler();
            return propertyHandler.unmarshal(type, xssfWorkbook.getProperties());
        } catch (IOException | OpenXML4JException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }

    private <T> T returnFromEncryptedFile(Class<T> type) {
        try (POIFSFileSystem fs = new POIFSFileSystem(file, true)) {
            InputStream stream = DocumentFactoryHelper.getDecryptedStream(fs, options.getPassword());
            try (OPCPackage open = OPCPackage.open(stream)) {
                XSSFWorkbook xssfWorkbook = new XSSFWorkbook(open);
                PropertyHandler propertyHandler = new PropertyHandler();
                return propertyHandler.unmarshal(type, xssfWorkbook.getProperties());
            } catch (IOException | OpenXML4JException e) {
                IOUtils.closeQuietly(fs);
                throw new PoijiException("Problem occurred while reading data", e);
            }
        } catch (IOException e) {
            throw new PoijiException("Problem occurred while reading data", e);
        }
    }
}
