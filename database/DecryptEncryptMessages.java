package com.wbg.selenium.qa.database;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Base64;
import java.util.HashMap;
import java.util.stream.Stream;

import org.testng.Assert;
import org.testng.Reporter;

import com.wbg.selenium.qa.manager.WebDriverManager;
import com.wbg.selenium.qa.utils.CreateResultFolderStructure;
import com.wbg.selenium.qa.utils.WebElementWrappers;

public class DecryptEncryptMessages extends WebDriverManager{
	/*
	 * @description : Method to decrypt Inbound message
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void decryptMessageInbound(String resultpath) throws IOException {
		try {
			/* Getting decoder */
			Base64.Decoder decoder = Base64.getDecoder();

			/* Read data from file */
			String filePath = resultpath + "/Inbound.txt";
			Path path = Paths.get(filePath);
			StringBuilder sb = new StringBuilder();
			try (Stream<String> stream = Files.lines(path)) {
				stream.forEach(s -> sb.append(s).append("\n"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			String strInboundcontents = sb.toString().trim();

			/* Decode string Value */
			String decodedStr = new String(decoder.decode(strInboundcontents));

			/* Write in a file */
			String filePath1 = resultpath + "/InboundDecyptedMsg.txt";
			boolean strFile = CreateResultFolderStructure.createFile(filePath1);
			FileWriter fileWriter = new FileWriter(filePath1);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(decodedStr);
			bufferedWriter.newLine();
			bufferedWriter.close();
			if (strFile) {
				WebElementWrappers.Reporter(driver,
						"Verify the file exist and decrypted value is stored in file,File exist and decrypted value should be stored in file,File exist and decrypted value is stored in file,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify the file exist and decrypted value is stored in file,File exist and decrypted value should be stored in file,File does not exist and decrypted value is not stored in file,Fail");
			}
			Assert.assertEquals(strFile, true,
					"Verify the file exist and decrypted value is stored is in file");
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Method to decrypt MQOutQ message
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void deCryptMQOutQMessage(String strPath) throws IOException {
		try {
			/* Getting decoder */
			Base64.Decoder decoder = Base64.getDecoder();

			/* Read data from file */
			String filePath = strPath + "/MqOutQ.txt";
			Path path = Paths.get(filePath);
			StringBuilder sb = new StringBuilder();
			try (Stream<String> stream = Files.lines(path)) {
				stream.forEach(s -> sb.append(s).append("\n"));
			} catch (IOException ex) {
				ex.printStackTrace();
			}
			String strInboundcontents = sb.toString().trim().split("<BLOBTXT>")[1].split("</BLOBTXT>")[0].trim();

			/* Decoding string value */
			String decodedStr = new String(decoder.decode(strInboundcontents));
			decodedStr = decodedStr.replace("utf-16", "utf-8");

			/* Write in a file */
			String filePath1 = strPath + "/MqOutQDecyptedMsg.xml";
			boolean strFile = CreateResultFolderStructure.createFile(filePath1);
			FileWriter fileWriter = new FileWriter(filePath1);
			BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
			bufferedWriter.write(decodedStr);
			bufferedWriter.newLine();
			bufferedWriter.close();
			if (strFile) {
				WebElementWrappers.Reporter(driver,
						"Verify the file exist and decrypted value is stored in file,File exist and decrypted value should be stored in file,File exist and decrypted value is stored in file,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify the file exist and decrypted value is stored in file,File exist and decrypted value should be stored in file,File does not exist and decrypted value is not stored in file,Fail");
			}
			Assert.assertEquals(strFile, true,
					"Verify the file exist and decrypted value is stored is in file");
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Method to get Inbound data from txt file
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void getInboundValidationDataTxtFile(String strPath) throws FileNotFoundException, IOException {
		HashMap<String, String> dataKeyValue = new HashMap<String, String>();
		BufferedReader reader;
		try {
			String filePath = strPath + "/InboundDecyptedMsg.txt";
			reader = new BufferedReader(new FileReader(filePath));
			String line = reader.readLine();
			while (line != null) {
				line = reader.readLine();
				if (line != null && line.contains(":") && !line.contains("HEADER") && !line.contains("TRAILER")) {
					String keyValueTxtFiledata[] = line.split(":");
					dataKeyValue.put(keyValueTxtFiledata[1], keyValueTxtFiledata[2]);
				}
			}
			reader.close();
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to get decrypted Inbound file data
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static String getInboundDecyptedFileData(String strPath) {
		String filePath = strPath + "/InboundDecyptedMsg.txt";
		Path path = Paths.get(filePath);
		StringBuilder sb = new StringBuilder();
		try (Stream<String> stream = Files.lines(path)) {
			stream.forEach(s -> sb.append(s).append("\n"));
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
		return sb.toString();
	}
	
	/*
	 * @description : Method to decrypt MQOutQ message
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void deCryptMQOutQMessage304(String strPath) throws IOException {
		try {
			/* Getting decoder */
			Base64.Decoder decoder = Base64.getDecoder();

			/* Read data from file */
			for (int i = 1; i<=2; i++) {
				String filePath = strPath + "/MqOutQ"+i+".txt";
				Path path = Paths.get(filePath);
				StringBuilder sb = new StringBuilder();
				try (Stream<String> stream = Files.lines(path)) {
					stream.forEach(s -> sb.append(s).append("\n"));
				} catch (IOException ex) {
					ex.printStackTrace();
				}
				String strInboundcontents = sb.toString().trim().split("<BLOBTXT>")[1].split("</BLOBTXT>")[0].trim();

				/* Decoding string value */
				String decodedStr = new String(decoder.decode(strInboundcontents));
				decodedStr = decodedStr.replace("utf-16", "utf-8");

				/* Write in a file */
				String filePath1 = strPath + "/MqOutQDecyptedMsg"+i+".xml";
				boolean strFile = CreateResultFolderStructure.createFile(filePath1);
				FileWriter fileWriter = new FileWriter(filePath1);
				BufferedWriter bufferedWriter = new BufferedWriter(fileWriter);
				bufferedWriter.write(decodedStr);
				bufferedWriter.newLine();
				bufferedWriter.close();
				if (strFile) {
					WebElementWrappers.Reporter(driver,
							"Verify the file exist and decrypted value is stored in file,File exist and decrypted value should be stored in file,File exist and decrypted value is stored in file,Pass");
				} else {
					WebElementWrappers.Reporter(driver,
							"Verify the file exist and decrypted value is stored in file,File exist and decrypted value should be stored in file,File does not exist and decrypted value is not stored in file,Fail");
				}
				Assert.assertEquals(strFile, true,
						"Verify the file exist and decrypted value is stored is in file");
			}
			
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}


}
