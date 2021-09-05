package com.poiji.bind.mapping;

import com.poiji.bind.PoijiInputStream;
import com.poiji.bind.Unmarshaller;
import com.poiji.option.PoijiOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.function.Consumer;

public final class CsvUnmarshallerStream implements Unmarshaller {

    private final PoijiInputStream<?> poijiStream;
    private final PoijiOptions options;

    public CsvUnmarshallerStream(final PoijiInputStream<?> poijiStream, final PoijiOptions options) {
        this.poijiStream = poijiStream;
        this.options = options;
    }

    @Override
    public <T> void unmarshal(final Class<T> type, final Consumer<? super T> consumer) {
        final CsvLineReader<T> csvLineReader = new CsvLineReader<>(type,  options);
        try (final BufferedReader reader = new BufferedReader(new InputStreamReader(poijiStream.stream()))){
            reader.lines().map(csvLineReader::readLine).filter(Objects::nonNull).forEach(consumer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
