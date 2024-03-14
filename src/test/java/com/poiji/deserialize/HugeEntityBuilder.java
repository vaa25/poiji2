package com.poiji.deserialize;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class HugeEntityBuilder {

    public static void main(String[] args) throws IOException {

//        final int totalFields = SpreadsheetVersion.EXCEL2007.getLastColumnIndex();
        final int totalFields = 50;
        final StringBuilder body = new StringBuilder("package com.poiji.deserialize.model;\n" +
                "\n" +
                "import com.poiji.annotation.ExcelCell;\n" +
                "import com.poiji.annotation.ExcelSheet;\n" +
                "\n" +
                "@ExcelSheet(\"test\")\n" +
                "public class HugeEntity {\n" +
                "public int totalFields = ")
                .append(totalFields)
                .append(";\n");

        for (int i = 0; i < totalFields; i++) {
            body.append(String.format("@ExcelCell(%d) public String field%d;%n", i, i));

        }

        body.append("}\n");

        System.out.println(body);
        final Path path = Paths.get(System.getProperty("user.dir") + "/src/test/java/com/poiji/deserialize/model/HugeEntity.java");
        Files.deleteIfExists(path);
        Files.createFile(path);
        Files.write(path, body.toString().getBytes(StandardCharsets.UTF_8));
    }
}
