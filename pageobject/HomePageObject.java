package com.wbg.selenium.qa.pageobject;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

import org.apache.log4j.Logger;
import org.openqa.selenium.By;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.testng.Assert;
import org.testng.Reporter;

import com.wbg.selenium.qa.configReader.ConfigFileReader;
import com.wbg.selenium.qa.database.DecryptEncryptMessages;
import com.wbg.selenium.qa.manager.WebDriverManager;
import com.wbg.selenium.qa.utils.LoggerHelper;
import com.wbg.selenium.qa.utils.WebElementWrappers;

public class HomePageObject extends WebDriverManager {
	Logger log = LoggerHelper.getLogger(HomePageObject.class);
	WebElementWrappers ele;
	ConfigFileReader filereader;

	/* Locators for web page */
	@FindBy(how = How.CSS, using = "input.csip-web-searchInput")
	WebElement searchInput;
	@FindBy(how = How.ID, using = "btnSearch")
	WebElement btnSearch;
	@FindBy(how = How.XPATH, using = "//table[@id='resultTable']/tbody/tr")
	WebElement searchResult;
	@FindBy(how = How.XPATH, using = "//table[@id='resultTable']//th")
	List<WebElement> resultCol;
	@FindBy(how = How.XPATH, using = "//td/a")
	WebElement transactionRef;
	@FindBy(how = How.CSS, using = "textarea#swift")
	WebElement swiftMsg;
	@FindBy(how = How.CSS, using = "button.ui-dialog-titlebar-close")
	WebElement dialogClose;
	@FindBy(how = How.XPATH, using = "//table[@id='resultTable']/tbody/tr")
	List<WebElement> resultRows;

	/*
	 * @description : Search CSIP Id
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 27 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public void SearchCsipIdData(String CSIPId) throws InterruptedException {
		try {
			driver.navigate().refresh();
			WebElementWrappers.waitForElementVisible(driver, searchInput);

			/* Verify search input field */
			if (searchInput.isDisplayed())
				WebElementWrappers.Reporter(driver,
						"Verify that the search input box exist for the user, Search input field should be displayed successfully,Search input field is displayed successfully,Pass");
			else
				WebElementWrappers.Reporter(driver,
						"Verify that the search input box exist for the user, Search input field should be displayed successfully,Search input field is not displayed successfully,Fail");
			Assert.assertEquals(searchInput.isDisplayed(), true, "Verify that the search input box exist for the user");

			/* Clear field and search for csip id */
			searchInput.clear();
			searchInput.sendKeys(CSIPId);
			if (btnSearch.isDisplayed())
				WebElementWrappers.Reporter(driver,
						"Verify that the search button is displayed in the result table, Search button should be displayed successfully,Search button is displayed successfully,Pass");
			else
				WebElementWrappers.Reporter(driver,
						"Verify that the search button exist for the user, Search button should be displayed successfully,Search button is not displayed successfully,Fail");
			Assert.assertEquals(btnSearch.isDisplayed(), true,
					"Verify that the search button is displayed for the user");
			WebElementWrappers.clickElement(btnSearch);
			Thread.sleep(700);
			WebElementWrappers.waitForElementVisible(driver, searchResult);
			if (searchResult.isDisplayed())
				WebElementWrappers.Reporter(driver,
						"Verify that the search result is displayed in the result table, Search result should be displayed successfully,Search result is displayed successfully,Pass");
			else
				WebElementWrappers.Reporter(driver,
						"Verify that the search result exist for the user, Search result should be displayed successfully,Search result is not displayed successfully,Fail");
			Assert.assertEquals(searchResult.isDisplayed(), true,
					"Verify that the search result is displayed in the result table");
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Get the column number by column name
	 * 
	 * @param : columnName
	 * 
	 * @return : columnNumber
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public int getColumnNumberResultTable(String ColName) throws Exception {
		int columnNumber = 0;
		/* code to get column Number based on column name */
		for (WebElement webElement : resultCol) {
			columnNumber = columnNumber + 1;
			if (WebElementWrappers.getElementText(webElement).equalsIgnoreCase(ColName)) {
				return columnNumber;
			}
		}
		return 0;
	}

	/*
	 * @description : Verify result table of application UI
	 * 
	 * @param : Column Name, Column Value
	 * 
	 * @return : NA
	 * 
	 * @date : 29 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public void validateResultDataWithInbound(String tableInputData, String strColumnName, String resultPath)
			throws Exception {
		try {
			/* Sheet input data */
			HashMap<String, String> tableInputDataFromSheet = new HashMap<>();
			String[] strtableData = tableInputData.split(",");
			for (String string : strtableData) {
				String[] testDatakeyvaluePair = string.split(":");
				tableInputDataFromSheet.put(testDatakeyvaluePair[0], testDatakeyvaluePair[1]);
			}

			/* Get column Number by column name */
			ArrayList<String> arrList = new ArrayList<>();
			String[] strtableHeader = strColumnName.split(",");
			for (String string : strtableHeader) {
				arrList.add(string);
			}

			/* Verify the table result */
			verifyResultTableData(tableInputDataFromSheet, arrList, resultPath);
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Method to verify table result data
	 * 
	 * @param : tableInputDataFromSheet,arrList,resultPath
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	private void verifyResultTableData(HashMap<String, String> tableInputDataFromSheet, ArrayList<String> arrList,
			String resultPath) throws Exception, NumberFormatException {
		try {
			/* Validate table data */
			WebElementWrappers.waitForElementVisible(driver, transactionRef);
			if (resultRows != null) {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is present in the result table, Data should be present in the result table, Data is present in the result table ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the data is present in the result table, Data should be present in the result table, Data is not present in the result table ,Fail");
			}
			Assert.assertEquals(resultRows != null, true, "Verify that the data is present in the result table");

			/* Validate the Source System */
			for (Entry<String, String> columnName : tableInputDataFromSheet.entrySet()) {
				int dataColNumber = getColumnNumberResultTable(columnName.getKey());
				if (columnName.getKey().equalsIgnoreCase("status")) {
					WebElement ele = driver.findElement(By.xpath("//table[@id='resultTable']/tbody/tr[1]/td["+ dataColNumber + "]//img[contains(@src,'" + columnName.getValue() + "')]"));
					if (ele.isDisplayed()) {
						WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + columnName.getKey()
								+ ",The result data should be verified for the column: " + columnName.getKey()
								+ ", The result data is verified for the column: " + columnName.getKey() + " ,Pass");
					} else {
						WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + columnName.getKey()
								+ ",The result data should be verified for the column: " + columnName.getKey()
								+ ", The result data is verified for the column: " + columnName.getKey() + ",Fail");
					}
					Assert.assertEquals(ele.isDisplayed(), true,
							"Verify that the result data is verified for the column: " + columnName.getKey());
				} else if (columnName.getKey().equalsIgnoreCase("Status Description")) {
					WebElement ele = driver
							.findElement(By.xpath("//table[@id='resultTable']/tbody/tr[1]/td[" + dataColNumber + "]"));
					if (WebElementWrappers.getElementText(ele) != ""
							|| !WebElementWrappers.getElementText(ele).isEmpty()
							|| WebElementWrappers.getElementText(ele) != null) {
						WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + columnName.getKey()
								+ ",The result data should be verified for the column: " + columnName.getKey()
								+ ", The result data is verified for the column: " + columnName.getKey() + " ,Pass");
					} else {
						WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + columnName.getKey()
								+ ",The result data should be verified for the column: " + columnName.getKey()
								+ ", The result data is verified for the column: " + columnName.getKey() + ",Fail");
					}
					Assert.assertEquals(
							(WebElementWrappers.getElementText(ele) != ""
									|| !WebElementWrappers.getElementText(ele).isEmpty()
									|| WebElementWrappers.getElementText(ele) != null),
							true, "Verify that the data is verified for the column: " + columnName.getKey());
				} else {
					WebElement ele = driver
							.findElement(By.xpath("//table[@id='resultTable']/tbody/tr[1]/td[" + dataColNumber + "]"));
					if (WebElementWrappers.getElementText(ele).contains(columnName.getValue())) {
						WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + columnName.getKey()
								+ ",The result data should be verified for the column: " + columnName.getKey()
								+ ", The result data is verified for the column: " + columnName.getKey() + " ,Pass");
					} else {
						WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + columnName.getKey()
								+ ",The result data should be verified for the column: " + columnName.getKey()
								+ ", The result data is verified for the column: " + columnName.getKey() + ",Fail");
					}
					Assert.assertEquals(WebElementWrappers.getElementText(ele).contains(columnName.getValue()), true,
							"Verify that the data is verified for the column: " + columnName.getKey());
				}
			}

			/* Get the encrypted inbound messages */
			String swiftExpectedMsg = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath);

			/* Validate the column data against inbound decrypted message */
			for (String inboundColData : arrList) {
				int dataColNumber = getColumnNumberResultTable(inboundColData);
				WebElement ele = driver
						.findElement(By.xpath("//table[@id='resultTable']/tbody/tr[1]/td[" + dataColNumber + "]"));
				String strActual = WebElementWrappers.getElementText(ele).toLowerCase();
				if (inboundColData.equalsIgnoreCase("Amount")) {
					DecimalFormat format = new DecimalFormat("###.##");
					strActual = format.format(Double.parseDouble(strActual.replace(",", ""))).replace(".", ",");
				} else if (inboundColData.trim().equalsIgnoreCase("Transaction Reference")) {
					strActual = strActual.replace("-", "").replace(" ", "");
				}

				if (swiftExpectedMsg.toLowerCase().replace("\n", "").replace("-", "").replace(".", ",").replace(",", "")
						.replace(" ", "").contains(strActual.replace(",", ""))) {
					WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + inboundColData
							+ ",The result data should be verified for the column: " + inboundColData
							+ ", The result data is verified for the column: " + inboundColData + " ,Pass");
				} else {
					WebElementWrappers.Reporter(driver,"Verify that the result data is verified for the column: " + inboundColData
							+ ",The result data should be verified for the column: " + inboundColData
							+ ", The result data is verified for the column: " + inboundColData + ",Fail");
				}
				Assert.assertEquals(
						swiftExpectedMsg.toLowerCase().replace("\n", "").replace("-", "").replace(".", ",")
								.replace(",", "").replace(" ", "").contains(strActual.replace(",", "")),
						true, "Verify that the data is verified for the column: " + inboundColData);
			}

		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Method to verify Complete swift message
	 * 
	 * @param : resultPath
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public void ValidateSwiftMessage(String resultPath) {
		try {
			/* Validate complete swift message */
			String swiftExpectedMsg = DecryptEncryptMessages.getInboundDecyptedFileData(resultPath);
			if (transactionRef.isDisplayed()) {
				WebElementWrappers.Reporter(driver,
						"Verify that the transaction link is displayed in the application,Transaction link is displayed in the application, Transaction link is displayed in the application,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the transaction link is displayed in the application,Transaction link is displayed in the application, Transaction link is not displayed in the application,Fail");
			}
			Assert.assertEquals(transactionRef.isDisplayed(), true,
					"Verify that the transaction link is displayed in the application");

			/* Click transaction Reference and validate complete swift message */
			WebElementWrappers.clickElement(transactionRef);
			WebElementWrappers.waitForElementVisible(driver, dialogClose);
			if (dialogClose.isDisplayed()) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Close Button link is displayed in the application,Close Button link is displayed in the application, Close Button link is displayed in the application,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the Close Button link is displayed in the application,Close Button link is displayed in the application, Close Button link is not displayed in the application,Fail");
			}
			Assert.assertEquals(dialogClose.isDisplayed(), true,
					"Verify that the transaction link is displayed in the application");

			String swiftActualMsg = WebElementWrappers.getElementText(swiftMsg);
			if (swiftActualMsg.trim().contains(swiftExpectedMsg.trim())) {
				WebElementWrappers.Reporter(driver,
						"Verify that the swift message is same as in the inbound table,Swift message should be same as in the inbound table,Swift message is same as in the inbound table,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the swift message is same as in the inbound table,Swift message should be same as in the inbound table,Swift message is not same as in the inbound table,Fail");
			}
			Assert.assertEquals(swiftActualMsg.trim().contains(swiftExpectedMsg.trim()), true,
					"Verify that the swift message is same as in the inbound table");
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Method to Close the transaction refernce window
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 04 Jan 2021
	 * 
	 * @author : Infosys Limited
	 */
	public void closeWindow() {
		try {
			WebElementWrappers.clickElement(dialogClose);
			driver.navigate().refresh();
			WebElementWrappers.waitForElementVisible(driver, btnSearch);
			if (btnSearch.isEnabled()) {
				WebElementWrappers.Reporter(driver,
						"Verify that the Close Button link clicked  in the application,Close Button link is clicked in the application, Close Button link is clicked in the application,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Verify that the Close Button link is clicked in the application,Close Button link is clicked in the application, Close Button link is not clicked in the application,Fail");
			}
			Assert.assertEquals(btnSearch.isEnabled(), true,
					"Verify that the transaction link is clicked in the application");
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,"Verify that the functionality is failed, Functionality is failed, Functionality is failed : "
					+ e.getClass().getSimpleName() + " ,Fail");
			Assert.fail();
		}
	}

}
