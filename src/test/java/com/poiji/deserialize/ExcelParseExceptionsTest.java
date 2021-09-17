package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.WriteEntity;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class ExcelParseExceptionsTest {

    private final String path;

    public ExcelParseExceptionsTest(String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> excel() {
        return Arrays.asList("src/test/resources/excelParseException.xlsx", "src/test/resources/excelParseException.xls", "src/test/resources/excelParseException.csv");
    }

    @Test
    public void write() {
        final Map<String, String> unknown = new HashMap<>();
        unknown.put("unKnown1", "unknown value 1");
        unknown.put("unKnown2", "unknown value 2");
        final List<WriteEntity> expected = new ArrayList<>();
        final WriteEntity entity = new WriteEntity()
            .setPrimitiveLong(0)
            .setPrimitiveDouble(10.0)
            .setWrappedDouble(11.0)
            .setPrimitiveFloat(0.0f)
            .setWrappedFloat(21.0f)
            .setText("test")
            .setPrimitiveBoolean(true)
            .setWrappedBoolean(true)
            .setDate(null)
            .setLocalDate(null)
            .setLocalDateTime(null)
            .setBigDecimal(null)
            .setPrimitiveByte((byte)0)
            .setWrappedByte(null)
            .setPrimitiveShort((short) -3)
            .setWrappedShort(null)
            .setAnotherUnknown(unknown)
            ;
        expected.add(entity);
        expected.add(new WriteEntity());
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
            .settings()
            .datePattern("dd-MM-yyyy HH:mm:ss")
            .preferNullOverDefault(true)
            .build();

        final List<WriteEntity> actual = Poiji.fromExcel(new File(path), WriteEntity.class, options);
        actual.forEach(writeEntity -> writeEntity.setUnknown(new HashMap<>()));
        assertThat(actual.toString(), equalTo(expected.toString()));
        final Map<String, Exception> exceptions1 = actual.get(0).getExceptions();
        assertThat(exceptions1.size(), equalTo(9));
        assertThat(exceptions1.get("LocalDateTime").getMessage(), equalTo("Text '01/02/2020 12:00:0d' could not be parsed at index 17"));
        assertThat(exceptions1.get("byte").getMessage(), equalTo("For input string: \"-1e\""));
        assertThat(exceptions1.get("Byte").getMessage(), equalTo("Value out of range. Value:\"-2224\" Radix:10"));
        assertThat(exceptions1.get("[0]").getMessage(), equalTo("1l"));
        assertThat(exceptions1.get("float").getMessage(), equalTo("20.9f"));
        assertThat(exceptions1.get("LocalDate").getMessage(), equalTo("Text '01/0e2/2020' could not be parsed at index 4"));
        assertThat(exceptions1.get("Date").getMessage(), equalTo("Unparseable date: \"01/15/197s0 09:56:07\""));
        assertThat(exceptions1.get("Short").getMessage(), equalTo("For input string: \"-422s\""));
        assertThat(exceptions1.get("BigDecimal").getMessage(), equalTo("123.3456e2"));

    }

}
