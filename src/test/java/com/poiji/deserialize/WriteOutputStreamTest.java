package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.WriteEntity;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.stream.Collectors.toList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class WriteOutputStreamTest {

    private final String path;

    public WriteOutputStreamTest(String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> excel() {
        return Arrays.asList(
            "src/test/resources/write.xlsx",
            "src/test/resources/write.xls",
            "src/test/resources/write.csv"
        );
    }

    @Test
    public void write() throws FileNotFoundException {
        final Map<String, String> unknown = new HashMap<>();
        unknown.put("unKnown1", "unknown value 1");
        unknown.put("unKnown2", "unknown value 2");
        final List<WriteEntity> expected = new ArrayList<>();
        final WriteEntity entity = new WriteEntity()
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
        expected.add(new WriteEntity());
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
            .settings()
            .csvDelimiter(';')
            .datePattern("dd-MM-yyyy HH:mm:ss")
            .preferNullOverDefault(true)
            .build();

        Poiji.toExcel(new FileOutputStream(path), PoijiExcelType.fromFileName(path), WriteEntity.class, expected, options);

        final List<WriteEntity> read = Poiji.fromExcel(new File(path), WriteEntity.class, options);
        read.forEach(writeEntity -> writeEntity.setUnknown(new HashMap<>()));
        assertThat(read.toString(), equalTo(expected.toString()));

        final List<WriteEntity> stream = Poiji.fromExcelToStream(new File(path), WriteEntity.class, options).collect(toList());
        stream.forEach(writeEntity -> writeEntity.setUnknown(new HashMap<>()));
        assertThat(stream.toString(), equalTo(expected.toString()));

    }

}
