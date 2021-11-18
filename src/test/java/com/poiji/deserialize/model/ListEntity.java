package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCell;
import com.poiji.annotation.ExcelList;
import java.util.List;
import java.util.Objects;

public final class ListEntity {

    @ExcelCell(0)
    private Integer data;
    @ExcelList(elementSize = 3, listStart = 1)
    private List<ListElement> elements;

    public ListEntity setData(final Integer data) {
        this.data = data;
        return this;
    }

    public ListEntity setElements(final List<ListElement> elements) {
        this.elements = elements;
        return this;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ListEntity that = (ListEntity) o;
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
