package com.poiji.save;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;

public final class CsvOutputStreamWriter implements CsvWriter {

    private final BufferedWriter outputStream;

    public CsvOutputStreamWriter(final OutputStream outputStream) {
        this.outputStream = new BufferedWriter(new OutputStreamWriter(outputStream));
    }

    @Override
    public void writeHeader(final String headers) {
        try {
            outputStream.write(headers);
            outputStream.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void writeRow(final String row) {
        try {
            outputStream.write(row);
            outputStream.newLine();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void close() {
        try {
            outputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


}
