package com.poiji.save;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

import static java.nio.file.StandardOpenOption.APPEND;
import static java.util.Collections.singleton;

public final class CsvFileWriter implements CsvWriter {

    private final File file;

    public CsvFileWriter(final File file) {
        this.file = file;
    }

    @Override
    public void writeHeader(final String headers) {
        try {
            Files.write(file.toPath(), singleton(headers));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeRow(final String row) {
        try {
            Files.write(file.toPath(), singleton(row), APPEND);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {

    }

}
