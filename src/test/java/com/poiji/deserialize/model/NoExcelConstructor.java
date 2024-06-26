package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelConstructor;

import java.util.Objects;

public final class NoExcelConstructor {

    @ExcelCell(0)
    private final Integer data;

    public NoExcelConstructor(Integer data, String arg2) {
        throw new RuntimeException("Wrong constructor selected");
    }

    public NoExcelConstructor(Integer data) {
        this.data = data;
    }

    public NoExcelConstructor() {
        throw new RuntimeException("Wrong constructor selected");
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final NoExcelConstructor that = (NoExcelConstructor) o;
        return Objects.equals(data, that.data);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data);
    }

    @Override
    public String toString() {
        return "ListEntity{" + "data=" + data + '}';
    }
}
