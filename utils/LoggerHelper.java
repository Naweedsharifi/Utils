package com.wbg.selenium.qa.utils;

import org.apache.log4j.Logger;
import org.apache.log4j.PropertyConfigurator;


/*
 * @description : Declaring logger class
 * @param :
 * @return : 
 * @date : 29 Dec 2020
 * @author : Infosys Limited
 */

public class LoggerHelper {
	private static boolean root = false;

	public static Logger getLogger(Class cls) {
		if (root) {
			return Logger.getLogger(cls);
		}
		PropertyConfigurator.configure("log4j.properties");
		root = true;
		return Logger.getLogger(cls);
	}

	public static void main(String[] args) {
		Logger log = LoggerHelper.getLogger(LoggerHelper.class);
		log.info("Hello World!");
	}
}
