package com.poiji.bind.mapping;

import com.poiji.bind.PoijiInputStream;
import com.poiji.bind.Unmarshaller;
import com.poiji.exception.PoijiException;
import com.poiji.option.PoijiOptions;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Objects;
import java.util.Optional;
import java.util.function.Consumer;
import java.util.stream.Stream;

public final class CsvUnmarshallerStream implements Unmarshaller {

    private final PoijiInputStream<?> poijiStream;
    private final PoijiOptions options;

    public CsvUnmarshallerStream(final PoijiInputStream<?> poijiStream, final PoijiOptions options) {
        this.poijiStream = poijiStream;
        this.options = options;
    }

    @Override
    public <T> void unmarshal(final Class<T> type, final Consumer<? super T> consumer) {
        stream(type).forEach(consumer);
    }

    @Override
    public <T> Stream<T> stream(final Class<T> type) {
        final CsvLineReader<T> csvLineReader = new CsvLineReader<>(type,  options);
        final BomInputStream bomInputStream = new BomInputStream(poijiStream.stream());
        final String charsetName = bomInputStream.getCharset().orElse(options.getCharset());
        try {
            final BufferedReader reader = new BufferedReader(new InputStreamReader(bomInputStream, charsetName));
            Stream<String> stream = reader.lines();
            if (options.getLimit() > 0) {
                stream =
                    stream.limit(
                        options.getLimit() + options.getHeaderStart() + options.getHeaderCount() + options.skip());
            }
            return stream.map(csvLineReader::readLine).filter(Objects::nonNull);
        } catch (IOException e) {
            throw new PoijiException("Problem occurred while reading CSV data", e);
        }
    }

    private static class BomInputStream extends InputStream{

        private final InputStream inner;
        private final CharsetDetector charsetDetector;
        private byte[] preread;

        private BomInputStream(final InputStream inner) {
            this.inner = inner;
            this.charsetDetector = new CharsetDetector();
        }

        public Optional<String> getCharset(){
            final byte[] b = new byte[4];
            try {
                inner.read(b);
                final int reduced = charsetDetector.detect(b);
                preread = new byte[b.length - reduced];
                System.arraycopy(b, 0, preread, 0, b.length - reduced);
                return charsetDetector.getCharset();
            } catch (IOException e) {
                e.printStackTrace();
                return Optional.empty();
            }
        }

        @Override
        public int read() throws IOException {
            return 0;
        }

        @Override
        public int read(final byte[] b) throws IOException {
            return inner.read(b);
        }

        @Override
        public int read(final byte[] b, final int off, final int len) throws IOException {
            final int length = preread.length;
            if (length > 0){
                System.arraycopy(preread, 0, b, 0, length);
                final int read = inner.read(b, length, len - length);
                preread = new byte[0];
                return read + length;
            }else {
                return inner.read(b, off, len);
            }
        }

        @Override
        public long skip(final long n) throws IOException {
            return inner.skip(n);
        }

        @Override
        public int available() throws IOException {
            return inner.available();
        }

        @Override
        public void close() throws IOException {
            inner.close();
        }

        @Override
        public void mark(final int readlimit) {
            inner.mark(readlimit);
        }

        @Override
        public void reset() throws IOException {
            inner.reset();
        }

        @Override
        public boolean markSupported() {
            return inner.markSupported();
        }
    }

}
