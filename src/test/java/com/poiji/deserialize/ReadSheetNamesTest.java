package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.exception.PoijiExcelType;
import com.poiji.option.PoijiOptions;
import org.junit.Test;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.util.Collections;
import java.util.List;

import static java.util.Arrays.asList;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNull;

public class ReadSheetNamesTest {

	@Test
	public void readSheetNamesFromExcelFormat() throws IOException {
        readFromExcelFormat(new File("src/test/resources/calculations.xlsx"));
        readFromExcelFormat(new File("src/test/resources/calculations.xls"));
	}

	private void readFromExcelFormat(File file) throws IOException {
		final List<String> expected = asList("Explanatory note", "SI calculations");

		assertEquals(expected, Poiji.fromExcel().withSource(file).readSheetNames());

		final InputStream inputStream = Files.newInputStream(file.toPath());
		final PoijiExcelType excelType = PoijiExcelType.fromFileName(file.getName());
		assertEquals(expected, Poiji.fromExcel().withSource(inputStream, excelType).readSheetNames());
	}

	@Test
	public void readSheetNamesFromCsvFile() {
		final List<String> expected = Collections.singletonList("SI calculations");

		final File file = new File("src/test/resources/SI calculations.csv");
		assertEquals(expected, Poiji.fromExcel().withSource(file).readSheetNames());
	}

	@Test
	public void readSheetNamesFromCsvStream() throws IOException {

        final File file = new File("src/test/resources/SI calculations.csv");
		final PoijiExcelType excelType = PoijiExcelType.CSV;

        assertEquals(Collections.<String>emptyList(), Poiji.fromExcel().withSource(Files.newInputStream(file.toPath()), excelType).readSheetNames());

		final PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().preferNullOverDefault(true).build();
		assertNull(Poiji.fromExcel().withSource(Files.newInputStream(file.toPath()), excelType).withOptions(options).readSheetNames());
	}

}
