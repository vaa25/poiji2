package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.IgnoreEntity;
import com.poiji.deserialize.model.IgnoreImmutable;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.util.ArrayList;
import java.util.List;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;

@RunWith(Parameterized.class)
public class IgnoreTest {

    private String path;

    public IgnoreTest(String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> excel() {
        return asList("src/test/resources/ignore.xlsx", "src/test/resources/ignore.xls", "src/test/resources/ignore.csv");
    }

    @Test
    public void caseInsensitiveColumnNames() {
        final List<IgnoreEntity> expected = new ArrayList<>();
        final IgnoreEntity writing = new IgnoreEntity()
            .setWriteText("test")
            .setPrimitiveLong(2L);
        expected.add(writing);
        expected.add(new IgnoreEntity());
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
            .settings()
            .datePattern("dd-MM-yyyy HH:mm:ss")
            .preferNullOverDefault(true)
            .build();
        Poiji.toExcel(new File(path), IgnoreEntity.class, expected, options);

        final List<IgnoreEntity> read = Poiji.fromExcel(new File(path), IgnoreEntity.class, options);
        final IgnoreEntity reading = new IgnoreEntity()
                .setReadText("test")
                .setPrimitiveLong(2L);
        expected.set(0, reading);
        Assert.assertThat(read.toString(), equalTo(expected.toString()));
    }

    @Test
    public void caseInsensitiveColumnNamesImmutable() {
        final List<IgnoreImmutable> expected = new ArrayList<>();
        final IgnoreImmutable entity = new IgnoreImmutable(null, 2L, "ignored", "test");
        expected.add(entity);
        expected.add(new IgnoreImmutable(null, 0L, null, null));
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
                .settings()
                .datePattern("dd-MM-yyyy HH:mm:ss")
                .preferNullOverDefault(true)
                .build();
        Poiji.toExcel(new File(path), IgnoreImmutable.class, expected, options);

        final List<IgnoreImmutable> read = Poiji.fromExcel(new File(path), IgnoreImmutable.class, options);
        final IgnoreImmutable reading = new IgnoreImmutable("test", 2L, null, null);
        expected.set(0, reading);
        Assert.assertThat(read.toString(), equalTo(expected.toString()));
    }

}
