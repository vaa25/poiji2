package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.CsvRules;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import org.apache.poi.util.IOUtils;
import org.junit.Test;

import static org.hamcrest.CoreMatchers.equalTo;
import static org.junit.Assert.assertThat;

public class WriteCsvRulesTest {

    @Test
    public void write() throws IOException {
        final List<CsvRules> rules = new ArrayList<>();
        final CsvRules entity = new CsvRules()
            .setNumber("48")
            .setQuoted("boyet.com")
            .setWithDelimiter("Saturday, April 23, 2005")
            .setWithQuote("Mack \"The Knife\"")
            .setWithQuoteInQuoted("Mack \"The Knife\"");
        rules.add(entity);
        final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder
            .settings()
            .csvDelimiter(',')
            .preferNullOverDefault(true)
            .build();
        final PipedInputStream inputStream = new PipedInputStream();
        final OutputStream outputStream = new PipedOutputStream(inputStream);
        Poiji.toExcel(outputStream, PoijiExcelType.CSV, CsvRules.class, rules, options);
        final byte[] actual = IOUtils.toByteArray(inputStream);
        final byte[] expected = Files.readAllBytes(Paths.get("src/test/resources/writeRules.csv"));
        assertThat(actual, equalTo(expected));

    }

}
