package com.wbg.selenium.qa.database;

import org.apache.poi.ss.usermodel.BorderStyle;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.FillPatternType;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.HorizontalAlignment;
import org.apache.poi.ss.usermodel.IndexedColors;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.testng.Assert;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import com.wbg.selenium.qa.manager.WebDriverManager;
import com.wbg.selenium.qa.utils.WebElementWrappers;

import javax.xml.parsers.*;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;
import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class DataBaseXmlStructureValidation extends WebDriverManager {
	public static File file;
	private static int count = 0;

	/*
	 * @description : Method to validate xpath
	 * 
	 * @param : nodes
	 * 
	 * @return : NA
	 * 
	 * @date : 08 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void ValidateXpath(String strNodes, String resultPath, CellStyle styleParent, CellStyle styleChild,
			CellStyle styleStatusPass, CellStyle styleStatusFail, Sheet sheet)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setNamespaceAware(true);
			dbf.setCoalescing(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setIgnoringComments(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc1 = db.parse(new File("src/test/resources/TestData/IFCRFP.xml"));
			doc1.normalizeDocument();
			Document doc2 = db.parse(new File(resultPath + "/MqOutQDecyptedMsg.xml"));
			doc2.normalizeDocument();

			/* Get The xpath */
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression expression = xpath.compile(strNodes);
			Object result = expression.evaluate(doc1, XPathConstants.NODE);
			NodeList nodeList = (NodeList) result;
			List<String> recieversChargesExpected = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (!nodeList.item(i).getNodeName().toLowerCase().contains("#text")) {
					recieversChargesExpected.add(nodeList.item(i).getNodeName().trim().toLowerCase());
				}
			}

			Object result1 = expression.evaluate(doc2, XPathConstants.NODE);
			NodeList nodeList1 = (NodeList) result1;
			List<String> recieversChargesActual = new ArrayList<>();
			for (int i = 0; i < nodeList1.getLength(); i++) {
				if (!nodeList1.item(i).getNodeName().toLowerCase().contains("#text")) {
					recieversChargesActual.add(nodeList1.item(i).getNodeName().trim().toLowerCase());
				}
			}

			/* Validate the nodes */
			if (recieversChargesActual.isEmpty() && recieversChargesExpected.isEmpty()) {
				WebElementWrappers.Reporter(driver,
						"Verify that the node is validated,Node should be validated,Node is validated, Pass");
				Assert.assertTrue(true, "Verify that the node is validated");
			} else {
				Row rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				String actualChileNodes = recieversChargesActual.stream().map(Object::toString)
						.collect(Collectors.joining("#"));
				String expectedChileNodes = recieversChargesExpected.stream().map(Object::toString)
						.collect(Collectors.joining("#"));
				Boolean boolean1 = recieversChargesExpected.removeAll(recieversChargesActual);
				Boolean boolean2 = recieversChargesExpected.isEmpty();
				if (boolean1 && boolean2) {
					Cell cell = rowParent.createCell(0);
					String[] strArr = strNodes.split("//")[1].split("/");
					cell.setCellValue(strNodes.split("//")[1].split("/")[strArr.length - 1]);
					cell.setCellStyle(styleParent);
					cell = rowParent.createCell(1);
					cell.setCellValue(strNodes.split("//")[1].split("/")[strArr.length - 1]);
					cell.setCellStyle(styleParent);
					cell = rowParent.createCell(2);
					cell.setCellValue("Pass");
					cell.setCellStyle(styleStatusPass);

					/* code to add child nodes */
					String[] strArrActual = actualChileNodes.split("#");
					String[] strArrExpected = expectedChileNodes.split("#");
					for (int i = 0; i < strArrActual.length; i++) {
						Row rowChildNode = sheet.createRow(sheet.getLastRowNum() + 1);
						cell = rowChildNode.createCell(0);
						cell.setCellValue(strArrExpected[i]);
						cell.setCellStyle(styleChild);

						cell = rowChildNode.createCell(1);
						cell.setCellValue(strArrActual[i]);
						cell.setCellStyle(styleChild);

						cell = rowChildNode.createCell(2);
						if (strArrActual[i].equalsIgnoreCase(strArrExpected[i])) {
							cell.setCellValue("Pass");
							cell.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the two file has same number of Child nodes,The two file should have same number of Child nodes,The Child Nodes for the first file -"
											+ strArrActual[i] + "is same as the Child node for the second file - "
											+ strArrExpected[i] + ",Pass");
						} else {
							cell.setCellValue("Fail");
							cell.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the two file has same number of Child nodes,The two file should have same number of Child nodes,The Child Nodes for the first file -"
											+ strArrActual[i] + "is not same as the Child node for the second file - "
											+ strArrExpected[i] + ",Fail");
							Assert.fail();
						}
					}

					WebElementWrappers.Reporter(driver, "Verify that" + strNodes
							+ " node and its child nodes is present in the XML and validated against the actual xml file,"
							+ strNodes + " node and its child nodes from Original RFP file  -" + expectedChileNodes
							+ " should be present," + strNodes
							+ " node and its child nodes from the Generated RFP File -" + actualChileNodes
							+ " are present,Pass");
				} else {
					WebElementWrappers.Reporter(driver, "Verify that" + strNodes
							+ " node and its child nodes is present in the XML and validated against the actual xml file,"
							+ strNodes
							+ " node and its child nodes should be present in the XML and validated against the actual xml file,"
							+ strNodes
							+ " node and its child nodes is not present in the XML and not validated against the actual xml file,Fail");
					Assert.fail();
				}
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to validate xpath
	 * 
	 * @param : nodes
	 * 
	 * @return : NA
	 * 
	 * @date : 08 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void ValidateXpath304(String strNodes, String resultPath, CellStyle styleParent, CellStyle styleChild,
			CellStyle styleStatusPass, CellStyle styleStatusFail, Sheet sheet)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
		try {
			dbf.setNamespaceAware(true);
			dbf.setCoalescing(true);
			dbf.setIgnoringElementContentWhitespace(true);
			dbf.setIgnoringComments(true);
			DocumentBuilder db = dbf.newDocumentBuilder();
			Document doc1 = db.parse(new File("src/test/resources/TestData/IFCRFP.xml"));
			doc1.normalizeDocument();
			String filePath = "";
			for (int i = 1; i <= 2; i++) {
				filePath = resultPath + "/MqOutQDecyptedMsg" + i + ".xml";
			}
			Document doc2 = db.parse(new File(filePath));
			doc2.normalizeDocument();

			/* Get The xpath */
			XPathFactory xPathFactory = XPathFactory.newInstance();
			XPath xpath = xPathFactory.newXPath();
			XPathExpression expression = xpath.compile(strNodes);
			Object result = expression.evaluate(doc1, XPathConstants.NODE);
			NodeList nodeList = (NodeList) result;
			List<String> recieversChargesExpected = new ArrayList<>();
			for (int i = 0; i < nodeList.getLength(); i++) {
				if (!nodeList.item(i).getNodeName().toLowerCase().contains("#text")) {
					recieversChargesExpected.add(nodeList.item(i).getNodeName().trim().toLowerCase());
				}
			}

			Object result1 = expression.evaluate(doc2, XPathConstants.NODE);
			NodeList nodeList1 = (NodeList) result1;
			List<String> recieversChargesActual = new ArrayList<>();
			for (int i = 0; i < nodeList1.getLength(); i++) {
				if (!nodeList1.item(i).getNodeName().toLowerCase().contains("#text")) {
					recieversChargesActual.add(nodeList1.item(i).getNodeName().trim().toLowerCase());
				}
			}

			/* Validate the nodes */
			if (recieversChargesActual.isEmpty() && recieversChargesExpected.isEmpty()) {
				WebElementWrappers.Reporter(driver,
						"Verify that the node is validated,Node should be validated,Node is validated, Pass");
				Assert.assertTrue(true, "Verify that the node is validated");
			} else {
				Row rowParent = sheet.createRow(sheet.getLastRowNum() + 1);
				String actualChileNodes = recieversChargesActual.stream().map(Object::toString)
						.collect(Collectors.joining("#"));
				String expectedChileNodes = recieversChargesExpected.stream().map(Object::toString)
						.collect(Collectors.joining("#"));
				Boolean boolean1 = recieversChargesExpected.removeAll(recieversChargesActual);
				Boolean boolean2 = recieversChargesExpected.isEmpty();
				if (boolean1 && boolean2) {
					Cell cell = rowParent.createCell(0);
					String[] strArr = strNodes.split("//")[1].split("/");
					cell.setCellValue(strNodes.split("//")[1].split("/")[strArr.length - 1]);
					cell.setCellStyle(styleParent);
					cell = rowParent.createCell(1);
					cell.setCellValue(strNodes.split("//")[1].split("/")[strArr.length - 1]);
					cell.setCellStyle(styleParent);
					cell = rowParent.createCell(2);
					cell.setCellValue("Pass");
					cell.setCellStyle(styleStatusPass);

					/* code to add child nodes */
					String[] strArrActual = actualChileNodes.split("#");
					String[] strArrExpected = expectedChileNodes.split("#");
					for (int i = 0; i < strArrActual.length; i++) {
						Row rowChildNode = sheet.createRow(sheet.getLastRowNum() + 1);
						cell = rowChildNode.createCell(0);
						cell.setCellValue(strArrExpected[i]);
						cell.setCellStyle(styleChild);

						cell = rowChildNode.createCell(1);
						cell.setCellValue(strArrActual[i]);
						cell.setCellStyle(styleChild);

						cell = rowChildNode.createCell(2);
						if (strArrActual[i].equalsIgnoreCase(strArrExpected[i])) {
							cell.setCellValue("Pass");
							cell.setCellStyle(styleStatusPass);
							WebElementWrappers.Reporter(driver,
									"Verify whether the two file has same number of Child nodes,The two file should have same number of Child nodes,The Child Nodes for the first file -"
											+ strArrActual[i] + "is same as the Child node for the second file - "
											+ strArrExpected[i] + ",Pass");
						} else {
							cell.setCellValue("Fail");
							cell.setCellStyle(styleStatusFail);
							WebElementWrappers.Reporter(driver,
									"Verify whether the two file has same number of Child nodes,The two file should have same number of Child nodes,The Child Nodes for the first file -"
											+ strArrActual[i] + "is not same as the Child node for the second file - "
											+ strArrExpected[i] + ",Fail");
							Assert.fail();
						}
					}

					WebElementWrappers.Reporter(driver, "Verify that" + strNodes
							+ " node and its child nodes is present in the XML and validated against the actual xml file,"
							+ strNodes + " node and its child nodes from Original RFP file  -" + expectedChileNodes
							+ " should be present," + strNodes
							+ " node and its child nodes from the Generated RFP File -" + actualChileNodes
							+ " are present,Pass");
				} else {
					WebElementWrappers.Reporter(driver, "Verify that" + strNodes
							+ " node and its child nodes is present in the XML and validated against the actual xml file,"
							+ strNodes
							+ " node and its child nodes should be present in the XML and validated against the actual xml file,"
							+ strNodes
							+ " node and its child nodes is not present in the XML and not validated against the actual xml file,Fail");
					Assert.fail();
				}
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}

	}

	/*
	 * @description : Method to evaluate structure of xml document
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void structureValidation(String resultPath)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		try {

			/* Check for the file */
			file = new File(resultPath + "/ExcelReportValidation.xlsx");

			if (!file.exists()) {
				System.out.println("File does not exist.");
				file = null;
			}

			try (OutputStream fileOut = new FileOutputStream(resultPath + "/ExcelReportValidation.xlsx")) {
				Workbook wb = new XSSFWorkbook();
				Sheet sheet = wb.createSheet("StructureValidation");

				/* Setting Foreground Color for header */
				Row row = sheet.createRow(count);

				/* Style for header */
				CellStyle style = cellStyleHeader(wb);

				/* Set Header */
				Cell cell = row.createCell(0);
				cell.setCellValue("Original RFP file");
				cell.setCellStyle(style);
				cell = row.createCell(1);
				cell.setCellValue("Generated RFP File");
				cell.setCellStyle(style);
				cell = row.createCell(2);
				cell.setCellValue("Results");
				cell.setCellStyle(style);

				/* Style for child and status */
				CellStyle styleParent = CellParentStyle(wb);
				CellStyle styleChild = CellChildStyle(wb);
				CellStyle styleStatusPass = CellStatusPassStyle(wb);
				CellStyle styleStatusFail = CellStatusFailStyle(wb);

				/* Node Validation */
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/Process", resultPath, styleParent, styleChild,
						styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/Cashflow", resultPath, styleParent, styleChild,
						styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/Purpose", resultPath, styleParent, styleChild,
						styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/BankAccount", resultPath, styleParent,
						styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/Other", resultPath, styleParent, styleChild,
						styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/AnalysisCodes", resultPath, styleParent,
						styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/OurInstructions", resultPath, styleParent,
						styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions", resultPath, styleParent,
						styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/Beneficiary", resultPath,
						styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/BeneBank", resultPath,
						styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/DeliveryNarrative", resultPath,
						styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/IntermediaryBank", resultPath,
						styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/BeneficiaryDetails",
						resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/Charges", resultPath,
						styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/Charges/SendersCharges",
						resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/TheirInstructions/Charges/ReceiversCharges",
						resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
				DataBaseXmlStructureValidation.ValidateXpath("//IFCRFP/Custom", resultPath, styleParent, styleChild,
						styleStatusPass, styleStatusFail, sheet);

				sheet.autoSizeColumn(0);
				sheet.autoSizeColumn(1);
				sheet.autoSizeColumn(2);
				sheet.autoSizeColumn(3);
				wb.write(fileOut);
			} catch (Exception e) {
				// TODO: handle exception
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Method to get style for header
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static CellStyle cellStyleHeader(Workbook wb) {
		/* Code for font */
		Font defaultFont = wb.createFont();
		defaultFont.setFontName("Calibri");
		defaultFont.setFontHeightInPoints((short) 11);
		defaultFont.setBold(true);
		defaultFont.setColor(IndexedColors.BLACK.getIndex());

		/* Create style */
		CellStyle style = wb.createCellStyle();
		style = wb.createCellStyle();
		style.setFillForegroundColor(IndexedColors.BRIGHT_GREEN1.index);
		style.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		style.setAlignment(HorizontalAlignment.LEFT);
		style.setBorderTop(BorderStyle.THIN);
		style.setBorderBottom(BorderStyle.THIN);
		style.setBorderLeft(BorderStyle.THIN);
		style.setBorderRight(BorderStyle.THIN);
		style.setFont(defaultFont);
		return style;
	}

	/*
	 * @description : Method to get style for parent node
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static CellStyle CellParentStyle(Workbook wb) {
		/* Code for font */
		Font defaultFont = wb.createFont();
		defaultFont.setFontName("Calibri");
		defaultFont.setFontHeightInPoints((short) 11);
		defaultFont.setColor(IndexedColors.BLACK.getIndex());

		CellStyle styleParent = wb.createCellStyle();
		styleParent = wb.createCellStyle();
		styleParent.setFillForegroundColor(IndexedColors.LIGHT_ORANGE.getIndex());
		styleParent.setFillPattern(FillPatternType.SOLID_FOREGROUND);
		styleParent.setAlignment(HorizontalAlignment.LEFT);
		styleParent.setBorderTop(BorderStyle.THIN);
		styleParent.setBorderBottom(BorderStyle.THIN);
		styleParent.setBorderLeft(BorderStyle.THIN);
		styleParent.setBorderRight(BorderStyle.THIN);
		return styleParent;
	}

	/*
	 * @description : Method to get child node style
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static CellStyle CellChildStyle(Workbook wb) {
		/* Code for font */
		Font defaultFont = wb.createFont();
		defaultFont.setFontName("Calibri");
		defaultFont.setFontHeightInPoints((short) 11);
		defaultFont.setColor(IndexedColors.BLACK.getIndex());

		/* Code for child data */
		CellStyle styleChild = wb.createCellStyle();
		styleChild = wb.createCellStyle();
		styleChild.setAlignment(HorizontalAlignment.LEFT);
		styleChild.setBorderTop(BorderStyle.THIN);
		styleChild.setBorderBottom(BorderStyle.THIN);
		styleChild.setBorderLeft(BorderStyle.THIN);
		styleChild.setBorderRight(BorderStyle.THIN);
		styleChild.setFont(defaultFont);
		return styleChild;
	}

	/*
	 * @description : Method to get style for pass
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static CellStyle CellStatusPassStyle(Workbook wb) {
		/* Code for font */
		Font defaultFont = wb.createFont();
		defaultFont.setFontName("Calibri");
		defaultFont.setFontHeightInPoints((short) 11);
		defaultFont.setColor(IndexedColors.GREEN.getIndex());

		/* Code for child data */
		CellStyle styleChild = wb.createCellStyle();
		styleChild = wb.createCellStyle();
		styleChild.setAlignment(HorizontalAlignment.LEFT);
		styleChild.setBorderTop(BorderStyle.THIN);
		styleChild.setBorderBottom(BorderStyle.THIN);
		styleChild.setBorderLeft(BorderStyle.THIN);
		styleChild.setBorderRight(BorderStyle.THIN);
		styleChild.setFont(defaultFont);
		return styleChild;
	}

	/*
	 * @description : Method to get style for fail
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static CellStyle CellStatusFailStyle(Workbook wb) {
		/* Code for font */
		Font defaultFont = wb.createFont();
		defaultFont.setFontName("Calibri");
		defaultFont.setFontHeightInPoints((short) 11);
		defaultFont.setColor(IndexedColors.RED.getIndex());

		/* Code for child data */
		CellStyle styleChild = wb.createCellStyle();
		styleChild = wb.createCellStyle();
		styleChild.setAlignment(HorizontalAlignment.LEFT);
		styleChild.setBorderTop(BorderStyle.THIN);
		styleChild.setBorderBottom(BorderStyle.THIN);
		styleChild.setBorderLeft(BorderStyle.THIN);
		styleChild.setBorderRight(BorderStyle.THIN);
		styleChild.setFont(defaultFont);
		return styleChild;
	}

	/*
	 * @description : Method to evaluate structure of xml document
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 07 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public static void structureValidation304(String resultPath)
			throws ParserConfigurationException, SAXException, IOException, XPathExpressionException {
		try {

			for (int i = 1; i <= 2; i++) {
				/* Check for the file */
				file = new File(resultPath + "/ExcelReportValidation" + i + ".xlsx");

				if (!file.exists()) {
					System.out.println("File does not exist.");
					file = null;
				}

				try (OutputStream fileOut = new FileOutputStream(resultPath + "/ExcelReportValidation" + i + ".xlsx")) {
					Workbook wb = new XSSFWorkbook();
					Sheet sheet = wb.createSheet("StructureValidation");

					/* Setting Foreground Color for header */
					Row row = sheet.createRow(count);

					/* Style for header */
					CellStyle style = cellStyleHeader(wb);

					/* Set Header */
					Cell cell = row.createCell(0);
					cell.setCellValue("Original RFP file");
					cell.setCellStyle(style);
					cell = row.createCell(1);
					cell.setCellValue("Generated RFP File");
					cell.setCellStyle(style);
					cell = row.createCell(2);
					cell.setCellValue("Results");
					cell.setCellStyle(style);

					/* Style for child and status */
					CellStyle styleParent = CellParentStyle(wb);
					CellStyle styleChild = CellChildStyle(wb);
					CellStyle styleStatusPass = CellStatusPassStyle(wb);
					CellStyle styleStatusFail = CellStatusFailStyle(wb);

					/* Node Validation */
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/Process", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/Cashflow", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/Purpose", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/BankAccount", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/Other", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/AnalysisCodes", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/OurInstructions", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions", resultPath,
							styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions/Beneficiary",
							resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions/BeneBank", resultPath,
							styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions/DeliveryNarrative",
							resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions/IntermediaryBank",
							resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions/BeneficiaryDetails",
							resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions/Charges", resultPath,
							styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/TheirInstructions/Charges/SendersCharges",
							resultPath, styleParent, styleChild, styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304(
							"//IFCRFP/TheirInstructions/Charges/ReceiversCharges", resultPath, styleParent, styleChild,
							styleStatusPass, styleStatusFail, sheet);
					DataBaseXmlStructureValidation.ValidateXpath304("//IFCRFP/Custom", resultPath, styleParent,
							styleChild, styleStatusPass, styleStatusFail, sheet);

					sheet.autoSizeColumn(0);
					sheet.autoSizeColumn(1);
					sheet.autoSizeColumn(2);
					sheet.autoSizeColumn(3);
					wb.write(fileOut);
				} catch (Exception e) {
					// TODO: handle exception
				}
			}

		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
							+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

}
