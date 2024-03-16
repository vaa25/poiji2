package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelList;

import java.beans.ConstructorProperties;
import java.util.List;
import java.util.Objects;

public final class ListConstructorProperties {

    @ExcelCell(0)
    private final Integer data;
    @ExcelList(elementSize = 3, listStart = 1)
    private final List<ListElementConstructorProperties> elements;

    @ConstructorProperties({"data", "elements"})
    public ListConstructorProperties(Integer data, List<ListElementConstructorProperties> elements) {
        this.data = data;
        this.elements = elements;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ListConstructorProperties that = (ListConstructorProperties) o;
        return Objects.equals(data, that.data) && Objects.equals(elements, that.elements);
    }

    @Override
    public int hashCode() {
        return Objects.hash(data, elements);
    }

    @Override
    public String toString() {
        return "ListEntity{" + "data=" + data + ", elements=" + elements + '}';
    }
}
