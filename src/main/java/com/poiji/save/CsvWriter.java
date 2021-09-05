package com.poiji.save;

public interface CsvWriter {
    void writeHeader(String headers);

    void writeRow(String row);

    void close();
}
