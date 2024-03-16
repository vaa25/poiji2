package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelConstructor;

import java.util.Objects;

public final class ManyExcelConstructor {

    @ExcelCell(0)
    private final Integer data;

    public ManyExcelConstructor(Integer data, String arg2) {
        throw new RuntimeException("Wrong constructor selected");
    }

    @ExcelConstructor
    public ManyExcelConstructor(Integer data) {
        this.data = data;
    }

    @ExcelConstructor
    public ManyExcelConstructor() {
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
        final ManyExcelConstructor that = (ManyExcelConstructor) o;
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
