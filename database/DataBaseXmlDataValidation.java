package com.wbg.selenium.qa.database;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream.GetField;
import java.math.BigDecimal;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeFormatterBuilder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.TreeMap;
import java.util.regex.Pattern;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathFactory;

import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.ss.usermodel.WorkbookFactory;
import org.testng.Assert;
import org.w3c.dom.Document;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import com.wbg.selenium.qa.configReader.Xlsx_FileReader;
import com.wbg.selenium.qa.manager.FileReaderManager;
import com.wbg.selenium.qa.manager.WebDriverManager;
import com.wbg.selenium.qa.utils.WebElementWrappers;

import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;

public class DataBaseXmlDataValidation extends WebDriverManager {

	/*
	 * @description : Method to evaluate xpath of xml document
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static List<String> evaluateXPath(Document document, String xpathExpression) throws Exception {
		XPathFactory xpathFactory = XPathFactory.newInstance();
		XPath xpath = xpathFactory.newXPath();
		List<String> values = new ArrayList<>();
		try {
			XPathExpression expr = xpath.compile(xpathExpression);
			NodeList nodes = (NodeList) expr.evaluate(document, XPathConstants.NODESET);
			for (int i = 0; i < nodes.getLength(); i++) {
				values.add(nodes.item(i).getTextContent());
			}

		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
		return values;
	}

	/*
	 * @description : Method to get document
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static Document getDocument(String fileName) throws Exception {
		/* code to get the document */
		try {
			DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
			factory.setNamespaceAware(true);
			DocumentBuilder builder = factory.newDocumentBuilder();
			Document doc = builder.parse(fileName);
			doc.normalizeDocument();
			return doc;
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
		return null;
	}

	/*
	 * @description : Method to validate xml data
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */

	public static void XmlDataValidation1(String strDealerName, String resultPath, int rowNumber, String Analyse01,
			String strPaymentMethodInput, String strEntityInput, String strInstrumentInput) throws Exception {
		try {
			/* Get DOM Node for XML */
			String fileName = resultPath + "/MqOutQDecyptedMsg.xml";
			Document document = getDocument(fileName);
			String inputSheetName = FileReaderManager.getInstance().getConfigReader().getInputExcelSheetName();

			/* Get the encypted inbound messages */
			String strInboundDecryptedMsg = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath);
			String strInboundDecryptedMsg1 = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath)
					.replaceAll("[\\t\\n\\r]+", " ").replace(" ", "").replace("::", ":").replace("HEADER", "")
					.replace("TRAILER", "").replace("-", "").replace("}{", ":").replace("{", "").replace("}", "")
					.replace(".", ",").replace(",", "").replace("/", "").toLowerCase();
			String strInboundDecryptedMsgArr[] = strInboundDecryptedMsg1.split(":");
			String strArr[] = WebElementWrappers.removeAllEmpty(strInboundDecryptedMsgArr);
			List<String> strList = Arrays.asList(strArr);
			System.out.println(strList);

			/* Validate data for payment method */
			String xpathExpressionPaymentMethod = "//PaymentMethod";
			List<String> strPaymentMethod = evaluateXPath(document, xpathExpressionPaymentMethod);
			strPaymentMethod.removeIf(item -> item == null || "".equals(item));
			boolean strDataPayment = false;
			if (!strPaymentMethod.isEmpty()) {
				for (int i = 0; i < strPaymentMethod.size(); i++) {
					if (strPaymentMethodInput.toLowerCase().contains(strPaymentMethod.get(i).toLowerCase())) {
						WebElementWrappers.Reporter(driver,
								"Verify that the PaymentMethod exist in the xml file,PaymentMethod- "
										+ strPaymentMethodInput.toUpperCase()
										+ " should exist in the file,PaymentMethod - "
										+ strPaymentMethod.get(i).toUpperCase() + " exists in the file,Pass");
						strDataPayment = true;
						break;
					}
				}
			}

			if (!strDataPayment) {
				WebElementWrappers.Reporter(driver,
						"Verify that the PaymentMethod exist in the xml file,PaymentMethod should exist in the xml file,PaymentMethod does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Entity */
			String xpathExpressionEntity = "//Entity";
			boolean strDataEntity = false;
			List<String> strEntity = evaluateXPath(document, xpathExpressionEntity);
			strEntity.removeIf(item -> item == null || "".equals(item));
			if (!strEntity.isEmpty()) {
				for (int i = 0; i < strEntity.size(); i++) {
					if (strEntityInput.toLowerCase().contains(strEntity.get(i).toLowerCase())) {
						WebElementWrappers.Reporter(driver,
								"Verify that the Entity exist in the xml file,Entity- " + strEntityInput.toUpperCase()
										+ " should exist in the file,Entity - " + strEntity.get(i).toUpperCase()
										+ " exists in the file,Pass");
						strDataEntity = true;
						break;
					}
				}
			}
			if (!strDataEntity) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Entity exist in the xml file,Entity should exist in the xml file,Entity does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Action */
			String xpathExpressionAction = "//Action";
			boolean strDataAction = false;
			String strActionInput = Xlsx_FileReader.excelreader.getCellData(inputSheetName, "strAction", rowNumber);
			List<String> strAction = evaluateXPath(document, xpathExpressionAction);
			strAction.removeIf(item -> item == null || "".equals(item));
			if (!strAction.isEmpty()) {
				for (int i = 0; i < strAction.size(); i++) {
					if (strActionInput.toLowerCase().contains(strAction.get(i).toLowerCase())) {
						strDataAction = true;
						WebElementWrappers.Reporter(driver,
								"Verify that the Action exist in the xml file,Action- " + strActionInput.toUpperCase()
										+ " should exist in the file,Action - " + strAction.get(i).toUpperCase()
										+ " exists in the file,Pass");
						break;
					}
				}
			}

			if (!strDataAction) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Action exist in the xml file,Action should exist in the xml file,Action does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Instrument */
			String xpathExpressionInstrument = "//Instrument";
			boolean strDataInstrument = false;
			List<String> strInstrument = evaluateXPath(document, xpathExpressionInstrument);
			strInstrument.removeIf(item -> item == null || "".equals(item));
			if (!strInstrument.isEmpty()) {
				for (int i = 0; i < strInstrument.size(); i++) {
					if (strInstrumentInput.toLowerCase().contains(strInstrument.get(i).toLowerCase())) {
						strDataInstrument = true;
						WebElementWrappers.Reporter(driver,
								"Verify that the Instrument exist in the xml file,Instrument- "
										+ strInstrumentInput.toUpperCase() + " should exist in the file,Instrument - "
										+ strInstrument.get(i).toUpperCase() + " exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataInstrument) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Instrument exist in the xml file,Instrument should exist in the xml file,Instrument does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for ticket number */
			String xpathExpression = "";
			xpathExpression = "//TicketNumber";
			boolean strDataTicketNumber = false;
			List<String> strTicketNumber = evaluateXPath(document, xpathExpression);
			strTicketNumber.removeIf(item -> item == null || "".equals(item));
			if (!strTicketNumber.isEmpty()) {
				for (int i = 0; i < strTicketNumber.size(); i++) {
					String actualData = strTicketNumber.get(i).replace("\n", "").replace("-", "").replace(" ", "");
					if (strInboundDecryptedMsg.replace("-", "").replace(" ", "").contains(actualData)) {
						strDataTicketNumber = true;
						int indexNo = WebElementWrappers.getIndex(strList, actualData.toLowerCase());
						WebElementWrappers.Reporter(driver,
								"Verify that the ticket number exist in the xml file,Ticket number -"
										+ strList.get(indexNo) + " should exist in the file,Ticket number - "
										+ actualData + "exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataTicketNumber) {
				WebElementWrappers.Reporter(driver,
						"Verify that the ticket number exist in the xml file,Ticket number should exist in the xml file,Ticket number does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for amount */
			xpathExpression = "//Amount";
			boolean strDataAmount = false;
			List<String> strAmount = evaluateXPath(document, xpathExpression);
			strAmount.removeIf(item -> item == null || "".equals(item));
			String strAmountData = "";
			if (!strAmount.isEmpty()) {
				String strAmountExpected = strInboundDecryptedMsg.replace("\n", "").replace(".", ",").replace(",", "");
				for (int i = 0; i < strAmount.size(); i++) {
					String strAmountActual = strAmount.get(i).replace(".", ",").replace(",", "");
					if (strAmountExpected.contains(strAmountActual)) {
						strDataAmount = true;
						int indexNo = WebElementWrappers.getIndex(strList, strAmountActual);
						strAmountData = strList.get(indexNo);
						WebElementWrappers.Reporter(driver,
								"Verify that the Amount exist in the xml file,Amount -" + strList.get(indexNo)
										+ " should exist in the file,Amount - " + strAmountActual
										+ " exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataAmount) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Amount exist in the xml file,Amount should exist in the xml file,Amount does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for currency */
			xpathExpression = "//CCY";
			boolean strDatacurrency = false;
			List<String> strCurrency = evaluateXPath(document, xpathExpression);
			strCurrency.removeIf(item -> item == null || "".equals(item));
			if (!strCurrency.isEmpty()) {
				for (int i = 0; i < strCurrency.size(); i++) {
					if (strInboundDecryptedMsg.toLowerCase().contains(strCurrency.get(i).toLowerCase())) {
						strDatacurrency = true;
						int indexNo = WebElementWrappers.getIndex(strList, strAmountData);
						WebElementWrappers.Reporter(driver,
								"Verify that the Currency exist in the xml file,Currency - " + strList.get(indexNo)
										+ " should exist in the file,Currency - " + strCurrency.get(i)
										+ " exists in the xml file,Pass");
						break;
					}
				}
			}

			if (!strDatacurrency) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Currency exist in the xml file,Currency should exist in the xml file,Currency does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for AccountNumber */
			xpathExpression = "//AccountNumber";
			boolean strDataAccountNumber = false;
			List<String> strAccountNumber = evaluateXPath(document, xpathExpression);
			strAccountNumber.removeIf(item -> item == null || "".equals(item));
			if (!strAccountNumber.isEmpty()) {
				for (int i = 0; i < strAccountNumber.size(); i++) {
					if (strInboundDecryptedMsg.contains(strAccountNumber.get(i))) {
						strDataAccountNumber = true;
						int indexNo = WebElementWrappers.getIndex(strList,
								strAccountNumber.get(i).toLowerCase().replace("-", ""));
						WebElementWrappers.Reporter(driver,
								"Verify that the AccountNumber exist in the xml file,AccountNumber- "
										+ strList.get(indexNo) + " should exist in the file,AccountNumber - "
										+ strAccountNumber.get(i) + " exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataAccountNumber) {
				WebElementWrappers.Reporter(driver,
						"Verify that the AccountNumber exist in the xml file,AccountNumber should exist in the xml file,AccountNumber does not exist in the xml file,Fail");

				Assert.fail();
			}

			/* Validate data for dealer */
			xpathExpression = "//Dealer";
			boolean strDataDealer = false;
			List<String> strDealer = evaluateXPath(document, xpathExpression);
			strDealer.removeIf(item -> item == null || "".equals(item));
			if (!strDealer.isEmpty()) {
				for (int i = 0; i < strDealer.size(); i++) {
					if (strDealerName.toLowerCase().contains(strDealer.get(i).toLowerCase())) {
						strDataDealer = true;
						WebElementWrappers.Reporter(driver,
								"Verify that the Dealer exist in the xml file,Dealer- " + strDealerName
										+ " should exist in the file,Dealer - " + strDealer.get(i)
										+ " exists in the file,Pass");
						break;
					}
				}
			}

			if (!strDataDealer) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Dealer exist in the xml file,Dealer should exist in the xml file,Dealer does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Analyse01 */
			String xpathExpressionAnalyse01 = "//Analyse01";
			boolean strDataAnalyse01 = false;
			List<String> strAnalyse01 = evaluateXPath(document, xpathExpressionAnalyse01);
			strAnalyse01.removeIf(item -> item == null || "".equals(item));
			if (!strAnalyse01.isEmpty()) {
				for (int i = 0; i < strAnalyse01.size(); i++) {
					if (Analyse01.toLowerCase().contains(strAnalyse01.get(i).toLowerCase())) {
						WebElementWrappers.Reporter(driver,
								"Verify that the Analyse01 exist in the xml file,Analyse01- " + Analyse01.toUpperCase()
										+ " should exist in the file,Analyse01 - " + strAnalyse01.get(i).toUpperCase()
										+ " exists in the file,Pass");
						strDataAnalyse01 = true;
						break;
					}
				}
			}
			if (!strDataAnalyse01) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Analyse01 exist in the xml file,Analyse01 should exist in the xml file,Analyse01 does not exist in the xml file,Fail");
				Assert.fail();
			}

		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to get month name by using month index
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static String getMonthName(int monthIndex) {
		return new DateFormatSymbols().getMonths()[monthIndex].toString();
	}

	/*
	 * @description : Method to validate xml data
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */

	public static void XmlDataValidation3041(String strDealerName, String resultPath, int rowNumber, String Analyse05,
			String strPaymentMethodInput, String strEntityInput, String strInstrumentInput) throws Exception {
		try {
			/* Get DOM Node for XML */
			String fileName = resultPath + "/MqOutQDecyptedMsg.xml";
			Document document = getDocument(fileName);
			String inputSheetName = FileReaderManager.getInstance().getConfigReader().getInputExcelSheetName();

			/* Get the encypted inbound messages */
			String strInboundDecryptedMsg = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath);
			String strInboundDecryptedMsg1 = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath)
					.replaceAll("[\\t\\n\\r]+", " ").replace(" ", "").replace("::", ":").replace("HEADER", "")
					.replace("TRAILER", "").replace("-", "").replace("}{", ":").replace("{", "").replace("}", "")
					.replace(".", ",").replace(",", "").replace("/", "").toLowerCase();
			String strInboundDecryptedMsgArr[] = strInboundDecryptedMsg1.split(":");
			String strArr[] = WebElementWrappers.removeAllEmpty(strInboundDecryptedMsgArr);
			List<String> strList = Arrays.asList(strArr);
			System.out.println(strList);

			/* Validate data for payment method */
			String xpathExpressionPaymentMethod = "//PaymentMethod";
			List<String> strPaymentMethod = evaluateXPath(document, xpathExpressionPaymentMethod);
			strPaymentMethod.removeIf(item -> item == null || "".equals(item));
			boolean strDataPayment = false;
			if (!strPaymentMethod.isEmpty()) {
				for (int i = 0; i < strPaymentMethod.size(); i++) {
					if (strPaymentMethodInput.toLowerCase().contains(strPaymentMethod.get(i).toLowerCase())) {
						WebElementWrappers.Reporter(driver,
								"Verify that the PaymentMethod exist in the xml file,PaymentMethod- "
										+ strPaymentMethodInput.toUpperCase()
										+ " should exist in the file,PaymentMethod - "
										+ strPaymentMethod.get(i).toUpperCase() + " exists in the file,Pass");
						strDataPayment = true;
						break;
					}
				}
			}

			if (!strDataPayment) {
				WebElementWrappers.Reporter(driver,
						"Verify that the PaymentMethod exist in the xml file,PaymentMethod should exist in the xml file,PaymentMethod does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Entity */
			String xpathExpressionEntity = "//Entity";
			boolean strDataEntity = false;
			List<String> strEntity = evaluateXPath(document, xpathExpressionEntity);
			strEntity.removeIf(item -> item == null || "".equals(item));
			if (!strEntity.isEmpty()) {
				for (int i = 0; i < strEntity.size(); i++) {
					if (strEntityInput.toLowerCase().contains(strEntity.get(i).toLowerCase())) {
						WebElementWrappers.Reporter(driver,
								"Verify that the Entity exist in the xml file,Entity- " + strEntityInput.toUpperCase()
										+ " should exist in the file,Entity - " + strEntity.get(i).toUpperCase()
										+ " exists in the file,Pass");
						strDataEntity = true;
						break;
					}
				}
			}
			if (!strDataEntity) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Entity exist in the xml file,Entity should exist in the xml file,Entity does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Action */
			String xpathExpressionAction = "//Action";
			boolean strDataAction = false;
			String strActionInput = Xlsx_FileReader.excelreader.getCellData(inputSheetName, "strAction", rowNumber);
			List<String> strAction = evaluateXPath(document, xpathExpressionAction);
			strAction.removeIf(item -> item == null || "".equals(item));
			if (!strAction.isEmpty()) {
				for (int i = 0; i < strAction.size(); i++) {
					if (strActionInput.toLowerCase().contains(strAction.get(i).toLowerCase())) {
						strDataAction = true;
						WebElementWrappers.Reporter(driver,
								"Verify that the Action exist in the xml file,Action- " + strActionInput.toUpperCase()
										+ " should exist in the file,Action - " + strAction.get(i).toUpperCase()
										+ " exists in the file,Pass");
						break;
					}
				}
			}

			if (!strDataAction) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Action exist in the xml file,Action should exist in the xml file,Action does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Instrument */
			String xpathExpressionInstrument = "//Instrument";
			boolean strDataInstrument = false;
			List<String> strInstrument = evaluateXPath(document, xpathExpressionInstrument);
			strInstrument.removeIf(item -> item == null || "".equals(item));
			if (!strInstrument.isEmpty()) {
				for (int i = 0; i < strInstrument.size(); i++) {
					if (strInstrumentInput.toLowerCase().contains(strInstrument.get(i).toLowerCase())) {
						strDataInstrument = true;
						WebElementWrappers.Reporter(driver,
								"Verify that the Instrument exist in the xml file,Instrument- "
										+ strInstrumentInput.toUpperCase() + " should exist in the file,Instrument - "
										+ strInstrument.get(i).toUpperCase() + " exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataInstrument) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Instrument exist in the xml file,Instrument should exist in the xml file,Instrument does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for ticket number */
			String xpathExpression = "";
			xpathExpression = "//TicketNumber";
			boolean strDataTicketNumber = false;
			List<String> strTicketNumber = evaluateXPath(document, xpathExpression);
			strTicketNumber.removeIf(item -> item == null || "".equals(item));
			if (!strTicketNumber.isEmpty()) {
				for (int i = 0; i < strTicketNumber.size(); i++) {
					String actualData = strTicketNumber.get(i).replace("\n", "").replace("-", "").replace(" ", "");
					if (strInboundDecryptedMsg.replace("-", "").replace(" ", "").contains(actualData)) {
						strDataTicketNumber = true;
						int indexNo = WebElementWrappers.getIndex(strList, actualData.toLowerCase());
						WebElementWrappers.Reporter(driver,
								"Verify that the ticket number exist in the xml file,Ticket number -"
										+ strList.get(indexNo) + " should exist in the file,Ticket number - "
										+ actualData + "exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataTicketNumber) {
				WebElementWrappers.Reporter(driver,
						"Verify that the ticket number exist in the xml file,Ticket number should exist in the xml file,Ticket number does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for amount */
			xpathExpression = "//Amount";
			boolean strDataAmount = false;
			List<String> strAmount = evaluateXPath(document, xpathExpression);
			strAmount.removeIf(item -> item == null || "".equals(item));
			String strAmountData = "";
			if (!strAmount.isEmpty()) {
				String strAmountExpected = strInboundDecryptedMsg.replace("\n", "").replace(".", ",").replace(",", "");
				for (int i = 0; i < strAmount.size(); i++) {
					String strAmountActual = strAmount.get(i).replace(".", ",").replace(",", "");
					if (strAmountExpected.contains(strAmountActual)) {
						strDataAmount = true;
						int indexNo = WebElementWrappers.getIndex(strList, strAmountActual);
						strAmountData = strList.get(indexNo);
						WebElementWrappers.Reporter(driver,
								"Verify that the Amount exist in the xml file,Amount -" + strList.get(indexNo)
										+ " should exist in the file,Amount - " + strAmountActual
										+ " exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataAmount) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Amount exist in the xml file,Amount should exist in the xml file,Amount does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for currency */
			xpathExpression = "//CCY";
			boolean strDatacurrency = false;
			List<String> strCurrency = evaluateXPath(document, xpathExpression);
			strCurrency.removeIf(item -> item == null || "".equals(item));
			if (!strCurrency.isEmpty()) {
				for (int i = 0; i < strCurrency.size(); i++) {
					if (strInboundDecryptedMsg.toLowerCase().contains(strCurrency.get(i).toLowerCase())) {
						strDatacurrency = true;
						int indexNo = WebElementWrappers.getIndex(strList, strAmountData);
						WebElementWrappers.Reporter(driver,
								"Verify that the Currency exist in the xml file,Currency - " + strList.get(indexNo)
										+ " should exist in the file,Currency - " + strCurrency.get(i)
										+ " exists in the xml file,Pass");
						break;
					}
				}
			}

			if (!strDatacurrency) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Currency exist in the xml file,Currency should exist in the xml file,Currency does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for AccountNumber */
			xpathExpression = "//AccountNumber";
			boolean strDataAccountNumber = false;
			List<String> strAccountNumber = evaluateXPath(document, xpathExpression);
			strAccountNumber.removeIf(item -> item == null || "".equals(item));
			if (!strAccountNumber.isEmpty()) {
				for (int i = 0; i < strAccountNumber.size(); i++) {
					if (strInboundDecryptedMsg.contains(strAccountNumber.get(i))) {
						strDataAccountNumber = true;
						int indexNo = WebElementWrappers.getIndex(strList,
								strAccountNumber.get(i).toLowerCase().replace("-", ""));
						WebElementWrappers.Reporter(driver,
								"Verify that the AccountNumber exist in the xml file,AccountNumber- "
										+ strList.get(indexNo) + " should exist in the file,AccountNumber - "
										+ strAccountNumber.get(i) + " exists in the file,Pass");
						break;
					}
				}
			}
			if (!strDataAccountNumber) {
				WebElementWrappers.Reporter(driver,
						"Verify that the AccountNumber exist in the xml file,AccountNumber should exist in the xml file,AccountNumber does not exist in the xml file,Fail");

				Assert.fail();
			}

			/* Validate data for dealer */
			xpathExpression = "//Dealer";
			boolean strDataDealer = false;
			List<String> strDealer = evaluateXPath(document, xpathExpression);
			strDealer.removeIf(item -> item == null || "".equals(item));
			if (!strDealer.isEmpty()) {
				for (int i = 0; i < strDealer.size(); i++) {
					if (strDealerName.toLowerCase().contains(strDealer.get(i).toLowerCase())) {
						strDataDealer = true;
						WebElementWrappers.Reporter(driver,
								"Verify that the Dealer exist in the xml file,Dealer- " + strDealerName
										+ " should exist in the file,Dealer - " + strDealer.get(i)
										+ " exists in the file,Pass");
						break;
					}
				}
			}

			if (!strDataDealer) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Dealer exist in the xml file,Dealer should exist in the xml file,Dealer does not exist in the xml file,Fail");
				Assert.fail();
			}

			/* Validate data for Analyse01 */
			String xpathExpressionAnalyse05 = "//Analyse05";
			boolean strDataAnalyse05 = false;
			List<String> strAnalyse05 = evaluateXPath(document, xpathExpressionAnalyse05);
			strAnalyse05.removeIf(item -> item == null || "".equals(item));
			if (!strAnalyse05.isEmpty()) {
				for (int i = 0; i < strAnalyse05.size(); i++) {
					if (Analyse05.toLowerCase().contains(strAnalyse05.get(i).toLowerCase())) {
						WebElementWrappers.Reporter(driver,
								"Verify that the Analyse05 exist in the xml file,Analyse05- " + Analyse05.toUpperCase()
										+ " should exist in the file,Analyse05 - " + strAnalyse05.get(i).toUpperCase()
										+ " exists in the file,Pass");
						strDataAnalyse05 = true;
						break;
					}
				}
			}
			if (!strDataAnalyse05) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Analyse05 exist in the xml file,Analyse01 should exist in the xml file,Analyse05 does not exist in the xml file,Fail");
				Assert.fail();
			}

		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to evaluate data of xml document
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void XmlDataValidation(String strDealerName, String resultPath, int rowNumber, String Analyse01,
			String strPaymentMethodInput, String strEntityInput, String strInstrumentInput)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		try {
			String fileResultPath = System.getProperty("user.dir").replace("\\", "/") + "/" + resultPath
					+ "/ExcelReportValidation.xlsx";
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet = wb.createSheet("DataValidation");

			/* Setting Foreground Color for header */
			Row row = sheet.createRow(0);

			/* Style for header */
			CellStyle style = DataBaseXmlStructureValidation.cellStyleHeader(wb);

			/* Set Header */
			Cell cell = row.createCell(0);
			cell.setCellValue("GeneratedRFP nodes");
			cell.setCellStyle(style);
			cell = row.createCell(1);
			cell.setCellValue("RFP Values");
			cell.setCellStyle(style);
			cell = row.createCell(2);
			cell.setCellValue("Swiftvalues");
			cell.setCellStyle(style);
			cell = row.createCell(3);
			cell.setCellValue("Swiftfield");
			cell.setCellStyle(style);
			cell = row.createCell(4);
			cell.setCellValue("Results");
			cell.setCellStyle(style);

			/* Style for child and status */
			CellStyle styleParent = DataBaseXmlStructureValidation.CellParentStyle(wb);
			CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
			CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
			CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

			/* Get DOM Node for XML */
			String fileName = resultPath + "/MqOutQDecyptedMsg.xml";
			Document document = getDocument(fileName);
			Map<String, String> strNodeAndData = new HashMap<>();

			/* Write node and its value in the sheet for process */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Process");
			Row rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			Cell cell1 = rowParent.createCell(0);
			cell1.setCellValue("Process");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for cashflow */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Cashflow");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("Cashflow");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for purpose */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Purpose");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("Purpose");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for Bank Account */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/BankAccount");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("BankAccount");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for other */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Other");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("Other");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for Analysis Codes */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/AnalysisCodes");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("AnalysisCodes");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for Our Instructions */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/OurInstructions");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("OurInstructions");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for Beneficiary */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Beneficiary");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("Beneficiary");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for BeneBank */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/BeneBank");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("BeneBank");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for DeliveryNarrative */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/DeliveryNarrative");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("DeliveryNarrative");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for IntermediaryBank */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/IntermediaryBank");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("IntermediaryBank");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for BeneficiaryDetails */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/BeneficiaryDetails");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("BeneficiaryDetails");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for Charges */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Charges");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("Charges");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for SendersCharges */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Charges/SendersCharges");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("SendersCharges");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for ReceiversCharges */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Charges/ReceiversCharges");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("ReceiversCharges");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Write node and its value in the sheet for Custom */
			strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Custom");
			rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			cell1 = rowParent.createCell(0);
			cell1.setCellValue("Custom");
			cell1.setCellStyle(styleParent);
			cell1 = rowParent.createCell(1);
			cell1.setCellValue("");
			cell1.setCellStyle(styleChild);
			setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

			/* Set border for all column for status */
			int totalrowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
			for (int i = 1; i <= totalrowCount; i++) {
				rowParent = sheet.getRow(i);
				cell = rowParent.createCell(4);
				cell.setCellValue("");
				cell.setCellStyle(styleChild);
			}

			/* Write Mandatory field data in the Excel */
			int actionCount, instrumentCount, entityCount, paymentmethodCount, dealerCount, analyse01Count;
			actionCount = instrumentCount = entityCount = paymentmethodCount = dealerCount = analyse01Count = 0;
			int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
			for (int i = 1; i <= rowCount; i++) {
				rowParent = sheet.getRow(i);
				Cell cell2 = rowParent.getCell(0);
				String cellValue = cell2.getStringCellValue();
				switch (cellValue) {
				case "action":
					if (actionCount == 0) {
						cell1 = rowParent.createCell(2);
						cell1.setCellValue("I");
						cell1.setCellStyle(styleChild);
						cell = rowParent.createCell(3);
						cell.setCellValue("Mandatory Non-Swift Fields");
						cell.setCellStyle(styleChild);
						cell1 = rowParent.createCell(4);
						if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase("I")) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is same as the value of the Swift Message -" + "I"
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is not same as the value of the Swift Message -" + "I"
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
						}
						actionCount++;
					}
					break;
				case "instrument":
					if (instrumentCount == 0) {
						cell1 = rowParent.createCell(2);
						cell1.setCellValue(strInstrumentInput);
						cell1.setCellStyle(styleChild);
						cell = rowParent.createCell(3);
						cell.setCellValue("Mandatory Non-Swift Fields");
						cell.setCellStyle(styleChild);
						cell1 = rowParent.createCell(4);
						if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strInstrumentInput)) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is same as the value of the Swift Message -" + strInstrumentInput
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is not same as the value of the Swift Message -" + strInstrumentInput
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
						}
						instrumentCount++;
					}

					break;
				case "entity":
					if (entityCount == 0) {
						cell1 = rowParent.createCell(2);
						cell1.setCellValue(strEntityInput);
						cell1.setCellStyle(styleChild);
						cell = rowParent.createCell(3);
						cell.setCellValue("Mandatory Non-Swift Fields");
						cell.setCellStyle(styleChild);
						cell1 = rowParent.createCell(4);
						if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strEntityInput)) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is same as the value of the Swift Message -" + strEntityInput
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is not same as the value of the Swift Message -" + strEntityInput
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
						}
						entityCount++;
					}
					break;
				case "paymentmethod":
					if (paymentmethodCount == 0) {
						cell1 = rowParent.createCell(2);
						cell1.setCellValue(strPaymentMethodInput);
						cell1.setCellStyle(styleChild);
						cell = rowParent.createCell(3);
						cell.setCellValue("Mandatory Non-Swift Fields");
						cell.setCellStyle(styleChild);
						cell1 = rowParent.createCell(4);
						if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strPaymentMethodInput)) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is same as the value of the Swift Message -" + strPaymentMethodInput
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is not same as the value of the Swift Message -" + strPaymentMethodInput
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
						}
						paymentmethodCount++;
					}
					break;
				case "dealer":
					if (dealerCount == 0) {
						cell1 = rowParent.createCell(2);
						cell1.setCellValue(
								strDealerName.substring(0, 1).toUpperCase() + strDealerName.substring(1).toLowerCase());
						cell1.setCellStyle(styleChild);
						cell = rowParent.createCell(3);
						cell.setCellValue("Mandatory Non-Swift Fields");
						cell.setCellStyle(styleChild);
						cell1 = rowParent.createCell(4);
						if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strDealerName)) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is same as the value of the Swift Message -" + strDealerName
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is not same as the value of the Swift Message -" + strDealerName
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
						}
						dealerCount++;
					}
					break;
				case "analyse01":
					if (analyse01Count == 0) {
						cell1 = rowParent.createCell(2);
						cell1.setCellValue(Analyse01);
						cell1.setCellStyle(styleChild);
						cell = rowParent.createCell(3);
						cell.setCellValue("Mandatory Non-Swift Fields");
						cell.setCellStyle(styleChild);
						cell1 = rowParent.createCell(4);
						if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(Analyse01)) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is same as the value of the Swift Message -" + Analyse01
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ rowParent.getCell(1).getStringCellValue()
											+ " is not same as the value of the Swift Message -" + Analyse01
											+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
						}
						analyse01Count++;
					}
					break;
				default:
					cell1 = rowParent.createCell(2);
					cell1.setCellValue("");
					cell1.setCellStyle(styleChild);
					cell = rowParent.createCell(3);
					cell.setCellValue("");
					cell.setCellStyle(styleChild);
					break;
				}
			}

			sheet.autoSizeColumn(0);
			sheet.autoSizeColumn(1);
			sheet.autoSizeColumn(2);
			sheet.autoSizeColumn(3);
			sheet.autoSizeColumn(4);

			FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
			wb.write(outFile);
			outFile.close();

			/* Get text file data */
			String strInboundDecryptedMsg = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath).trim();
			if (Analyse01.trim().equalsIgnoreCase("599")) {
				swiftFieldsof599(fileResultPath, strInboundDecryptedMsg);
			} else if (Analyse01.trim().equalsIgnoreCase("541") || Analyse01.trim().equalsIgnoreCase("543")) {
				swiftFieldsof541And543(fileResultPath, strInboundDecryptedMsg);
			} else if (Analyse01.trim().equalsIgnoreCase("210")) {
				swiftFieldsValidation210(fileResultPath, strInboundDecryptedMsg);
			} else {
				swiftFieldsValidation(fileResultPath, strInboundDecryptedMsg);
			}
			verifyStatus(fileResultPath);
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			e.printStackTrace();
			Assert.fail();
		}
	}

	/*
	 * @description : Method to Validate status
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyStatus(String fileResultPath) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet = wb.getSheet("DataValidation");
			Cell cell;
			Row rowParent;
			int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
			for (int i = 0; i < rowCount; i++) {
				rowParent = sheet.getRow(i);
				cell = rowParent.getCell(4);
				if (cell.getStringCellValue().equalsIgnoreCase("Fail")) {
					WebElementWrappers.Reporter(driver,
							"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP is not same as the value of the Swift Message,Fail");
					Assert.fail();
				}
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			e.printStackTrace();
			Assert.fail();
		}
	}

	/*
	 * @description : Method to Validate swift field for 210
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void swiftFieldsValidation210(String fileResultPath, String strInboundDecryptedMsg) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet1 = wb.getSheet("DataValidation");

			/* Style for child and status */
			CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
			CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
			CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

			Cell cell;
			Row rowParent;
			Cell cell1;
			strInboundDecryptedMsg.replace(" ", "");
			String lines[] = strInboundDecryptedMsg.split("\\r?\\n");

			/*
			 * code to add next line to append in the same line if it is continuation of the
			 * previous line for comments
			 */
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < lines.length; i++) {
				lines[i].replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::", ":");
				if (lines[i].isEmpty()) {
					continue;
				}
				int j = 0;
				if ((lines[i].contains("70") || lines[i].contains("50A") || lines[i].contains("53A")
						|| lines[i].contains("57A") || lines[i].contains("58A") || lines[i].contains("59"))
						&& (!lines[i].contains("HEADER:"))) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(" " + lines[j].trim() + " ");
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				}  else if (lines[i].contains("50K")) {
					StringBuffer str = new StringBuffer();
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							str.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.append("#").append(str.toString()).toString().replace("  ", " ").trim();
					sb = new StringBuffer();				
				} else if (lines[i].contains("72")) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				}
				
				else {

				}
			}

			/* Switch case for each line */
			for (String string : lines) {
				string = string.replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::",
						":");
				if (string.isEmpty()) {
					continue;
				}
				String strVal = "";
				String strVal1 = "";
				if (!string.contains(":")) {
					continue;
				} else if (string.charAt(0) == ':') {
					strVal = string.split(":")[1].trim();
					strVal1 = string.split(":")[2].trim();
				} else {
					strVal = string.split(":")[0].trim();
					strVal1 = string.split(":")[1].trim();
				}
				System.out.println("strVal -" + strVal);
				System.out.println("strVal1 -" + strVal1);
				String excelData = "";
				Cell cell2 = null;
				switch (strVal) {
				case "20":
					rowParent = sheet1.getRow(12);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "25":
					rowParent = sheet1.getRow(19);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "52A":
					rowParent = sheet1.getRow(23);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", "")										+ " for the Swift Field -" + strVal + " ,Pass");
					
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");
					}

					rowParent = sheet1.getRow(74);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "21":
					rowParent = sheet1.getRow(29);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "")+ " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "50K":
					if (strVal1.contains("#")) {
						rowParent = sheet1.getRow(65);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);
						excelData = getCellData(rowParent);
						cell2.setCellValue(strVal1.split("#")[1]);
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strVal1.split("#")[1].replace("  ", "").replace("//s", "").replace(" ", "").toString()
								.trim().equalsIgnoreCase(excelData.replaceAll("[ \t\n\r]*", "").replace("  ", "")
										.replace("//s", "").toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -"
											+ strVal1.split("#")[1].replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
											+ strVal1.split("#")[1].replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");

						}
					}

					break;

				case "23B":
					rowParent = sheet1.getRow(114);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;

				case "32B":
					String strCurrency = strVal1.trim().substring(0, 3);
					String strAmount = strVal1.trim().substring(3);

					/* Code for currency */
					rowParent = sheet1.getRow(7);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strCurrency);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().substring(0, 3).trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -" + strVal1.trim().substring(0, 3).trim().replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.trim().substring(0, 3).trim().replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");
					}

					/* Code for amount */
					rowParent = sheet1.getRow(5);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					DecimalFormat df = new DecimalFormat("#.##");
					cell2.setCellValue(new Double(df.format(new Double(strAmount.replace(",", ".")))));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().toLowerCase()
							.equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -"
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "53A":
					String strData53A = strVal1.split(" ")[0].replace("/", "");
					rowParent = sheet1.getRow(19);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData53A);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData53A.trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strData53A
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strData53A + " for the Swift Field -" + strVal + " ,Fail");

					}

					String strData53 = strVal1.split(" ")[1].trim();
					rowParent = sheet1.getRow(61);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData53);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData53.trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strData53
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strData53 + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "57A":
					String[] strData111 = strVal1.trim().split(" ");
					if (isNumeric(strData111[0].replace("/", ""))) {
						rowParent = sheet1.getRow(72);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData111[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData111[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strData111[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strData111[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Fail");
						}

						if (strData111.length >= 2) {
							rowParent = sheet1.getRow(74);
							cell = rowParent.createCell(3);
							cell1 = rowParent.createCell(4);
							cell2 = rowParent.createCell(2);

							/* Get Excel data */
							excelData = getCellData(rowParent);
							cell2.setCellValue(strData111[1].replace("/", "").toString());
							cell.setCellStyle(styleChild);
							cell.setCellValue(strVal);
							cell.setCellStyle(styleChild);
							if (strData111[1].replace("/", "").toString()
									.equalsIgnoreCase(excelData.toString().trim())) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim() + " is same as the value of the Swift Message -"
												+ strData111[1].replace("/", "") + " for the Swift Field -" + strVal
												+ " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim() + " is not same as the value of the Swift Message -"
												+ strData111[1].replace("/", "") + " for the Swift Field -" + strVal
												+ " ,Fail");
							}
						}
					} else {
						rowParent = sheet1.getRow(74);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData111[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData111[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strData111[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strData111[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Fail");
						}
					}
					break;
				case "59":
					rowParent = sheet1.getRow(68);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);

					/* Get Excel data */
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1.trim().split(" ")[0].replace("/", ""));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().split(" ")[0].replace("/", "").toString()
							.equalsIgnoreCase(excelData.toString().trim())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strVal1 + " for the Swift Field -" + strVal + " ,Fail");
					}

					/* Check for the second data */
					String[] strDataVal = strVal1.trim().split(" ");
					if (strDataVal.length >= 2) {
						String strCellData = strVal1.trim().replace(strDataVal[0], "");
						rowParent = sheet1.getRow(100);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strCellData);
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strCellData.replace("  ", "").replace("//s", "").replace(" ", "").toString()
								.equalsIgnoreCase(excelData.replaceAll("[ \t\n\r]*", "").replace("  ", "")
										.replace("//s", "").toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strCellData + " for the Swift Field -" + strVal + " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strCellData + " for the Swift Field -" + strVal + " ,Fail");
						}
					}
					break;
				case "59A":
					String[] strData = strVal1.trim().split(" ");
					if (isNumeric(strData[0].replace("/", ""))) {
						rowParent = sheet1.getRow(68);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strData[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strData[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Fail");
						}

						if (strData.length >= 2) {
							rowParent = sheet1.getRow(74);
							cell = rowParent.createCell(3);
							cell1 = rowParent.createCell(4);
							cell2 = rowParent.createCell(2);

							/* Get Excel data */
							excelData = getCellData(rowParent);
							cell2.setCellValue(strData[1].replace("/", "").toString());
							cell.setCellStyle(styleChild);
							cell.setCellValue(strVal);
							cell.setCellStyle(styleChild);
							if (strData[1].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim() + " is same as the value of the Swift Message -"
												+ strData[1].replace("/", "") + " for the Swift Field -" + strVal
												+ " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim() + " is not same as the value of the Swift Message -"
												+ strData[1].replace("/", "") + " for the Swift Field -" + strVal
												+ " ,Fail");

							}
						}

					} else {
						rowParent = sheet1.getRow(67);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strData[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strData[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Fail");

						}
					}
					break;
				case "58A":
					String[] strData1 = strVal1.trim().split(" ");
					if (strData1.length >= 2) {
						rowParent = sheet1.getRow(68);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData1[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData1[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strData1[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strData1[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Fail");
						}

						rowParent = sheet1.getRow(67);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData1[1].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData1[1].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strData1[1].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strData1[1].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Fail");
						}

					} else {
						rowParent = sheet1.getRow(67);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData1[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData1[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strData1[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strData1[0].replace("/", "") + " for the Swift Field -" + strVal
											+ " ,Fail");
						}
					}
					break;
				case "71A":
					rowParent = sheet1.getRow(103);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().equalsIgnoreCase(excelData.trim())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strVal1 + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "50A":
					String strData50A = strVal1.split(" ")[0].replace("/", "");
					rowParent = sheet1.getRow(61);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData50A);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData50A.trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strData50A
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strData50A + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;

				case "56A":
					String strData56A = strVal1.split(" ")[0].replace("/", "");
					rowParent = sheet1.getRow(91);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData56A);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData56A.trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strData56A
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strData56A + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "72":
					String strData572 = strVal1.replace("  ", "");
					rowParent = sheet1.getRow(86);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData572);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData572.replace("  ", "").replace("//s", "").replace(" ", "").trim()
							.equalsIgnoreCase(excelData.replaceAll("[ \t\n\r]*", "").replace("  ", "")
									.replace("//s", "").trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strData572
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strData572 + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "70":
					String strData70 = strVal1.replace("  ", "");
					rowParent = sheet1.getRow(99);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData70);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData70.trim().toLowerCase().replace("//s", "")
							.equals(excelData.replace("/n", "").replace("//s", "").trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strData70
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strData70 + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				default:
					break;
				}
			}
			FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
			wb.write(outFile);
			outFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to Validate swift field
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void swiftFieldsValidation(String fileResultPath, String strInboundDecryptedMsg) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet1 = wb.getSheet("DataValidation");

			/* Style for child and status */
			CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
			CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
			CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

			Cell cell;
			Row rowParent;
			Cell cell1;
			strInboundDecryptedMsg.replace(" ", "");
			String lines[] = strInboundDecryptedMsg.split("\\r?\\n");

			/*
			 * code to add next line to append in the same line if it is continuation of the
			 * previous line for comments
			 */
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < lines.length; i++) {
				lines[i].replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::", ":");
				if (lines[i].isEmpty()) {
					continue;
				}
				int j = 0;
				if ((lines[i].contains("70") || lines[i].contains("50A") || lines[i].contains("53A")
						|| lines[i].contains("57A") || lines[i].contains("59"))
						&& (!lines[i].contains("HEADER:"))) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(" " + lines[j].trim() + " ");
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				}else if (lines[i].contains("50K")||lines[i].contains("58A")) {
					System.out.println("-----------------"+lines[i]+"----");
					StringBuffer str = new StringBuffer();
					sb.append(lines[i].trim().replace(" ", ""));
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							str.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.append("#").append(str.toString()).toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} 
				else if (lines[i].contains("72")) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} 				
				else {

				}
			}

			/* Switch case for each line */
			for (String string : lines) {
				string = string.replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::",
						":");
				if (string.isEmpty()) {
					continue;
				}
				String strVal = "";
				String strVal1 = "";
				if (!string.contains(":")) {
					continue;
				} else if (string.charAt(0) == ':') {
					strVal = string.split(":")[1].trim();
					strVal1 = string.split(":")[2].trim();
				} else {
					strVal = string.split(":")[0].trim();
					strVal1 = string.split(":")[1].trim();
				}
				System.out.println("strVal -" + strVal);
				System.out.println("strVal1 -" + strVal1);
				String excelData = "";
				Cell cell2 = null;
				switch (strVal) {
				case "20":
					rowParent = sheet1.getRow(12);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1
										+ " for the Swift Field -" + strVal + " ,Pass");
					}  else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					
					}else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "25":
					rowParent = sheet1.getRow(19);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ")+ " is same as the value of the Swift Message -" + strVal1.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "21":
					rowParent = sheet1.getRow(29);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strVal1 .replaceAll(",", " ")+ " for the Swift Field -" + strVal + " ,Fail");
					}
					break;
				case "50K":
					if (strVal1.contains("#")) {
						rowParent = sheet1.getRow(65);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);
						excelData = getCellData(rowParent);
						cell2.setCellValue(strVal1.split("#")[1]);
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strVal1.split("#")[1].replace("  ", "").replace("//s", "").replace(" ", "").toString()
								.trim().equalsIgnoreCase(excelData.replaceAll("[ \t\n\r]*", "").replace("  ", "")
										.replace("//s", "").toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strVal1.split("#")[1].replace("  ", "").replace("//s", "")
													.replace(" ", "").toString().replaceAll(",", " ")
											+ " for the Swift Field -" + strVal + " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strVal1.split("#")[1].replace("  ", "").replace("//s", "")
													.replace(" ", "").toString().replaceAll(",", " ")
											+ " for the Swift Field -" + strVal + " ,Fail");

						}
					}

					break;

				case "23B":
					rowParent = sheet1.getRow(114);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ")+ " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;

				case "32A":
					String strValueDate = strVal1.trim().substring(0, 6);
					String strCurrency = strVal1.trim().substring(6, 9);
					String strAmount = strVal1.trim().substring(9);
					/* Code for currency */
					rowParent = sheet1.getRow(7);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strCurrency);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().substring(6, 9).trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
										+ strVal1.trim().substring(6, 9).trim().replaceAll(",", " ") + " for the Swift Field -" + strVal
										+ " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strVal1.trim().substring(6, 9).trim().replaceAll(",", " ") + " for the Swift Field -" + strVal
										+ " ,Fail");

					}

					/* Code for amount */
					rowParent = sheet1.getRow(5);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					DecimalFormat df = new DecimalFormat("#.##");
					cell2.setCellValue(new Double(df.format(new Double(strAmount.replace(",", ".")))));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().toLowerCase()
							.equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Fail");

					}
					DateTimeFormatter formatter = null;
					if (strValueDate.contains("-") && strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					} else if (strValueDate.contains("/") && strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

					} else if (!strValueDate.contains("-") && !strValueDate.contains("/")
							&& strValueDate.length() == 6) {
						formatter = DateTimeFormatter.ofPattern("yyMMdd");
					} else {
						formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
								.append(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toFormatter();

					}
					LocalDate ldt = LocalDate.parse(strValueDate, formatter);
					rowParent = sheet1.getRow(13);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim().toLowerCase()
							.equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "53A":
					String strData53A = strVal1.split(" ")[0].replace("/", "");
					rowParent = sheet1.getRow(19);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData53A);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData53A.trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strData53A.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strData53A.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}

					String strData53 = strVal1.split(" ")[1].trim();
					rowParent = sheet1.getRow(61);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData53);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData53.trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strData53.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strData53.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "57A":
					String[] strData111 = strVal1.trim().split(" ");
					if (isNumeric(strData111[0].replace("/", ""))) {
						rowParent = sheet1.getRow(72);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData111[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData111[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strData111[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strData111[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Fail");

						}

						if (strData111.length >= 2) {
							rowParent = sheet1.getRow(74);
							cell = rowParent.createCell(3);
							cell1 = rowParent.createCell(4);
							cell2 = rowParent.createCell(2);

							/* Get Excel data */
							excelData = getCellData(rowParent);
							cell2.setCellValue(strData111[1].replace("/", "").toString());
							cell.setCellStyle(styleChild);
							cell.setCellValue(strVal);
							cell.setCellStyle(styleChild);
							if (strData111[1].replace("/", "").toString()
									.equalsIgnoreCase(excelData.toString().trim())) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
												+ strData111[1].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
												+ " ,Pass");
							} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
								cell1.setCellValue("");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
												+ strData111[1].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
												+ " ,Fail");

							}
						}
					} else {
						String strCellData="";
						if (strData111.length >= 2) {
							strCellData=strData111[1].replace("/", "").toString();
						} else {
							strCellData=strData111[0].replace("/", "").toString();
						}
						rowParent = sheet1.getRow(74);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strCellData);
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strCellData.equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strData111[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strData111[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Fail");

						}
					}
					break;
				case "59":
					rowParent = sheet1.getRow(68);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);

					/* Get Excel data */
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1.trim().split(" ")[0].replace("/", ""));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().split(" ")[0].replace("/", "").toString()
							.equalsIgnoreCase(excelData.toString().trim())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}

					/* Check for the second data */
					String[] strDataVal = strVal1.trim().split(" ");
					if (strDataVal.length >= 2) {
						String strCellData = strVal1.trim().replace(strDataVal[0], "");
						rowParent = sheet1.getRow(100);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strCellData);
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strCellData.replace("  ", "").replace("//s", "").replace(" ", "").toString()
								.equalsIgnoreCase(excelData.replaceAll("[ \t\n\r]*", "").replace("  ", "")
										.replace("//s", "").toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strCellData.replace("  ", "").replace("//s", "").replace(" ", "").replaceAll(",", " ")
													.toString()
											+ " for the Swift Field -" + strVal + " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strCellData.replace("  ", "").replace("//s", "").replace(" ", "").replaceAll(",", " ")
													.toString()
											+ " for the Swift Field -" + strVal + " ,Fail");

						}
					}
					break;
				case "59A":
					String[] strData = strVal1.trim().split(" ");
					if (isNumeric(strData[0].replace("/", ""))) {
						rowParent = sheet1.getRow(68);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strData[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strData[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Fail");

						}

						if (strData.length >= 2) {
							rowParent = sheet1.getRow(67);// 74
							cell = rowParent.createCell(3);
							cell1 = rowParent.createCell(4);
							cell2 = rowParent.createCell(2);

							/* Get Excel data */
							excelData = getCellData(rowParent);
							cell2.setCellValue(strData[1].replace("/", "").toString());
							cell.setCellStyle(styleChild);
							cell.setCellValue(strVal);
							cell.setCellStyle(styleChild);
							if (strData[1].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
												+ strData[1].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
												+ " ,Pass");
							} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
								cell1.setCellValue("");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
												+ strData[1].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
												+ " ,Fail");

							}
						}

					} else {
						rowParent = sheet1.getRow(67);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strData[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strData[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Fail");

						}
					}
					break;
				case "58A":
					String[] strData1 = strVal1.trim().split("#");
					if (strData1.length >= 2 ) {
						rowParent = sheet1.getRow(68);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData1[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData1[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim().replace(" ", ""))) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strData1[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strData1[1].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Fail");

						}

						rowParent = sheet1.getRow(67);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData1[1].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData1[1].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strData1[1].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strData1[1].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Fail");

						}

					} else {
						rowParent = sheet1.getRow(67);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);

						/* Get Excel data */
						excelData = getCellData(rowParent);
						cell2.setCellValue(strData1[0].replace("/", "").toString());
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strData1[0].replace("/", "").toString().equalsIgnoreCase(excelData.toString().trim())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -"
											+ strData1[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
											+ strData1[0].replace("/", "").replaceAll(",", " ") + " for the Swift Field -" + strVal
											+ " ,Fail");
						}
					}
					break;
				case "71A":
					rowParent = sheet1.getRow(103);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().equalsIgnoreCase(excelData.trim())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				
				case "56A":
					String strData56A = strVal1.split(" ")[0].replace("/", "");
					rowParent = sheet1.getRow(91);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData56A);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData56A.trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strData56A.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strData56A.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "72":
					String strData572 = strVal1.replace("  ", "");
					rowParent = sheet1.getRow(86);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData572);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData572.replace("  ", "").replace("//s", "").replace(" ", "").trim()
							.equalsIgnoreCase(excelData.replaceAll("[ \t\n\r]*", "").replace("  ", "")
									.replace("//s", "").trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strData572.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strData572.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "70":
					String strData70 = strVal1.replace("  ", "");
					rowParent = sheet1.getRow(99);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData70);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData70.trim().toLowerCase().replace("//s", "")
							.equals(excelData.replace("/n", "").replace("//s", "").trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is same as the value of the Swift Message -" + strData70.replaceAll(",", " ")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", " ") + " is not same as the value of the Swift Message -"
										+ strData70.replaceAll(",", " ") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				default:
					break;
				}
			}
			FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
			wb.write(outFile);
			outFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to Validate field for 541 and 543
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void swiftFieldsof541And543(String fileResultPath, String strInboundDecryptedMsg) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet1 = wb.getSheet("DataValidation");

			/* Style for child and status */
			CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
			CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
			CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

			Cell cell;
			Row rowParent;
			Cell cell1;
			strInboundDecryptedMsg.replace(" ", "");
			String lines[] = strInboundDecryptedMsg.split("\\r?\\n");
			int countAmount = 0;

			/*
			 * code to add next line to append in the same line if it is continuation of the
			 * previous line for comments
			 */
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < lines.length; i++) {
				lines[i].replace("-}TRAILER:", "").replace("}TRAILER", "").replace("-}TRAILER", "");
				if (lines[i].isEmpty()) {
					continue;
				}
				int j = 0;
				if (lines[i].contains("35B")) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(" " + lines[j].trim() + " ");
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					break;
				}
			}

			/* Switch case for each line */
			for (String string : lines) {
				string = string.replace("-}TRAILER:", "").replace("}TRAILER", "").replace("-}TRAILER", "").replace("::",
						":");
				if (string.isEmpty()) {
					continue;
				}
				String strVal = "";
				String strVal1 = "";
				if (!string.contains(":")) {
					continue;
				} else if (string.charAt(0) == ':') {
					strVal = string.split(":")[1].trim();
					strVal1 = string.split(":")[2].trim();
				} else {
					strVal = string.split(":")[0].trim();
					strVal1 = string.split(":")[1].trim();
				}
				System.out.println("strVal -" + strVal);
				System.out.println("strVal1 -" + strVal1);
				String excelData = "";
				Cell cell2 = null;
				switch (strVal) {
				case "35B":
					rowParent = sheet1.getRow(23);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "20C":
					if (strVal1.contains("//")) {
						strVal1 = strVal1.split("//")[1];
					}
					rowParent = sheet1.getRow(12);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -" + strVal1.replaceAll(",", "")
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					
					
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
										+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "19A":
					if (strVal1.contains("SETT")) {
						String strCurrency = "";
						if (strVal1.contains("//")) {
							strCurrency = strVal1.split("//")[1].substring(0, 3);
						}

						/* Code for currency */
						rowParent = sheet1.getRow(7);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);
						excelData = getCellData(rowParent);
						cell2.setCellValue(strCurrency);
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -"
											+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
											+ strVal1.replaceAll(",", "") + " for the Swift Field -" + strVal + " ,Fail");

						}

						/* Code for amount */
						rowParent = sheet1.getRow(5);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);
						excelData = getCellData(rowParent);
						DecimalFormat df = new DecimalFormat("#.##");
						cell2.setCellValue(new Double(
								df.format(new Double(strVal1.trim().split("//")[1].substring(3).replace(",", ".")))));
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (new Double(
								df.format(new Double(strVal1.trim().split("//")[1].substring(3).replace(",", "."))))
										.toString().toLowerCase().contains(excelData.trim().toLowerCase())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", "") + " is same as the value of the Swift Message -"
											+ new Double(df.format(new Double(
													strVal1.trim().split("//")[1].substring(3).replace(",", "."))))
															.toString().replaceAll(",", "")
											+ " for the Swift Field -" + strVal + " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim().replaceAll(",", "") + " is not same as the value of the Swift Message -"
											+ new Double(df.format(new Double(
													strVal1.trim().split("//")[1].substring(3).replace(",", "."))))
															.toString().replaceAll(",", "")
											+ " for the Swift Field -" + strVal + " ,Fail");

						}
					}

					break;

				case "97A":
				case "97B":
				case "97E":
					if (strVal1.contains("SAFE") && countAmount == 0) {
						String strData = "";
						if (strVal1.contains("//")) {
							strData = strVal1.split("//")[1];
						}
						rowParent = sheet1.getRow(19);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);
						excelData = getCellData(rowParent);
						cell2.setCellValue(new Integer(strData));
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (new Integer(strData).toString().toLowerCase().contains(excelData.trim().toLowerCase())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ strVal1 + " for the Swift Field -" + strVal + " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ strVal1 + " for the Swift Field -" + strVal + " ,Fail");

						}
						countAmount++;
					}
					break;
				case "98A":
					if (strVal1.contains("SETT")) {
						String strData = "";
						if (strVal1.contains("//")) {
							strData = strVal1.split("//")[1];
						}
						DateTimeFormatter formatter = null;
						if (strVal1.contains("-") && strVal1.length() == 8) {
							formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
						} else if (strVal1.contains("/") && strVal1.length() == 8) {
							formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

						} else if (!strData.contains("-") && !strData.contains("/") && strData.length() == 8) {
							formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
						} else {
							formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
									.append(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toFormatter();

						}
						LocalDate ldt = LocalDate.parse(strData, formatter);
						rowParent = sheet1.getRow(13);
						cell = rowParent.createCell(3);
						cell1 = rowParent.createCell(4);
						cell2 = rowParent.createCell(2);
						excelData = getCellData(rowParent);
						cell2.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt));
						cell.setCellStyle(styleChild);
						cell.setCellValue(strVal);
						cell.setCellStyle(styleChild);
						if (DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim().toLowerCase()
								.contains(excelData.trim().toLowerCase())) {
							cell1.setCellValue("Pass");
							cell1.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is same as the value of the Swift Message -"
											+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt)
											+ " for the Swift Field -" + strVal + " ,Pass");
						} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
							cell1.setCellValue("");
						} else {
							cell1.setCellValue("Fail");
							cell1.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
											+ excelData.trim() + " is not same as the value of the Swift Message -"
											+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt)
											+ " for the Swift Field -" + strVal + " ,Fail");

						}
					}
					break;
				default:
					break;
				}
			}
			FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
			wb.write(outFile);
			outFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to Validate swift field for 599
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void swiftFieldsof599(String fileResultPath, String strInboundDecryptedMsg) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet = wb.getSheet("DataValidation");

			/* Style for child and status */
			CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
			CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
			CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

			Cell cell;
			Row rowParent;
			Cell cell1;
			strInboundDecryptedMsg.replace(" ", "");
			String lines[] = strInboundDecryptedMsg.split("\\r?\\n");

			/* Switch case for each line */
			for (String string : lines) {
				string = string.replace("}TRAILER", "").replace("-}TRAILER", "");
				if (string.isEmpty()) {
					continue;
				}
				String strVal = "";
				String strVal1 = "";
				if (string.contains("COMMENT DEAL QUANTUM")) {
					strVal = "COMMENT DEAL QUANTUM";
					strVal1 = string.split(":")[1].trim();
				} else if (string.charAt(0) == ':') {
					strVal = string.split(":")[1].trim();
					strVal1 = string.split(":")[2].trim();
				} else {
					strVal = string.split(":")[0].trim();
					strVal1 = string.split(":")[1].trim();
				}
				System.out.println("strVal -" + strVal);
				System.out.println("strVal1 -" + strVal1);
				String excelData = "";
				Cell cell2 = null;
				switch (strVal) {
				case "20":
					rowParent = sheet.getRow(12);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1
										+ " for the Swift Field -" + strVal + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strVal1 + " for the Swift Field -" + strVal + " ,Fail");

					}
					break;
				case "IFCACNBR":
					rowParent = sheet.getRow(19);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer("79"));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1
										+ " for the Swift Field -" + "79" + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strVal1 + " for the Swift Field -" + "79" + " ,Fail");

					}
					break;
				case "FUNDCURR":
					rowParent = sheet.getRow(7);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer("79"));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1
										+ " for the Swift Field -" + "79" + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strVal1 + " for the Swift Field -" + "79" + " ,Fail");

					}
					break;
				case "COMMENT DEAL QUANTUM":
					rowParent = sheet.getRow(23);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer("79"));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -" + strVal1
										+ " for the Swift Field -" + "79" + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ strVal1 + " for the Swift Field -" + "79" + " ,Fail");

					}
					break;
				case "FUNDAMT":
					DecimalFormat df = new DecimalFormat("#.##");
					rowParent = sheet.getRow(5);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(new Double(df.format(new Double(strVal1.replace(",", "")))));
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer("79"));
					cell.setCellStyle(styleChild);
					if (new Double(df.format(new Double(strVal1.replace(",", "")))).toString()
							.contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -"
										+ new Double(df.format(new Double(strVal1.replace(",", "")))).toString()
										+ " for the Swift Field -" + "79" + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ new Double(df.format(new Double(strVal1.replace(",", "")))).toString()
										+ " for the Swift Field -" + "79" + " ,Fail");

					}
					break;

				case "VALUEDATE":
					DateTimeFormatter formatter = null;
					if (strVal1.contains("-") && strVal1.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					} else if (strVal1.contains("/") && strVal1.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");
					} else {
						formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
								.append(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toFormatter();

					}
					LocalDate ldt = LocalDate.parse(strVal1, formatter);
					rowParent = sheet.getRow(13);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt));
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer("79"));
					cell.setCellStyle(styleChild);
					if (DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim().toLowerCase()
							.contains(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value of the Swift Message -"
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim()
										+ " for the Swift Field -" + "79" + " ,Pass");
					} else if (excelData.trim().isEmpty() || excelData.trim() == "") {
						cell1.setCellValue("");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value of the Swift Message -"
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim()
										+ " for the Swift Field -" + "79" + " ,Fail");

					}
					break;
				default:
					break;
				}
			}
			FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
			wb.write(outFile);
			outFile.close();
		} catch (

		Exception e) {
			e.printStackTrace();
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Method to get cell data
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	private static String getCellData(Row rowParent) {
		String excelData;
		if (rowParent.getCell(1).getCellType() == CellType.NUMERIC) {
			excelData = String.valueOf(rowParent.getCell(1).getNumericCellValue());
		} else {
			excelData = rowParent.getCell(1).getStringCellValue();
		}
		return excelData;
	}
	/*
	 * @description : Method to set Node and value in excel
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */

	public static void setNodeAndValueExcel(Sheet sheet, CellStyle styleParent, CellStyle styleChild,
			Map<String, String> strNodeAndData) {

		/* Copy all data from hashMap into TreeMap */
		TreeMap<String, String> sorted = new TreeMap<>();
		sorted.putAll(strNodeAndData);
		for (Map.Entry<String, String> entry : sorted.entrySet()) {
			Row rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
			Cell cell1 = rowParent.createCell(0);
			Cell cell2 = rowParent.createCell(1);
			cell1.setCellValue(entry.getKey());
			cell1.setCellStyle(styleChild);

			/* Set data */
			if (entry.getValue().isEmpty() || entry.getValue() == "") {
				cell2.setCellValue("");
			} else {
				switch (entry.getKey()) {
				case "amount":
					BigDecimal b1 = new BigDecimal(entry.getValue());
					cell2.setCellValue(new Double(b1.stripTrailingZeros().toPlainString()));
					break;
				case "dealdate":
					DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					LocalDate ldt = LocalDate.parse(entry.getValue(), formatter);
					cell2.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt));
					break;
				case "valuedate":
					DateTimeFormatter formatter1 = null;
					if (entry.getValue().contains("-") && entry.getValue().split("-")[0].length() == 4) {
						formatter1 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					} else {
						formatter1 = DateTimeFormatter.ofPattern("yyyyMMdd");
					}
					LocalDate ldt1 = LocalDate.parse(entry.getValue(), formatter1);
					cell2.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt1));
					break;
				default:
					cell2.setCellValue(entry.getValue());
				}
			}
			cell2.setCellStyle(styleChild);
		}
	}

	/*
	 * @description : Method to check numeric
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static boolean isNumeric(String strNum) {
		Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
		if (strNum == null) {
			return false;
		}
		return pattern.matcher(strNum).matches();
	}

	/*
	 * @description : Method to evaluate xpath of xml document
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static HashMap<String, String> evaluateXPathDataNode(Document document, String xpathExpression)
			throws Exception {
		HashMap<String, String> values = new HashMap<>();
		try {
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpathData = xPathFactory.newXPath();
			XPathExpression expression = xpathData.compile(xpathExpression);
			Object result = expression.evaluate(document, XPathConstants.NODE);
			NodeList nodeList1 = (NodeList) result;
			for (int i = 0; i < nodeList1.getLength(); i++) {
				if (!nodeList1.item(i).getNodeName().toLowerCase().contains("#text")) {
					values.put(nodeList1.item(i).getNodeName().trim().toLowerCase(),
							nodeList1.item(i).getTextContent());
				}
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");

		}
		return values;
	}

	/*
	 * @description : Method to evaluate data of xml document
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void XmlDataValidation304(String strDealerName, String resultPath, int rowNumber, String Analyse05,
			String strPaymentMethodInput, String strEntityInput, String strInstrumentInput)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		try {
			for (int k = 1; k <= 2; k++) {
				String fileResultPath = System.getProperty("user.dir").replace("\\", "/") + "/" + resultPath
						+ "/ExcelReportValidation" + k + ".xlsx";
				Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
				Sheet sheet = wb.createSheet("DataValidation");

				/* Setting Foreground Color for header */
				Row row = sheet.createRow(0);

				/* Style for header */
				CellStyle style = DataBaseXmlStructureValidation.cellStyleHeader(wb);

				/* Set Header */
				Cell cell = row.createCell(0);
				cell.setCellValue("GeneratedRFP nodes");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("RFP Values");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("Swiftvalues");
				cell.setCellStyle(style);
				cell = row.createCell(3);
				cell.setCellValue("Swiftfield");
				cell.setCellStyle(style);
				cell = row.createCell(4);
				cell.setCellValue("Results");
				cell.setCellStyle(style);

				/* Style for child and status */
				CellStyle styleParent = DataBaseXmlStructureValidation.CellParentStyle(wb);
				CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
				CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
				CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

				/* Get DOM Node for XML */
				String fileName = resultPath + "/MqOutQDecyptedMsg" + k + ".xml";
				Document document = getDocument(fileName);
				Map<String, String> strNodeAndData = new HashMap<>();

				/* Write node and its value in the sheet for process */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Process");
				Row rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				Cell cell1 = rowParent.createCell(0);
				cell1.setCellValue("Process");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for cashflow */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Cashflow");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("Cashflow");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for purpose */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Purpose");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("Purpose");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for Bank Account */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/BankAccount");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("BankAccount");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for other */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Other");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("Other");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for Analysis Codes */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/AnalysisCodes");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("AnalysisCodes");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for Our Instructions */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/OurInstructions");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("OurInstructions");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for Beneficiary */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Beneficiary");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("Beneficiary");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for BeneBank */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/BeneBank");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("BeneBank");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for DeliveryNarrative */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/DeliveryNarrative");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("DeliveryNarrative");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for IntermediaryBank */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/IntermediaryBank");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("IntermediaryBank");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for BeneficiaryDetails */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/BeneficiaryDetails");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("BeneficiaryDetails");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for Charges */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Charges");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("Charges");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for SendersCharges */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Charges/SendersCharges");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("SendersCharges");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for ReceiversCharges */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/TheirInstructions/Charges/ReceiversCharges");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("ReceiversCharges");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Write node and its value in the sheet for Custom */
				strNodeAndData = evaluateXPathDataNode(document, "//IFCRFP/Custom");
				rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				cell1 = rowParent.createCell(0);
				cell1.setCellValue("Custom");
				cell1.setCellStyle(styleParent);
				cell1 = rowParent.createCell(1);
				cell1.setCellValue("");
				cell1.setCellStyle(styleChild);
				setNodeAndValueExcel(sheet, styleParent, styleChild, strNodeAndData);

				/* Set border for all column for status */
				int totalrowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
				for (int i = 1; i <= totalrowCount; i++) {
					rowParent = sheet.getRow(i);
					cell = rowParent.createCell(4);
					cell.setCellValue("");
					cell.setCellStyle(styleChild);
				}

				/* Write Mandatory field data in the Excel */
				int actionCount, instrumentCount, entityCount, paymentmethodCount, dealerCount, analyse05Count,
						analyse01Count;
				actionCount = instrumentCount = entityCount = paymentmethodCount = dealerCount = analyse05Count = analyse01Count = 0;
				int rowCount = sheet.getLastRowNum() - sheet.getFirstRowNum();
				for (int i = 1; i <= rowCount; i++) {
					rowParent = sheet.getRow(i);
					Cell cell2 = rowParent.getCell(0);
					String cellValue = cell2.getStringCellValue();
					switch (cellValue) {
					case "action":
						if (actionCount == 0) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue("I");
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase("I")) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + "I"
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -" + "I"
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");

							}
							actionCount++;
						}
						break;
					case "instrument":
						if (instrumentCount == 0) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue(strInstrumentInput);
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strInstrumentInput)) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + strInstrumentInput
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -"
												+ strInstrumentInput + " for the Swift Field -"
												+ "Mandatory Non-Swift Fields" + " ,Fail");

							}
							instrumentCount++;
						}

						break;
					case "entity":
						if (entityCount == 0) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue(strEntityInput);
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strEntityInput)) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + strEntityInput
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -" + strEntityInput
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");

							}
							entityCount++;
						}
						break;
					case "paymentmethod":
						if (paymentmethodCount == 0) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue(strPaymentMethodInput);
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strPaymentMethodInput)) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + strPaymentMethodInput
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -"
												+ strPaymentMethodInput + " for the Swift Field -"
												+ "Mandatory Non-Swift Fields" + " ,Fail");

							}
							paymentmethodCount++;
						}
						break;
					case "dealer":
						if (dealerCount == 0) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue(strDealerName.substring(0, 1).toUpperCase()
									+ strDealerName.substring(1).toLowerCase());
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(strDealerName)) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + strDealerName
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -" + strDealerName
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
							}
							dealerCount++;
						}
						break;
					case "analyse05":
						if (analyse05Count == 0) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue(Analyse05);
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase(Analyse05)) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + Analyse05
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -" + Analyse05
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");
								Assert.fail();
							}
							analyse05Count++;
						}
						break;
					case "analyse01":
						if (analyse01Count == 0 && k == 1) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue("202");
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase("202")) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + "202"
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -" + "202"
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");

							}
							analyse01Count++;
						} else if (k == 2) {
							cell1 = rowParent.createCell(2);
							cell1.setCellValue("210");
							cell1.setCellStyle(styleChild);
							cell = rowParent.createCell(3);
							cell.setCellValue("Mandatory Non-Swift Fields");
							cell.setCellStyle(styleChild);
							cell1 = rowParent.createCell(4);
							if (rowParent.getCell(1).getStringCellValue().equalsIgnoreCase("210")) {
								cell1.setCellValue("Pass");
								cell1.setCellStyle(styleStatusPass);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is same as the value of the Swift Message -" + "210"
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Pass");
							} else {
								cell1.setCellValue("Fail");
								cell1.setCellStyle(styleStatusFail);
								WebElementWrappers.Reporter(driver,
										"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
												+ rowParent.getCell(1).getStringCellValue()
												+ " is not same as the value of the Swift Message -" + "210"
												+ " for the Swift Field -" + "Mandatory Non-Swift Fields" + " ,Fail");

							}
						}
						break;
					default:
						cell1 = rowParent.createCell(2);
						cell1.setCellValue("");
						cell1.setCellStyle(styleChild);
						cell = rowParent.createCell(3);
						cell.setCellValue("");
						cell.setCellStyle(styleChild);
						break;
					}
				}

				sheet.autoSizeColumn(0);
				sheet.autoSizeColumn(1);
				sheet.autoSizeColumn(2);
				sheet.autoSizeColumn(3);
				sheet.autoSizeColumn(4);

				FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
				wb.write(outFile);
				outFile.close();

				/* Get text file data */
				String strInboundDecryptedMsg = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath).trim();
				if (k == 1) {
					swiftFieldsValidation3041(fileResultPath, strInboundDecryptedMsg);
					verifyStatus(fileResultPath);
				} else if (k == 2) {
					swiftFieldsValidation3042(fileResultPath, strInboundDecryptedMsg);
					verifyStatus(fileResultPath);
				}
			}

		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			e.printStackTrace();
			Assert.fail();
		}
	}

	/*
	 * @description : Method to Validate swift field
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void swiftFieldsValidation3041(String fileResultPath, String strInboundDecryptedMsg) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet1 = wb.getSheet("DataValidation");

			/* Style for child and status */
			CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
			CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
			CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

			Cell cell;
			Row rowParent;
			Cell cell1;
			strInboundDecryptedMsg.replace(" ", "");
			String lines[] = strInboundDecryptedMsg.split("\\r?\\n");

			/*
			 * code to add next line to append in the same line if it is continuation of the
			 * previous line for comments
			 */
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < lines.length; i++) {
				lines[i].replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::", ":");
				if (lines[i].isEmpty()) {
					continue;
				}
				int j = 0;
				if ((lines[i].contains("70") || lines[i].contains("50A") || lines[i].contains("53A")
						|| lines[i].contains("57A") || lines[i].contains("58A") || lines[i].contains("59"))
						&& (!lines[i].contains("HEADER:"))) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(" " + lines[j].trim() + " ");
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} else if (lines[i].contains("72")) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} else if (lines[i].contains("50K")) {
					StringBuffer str = new StringBuffer();
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							str.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.append("#").append(str.toString()).toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} else {

				}
			}

			/* Switch case for each line */
			for (String string : lines) {
				string = string.replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::",
						":");
				if (string.isEmpty()) {
					continue;
				}
				String strVal = "";
				String strVal1 = "";
				System.out.println("string - " + string);
				if (!string.contains(":")) {
					continue;
				} else if (string.charAt(0) == ':') {
					strVal = string.split(":")[1].trim();
					if (string.split(":").length > 2) {
						strVal1 = string.split(":")[2].trim();
					}

				} else {
					strVal = string.split(":")[0].trim();
					strVal1 = string.split(":")[1].trim();
				}
				System.out.println("strVal -" + strVal);
				System.out.println("strVal1 -" + strVal1);
				String excelData = "";
				Cell cell2 = null;
				switch (strVal) {
				case "20":
					rowParent = sheet1.getRow(12);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ strVal1.trim() + " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ strVal1.trim() + " for the Swift Field - " + strVal + ",Fail");

					}
					break;
				case "33B":
					String strCurrency = strVal1.trim().substring(0, 3);
					String strAmount = strVal1.trim().substring(3);

					/* Code for currency */
					rowParent = sheet1.getRow(7);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strCurrency);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().substring(0, 3).trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ strVal1.trim().substring(0, 3) + " for the Swift Field - " + strVal
										+ ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ strVal1.trim().substring(0, 3) + " for the Swift Field - " + strVal
										+ ",Fail");

					}

					/* Code for amount */
					rowParent = sheet1.getRow(5);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					DecimalFormat df = new DecimalFormat("#.##");
					cell2.setCellValue(new Double(df.format(new Double(strAmount.replace(",", ".")))));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().toLowerCase()
							.equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString()
										+ " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString()
										+ " for the Swift Field - " + strVal + ",Fail");

					}
					break;
				case "83J":
					String strData = strVal1.split("/")[2];
					rowParent = sheet1.getRow(19);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - " + strData
										+ " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ strData + " for the Swift Field - " + strVal + ",Fail");

					}
					break;
				case "30V":
					String strValueDate = strVal1;
					DateTimeFormatter formatter = null;
					if (strValueDate.contains("-") && strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					} else if (strValueDate.contains("/") && strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

					} else if (!strValueDate.contains("-") && !strValueDate.contains("/")
							&& strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					} else {
						formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
								.append(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toFormatter();

					}
					LocalDate ldt = LocalDate.parse(strValueDate, formatter);
					rowParent = sheet1.getRow(13);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim().toLowerCase()
							.equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim()
										+ " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim()
										+ " for the Swift Field - " + strVal + ",Fail");
					}
				default:
					break;
				}
			}
			FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
			wb.write(outFile);
			outFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to Validate swift field
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void swiftFieldsValidation3042(String fileResultPath, String strInboundDecryptedMsg) {
		try {
			Workbook wb = WorkbookFactory.create(new FileInputStream(new File(fileResultPath)));
			Sheet sheet1 = wb.getSheet("DataValidation");

			/* Style for child and status */
			CellStyle styleChild = DataBaseXmlStructureValidation.CellChildStyle(wb);
			CellStyle styleStatusPass = DataBaseXmlStructureValidation.CellStatusPassStyle(wb);
			CellStyle styleStatusFail = DataBaseXmlStructureValidation.CellStatusFailStyle(wb);

			Cell cell;
			Row rowParent;
			Cell cell1;
			strInboundDecryptedMsg.replace(" ", "");
			String lines[] = strInboundDecryptedMsg.split("\\r?\\n");

			/*
			 * code to add next line to append in the same line if it is continuation of the
			 * previous line for comments
			 */
			StringBuffer sb = new StringBuffer();
			for (int i = 0; i < lines.length; i++) {
				lines[i].replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::", ":");
				if (lines[i].isEmpty()) {
					continue;
				}
				int j = 0;
				if ((lines[i].contains("70") || lines[i].contains("50A") || lines[i].contains("53A")
						|| lines[i].contains("57A") || lines[i].contains("58A") || lines[i].contains("59"))
						&& (!lines[i].contains("HEADER:"))) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(" " + lines[j].trim() + " ");
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} else if (lines[i].contains("72")) {
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							sb.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} else if (lines[i].contains("50K")) {
					StringBuffer str = new StringBuffer();
					sb.append(lines[i].trim());
					for (j = i + 1; j < lines.length; j++) {
						if (lines[j].contains(":")) {
							break;
						} else {
							str.append(lines[j].trim().replace("//", ""));
						}
					}
					lines[i] = sb.append("#").append(str.toString()).toString().replace("  ", " ").trim();
					sb = new StringBuffer();
				} else {

				}
			}

			/* Switch case for each line */
			for (String string : lines) {
				string = string.replace("-}TRAILER:", "").replace("-}TRAILER", "").replace("}TRAILER", "").replace("::",
						":");
				if (string.isEmpty()) {
					continue;
				}
				String strVal = "";
				String strVal1 = "";
				if (!string.contains(":")) {
					continue;
				} else if (string.charAt(0) == ':') {
					strVal = string.split(":")[1].trim();
					if (string.split(":").length > 2) {
						strVal1 = string.split(":")[2].trim();
					}
				} else {
					strVal = string.split(":")[0].trim();
					strVal1 = string.split(":")[1].trim();
				}
				System.out.println("strVal -" + strVal);
				System.out.println("strVal1 -" + strVal1);
				String excelData = "";
				Cell cell2 = null;
				switch (strVal) {
				case "20":
					rowParent = sheet1.getRow(12);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strVal1);
					cell.setCellStyle(styleChild);
					cell.setCellValue(new Integer(strVal));
					cell.setCellStyle(styleChild);
					if (strVal1.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ strVal1.trim() + " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ strVal1.trim() + " for the Swift Field - " + strVal + ",Fail");
					}
					break;
				case "32B":
					String strCurrency = strVal1.trim().substring(0, 3);
					String strAmount = strVal1.trim().substring(3);

					/* Code for currency */
					rowParent = sheet1.getRow(7);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strCurrency);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strVal1.trim().substring(0, 3).trim().toLowerCase().equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ strCurrency + " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ strCurrency + " for the Swift Field - " + strVal + ",Fail");
					}

					/* Code for amount */
					rowParent = sheet1.getRow(5);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					DecimalFormat df = new DecimalFormat("#.##");
					cell2.setCellValue(new Double(df.format(new Double(strAmount.replace(",", ".")))));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().toLowerCase()
							.equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().toLowerCase() + " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ new Double(df.format(new Double(strAmount.replace(",", ".")))).toString().toLowerCase() + " for the Swift Field - " + strVal + ",Fail");

					}
					break;
				case "83J":
					String strData = strVal1.split("/")[2];
					rowParent = sheet1.getRow(19);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(strData);
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (strData.trim().toLowerCase().replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
							.equalsIgnoreCase(excelData.replace(" ", "").replaceAll("//s", "").replaceAll("  ", "")
									.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - " + strData
										+ " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ strData + " for the Swift Field - " + strVal + ",Fail");
					}
					break;
				case "30V":
					String strValueDate = strVal1;
					DateTimeFormatter formatter = null;
					if (strValueDate.contains("-") && strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd");
					} else if (strValueDate.contains("/") && strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyy/MM/dd");

					} else if (!strValueDate.contains("-") && !strValueDate.contains("/")
							&& strValueDate.length() == 8) {
						formatter = DateTimeFormatter.ofPattern("yyyyMMdd");
					} else {
						formatter = new DateTimeFormatterBuilder().parseCaseInsensitive()
								.append(DateTimeFormatter.ofPattern("dd-MMM-yyyy")).toFormatter();

					}
					LocalDate ldt = LocalDate.parse(strValueDate, formatter);
					rowParent = sheet1.getRow(13);
					cell = rowParent.createCell(3);
					cell1 = rowParent.createCell(4);
					cell2 = rowParent.createCell(2);
					excelData = getCellData(rowParent);
					cell2.setCellValue(DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt));
					cell.setCellStyle(styleChild);
					cell.setCellValue(strVal);
					cell.setCellStyle(styleChild);
					if (DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim().toLowerCase()
							.equals(excelData.trim().toLowerCase())) {
						cell1.setCellValue("Pass");
						cell1.setCellStyle(styleStatusPass);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is same as the value for the Swift Message - "
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim()
										+ " for the Swift Field - " + strVal + ",Pass");
					} else {
						cell1.setCellValue("Fail");
						cell1.setCellStyle(styleStatusFail);
						WebElementWrappers.Reporter(driver,
								"Verify whether the value for the RFP is same as the value for the Swift Message,The Value for the RFP should be same as the value for the Swift Message,The Value for the RFP- "
										+ excelData.trim() + " is not same as the value for the Swift Message - "
										+ DateTimeFormatter.ofPattern("dd/MM/yyyy", Locale.ENGLISH).format(ldt).trim()
										+ " for the Swift Field - " + strVal + ",Fail");

					}
				default:
					break;
				}
			}
			FileOutputStream outFile = new FileOutputStream(new File(fileResultPath));
			wb.write(outFile);
			outFile.close();
		} catch (Exception e) {
			e.printStackTrace();
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}
}
