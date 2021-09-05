package com.poiji.bind.mapping;

import com.poiji.bind.PoijiFile;
import com.poiji.bind.Unmarshaller;
import com.poiji.option.PoijiOptions;
import java.io.IOException;
import java.nio.file.Files;
import java.util.Objects;
import java.util.function.Consumer;

public final class CsvUnmarshallerFile implements Unmarshaller {

    private final PoijiFile<?> poijiFile;
    private final PoijiOptions options;

    public CsvUnmarshallerFile(final PoijiFile<?> poijiFile, final PoijiOptions options) {
        this.poijiFile = poijiFile;
        this.options = options;
    }

    @Override
    public <T> void unmarshal(final Class<T> type, final Consumer<? super T> consumer) {
        final CsvLineReader<T> csvLineReader = new CsvLineReader<>(type,  options);

        try {
            Files
                .lines(poijiFile.file().toPath())
                .map(csvLineReader::readLine)
                .filter(Objects::nonNull)
                .forEach(consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
