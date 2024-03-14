package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.HugeEntity;
import com.poiji.option.PoijiOptions;
import org.apache.poi.ss.SpreadsheetVersion;
import org.junit.Ignore;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Stream;

import static java.util.Arrays.asList;

/**
 * For manual testing only.
 */
public class HugeTest {

    /**
     * Rows: 1048575
     * Columns: 50
     * Written in 138303 ms
     * src/test/resources/concurrent4.xlsx (210273 kB, 1048575 rows) read in 138001 ms
     * src/test/resources/concurrent3.xlsx (210273 kB, 1048575 rows) read in 138401 ms
     * src/test/resources/concurrent1.xlsx (210273 kB, 1048575 rows) read in 138672 ms
     * src/test/resources/concurrent2.xlsx (210273 kB, 1048575 rows) read in 138934 ms
     * Total read in 138935 ms
     */
    @Test
    @Ignore("Test disabled to prevent huge xlsx files writing in CI")
    public void writeThenReadStream() {

        final long start = System.nanoTime();
        final int size = SpreadsheetVersion.EXCEL2007.getLastRowIndex();
        System.out.println("Rows: " + size);
        System.out.println("Columns: " + new HugeEntity().totalFields);
        final Stream<HugeEntity> entities1 = generateEntities(size, "1");
        final Stream<HugeEntity> entities2 = generateEntities(size, "2");
        final Stream<HugeEntity> entities3 = generateEntities(size, "3");
        final Stream<HugeEntity> entities4 = generateEntities(size, "4");
        final String name1 = "src/test/resources/concurrent1.xlsx";
        final String name2 = "src/test/resources/concurrent2.xlsx";
        final String name3 = "src/test/resources/concurrent3.xlsx";
        final String name4 = "src/test/resources/concurrent4.xlsx";
        final List<WriteData> writeData = asList(
                new WriteData(name1, entities1),
                new WriteData(name2, entities2),
                new WriteData(name3, entities3),
                new WriteData(name4, entities4)
        );

        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().preferNullOverDefault(true).build();

        writeData.parallelStream()
                .forEach(data -> Poiji.toExcel(new File(data.path), HugeEntity.class, data.entities, options));

        final long written = System.nanoTime();
        System.out.println("Written in " + (written - start) / 1000000 + " ms");

        asList(name1, name2, name3, name4)
                .parallelStream()
                .forEach(fileName -> load(fileName, options));

        final long read = System.nanoTime();
        System.out.println("Total read in " + (read - written) / 1000000 + " ms");

    }

    private Stream<HugeEntity> generateEntities(final int size, final String marker) {
        final AtomicInteger index = new AtomicInteger();
        return Stream.generate(() -> generateEntity(index.getAndIncrement(), marker))
                .limit(size);
    }

    public HugeEntity generateEntity(int index, final String marker) {
        final HugeEntity hugeEntity = new HugeEntity();
        final Field[] declaredFields = hugeEntity.getClass().getDeclaredFields();
        for (Field declaredField : declaredFields) {
            if (declaredField.getName().startsWith("field")) {
                final String fieldIndex = declaredField.getName().substring("field".length());
                final String value = fieldIndex + "_" + index + "_" + marker;
                try {
                    declaredField.set(hugeEntity, value);
                } catch (IllegalAccessException e) {
                    throw new RuntimeException(e);
                }
            }
        }
        return hugeEntity;
    }

    private void load(String fileName, PoijiOptions options) {
        final long written = System.nanoTime();
        final File file = new File(fileName);
        final long count = Poiji.fromExcelToStream(file, HugeEntity.class, options).count();
        final long size = getSize(file);
        System.out.printf("%s (%d kB, %d rows) read in %d ms%n", fileName, size / 1024, count, (System.nanoTime() - written) / 1000000);
    }

    private long getSize(File file) {
        try {
            return Files.size(file.toPath());
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static class WriteData {
        private final String path;
        private final Stream<HugeEntity> entities;

        public WriteData(final String path, final Stream<HugeEntity> entities) {
            this.path = path;
            this.entities = entities;
        }
    }

}
