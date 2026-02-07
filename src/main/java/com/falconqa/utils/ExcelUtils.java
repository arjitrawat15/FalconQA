package com.falconqa.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * ExcelUtils - Utility class for reading and writing Excel files
 * Supports .xlsx format using Apache POI
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class ExcelUtils {
    
    private static final Logger logger = LogManager.getLogger(ExcelUtils.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    
    private String excelFilePath;
    private Workbook workbook;
    private Sheet sheet;
    
    /**
     * Constructor - Opens Excel file
     * 
     * @param excelFilePath Path to Excel file
     * @param sheetName Name of the sheet to read
     */
    public ExcelUtils(String excelFilePath, String sheetName) {
        this.excelFilePath = excelFilePath;
        try {
            FileInputStream fis = new FileInputStream(excelFilePath);
            workbook = new XSSFWorkbook(fis);
            sheet = workbook.getSheet(sheetName);
            
            if (sheet == null) {
                throw new IllegalArgumentException("Sheet '" + sheetName + "' not found in Excel file");
            }
            
            logger.info("Excel file opened successfully: {}, Sheet: {}", excelFilePath, sheetName);
            
        } catch (IOException e) {
            logger.error("Failed to open Excel file: {}", excelFilePath, e);
            throw new RuntimeException("Excel file not found or cannot be opened: " + excelFilePath, e);
        }
    }
    
    /**
     * Get row count (excluding header)
     * 
     * @return Number of data rows
     */
    public int getRowCount() {
        int rowCount = sheet.getPhysicalNumberOfRows() - 1; // Exclude header
        logger.debug("Row count: {}", rowCount);
        return rowCount;
    }
    
    /**
     * Get column count
     * 
     * @return Number of columns
     */
    public int getColumnCount() {
        if (sheet.getPhysicalNumberOfRows() == 0) {
            return 0;
        }
        int colCount = sheet.getRow(0).getPhysicalNumberOfCells();
        logger.debug("Column count: {}", colCount);
        return colCount;
    }
    
    /**
     * Get cell data as String
     * 
     * @param rowNum Row number (0-based, where 0 is header)
     * @param colNum Column number (0-based)
     * @return Cell value as String
     */
    public String getCellData(int rowNum, int colNum) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                logger.warn("Row {} is null", rowNum);
                return "";
            }
            
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                logger.warn("Cell [{}, {}] is null", rowNum, colNum);
                return "";
            }
            
            String cellValue = getCellValueAsString(cell);
            logger.debug("Cell [{}, {}] value: {}", rowNum, colNum, cellValue);
            return cellValue;
            
        } catch (Exception e) {
            logger.error("Error reading cell [{}, {}]", rowNum, colNum, e);
            return "";
        }
    }
    
    /**
     * Get cell data by column name
     * 
     * @param rowNum Row number (1-based for data rows, header is row 0)
     * @param columnName Column header name
     * @return Cell value as String
     */
    public String getCellData(int rowNum, String columnName) {
        int colNum = getColumnNumber(columnName);
        return getCellData(rowNum, colNum);
    }
    
    /**
     * Convert Cell to String value (handles all cell types)
     * 
     * @param cell Cell object
     * @return String value
     */
    private String getCellValueAsString(Cell cell) {
        switch (cell.getCellType()) {
            case STRING:
                return cell.getStringCellValue().trim();
                
            case NUMERIC:
                if (DateUtil.isCellDateFormatted(cell)) {
                    return cell.getDateCellValue().toString();
                } else {
                    // Handle both integers and decimals
                    double numericValue = cell.getNumericCellValue();
                    if (numericValue == (long) numericValue) {
                        return String.valueOf((long) numericValue);
                    } else {
                        return String.valueOf(numericValue);
                    }
                }
                
            case BOOLEAN:
                return String.valueOf(cell.getBooleanCellValue());
                
            case FORMULA:
                return cell.getCellFormula();
                
            case BLANK:
                return "";
                
            default:
                return "";
        }
    }
    
    /**
     * Get column number by column name (header)
     * 
     * @param columnName Column header name
     * @return Column number (0-based)
     */
    private int getColumnNumber(String columnName) {
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            throw new IllegalArgumentException("Header row not found");
        }
        
        for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
            Cell cell = headerRow.getCell(i);
            if (cell != null && cell.getStringCellValue().trim().equalsIgnoreCase(columnName.trim())) {
                return i;
            }
        }
        
        throw new IllegalArgumentException("Column '" + columnName + "' not found in header");
    }
    
    /**
     * Get all test data as 2D Object array (for TestNG DataProvider)
     * 
     * @return 2D Object array with test data
     */
    public Object[][] getTestData() {
        int rowCount = getRowCount();
        int colCount = getColumnCount();
        
        Object[][] data = new Object[rowCount][colCount];
        
        for (int i = 0; i < rowCount; i++) {
            for (int j = 0; j < colCount; j++) {
                data[i][j] = getCellData(i + 1, j); // +1 to skip header
            }
        }
        
        logger.info("Retrieved test data: {} rows x {} columns", rowCount, colCount);
        return data;
    }
    
    /**
     * Get test data as List of Maps (column name -> value)
     * Useful for complex test scenarios
     * 
     * @return List of Maps containing test data
     */
    public List<Map<String, String>> getTestDataAsMapList() {
        List<Map<String, String>> testDataList = new ArrayList<>();
        
        // Get header row
        Row headerRow = sheet.getRow(0);
        if (headerRow == null) {
            logger.error("Header row not found");
            return testDataList;
        }
        
        // Get column names from header
        List<String> columnNames = new ArrayList<>();
        for (Cell cell : headerRow) {
            columnNames.add(cell.getStringCellValue().trim());
        }
        
        // Read each data row
        int rowCount = getRowCount();
        for (int i = 1; i <= rowCount; i++) { // Start from 1 (skip header)
            Map<String, String> rowData = new HashMap<>();
            
            for (int j = 0; j < columnNames.size(); j++) {
                String columnName = columnNames.get(j);
                String cellValue = getCellData(i, j);
                rowData.put(columnName, cellValue);
            }
            
            testDataList.add(rowData);
        }
        
        logger.info("Retrieved test data as map list: {} rows", testDataList.size());
        return testDataList;
    }
    
    /**
     * Get specific rows based on filter condition
     * Example: Get all rows where "TestCase" column = "TC001"
     * 
     * @param filterColumn Column name to filter on
     * @param filterValue Value to match
     * @return List of matching rows as Maps
     */
    public List<Map<String, String>> getFilteredTestData(String filterColumn, String filterValue) {
        List<Map<String, String>> allData = getTestDataAsMapList();
        List<Map<String, String>> filteredData = new ArrayList<>();
        
        for (Map<String, String> row : allData) {
            if (row.get(filterColumn) != null && row.get(filterColumn).equals(filterValue)) {
                filteredData.add(row);
            }
        }
        
        logger.info("Filtered test data: {} rows (filter: {}={})", 
                   filteredData.size(), filterColumn, filterValue);
        return filteredData;
    }
    
    /**
     * Set cell data (for writing to Excel)
     * 
     * @param rowNum Row number
     * @param colNum Column number
     * @param value Value to write
     */
    public void setCellData(int rowNum, int colNum, String value) {
        try {
            Row row = sheet.getRow(rowNum);
            if (row == null) {
                row = sheet.createRow(rowNum);
            }
            
            Cell cell = row.getCell(colNum);
            if (cell == null) {
                cell = row.createCell(colNum);
            }
            
            cell.setCellValue(value);
            logger.debug("Set cell [{}, {}] to: {}", rowNum, colNum, value);
            
        } catch (Exception e) {
            logger.error("Error writing to cell [{}, {}]", rowNum, colNum, e);
        }
    }
    
    /**
     * Write test results back to Excel
     * 
     * @param rowNum Row number
     * @param result Test result (PASS/FAIL)
     */
    public void writeTestResult(int rowNum, String result) {
        try {
            // Check if "Result" column exists, if not create it
            Row headerRow = sheet.getRow(0);
            int resultColNum = -1;
            
            for (int i = 0; i < headerRow.getPhysicalNumberOfCells(); i++) {
                if (headerRow.getCell(i).getStringCellValue().equals("Result")) {
                    resultColNum = i;
                    break;
                }
            }
            
            if (resultColNum == -1) {
                resultColNum = headerRow.getPhysicalNumberOfCells();
                headerRow.createCell(resultColNum).setCellValue("Result");
            }
            
            setCellData(rowNum, resultColNum, result);
            logger.info("Test result written: Row {}, Result: {}", rowNum, result);
            
        } catch (Exception e) {
            logger.error("Error writing test result to row {}", rowNum, e);
        }
    }
    
    /**
     * Save and close the Excel file
     */
    public void saveAndClose() {
        try {
            FileOutputStream fos = new FileOutputStream(excelFilePath);
            workbook.write(fos);
            fos.close();
            workbook.close();
            logger.info("Excel file saved and closed: {}", excelFilePath);
            
        } catch (IOException e) {
            logger.error("Failed to save Excel file: {}", excelFilePath, e);
        }
    }
    
    /**
     * Close the Excel file without saving
     */
    public void close() {
        try {
            if (workbook != null) {
                workbook.close();
                logger.info("Excel file closed: {}", excelFilePath);
            }
        } catch (IOException e) {
            logger.error("Failed to close Excel file: {}", excelFilePath, e);
        }
    }
}
