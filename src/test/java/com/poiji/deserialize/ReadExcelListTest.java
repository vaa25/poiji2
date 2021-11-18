package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.ListElement;
import com.poiji.deserialize.model.ListEntity;
import com.poiji.option.PoijiOptions;
import java.io.File;
import java.io.FileNotFoundException;
import java.util.List;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import static com.poiji.deserialize.model.ListElement.Gender.female;
import static com.poiji.deserialize.model.ListElement.Gender.male;
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
        final ListElement person11 = new ListElement().setRow(2).setName("test").setGender(male).setAge(10);
        final ListElement person12 = new ListElement().setRow(2).setName("gogo").setGender(female).setAge(20);
        final ListElement person21 = new ListElement().setRow(3).setName("abc").setGender(male).setAge(30);
        final ListElement person22 = new ListElement().setRow(3).setName("vivi").setGender(female).setAge(40);
        final ListElement person23 = new ListElement().setRow(3).setName("vava").setGender(female).setAge(40);
        final List<ListEntity> expected = asList(
            new ListEntity().setElements(asList(person11, person12)).setData(1),
            new ListEntity().setElements(asList(person21, person22, person23)).setData(2)
        );

        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerCount(2).preferNullOverDefault(true).build();
        final List<ListEntity> read = Poiji.fromExcel(new File(path), ListEntity.class, options);
        assertThat(read, equalTo(expected));

    }

}
