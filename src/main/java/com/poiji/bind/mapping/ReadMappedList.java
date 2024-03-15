package com.poiji.bind.mapping;

import com.poiji.annotation.ExcelList;
import com.poiji.option.PoijiOptions;

import java.util.List;

public final class ReadMappedList {

    private final ExcelList excelList;
    private final ReadMappedFields readMappedFields;

    public ReadMappedList(final ExcelList excelList, final Class<?> entity, final PoijiOptions options) {
        this.excelList = excelList;
        this.readMappedFields = new ReadMappedFields(entity, options).parseEntity();
    }

    public boolean parseColumnName(final int columnOrder, final String columnName) {
        if (columnOrder >= excelList.listStart() && columnOrder <= excelList.listEnd()){
            readMappedFields.parseColumnName(columnOrder - excelList.listStart(), columnName);
            return true;
        } else {
            return false;
        }
    }

    public void validateMandatoryNameColumns(){
        readMappedFields.validateMandatoryNameColumns();
    }

    public void setCellInList(final int row, final int column, final String content, final List<Data> listFieldData) {
        final int listStart = excelList.listStart();
        final int listEnd = excelList.listEnd();
        if (column >= listStart && column <= listEnd){
            final int elementSize = excelList.elementSize();
            final int index = (column - listStart) / elementSize;
            while (listFieldData.size() <= index){
                listFieldData.add(readMappedFields.createInstanceData());
            }
            final int firstElementColumn = (column - listStart) % elementSize;
            readMappedFields.setCellInData(row, firstElementColumn, content, listFieldData.get(index));
        }
    }
}
