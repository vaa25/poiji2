package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.BomReadEntity;
import com.poiji.deserialize.model.BomReadImmutable;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.junit.Ignore;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ReadCsvStreamPrefixedTest {

    private final String path;

    public ReadCsvStreamPrefixedTest(String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> excel() {
        return Collections.singletonList("src/test/resources/bomReadStream.csv");
    }

    @Test
    @Ignore("Failed in github actions")
    public void read() throws FileNotFoundException {
        final Map<String, String> unknown = new HashMap<>();
        unknown.put("foo", "bar");
        final List<BomReadEntity> expected = new ArrayList<>();
        final BomReadEntity entity = new BomReadEntity()
            .setPrimitiveDouble(10.0)
            .setWrappedDouble(11.0)
            .setPrimitiveFloat(20.0f)
            .setWrappedFloat(21.0f)
            .setPrimitiveLong(1)
            .setText("test")
            .setPrimitiveBoolean(true)
            .setWrappedBoolean(true)
            .setDate(new Date(1234567890L))
            .setLocalDate(LocalDate.of(2020, 1, 2))
            .setLocalDateTime(LocalDateTime.of(2020, 1, 2, 12, 0))
            .setBigDecimal(new BigDecimal("123.3456"))
            .setPrimitiveByte((byte) -1)
            .setWrappedByte((byte) -2)
            .setPrimitiveShort((short) -3)
            .setWrappedShort((short) -4)
            .setAnotherUnknown(unknown);
        expected.add(entity);
        expected.add(new BomReadEntity());
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
            .settings()
            .csvDelimiter(',')
            .datePattern("dd-MM-yyyy HH:mm:ss")
            .preferNullOverDefault(true)
            .build();


        final List<BomReadEntity> read = Poiji.fromExcel(getFileInputStream(), PoijiExcelType.CSV, BomReadEntity.class, options);
        read.forEach(writeEntity -> writeEntity.setUnknown(new HashMap<>()));
        assertThat(read.toString(), equalTo(expected.toString()));

        final List<BomReadEntity> stream = Poiji.fromExcelToStream(getFileInputStream(), PoijiExcelType.CSV, BomReadEntity.class, options).collect(toList());
        stream.forEach(writeEntity -> writeEntity.setUnknown(new HashMap<>()));
        assertThat(stream.toString(), equalTo(expected.toString()));

    }

    @Test
    @Ignore("Failed in github actions")
    public void readImmutable() throws FileNotFoundException {
        final List<BomReadImmutable> expected = new ArrayList<>();
        final BomReadImmutable first = new BomReadImmutable(
                1L,
                "test",
                21.0f,
                20.0f,
                10.0,
                11.0,
                true,
                true,
                new Date(1234567890L),
                LocalDate.of(2020, 1, 2),
                LocalDateTime.of(2020, 1, 2, 12, 0),
                new BigDecimal("123.3456"),
                (byte) -1,
                (byte) -2,
                (short) -3,
                (short) -4,
                null
                );
        first.getUnknown().put("foo", "bar");
        first.getAnotherUnknown().put("foo", "bar");

        expected.add(first);
        final BomReadImmutable second = new BomReadImmutable(0L,
                null,
                null,
                0.0f,
                0.0,
                null,
                false,
                null,
                null,
                null,
                null,
                null,
                (byte) 0,
                null,
                (short) 0,
                null,
                null
        );
        expected.add(second);
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
                .settings()
                .csvDelimiter(',')
                .datePattern("dd-MM-yyyy HH:mm:ss")
                .preferNullOverDefault(true)
                .build();


        final List<BomReadImmutable> read = Poiji.fromExcel(getFileInputStream(), PoijiExcelType.CSV, BomReadImmutable.class, options);
        assertThat(read.toString(), equalTo(expected.toString()));

        final List<BomReadImmutable> stream = Poiji.fromExcelToStream(getFileInputStream(), PoijiExcelType.CSV, BomReadImmutable.class, options).collect(toList());
        assertThat(stream.toString(), equalTo(expected.toString()));

    }

    private FileInputStream getFileInputStream() throws FileNotFoundException {
        return new FileInputStream(path);
    }

}
