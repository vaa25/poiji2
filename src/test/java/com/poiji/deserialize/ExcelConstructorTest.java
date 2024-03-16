package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.*;
import com.poiji.exception.PoijiInstantiationException;
import com.poiji.option.PoijiOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.*;

@RunWith(Parameterized.class)
public class ExcelConstructorTest {

    private final String path;

    public ExcelConstructorTest(final String path) {
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
    public void selectConstructorWithAnnotation() {
        final List<OneExcelConstructor> expected = asList(
                new OneExcelConstructor(1),
                new OneExcelConstructor(2)
        );

        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerCount(2).preferNullOverDefault(true).build();
        final List<OneExcelConstructor> read = Poiji.fromExcel(new File(path), OneExcelConstructor.class, options);
        assertThat(read, equalTo(expected));
    }

    @Test
    public void trySelectConstructorWithoutAnnotation() {
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerCount(2).preferNullOverDefault(true).build();
        final PoijiInstantiationException exception = assertThrows(PoijiInstantiationException.class,
                () -> Poiji.fromExcel(new File(path), NoExcelConstructor.class, options));
        assertEquals("Several constructors were found in com.poiji.deserialize.model.NoExcelConstructor. Mark one of it with @ExcelConstructor please.", exception.getMessage());
    }

    @Test
    public void trySelectConstructorWithManyAnnotations() {
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().headerCount(2).preferNullOverDefault(true).build();
        final PoijiInstantiationException exception = assertThrows(PoijiInstantiationException.class,
                () -> Poiji.fromExcel(new File(path), ManyExcelConstructor.class, options));
        assertEquals("Several constructors are marked with @ExcelConstructor in com.poiji.deserialize.model.ManyExcelConstructor. Mark only one of it please.", exception.getMessage());
    }

}
