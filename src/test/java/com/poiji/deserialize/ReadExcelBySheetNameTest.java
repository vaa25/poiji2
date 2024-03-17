package com.poiji.deserialize;

import com.poiji.bind.Poiji;
import com.poiji.deserialize.model.byid.Calculation;
import com.poiji.option.PoijiOptions;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.io.File;
import java.util.List;

import static java.util.Arrays.asList;
import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

@RunWith(Parameterized.class)
public class ReadExcelBySheetNameTest {

	private String path;

	public ReadExcelBySheetNameTest(String path) {
		this.path = path;
	}

	@Parameterized.Parameters(name = "{index}: ({0})={1}")
	public static Iterable<Object[]> queries() {
		return asList(new Object[][]{
				{"src/test/resources/calculations.xlsx"},
				{"src/test/resources/calculations.xls"},
				{"src/test/resources/SI calculations.csv"},
		});
	}

	@Test
	public void shouldReadExcelBySheetName() {
		PoijiOptions options = PoijiOptions.PoijiOptionsBuilder.settings().sheetName("SI calculations").build();

		List<Calculation> calculations = Poiji.fromExcel(new File(path), Calculation.class, options);

		assertThat(calculations.get(0).getIs(), is("ABXXXXXXXXXX"));
		assertThat(calculations.get(1).getIs(), is("BCXXXXXXXXXX"));
		assertThat(calculations.get(2).getIs(), is("CDXXXXXXXXXX"));
		assertThat(calculations.get(3).getIs(), is("DEXXXXXXXXXX"));
	}

}
