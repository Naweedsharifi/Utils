package com.wbg.selenium.qa.manager;

import com.wbg.selenium.qa.configReader.ConfigFileReader;

public class FileReaderManager {

	private static FileReaderManager fileReaderManager = new FileReaderManager();
	private static ConfigFileReader configFileReader;

	private FileReaderManager() {

	}

	/*
	 * @description : Method to get the Instance of Config file reader
	 * @param :
	 * @return : fileReaderManager
	 * @date : 28 Dec 2020
	 * @author : Infosys Limited
	 */
	public static FileReaderManager getInstance() {
		return fileReaderManager;
	}

	/*
	 * @description : Method to get the details  of Config file reader
	 * @param :
	 * @return : configFileReader
	 * @date : 28 Dec 2020
	 * @author : Infosys Limited
	 */
	public ConfigFileReader getConfigReader() {
		return (configFileReader == null) ? new ConfigFileReader() : configFileReader;

	}

}
