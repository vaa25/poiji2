package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.byname.OrganisationByName;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.util.Arrays;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class MissingRowIndexNullLineTest {

    private final String path;

    public MissingRowIndexNullLineTest(final String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> excel() {
        return Arrays.asList(
            "src/test/resources/missing-row-2.xlsx", "src/test/resources/missing-row-2.xls",
            "src/test/resources/missing-row-2.csv"
        );
    }

    @Test
    public void nullLineTest() {
        List<OrganisationByName> organisations = Poiji.fromExcel(
                new File(path),
                OrganisationByName.class,
                PoijiOptions.PoijiOptionsBuilder.settings()
                        .sheetName("Organisation")
                        .build()
        );

        assertThat(organisations, notNullValue());
        assertThat(organisations.size(), is(4));
        assertThat(organisations.stream().map(OrganisationByName::getRowIndex).min(Integer::compareTo).get(), is(2));
        assertThat(organisations.stream().map(OrganisationByName::getRowIndex).max(Integer::compareTo).get(), is(5));
    }
}
