package com.poiji.deserialize.model;

import com.poiji.annotation.*;

import java.util.Objects;
import java.util.StringJoiner;

@ExcelSheet("test")
public final class IgnoreImmutable {

    @ExcelCell(0)
    private final long primitiveLong;
    @ExcelCellName(value = "TexT", order = 5)
    @ExcelReadOnly
    private final String readText;
    @ExcelCellName(value = "TexT", order = 5)
    @ExcelWriteOnly
    private final String writeText;

    public IgnoreImmutable(String readText, long primitiveLong, String ignored, String writeText) {
        this.primitiveLong = primitiveLong;
        this.readText = readText;
        this.writeText = writeText;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final IgnoreImmutable that = (IgnoreImmutable) o;
        return primitiveLong == that.primitiveLong && Objects.equals(readText, that.readText) && Objects.equals(
            writeText,
            that.writeText
        );
    }

    @Override
    public int hashCode() {
        return Objects.hash(primitiveLong, readText, writeText);
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", IgnoreImmutable.class.getSimpleName() + "[", "]")
            .add("primitiveLong=" + primitiveLong)
            .add("readText='" + readText + "'")
            .add("writeText='" + writeText + "'")
            .toString();
    }
}
