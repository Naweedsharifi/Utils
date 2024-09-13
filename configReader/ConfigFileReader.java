package com.wbg.selenium.qa.configReader;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Properties;

import com.wbg.selenium.qa.enums.Browser;

public class ConfigFileReader {
	private static Properties properties;
	private static String propertyFilePath = "config/config.properties";

	/*
	 * @description : Class constructor to load properties
	 * 
	 * @param : query
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public ConfigFileReader() {
		BufferedReader reader;
		try {
			/* Read property file */
			reader = new BufferedReader(new FileReader(propertyFilePath));
			properties = new Properties();
			try {
				properties.load(reader);
				reader.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		} catch (FileNotFoundException e) {
			e.printStackTrace();
			throw new RuntimeException("properties file not found" + propertyFilePath);
		}
	}

	/*
	 * @description : Method to get driver path
	 * 
	 * @param : NA
	 * 
	 * @return : String
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public String getDriverPath() {
		/* code to get driver */
		String driverpath = properties.getProperty("driverpath");
		if (driverpath != null)
			return driverpath;
		else
			throw new RuntimeException("driverpath  not specified in the config file");
	}

	/*
	 * @description : Method to get browser
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public Browser getBrowser() {
		/* code to get browser */
		String BrowseName = System.getProperty("browser");
		if (BrowseName == null || BrowseName.equalsIgnoreCase("chrome"))
			return Browser.CHROME;
		else if (BrowseName.equalsIgnoreCase("firefox"))
			return Browser.FIREFOX;
		else if (BrowseName.equalsIgnoreCase("IE"))
			return Browser.IE;
		else
			throw new RuntimeException("browser name not specified in the config file");
	}

	/*
	 * @description : Method to get application url
	 * 
	 * @param : NA
	 * 
	 * @return : String type value
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public String getApplicationURL() {

		/* code to get application url */
		String getURL = properties.getProperty("URL");
		if (getURL != null)
			return getURL;
		else
			throw new RuntimeException("Application URL  not specified in the config file");
	}

	/*
	 * @description : Method to get resource path
	 * 
	 * @param : NA
	 * 
	 * @return : resource path
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public String getTestDataResourcePath() {
		/* code to get resource path */
		String testDataResourcePath = properties.getProperty("ResourcePath");
		if (testDataResourcePath != null)
			return testDataResourcePath;
		else
			throw new RuntimeException("testDataResourcePath  not specified in the config file");
	}

	/*
	 * @description : Method to get custom wait
	 * 
	 * @param : getImplicitWait
	 * 
	 * @return : custom wait of type long
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public long getCustomWait(String getImplicitWait) {

		/* code to get wait time */
		String getWait = properties.getProperty("ImplicitWait");
		if (getWait != null)
			return Long.parseLong(getWait);
		else
			throw new RuntimeException("Implicit wait values is  not specified in the config file");
	}

	/*
	 * @description : Method to config property vale
	 * 
	 * @param : property
	 * 
	 * @return : property value
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String getConfigPropertyValue(String property) {
		/* code to get property value */
		return properties.getProperty(property);
	}

	
	/*
	 * @description : Method to get Input Excel Data path
	 * 
	 * @param : NA
	 * 
	 * @return : String
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public String getInputExcelPath() {
		/* code to get driver */
		String inputExcelPath = properties.getProperty("InputExcelPath");
		if (inputExcelPath != null)
			return inputExcelPath;
		else
			throw new RuntimeException("InputExcelPath  not specified in the config file");
	}
	
	/*
	 * @description : Method to get Input Sheet Name
	 * 
	 * @param : NA
	 * 
	 * @return : String
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public String getInputExcelSheetName() {
		/* code to get driver */
		String InputSheetName = properties.getProperty("InputSheetName");
		if (InputSheetName != null)
			return InputSheetName;
		else
			throw new RuntimeException("InputSheetName  not specified in the config file");
	}
	
	/*
	 * @description : Method to get common Sheet Name
	 * 
	 * @param : NA
	 * 
	 * @return : String
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public String getInputExcelCommonSheetName() {
		/* code to get driver */
		String CommonDataSheet = properties.getProperty("CommonDataSheet");
		if (CommonDataSheet != null)
			return CommonDataSheet;
		else
			throw new RuntimeException("CommonDataSheet  not specified in the config file");
	}
	
	/*
	 * @description : Method to get expected input file
	 * 
	 * @param : NA
	 * 
	 * @return : String
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public String getInputExpectedInputXmlFile() {
		/* code to get driver */
		String InputExpectedFilePath = properties.getProperty("InputExpectedFilePath");
		if (InputExpectedFilePath != null)
			return InputExpectedFilePath;
		else
			throw new RuntimeException("InputExpectedFilePath  not specified in the config file");
	}
	
	/*
	 * @description : Method to get actual input file
	 * 
	 * @param : NA
	 * 
	 * @return : String
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public String getActualXmlFile() {
		/* code to get driver */
		String ActualXmlFile = properties.getProperty("ActualXmlFile");
		if (ActualXmlFile != null)
			return ActualXmlFile;
		else
			throw new RuntimeException("ActualXmlFile  not specified in the config file");
	}
	
}
