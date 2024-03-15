package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;

import java.util.Objects;

public final class ListElementImmutable {

    @ExcelRow
    private final int row;
    @ExcelCellName("Name")
    private final String name;
    @ExcelCellName("Gender")
    private final Gender gender;
    @ExcelCellName("Age")
    private final Integer age;

    public ListElementImmutable(int row, String name, Gender gender, Integer age) {
        this.row = row;
        this.name = name;
        this.gender = gender;
        this.age = age;
    }

    @Override
    public boolean equals(final Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        final ListElementImmutable that = (ListElementImmutable) o;
        return row == that.row && Objects.equals(name, that.name) && Objects.equals(gender, that.gender) &&
            Objects.equals(age, that.age);
    }

    @Override
    public int hashCode() {
        return Objects.hash(row, name, gender, age);
    }

    @Override
    public String toString() {
        return "ListElement{" + "row=" + row + ", name='" + name + '\'' + ", gender='" + gender + '\'' + ", age='" +
            age + '\'' + '}';
    }

    public enum Gender {

        male, female

    }
}
