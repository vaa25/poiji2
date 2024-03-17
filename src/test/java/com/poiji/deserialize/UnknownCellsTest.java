package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.byid.OrgWithUnknownCells;
import com.poiji.deserialize.model.byname.OrgWithUnknownCellsByName;
import com.poiji.option.PoijiOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.MatcherAssert.assertThat;

@RunWith(Parameterized.class)
public class UnknownCellsTest {

    private String path;

    public UnknownCellsTest(String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> excel() {
        return Arrays.asList(
                "src/test/resources/unknown-cells.xlsx",
                "src/test/resources/unknown-cells.xls",
                "src/test/resources/unknown-cells.csv"
        );
    }

    @Test
    public void byNameFromTreeMap() {
        List<OrgWithUnknownCellsByName> organisations = Poiji.fromExcel(
                new File(path),
                OrgWithUnknownCellsByName.class,
                PoijiOptions.PoijiOptionsBuilder.settings()
                        .sheetName("Organisation")
                        .build()
        );

        assertThat(organisations, notNullValue());
        assertThat(organisations.size(), is(2));

        OrgWithUnknownCellsByName firstRow = organisations.stream()
                .filter(org -> org.getId().equals("CrEaTe"))
                .findFirst()
                .get();
        final Map<String, String> unknownCells1 = firstRow.getSortedUnknownCells();
        assertThat(unknownCells1.size(), is(4));

        assertRow1Values(unknownCells1);

        final Iterator<String> iterator1 = unknownCells1.keySet().iterator();
        assertThat(iterator1.next(), is("Region"));
        assertThat(iterator1.next(), is("UnknownName1"));
        assertThat(iterator1.next(), is("UnknownName2"));
        assertThat(iterator1.next(), is("UnknownName3"));

        OrgWithUnknownCellsByName secondRow = organisations.stream()
                .filter(org -> org.getId().equals("8d9e6430-8626-4556-8004-079085d2df2d"))
                .findFirst()
                .get();
        final Map<String, String> unknownCells2 = secondRow.getSortedUnknownCells();
        assertRow2Values(unknownCells2);

        final Iterator<String> iterator2 = unknownCells2.keySet().iterator();
        assertThat(iterator2.next(), is("Region"));
        assertThat(iterator2.next(), is("UnknownName1"));
        assertThat(iterator2.next(), is("UnknownName2"));
        assertThat(iterator2.next(), is("UnknownName3"));

    }

    @Test
    public void byNameFromMap() {
        List<OrgWithUnknownCellsByName> organisations = Poiji.fromExcel(
                new File(path),
                OrgWithUnknownCellsByName.class,
                PoijiOptions.PoijiOptionsBuilder.settings()
                        .sheetName("Organisation")
                        .build()
        );

        assertThat(organisations, notNullValue());
        assertThat(organisations.size(), is(2));

        OrgWithUnknownCellsByName firstRow = organisations.stream()
                .filter(org -> org.getId().equals("CrEaTe"))
                .findFirst()
                .get();
        final Map<String, String> unknownCells1 = firstRow.getLinkedUnknownCells();
        assertThat(unknownCells1.size(), is(4));

        assertRow1Values(unknownCells1);

        final Iterator<String> iterator1 = unknownCells1.keySet().iterator();
        assertThat(iterator1.next(), is("Region"));
        assertThat(iterator1.next(), is("UnknownName3"));
        assertThat(iterator1.next(), is("UnknownName1"));
        assertThat(iterator1.next(), is("UnknownName2"));

        OrgWithUnknownCellsByName secondRow = organisations.stream()
                .filter(org -> org.getId().equals("8d9e6430-8626-4556-8004-079085d2df2d"))
                .findFirst()
                .get();
        final Map<String, String> unknownCells2 = secondRow.getLinkedUnknownCells();
        assertRow2Values(unknownCells2);

        final Iterator<String> iterator2 = unknownCells2.keySet().iterator();
        assertThat(iterator2.next(), is("Region"));
        assertThat(iterator2.next(), is("UnknownName3"));
        assertThat(iterator2.next(), is("UnknownName1"));
        assertThat(iterator2.next(), is("UnknownName2"));

    }

    private void assertRow1Values(Map<String, String> unknownCells1) {
        assertThat(unknownCells1.get("Region"), is("EMEA"));
        assertThat(unknownCells1.get("UnknownName1"), is("UnknownValue11"));
        assertThat(unknownCells1.get("UnknownName2"), is("UnknownValue21"));
        assertThat(unknownCells1.get("UnknownName3"), is("UnknownValue31"));
    }

    private void assertRow2Values(Map<String, String> unknownCells2) {
        assertThat(unknownCells2.size(), is(4));
        assertThat(unknownCells2.get("Region"), is("NA"));
        assertThat(unknownCells2.get("UnknownName1"), is("UnknownValue12"));
        assertThat(unknownCells2.get("UnknownName2"), is("UnknownValue22"));
        assertThat(unknownCells2.get("UnknownName3"), is("UnknownValue32"));
    }

    @Test
    public void byIndex() {
        List<OrgWithUnknownCells> organisations = Poiji.fromExcel(
                new File(path),
                OrgWithUnknownCells.class,
                PoijiOptions.PoijiOptionsBuilder.settings()
                        .sheetName("Organisation")
                        .build()
        );

        assertThat(organisations, notNullValue());
        assertThat(organisations.size(), is(2));

        OrgWithUnknownCells firstRow = organisations.stream()
                .filter(org -> org.getId().equals("CrEaTe"))
                .findFirst()
                .get();
        assertThat(firstRow.getUnknownCells().size(), is(4));
        assertThat(firstRow.getUnknownCells().get("Region"), is("EMEA"));


        OrgWithUnknownCells secondRow = organisations.stream()
                .filter(org -> org.getId().equals("8d9e6430-8626-4556-8004-079085d2df2d"))
                .findFirst()
                .get();
        assertThat(secondRow.getUnknownCells().size(), is(4));
        assertThat(secondRow.getUnknownCells().get("Region"), is("NA"));
    }
}
