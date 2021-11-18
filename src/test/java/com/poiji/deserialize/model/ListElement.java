package com.poiji.deserialize.model;

import com.poiji.annotation.ExcelCellName;
import com.poiji.annotation.ExcelRow;
import java.util.Objects;

public final class ListElement {

    @ExcelRow
    private int row;
    @ExcelCellName("Name")
    private String name;
    @ExcelCellName("Gender")
    private Gender gender;
    @ExcelCellName("Age")
    private Integer age;

    public ListElement setRow(final int row) {
        this.row = row;
        return this;
    }

    public ListElement setName(final String name) {
        this.name = name;
        return this;
    }

    public ListElement setGender(final Gender gender) {
        this.gender = gender;
        return this;
    }

    public ListElement setAge(final Integer age) {
        this.age = age;
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
        final ListElement that = (ListElement) o;
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
