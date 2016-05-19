package com.huotu.agento2o.common.util;

import com.huotu.agento2o.common.SysConstant;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;
import org.apache.poi.hssf.usermodel.HSSFCell;
import org.apache.poi.hssf.usermodel.HSSFRow;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by allan on 3/24/16.
 */
public class ExcelHelper {
    /**
     * 创建一个excel文档
     *
     * @param sheetName   sheet的名称
     * @param thCells     标题
     * @param rowAndCells 行和列信息 {@link CellDesc}
     * @return
     */
    public static HSSFWorkbook createWorkbook(
            String sheetName,
            String[] thCells,
            List<List<CellDesc>> rowAndCells
    ) {
        HSSFWorkbook workbook = new HSSFWorkbook();
        HSSFSheet sheet = workbook.createSheet(sheetName);
        //创建表头
        HSSFRow thRow = sheet.createRow(0);
        for (int i = 0; i < thCells.length; i++) {
            thRow.createCell(i).setCellValue(thCells[i]);
        }
        //创建表单
        for (int i = 0; i < rowAndCells.size(); i++) {
            HSSFRow row = sheet.createRow(i + 1);
            for (int j = 0; j < rowAndCells.get(i).size(); j++) {
                CellDesc cellDesc = rowAndCells.get(i).get(j);
                HSSFCell cell = row.createCell(j);
                cell.setCellType(cellDesc.cellType);
                cell.setCellValue(String.valueOf(cellDesc.getValue()));
            }
        }
        return workbook;
    }

    /**
     * 读取excel文档
     *
     * @param workbook
     * @param rowStartWith  起始行数
     * @param cellStartWith 起始列数
     * @return
     */
    public static List<List<CellDesc>> readWorkbook(
            HSSFWorkbook workbook,
            int rowStartWith,
            int cellNum,
            int cellStartWith
    ) {
        List<List<CellDesc>> rowAndCells = new ArrayList<>();
        HSSFSheet sheet = workbook.getSheet(SysConstant.ORDER_BATCH_DELIVER_SHEET_NAME);
        int physicalRowNum = sheet.getPhysicalNumberOfRows();
        for (int i = rowStartWith; i < physicalRowNum; i++) {
            List<CellDesc> cellDescList = new ArrayList<>();
            HSSFRow row = sheet.getRow(i);
            for (int j = cellStartWith; j < cellNum; j++) {
                HSSFCell cell = row.getCell(j);
                String value = cell == null ? "" : getValue(cell);
                cellDescList.add(asCell(value));
            }
            rowAndCells.add(cellDescList);
        }
        return rowAndCells;
    }

    /**
     * 得到Excel表中的值
     *
     * @param hssfCell Excel中的每一个格子
     * @return Excel中每一个格子中的值
     */
    public static String getValue(Cell hssfCell) {
        if (hssfCell.getCellType() == hssfCell.CELL_TYPE_BOOLEAN) {
            // 返回布尔类型的值
            return String.valueOf(hssfCell.getBooleanCellValue());
        } else if (hssfCell.getCellType() == hssfCell.CELL_TYPE_NUMERIC) {
            // 返回数值类型的值
            return String.valueOf(hssfCell.getNumericCellValue());
        } else {
            // 返回字符串类型的值
            return String.valueOf(hssfCell.getStringCellValue());
        }
    }


    public static CellDesc asCell(Object value, int cellType) {
        return new CellDesc(value, cellType);
    }

    public static CellDesc asCell(Object value) {
        return new CellDesc(value, Cell.CELL_TYPE_STRING);
    }

    @Setter
    @Getter
    @AllArgsConstructor
    public static class CellDesc {
        private Object value;
        private int cellType;
    }
}
