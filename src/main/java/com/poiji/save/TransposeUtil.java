package com.poiji.save;

import java.util.ArrayList;
import java.util.List;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.RichTextString;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

public final class TransposeUtil {

    static class CellModel {
        private int rowNum = -1;
        private int colNum = -1;
        private CellStyle cellStyle;
        private CellType cellType;
        private Object cellValue;

        public CellModel(final Cell cell) {
            if (cell != null) {
                this.rowNum = cell.getRowIndex();
                this.colNum = cell.getColumnIndex();
                this.cellStyle = cell.getCellStyle();
                this.cellType = cell.getCellType();
                switch (this.cellType) {
                    case BLANK:
                        break;
                    case BOOLEAN:
                        cellValue = cell.getBooleanCellValue();
                        break;
                    case ERROR:
                        cellValue = cell.getErrorCellValue();
                        break;
                    case FORMULA:
                        cellValue = cell.getCellFormula();
                        break;
                    case NUMERIC:
                        cellValue = cell.getNumericCellValue();
                        break;
                    case STRING:
                        cellValue = cell.getRichStringCellValue();
                        break;
                }
            }
        }

        public boolean isBlank() {
            return this.cellType == CellType._NONE && this.rowNum == -1 && this.colNum == -1;
        }

        public void insertInto(final Cell cell) {
            if (isBlank()) {
                return;
            }
            cell.setCellStyle(this.cellStyle);
            switch (this.cellType) {
                case BLANK:
                    break;
                case BOOLEAN:
                    cell.setCellValue((boolean) this.cellValue);
                    break;
                case ERROR:
                    cell.setCellErrorValue((byte) this.cellValue);
                    break;
                case FORMULA:
                    cell.setCellFormula((String) this.cellValue);
                    break;
                case NUMERIC:
                    cell.setCellValue((double) this.cellValue);
                    break;
                case STRING:
                    cell.setCellValue((RichTextString) this.cellValue);
                    break;
            }
        }

        public int getRowNum() {
            return rowNum;
        }

        public int getColNum() {
            return colNum;
        }

    }

    public static void transpose(final Workbook workbook) {
        final Sheet sheet = workbook.getSheetAt(0);

        int lastColumn = getLastColumn(sheet);
        int lastRow = sheet.getLastRowNum();


        final List<CellModel> allCells = new ArrayList<>();
        for (int rowNum = 0; rowNum <= lastRow; rowNum++) {
            final Row row = sheet.getRow(rowNum);
            if (row == null) {
                continue;
            }
            for (int columnNum = 0; columnNum < lastColumn; columnNum++) {
                final Cell cell = row.getCell(columnNum);
                if (cell!=null){
                    allCells.add(new CellModel(cell));
                }
            }
        }

        final String sheetName = sheet.getSheetName();
        final Sheet tSheet = workbook.createSheet(sheetName + "_transposed");
        for (final CellModel cellModel : allCells) {
            if (cellModel.isBlank()) {
                continue;
            }

            int tRow = cellModel.getColNum();

            Row row = tSheet.getRow(tRow);
            if (row == null) {
                row = tSheet.createRow(tRow);
            }

            cellModel.insertInto(row.createCell(cellModel.getRowNum()));
        }

        int pos = workbook.getSheetIndex(sheet);
        workbook.removeSheetAt(pos);
        workbook.setSheetOrder(tSheet.getSheetName(), pos);
        workbook.setSheetName(workbook.getSheetIndex(tSheet), sheetName);

    }

    private static int getLastColumn(final Sheet sheet) {
        int result = 0;
        for (final Row row : sheet) {
            if (result < row.getLastCellNum()) {
                result = row.getLastCellNum();
            }
        }
        return result;
    }

}
