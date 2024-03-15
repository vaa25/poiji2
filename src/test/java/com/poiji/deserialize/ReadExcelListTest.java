package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.util.ImmutableInstanceRegistrar;
import com.poiji.deserialize.model.ListElement;
import com.poiji.deserialize.model.ListElementImmutable;
import com.poiji.deserialize.model.ListEntity;
import com.poiji.deserialize.model.ListImmutable;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ReadExcelListTest {

    private final String path;

    public ReadExcelListTest(final String path) {
        this.path = path;
    }

    @Parameterized.Parameters
    public static List<String> excel() {
        return asList(
            "src/test/resources/excel-list.xlsx",
            "src/test/resources/excel-list.xls",
            "src/test/resources/excel-list.csv"
        );
    }

    @Test
    public void read() throws FileNotFoundException {
        final ListElement person11 = new ListElement().setRow(2).setName("test").setGender(ListElement.Gender.male).setAge(10);
        final ListElement person12 = new ListElement().setRow(2).setName("gogo").setGender(ListElement.Gender.female).setAge(20);
        final ListElement person21 = new ListElement().setRow(3).setName("abc").setGender(ListElement.Gender.male).setAge(30);
        final ListElement person22 = new ListElement().setRow(3).setName("vivi").setGender(ListElement.Gender.female).setAge(40);
        final ListElement person23 = new ListElement().setRow(3).setName("vava").setGender(ListElement.Gender.female).setAge(40);
        final List<ListEntity> expected = asList(
            new ListEntity().setElements(asList(person11, person12)).setData(1),
            new ListEntity().setElements(asList(person21, person22, person23)).setData(2)
        );

        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerCount(2).preferNullOverDefault(true).build();
        final List<ListEntity> read = Poiji.fromExcel(new File(path), ListEntity.class, options);
        assertThat(read, equalTo(expected));
    }

    @Test
    public void readImmutable() {
        ImmutableInstanceRegistrar.register(ListElementImmutable.Gender.class, ListElementImmutable.Gender.male);
        final ListElementImmutable person11 = new ListElementImmutable(2, "test", ListElementImmutable.Gender.male, 10);
        final ListElementImmutable person12 = new ListElementImmutable(2, "gogo", ListElementImmutable.Gender.female,20);
        final ListElementImmutable person21 = new ListElementImmutable(3, "abc", ListElementImmutable.Gender.male, 30);
        final ListElementImmutable person22 = new ListElementImmutable(3, "vivi", ListElementImmutable.Gender.female, 40);
        final ListElementImmutable person23 = new ListElementImmutable(3, "vava", ListElementImmutable.Gender.female, 40);
        final List<ListImmutable> expected = asList(
                new ListImmutable(1, asList(person11, person12)),
                new ListImmutable(2, asList(person21, person22, person23))
        );

        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerCount(2).preferNullOverDefault(true).build();
        final List<ListImmutable> read = Poiji.fromExcel(new File(path), ListImmutable.class, options);
        assertThat(read, equalTo(expected));
    }

}
