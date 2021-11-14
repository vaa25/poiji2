package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.byname.PersonByNameWithMandatoryColumn;
import com.poiji.deserialize.model.byname.PersonByNameWithMissingColumn;
import com.poiji.exception.HeaderMissingException;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

@RunWith(Parameterized.class)
public class MandatoryNamedColumnsTest {

    private String path;

    public MandatoryNamedColumnsTest(String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> queries() {
        return Arrays.asList(
            "src/test/resources/person.xlsx",
            "src/test/resources/person.xls",
            "src/test/resources/person.csv"
        );
    }

    @Test
    public void testExcelSuccess() {
        Poiji.fromExcel(new File(path), PersonByNameWithMissingColumn.class, PoijiOptions.PoijiOptionsBuilder
            .settings()
            .namedHeaderMandatory(false)
            .build());
    }

    @Test(expected = HeaderMissingException.class)
    public void testExcelFail() {

        Poiji.fromExcel(new File(path), PersonByNameWithMissingColumn.class, PoijiOptions.PoijiOptionsBuilder
            .settings()
            .namedHeaderMandatory(true)
            .build());
    }

    @Test(expected = HeaderMissingException.class)
    public void testExcelMandatoryColumnFail() {

        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().namedHeaderMandatory(false).build();
        Poiji.fromExcel(new File(path), PersonByNameWithMandatoryColumn.class, options);
    }
}
