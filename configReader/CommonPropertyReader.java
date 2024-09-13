package com.wbg.selenium.qa.configReader;

import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Properties;

public class CommonPropertyReader {

	/*
	 * @description : Method to load file
	 * 
	 * @param : filePath
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static Properties loadFile(String filePath) {
		Properties prop = new Properties();
		InputStream input = null;
		try {
			input = new FileInputStream(filePath);
			/* load the properties file */
			prop.load(input);
		} catch (IOException ex) {
			/* Just print the exception */
			ex.printStackTrace();
		} finally {
			/* Close the InputStream object */
			if (input != null) {
				try {
					input.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		/* return the properties object */
		return prop;
	}

	/*
	 * @description : Method to write properties
	 * 
	 * @param : prop, filePath
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void writeProperties(Properties prop, String filePath) {
		OutputStream output = null;

		try {

			output = new FileOutputStream(filePath);
			 /* save the properties file to the location provided as parameter*/
			prop.store(output, null);

		} catch (IOException io) {
			/* Just print the exception */
			io.printStackTrace();
		} finally {
			/* Close the OutputStream object */
			if (output != null) {
				try {
					output.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}

		}
	}

}
