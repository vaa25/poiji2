package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.CsvRules;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.List;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class ReadCsvRulesTest {

    @Test
    public void read() throws FileNotFoundException {
        final List<CsvRules> expected = new ArrayList<>();
        final CsvRules entity = new CsvRules()
            .setNumber("48")
            .setQuoted("boyet.com")
            .setWithDelimiter("Saturday, April 23, 2005")
            .setWithQuote("Mack \"The Knife\"")
            .setWithQuoteInQuoted("Mack \"The Knife\"");
        expected.add(entity);
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
            .settings()
            .csvDelimiter(',')
            .preferNullOverDefault(true)
            .build();
        final FileInputStream fileInputStream = new FileInputStream("src/test/resources/readRules.csv");
        final List<CsvRules> read = Poiji.fromExcel(fileInputStream, PoijiExcelType.CSV, CsvRules.class, options);
        assertThat(read, equalTo(expected));

    }

}
