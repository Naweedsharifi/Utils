package com.wbg.selenium.qa.configReader;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.DataFormatter;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;

public class MapDataProvider {

	private static String inputFile = System.getProperty("file");

	private static final String FILE = "src/test/resources/TSRTestData.xlsx";

	private static final DataFormatter dataFormatter = new DataFormatter();

	/*
	 * @description : Method to get data from file
	 * 
	 * @param : sheetName
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static List<Map<String, String>> getData(String sheetName) {
		List<Map<String, String>> results = new ArrayList<>();
		try (Workbook workbook = WorkbookFactory.create(new File(FILE));) {

			/* Get the sheet */
			Sheet sheet = workbook.getSheet(sheetName);
			Iterable<Row> rows = sheet::rowIterator;

			boolean header = true;
			List<String> keys = null;
			for (Row row : rows) {
				List<String> values = getValuesInEachRow(row);
				if (header) {
					header = false;
					keys = values;
					continue;
				}
				results.add(transform(keys, values));
			}
		} catch (Exception e) {
			System.out.println(e.getMessage());
		}
		return results;
	}

	/*
	 * @description : Method to transform data
	 * 
	 * @param : names,values
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	private static Map<String, String> transform(List<String> names, List<String> values) {
		/* code to transform data */
		Map<String, String> results = new HashMap<>();
		for (int i = 0; i < names.size(); i++) {
			String key = names.get(i);
			String value = values.get(i);
			results.put(key, value);
		}
		return results;
	}

	/*
	 * @description : Method to get value from each row
	 * 
	 * @param : row
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	private static List<String> getValuesInEachRow(Row row) {
		List<String> data = new ArrayList<>();

		/* Get each row data */
		Iterable<Cell> columns = row::cellIterator;
		for (Cell column : columns) {
			data.add(dataFormatter.formatCellValue(column));
		}
		return data;
	}

	/*
	 * @description : Method to get row value in string
	 * 
	 * @param : row
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public List<String> rowToString(Row row) {
		Iterator<Cell> cells = row.cellIterator();
		String[] data = new String[row.getLastCellNum()];

		int previousCell = 0;

		/* Get cell data */
		Cell cell = cells.next();
		int currentCell = cell.getColumnIndex();

		while (true) {
			if (previousCell == currentCell) {
				switch (cell.getCellType()) {
				case NUMERIC:
					data[previousCell] = cell.getNumericCellValue() + "";
					break;
				case STRING:
					data[previousCell] = cell.getStringCellValue();
					break;
				case BLANK:
					break;
				case BOOLEAN:
					break;
				case ERROR:
					break;
				case FORMULA:
					break;
				case _NONE:
					break;
				default:
					break;

				}
				if (cells.hasNext()) {
					cell = cells.next();
					currentCell = cell.getColumnIndex();
				} else {
					break;
				}

			} else {
				data[previousCell] = "";
			}
			previousCell++;

		}
		return new ArrayList<String>(Arrays.asList(data));

	}

	/*
	 * @description : Method to get column name
	 * 
	 * @param : sheetName,a
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static int columnName(String sheetName, String a) throws IOException {

		int coefficient = 0;

		/* Code to get column name */
		FileInputStream inputStream = new FileInputStream(FILE);
		try (Workbook wb = WorkbookFactory.create(inputStream);) {
			Sheet sh = wb.getSheet(sheetName);
			Row row = sh.getRow(0);
			int cellNum = row.getPhysicalNumberOfCells();
			for (int i = 0; i < cellNum; i++) {
				if ((row.getCell(i).toString()).equals(a)) {
					coefficient = i;
				}
			}
		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}

		return coefficient;
	}

	/*
	 * @description : Method to update value in workbook
	 * 
	 * @param : sheetName
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void updateValueInWorkbook(String sheetName, int rowVal, int colVal, String value)
			throws IOException {
		/* Obtain a workbook from the excel file */
		try (Workbook workbook = new XSSFWorkbook(new FileInputStream(FILE));) {

			/* Get Sheet by sheet name */
			Sheet sheet = workbook.getSheet(sheetName);

			/* Get Row at row value */
			Row row = sheet.getRow(rowVal);

			/* Get the Cell at index 2 from the above row */
			Cell cell = row.getCell(colVal);

			/* Create the cell if it doesn't exist */
			if (cell == null)
				cell = row.createCell(colVal);

			/* Update the cell's value */
			cell.setCellValue(value);

			/* Write the output to the file */
			FileOutputStream fileOut = new FileOutputStream(FILE);
			workbook.write(fileOut);
			fileOut.close();

		} catch (Exception e) {
			System.out.println(e.getStackTrace());
		}
	}
}
