package com.wbg.selenium.qa.configReader;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Properties;

import com.wbg.selenium.qa.manager.FileReaderManager;

/*
 * @description : Class to load and read xlsx file
 * 
 * @param : NA
 * 
 * @return : NA
 * 
 * @date : 28 Dec 2020
 * 
 * @author : Infosys Limited
 */
public class Xlsx_FileReader {
	public static Common_XlsxReader excelreader;
	public static Properties prop;
	public static String excelPath;
	public Xlsx_FileReader(){
		try {
			/*load properties */
		excelPath=FileReaderManager.getInstance().getConfigReader().getInputExcelPath();
		prop = new Properties();
		excelreader = new Common_XlsxReader(excelPath);
		FileInputStream ip = new FileInputStream(excelPath);
		prop.load(ip); 
	} catch (FileNotFoundException e) {
		e.printStackTrace();
	} catch (IOException e) {
		e.printStackTrace();
	}
}
}
