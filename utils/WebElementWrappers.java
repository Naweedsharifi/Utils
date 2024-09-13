package com.wbg.selenium.qa.utils;

import java.awt.AWTException;
import java.awt.Robot;
import java.awt.event.KeyEvent;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.apache.commons.io.FileUtils;
import org.apache.log4j.Logger;
import org.apache.poi.ss.usermodel.CellType;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.junit.Assert;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.Keys;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.StaleElementReferenceException;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedCondition;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.FluentWait;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.Reporter;

import com.wbg.selenium.qa.configReader.MapDataProvider;
import com.wbg.selenium.qa.manager.WebDriverManager;

public class WebElementWrappers {
	static Logger log = Logger.getLogger(WebElementWrappers.class);

	static WebDriverWait wait;

	public WebElementWrappers() {
		throw new UnsupportedOperationException();
	}

	/*
	 * @description :This method will click on web element through action class
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public static void clickbyActionClass(WebDriver driver, WebElement element) {
		/* click on web element through action class */
		Actions action = new Actions(driver);
		action.moveToElement(element).click().build().perform();
	}

	/*
	 * @description :This method will wait for specific time for the element to be
	 * visible
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static WebElement waitForElementVisible(WebDriver driver, WebElement element) {
		/* wait for element to be visible */
		try {
			wait = new WebDriverWait(driver, 120);
			element = wait.until(ExpectedConditions.visibilityOf(element));
		} catch (Exception e) {
			log.info(e.getMessage());
			Assert.assertTrue(false);
		}
		return element;
	}

	/*
	 * @description :This method will wait for specific time as passed in parameter
	 * for the element to be visible
	 * 
	 * @param :driver,element,intWait
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static WebElement customWaitForElementVisible(WebDriver driver, WebElement element, int intWait) {
		/* waiting for element to be visible */
		wait = new WebDriverWait(driver, intWait);
		element = wait.until(ExpectedConditions.visibilityOf(element));

		return element;
	}

	/*
	 * @description :This method will click Element specified
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void clickElement(WebElement element) {
		/* clicking the element */
		element.click();

	}

	/*
	 * @description :This method will enter text in the element
	 * 
	 * @param :element, text
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public static void enterText(WebElement element, String text) {
		/* Entering text in the element */
		element.clear();
		element.sendKeys(text);
	}

	/*
	 * @description :This method will scroll to element specified
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void scrollToElement(WebDriver driver, WebElement element) {
		/* scrolling to the particular element */
		((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView(true);", element);

	}

	/*
	 * @description :This method will scroll to the center of the element specified
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void scrollToElementCenter(WebDriver driver, WebElement element) {
		/* scrolling to the centre of particular webelement */
		try {
			((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block:'center'});", element);
		} catch (Exception e) {
			log.info(e.getMessage());
			Assert.assertTrue(false);
		}
	}

	/*
	 * @description :This method will highlight the specified element
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void highlightElement(WebDriver driver, WebElement element) {
		/* Highlighting the element */
		((JavascriptExecutor) driver).executeScript("arguments[0].style.border='3px solid red'", element);
	}

	/*
	 * @description :This method will expand the tab section
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void expandTabSection(WebDriver driver, WebElement element) {
		/* expanding the tab section */
		((JavascriptExecutor) driver).executeScript("arguments[0].setAttribute('class','collapse show')", element);
	}

	/*
	 * @description :This method will wait for the page to be loaded
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static boolean waitForPageLoad(WebDriver driver) {

		/* waiting for the page to get loaded */
		ExpectedCondition<Boolean> jQueryLoad = new ExpectedCondition<Boolean>() {
			// @Override
			public Boolean apply(WebDriver driver) {
				try {
					Long r = (Long) ((JavascriptExecutor) driver).executeScript("return $.active");
					return r == 0;
				} catch (Exception e) {
					return true;
				}
			}
		};

		// wait for Javascript to load
		ExpectedCondition<Boolean> jsLoad = new ExpectedCondition<Boolean>() {
			// @Override
			public Boolean apply(WebDriver driver) {
				return ((JavascriptExecutor) driver).executeScript("return document.readyState").toString()
						.equals("complete");
			}
		};
		WebDriverWait wait = new WebDriverWait(driver, 120);
		return wait.until(jQueryLoad) && wait.until(jsLoad);
	}

	/*
	 * @description :This method will capture screenshot
	 * 
	 * @param :driver, fileName
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void captureScreenshot(WebDriver driver, String stringFileName) {
		/* capturing the screenshot */
		File srcFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
		try {
			FileUtils.copyFile(srcFile, new File("Screenshots//Screenshot_" + stringFileName + ".jpg"));
		} catch (Exception e) {
			log.info(e.getMessage());
		}
	}

	/*
	 * @description :This method will hover the mouse to the specific element
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void mouseHoverElement(WebDriver driver, WebElement element) {
		/* hovering the mouse to particular element */
		Actions action = new Actions(driver);
		action.moveToElement(element).perform();
	}

	/*
	 * @description :This method will check if element is enabled
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static boolean isElementEnabled(WebDriver driver, WebElement element) {
		/* checking if element is enabled */
		WebElement ele = waitForElementVisible(driver, element);
		return ele.isEnabled();

	}

	/*
	 * @description :This method will fetch the attribute value for the element
	 * 
	 * @param :element, name
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String getAttribute(WebElement element, String name) {
		/* fetching the attribute value */

		return element.getAttribute(name);

	}

	/*
	 * @description :This method will fetch the title of the page and comparing
	 * 
	 * @param :driver, value
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void getTitle(WebDriver driver, String value) {
		/* fetching the title of the page */
		if (driver.getTitle().equalsIgnoreCase(value))
			log.info(value + " page is initiated successfully");
		else
			log.info(value + " page is not initiated successfully");

	}

	/*
	 * @description :This method will select a text from dropdown using selectByText
	 * 
	 * @param :driver, textToBeSelected
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	// reusable method to select a text from dropdown
	public static void dropdown(WebElement element, String textToBeSelected) {
		/* selecting value from dropdown */
		Select s = new Select(element);
		s.selectByVisibleText(textToBeSelected);
	}

	/*
	 * @description :This method will select a text from dropdown using
	 * selectByValue
	 * 
	 * @param :driver, value
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void selectDropdownByValue(WebElement element, String value) {
		/* selecting value from dropdown */
		Select s = new Select(element);
		s.selectByValue(value);
	}

	/*
	 * @description :This method will get the text of the element specified
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String getElementText(WebElement element) {
		/* getting the text value of the element */
		return element.getText().trim();
	}

	/*
	 * @description :This method will help switch to frame
	 * 
	 * @param :driver,intTime,intIndex
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void switchFrame(WebDriver driver, int intTime, int intIndex) throws InterruptedException {
		/* Switching to frame */
		Thread.sleep(1000);
		wait = new WebDriverWait(driver, intTime);
		wait.until(ExpectedConditions.frameToBeAvailableAndSwitchToIt(intIndex));
	}

	/*
	 * @description :This method will help switch to frame and click specific
	 * element
	 * 
	 * @param :driver, element
	 * 
	 * @return :
	 * 
	 * @date : 29 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void switchFrameClickElement(WebDriver driver, WebElement element) throws InterruptedException {
		/* switching to diff frame and click element */
		int size = driver.findElements(By.tagName("iframe")).size();
		log.info("Number of frames on " + element + " is : " + size);

		for (int i = 0; i <= size; i++) {
			WebElementWrappers.switchFrame(driver, 5, i);
			WebElementWrappers.customWaitForElementVisible(driver, element, 5);

			if (element.isDisplayed()) {
				log.info("Element present on frame " + i);

				element.click();
				break;
			}
		}
		driver.switchTo().defaultContent();
	}

	/*
	 * @description :This method will help switch to frame and enter value in the
	 * specific element
	 * 
	 * @param :driver, element ,value
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void switchFrameEnterText(WebDriver driver, WebElement element, String value)
			throws InterruptedException {
		/* switching to diff frame and enter text */
		int size = driver.findElements(By.tagName("iframe")).size();
		log.info("Number of frames = " + size);

		for (int i = 0; i <= size; i++) {
			WebElementWrappers.switchFrame(driver, 10, i);
			if (element.isDisplayed()) {
				log.info("Element present on frame " + i);
				element.clear();
				WebElementWrappers.enterText(element, value);
				break;
			}
		}
		driver.switchTo().defaultContent();
	}

	/*
	 * @description :This method will return next day's date
	 * 
	 * @param :null
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String datePlusFive() {
		/* accessing next day date */
		SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
		Calendar c = Calendar.getInstance();
		c.setTime(new Date()); // Now use today date.
		c.add(Calendar.DATE, 1); // Adding 1 days
		String output = sdf.format(c.getTime());
		log.info("Get datevalue : " + output);
		return output;
	}

	/*
	 * @description :This method will return todays current date
	 * 
	 * @param :null
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String todaysDate() {
		/* accessing todays date */
		Date date = new Date();
		String dateFormat = "MM/dd/yyyy";
		SimpleDateFormat sdf = new SimpleDateFormat(dateFormat);
		log.info("Today is " + sdf.format(date));
		return sdf.format(date);
	}

	/*
	 * @description :This method will enable the checkbox
	 * 
	 * @param :driver, element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void enableCheckBox(WebDriver driver, WebElement element) {
		/* enabling the specific checkbox */
		WebElementWrappers.waitForElementVisible(driver, element);
		if (element.isSelected()) {
			log.info("Checkbox " + element + " was already enabled");
		} else {
			element.click();
			log.info("Checkbox " + element + " was enabled successfully");
		}
	}

	/*
	 * @description :This method will retrun random string value
	 * 
	 * @param :null
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String randomString() {
		/* returning random string */
		SecureRandom random = new SecureRandom();
		return new BigInteger(20, random).toString();
	}

	/*
	 * @description :This method will click downward arrow key from Keyboard using
	 * Actions class
	 * 
	 * @param :driver, sequence
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void keysdown(WebDriver driver, int sequence) throws InterruptedException {
		/* Method to click Arrow Key Down button from Keyboard using Actions class */
		Actions action = new Actions(driver);
		for (int i = 0; i < sequence; i++) {
			action.sendKeys(Keys.ARROW_DOWN).build().perform();
			Thread.sleep(300);

			// element.sendKeys(Keys.ARROW_DOWN);
			// action.moveToElement(element).sendKeys(Keys.ARROW_DOWN).build().perform();

		}
	}

	/*
	 * @description :This method take the cursor to the bottom of the page
	 * 
	 * @param :driver
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void pagedown(WebDriver driver) {
		/* navigating to bottom of page */
		Actions action = new Actions(driver);
		action.sendKeys(Keys.PAGE_DOWN).build().perform();

	}

	/*
	 * @description :This method will wait for the web element to be visible and
	 * then click on it
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void waitforelementToBeClickable(WebDriver driver, WebElement element) {
		/* waiting for a specified time for element to be visible and clicked */
		try {
			WebDriverWait wait = new WebDriverWait(driver, 35);
			wait.until(ExpectedConditions.elementToBeClickable(element));
		} catch (Exception e) {
			Assert.assertTrue("Element is not visible or not clickable", false);
		}
	}

	/*
	 * @description :This method will wait for a file to be visible and then
	 * download the corresponding file
	 * 
	 * @param :driver,file,timeoutSeconds
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void waitForFileDownloaded(WebDriver driver, File file, int timeoutSeconds) {
		/* waiting for file to be visible and then download the corresponding file */
		// WebDriver driver = getDriver();
		FluentWait<WebDriver> wait = new FluentWait<>(driver).withTimeout(Duration.ofSeconds(timeoutSeconds))
				.pollingEvery(Duration.ofMillis(500))
				.ignoring(NoSuchElementException.class, StaleElementReferenceException.class);
		wait.until((webDriver) -> file.exists());
	}

	/*
	 * @description :This method will click on Arrow Key Up button from keyboard
	 * through Actions class
	 * 
	 * @param :driver,sequence
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void keysUp(WebDriver driver, int sequence) throws InterruptedException {
		/* Method to click Arrow Key Up button from Keyboard using Actions class */
		Actions action = new Actions(driver);
		for (int i = 0; i < sequence; i++) {
			action.sendKeys(Keys.ARROW_UP).build().perform();
			Thread.sleep(2000);
		}
	}

	/*
	 * @description :This method will click on Arrow Key Right button from keyboard
	 * through Actions class
	 * 
	 * @param :driver,sequence
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void keysRight(WebDriver driver, int sequence) throws InterruptedException {
		/* Method to click Arrow Key Right button from Keyboard using Actions class */
		Actions action = new Actions(driver);
		for (int i = 0; i < sequence; i++) {
			action.sendKeys(Keys.ARROW_RIGHT).build().perform();
			Thread.sleep(2000);

		}
	}

	/*
	 * @description :This method will click on PageDown button from keyboard through
	 * Actions class
	 * 
	 * @param :driver,iteration
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void pagedown(WebDriver driver, int iteration) throws InterruptedException {
		/* Method to click Page down button from Keyboard using Actions class */
		Actions action = new Actions(driver);

		for (int i = 0; i < iteration; i++) {

			action.sendKeys(Keys.PAGE_DOWN).build().perform();
			Thread.sleep(2000);
		}

	}

	/*
	 * @description :This method will click on PageUp button from keyboard through
	 * Actions class
	 * 
	 * @param :driver,iteration
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void pageUp(WebDriver driver, int iteration) throws InterruptedException {
		/* Method to click Page Up button from Keyboard using Actions class */
		Actions action = new Actions(driver);

		for (int i = 0; i < iteration; i++) {

			action.sendKeys(Keys.PAGE_UP).build().perform();
			Thread.sleep(2000);
		}

	}

	/*
	 * @description :This method will decode Base64 encoded String. Input Parameters
	 * - Base64 encoded
	 * 
	 * @param :encodedString
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String decodeString(String encodedString) {
		/* Method to decode Base64 encoded String. Input Parameters - Base64 encoded */
		byte[] decoded = Base64.getDecoder().decode(encodedString);
		return new String(decoded, StandardCharsets.UTF_8);
	}

	/*
	 * @description :This Method will return a list of options available in a
	 * dropdown under tag <li>
	 * 
	 * @param :drpElement
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static List<String> getDropdownValuesbyTagLi(WebElement drpElement) {
		/*
		 * This method will return a list of options available in a dropdown under tag
		 * <li>
		 */
		List<WebElement> options = drpElement.findElements(By.tagName("li"));
		List<String> drpOptions = new ArrayList<>();

		for (WebElement option : options) {
			drpOptions.add(option.getText());
		}
		return drpOptions;
	}

	/*
	 * @description :This method will verify actual and expected values in a list
	 * Input Parameters -
	 * 
	 * @param :List<String> actualList,List<String> expectedList
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void compareLists(List<String> actualList, List<String> expectedList) {
		/* Method to verify actual and expected values in a list Input Parameters */
		// Check for nulls
		if ((actualList.isEmpty() || expectedList.isEmpty())) {
			log.info("One of the lists to be compared is empty");
			// Assert.assertTrue(false);
		} else {
			for (int i = 0; i < expectedList.size(); i++) {
				if (actualList.contains(expectedList.get(i))) {
					log.info(expectedList.get(i) + " is present in the actual values list");
				} else {
					log.info(expectedList.get(i) + " is not present in the actual values list");
					Assert.assertTrue(false);

				}
			}
		}
	}

	/*
	 * @description :This method will return a list of options available in a
	 * dropdown under tag <li>
	 * 
	 * @param :driver,value
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static WebElement getDropdownValuesbyTagLi(WebDriver driver, String value) {
		/*
		 * This method will return a list of options available in a dropdown under tag
		 * <li>
		 */
		return driver.findElement(By.xpath("//ul/li/a[text()='" + value + "']"));
	}

	/*
	 * @description :This method will scroll down by PageDown button through
	 * Keyboard by the usage of Robot class
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void scrollDownByPageDown() throws AWTException {
		/*
		 * method to scroll down by PageDown button through Keyboard by the usage of
		 * Robot class
		 */
		Robot robot = new Robot();
		for (int i = 0; i < 2; i++) {
			robot.keyPress(KeyEvent.VK_PAGE_DOWN);
			robot.keyRelease(KeyEvent.VK_PAGE_DOWN);
		}
	}

	/*
	 * @description :This method will get cell values from the table
	 * 
	 * @param :cellNumber,tableRows
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static List<String> getCellValueFromTable(int cellNumber, List<WebElement> tableRows) {
		/*
		 * Method to create a list and capture all the cell values from the table using
		 * <td> tag
		 */
		List<String> cellValue = new ArrayList<>();
		for (WebElement row : tableRows) {
			List<WebElement> cells = row.findElements(By.tagName("td"));
			cellValue.add(cells.get(cellNumber).getText().trim());
		}
		return cellValue;
	}

	/*
	 * @description :This method will get table header tag <th> values from the
	 * table
	 * 
	 * @param :tableRows
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static List<String> getTableHeadersByTh(WebElement tableRows) {
		/*
		 * Method to create a list and capture all the Header values from the table
		 * using <th> tag
		 */
		List<String> cellValue = new ArrayList<>();
		List<WebElement> cells = tableRows.findElements(By.tagName("th"));
		for (int i = 0; i < cells.size(); i++) {
			cellValue.add(cells.get(i).getText().toLowerCase().trim());
		}
		return cellValue;
	}

	/*
	 * @description :This method will get Excel values using Map data Provider class
	 * 
	 * @param :workSheet,column,i
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String getExcelValue(String workSheet, String column, int i) throws IOException {
		/*
		 * Method to get the Excel values from Excel sheets using Map Data Provider
		 * class
		 */
		List<Map<String, String>> getMap = MapDataProvider.getData(workSheet);
		// getMap.
		return getMap.get(i).get(column);
	}

	/*
	 * @description :This method will return result from Excel for a particular
	 * column scenario
	 * 
	 * @param :workSheet,column,i
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String getExcelValueByScenario(String workSheet, String column, String scenarioName)
			throws IOException {
		/* Method will return result from Excel for a particular column scenario */
		int i;
		boolean isScenarioPresent = false;
		String result = null;
		List<Map<String, String>> getMap = MapDataProvider.getData(workSheet);

		if (getMap.isEmpty()) {
			log.info("No rows found in the excel worksheet " + workSheet);
			Assert.fail();
		} else {
			for (i = 0; i < getMap.size(); i++) {
				if (getMap.get(i).get("ScenarioName").equalsIgnoreCase(scenarioName)) {
					log.info("Scenario name found in the excel - '" + getMap.get(i).get("ScenarioName") + "'");
					result = getMap.get(i).get(column);
					log.info("Value retrieved from excel - '" + result + "'");
					isScenarioPresent = true;
					break;
				}
			}
		}
		if (!isScenarioPresent) {
			log.info("Scenario name is not present in the excel");
			Assert.fail();
		}
		return result;
	}

	/*
	 * @description :This method will split a string into parts and will return in
	 * the form of Array List
	 * 
	 * @param :value,splitBy
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static List<String> splitStringReturnList(String value, String splitBy) throws IOException {
		/*
		 * method to split a string into parts and to return in the form of Array List
		 */
		return Arrays.asList(value.split(splitBy));
	}

	/*
	 * @description :This method will scroll the cursor to the Web Element specified
	 * using Actions class
	 * 
	 * @param :driver,element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void scrollToElementByActionClass(WebDriver driver, WebElement element) {
		/*
		 * Method to scroll the cursor to the Web Element specified using Actions class
		 */
		Actions actions = new Actions(driver);
		actions.moveToElement(element);
		actions.perform();
	}

	/*
	 * @description :This method will assert the Web Element if present
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void assertElementPresentGetText(WebElement element) {
		/* Method to assert the Web Element if it is present */
		if (element.isDisplayed()) {
			log.info("Element, '" + element.getText().trim() + "' is present in the application");
			Assert.assertTrue(true);
		} else {
			log.info("Element, '" + element.getText().trim() + "' is not present in the application");
			Assert.assertTrue(false);
		}
	}

	/*
	 * @description :This method will assert the Web Element if present and compare
	 * the text accordingly
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void assertElementPresentCompareText(WebElement element, String expectedText) {
		/*
		 * Method to assert the Web Element if it is present and compare the text
		 * accordingly
		 */
		if (element.isDisplayed()) {
			log.info("Element, '" + element.getText().trim() + "' is present in the application");
			if (element.getText().trim().equalsIgnoreCase(expectedText)) {
				log.info("Expected and actual vaues are same : " + expectedText);
				Assert.assertTrue(true);
			} else {
				log.info("Expected and actual vaues are not same. Value in application is " + element.getText().trim());
				Assert.assertTrue(false);
			}
		} else {
			log.info("Element, '" + element.getText().trim() + "' is not present in the application");
			Assert.assertTrue(false);
		}
	}

	/*
	 * @description :This method will select the options present in a DropDown list
	 * and click on a particular required Element
	 * 
	 * @param :drpElement,optionValue
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void selectDropdownByOptions(WebElement drpElement, String optionValue) {
		/*
		 * Method to select the options present in a DropDown list and click on a
		 * particular required Element
		 */
		List<WebElement> options = drpElement.findElements(By.tagName("option"));
		clickElement(drpElement);
		for (WebElement option : options) {
			if (option.getText().trim().equalsIgnoreCase(optionValue)) {
				option.click();
			}
		}
	}

	/*
	 * @description :This method will validate whether user is select values from
	 * the drop down using Actions class
	 * 
	 * @param :driver,drpElement,optionValue
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void validateDropdown(WebDriver driver, WebElement drpElement, String optionValue) {
		/*
		 * method to validate whether user is able to select values from the drop down
		 */
		Actions act = new Actions(driver);
		act.moveToElement(drpElement).click().perform();
		WebElementWrappers.clickElement(getDynamicDrpDwn(driver, optionValue));
		log.info(drpElement.getText());
		if (drpElement.getText().equalsIgnoreCase(optionValue)) {
			log.info("User is able to select dropdown value as " + optionValue);
			Assert.assertTrue(true);
		} else {
			log.info("User is able not able to select dropdown value" + optionValue);
			Assert.assertTrue(false);
		}
	}

	/*
	 * @description :This method will select and click on a particular drop down
	 * element using Actions class
	 * 
	 * @param :driver,drpElement,optionValue
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void selectDropdown(WebDriver driver, WebElement drpElement, String optionValue) {
		/*
		 * method to select and click on a particular drop down element using Actions
		 * class
		 */
		Actions act = new Actions(driver);
		act.moveToElement(drpElement).click().perform();
		WebElementWrappers.clickElement(getDynamicDrpDwn(driver, optionValue));
	}

	/*
	 * @description :This method will get dynamic values drop down list
	 * 
	 * @param :driver,value
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static WebElement getDynamicDrpDwn(WebDriver driver, String value) {
		/* method to get dynamic values in the drop down list */
		return driver.findElement(By.xpath("//ul//li/span[text()='" + value + "']"));
	}

	/*
	 * @description :This method will validate whether the element is not present in
	 * application
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void validateElementAbsence(WebElement element) {
		/* Method to validate whether the element is not present in application */
		if (!element.isDisplayed()) {
			log.info("Element is not displayed in the application");
			Assert.assertTrue("Element is not displayed in the application", true);
		} else {
			log.info("Element is displayed in the application");
			Assert.assertTrue("Element is displayed in the application", false);
		}
	}

	/*
	 * @description :This method will validate whether the element is present in
	 * application
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void validateElementPresence(WebElement element) {
		/* Method to validate whether the element is present in application */
		if (element.isDisplayed()) {
			log.info("Element is displayed in the application");
			Assert.assertTrue("Element is displayed in the application", true);
		} else {
			log.info("Element is not displayed in the application");
			Assert.assertTrue("Element is not displayed in the application", false);
		}
	}

	/*
	 * @description :This method will validate whether the element is not Enabled in
	 * application
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyElementDisabled(WebElement element) {
		/* Method to validate whether the element is not Enabled in application */
		if (!element.isEnabled()) {
			log.info("Element is disabled in the application");
			Assert.assertTrue("Element is disabled in the application", true);
		} else {
			log.info("Element is not disabled in the application");
			Assert.assertTrue("Element is not disabled in the application", false);
		}
	}

	/*
	 * @description :This method will validate whether the attribute of a web
	 * element is Disabled in application
	 * 
	 * @param :element,attribute
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyElementDisabledAttribute(WebElement element, String attribute) {
		/*
		 * method to validate whether the attribute of a web element is Disabled in
		 * application
		 */
		if (attribute.contains("disabled")) {
			if (element.getAttribute(attribute).toLowerCase().contains("true")) {
				log.info("Element is disabled in the application, disabled attribute value :"
						+ element.getAttribute(attribute));
				Assert.assertTrue("Element is disabled in the application", true);
			} else {
				log.info("Element is not disabled in the application, disabled attribute value :"
						+ element.getAttribute(attribute));
				Assert.assertTrue("Element is not disabled in the application", false);
			}
		} else {
			if (element.getAttribute(attribute).toLowerCase().contains("disabled")) {
				log.info("Element is disabled in the application, disabled attribute value :"
						+ element.getAttribute(attribute));
				Assert.assertTrue("Element is disabled in the application", true);
			} else {
				log.info("Element is not disabled in the application, disabled attribute value :"
						+ element.getAttribute(attribute));
				Assert.assertTrue("Element is not disabled in the application", false);
			}
		}
	}

	/*
	 * @description :This method will verify whether the element is selected in
	 * application
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void verifyElementSelected(WebElement element) {
		/* Method to verify whether the element is selected in application */
		if (!element.isSelected()) {
			log.info("Element is selected in the application");
			Assert.assertTrue("Element is selected in the application", true);
		} else {
			log.info("Element is not selected in the application");
			Assert.assertTrue("Element is not selected in the application", false);
		}
	}

	/*
	 * @description :This method will click on a web element if it is present in
	 * application
	 * 
	 * @param :element
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void clickElementIfPresent(WebElement element) {
		/* Method to click on a web element if it is present in application */
		if (element.isDisplayed()) {
			clickElement(element);
			log.info("Element is displayed in the application");
		} else {
			log.info("Element is not displayed in the application");
		}
	}

	/*
	 * @description :This method will click on Arrow Key Enter button from keyboard
	 * through Actions class
	 * 
	 * @param :driver,sequence
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static void keysEnter(WebDriver driver, int sequence) throws InterruptedException {
		/*
		 * Method to click on Arrow Key Enter button from keyboard through Actions class
		 */
		Actions action = new Actions(driver);
		for (int i = 0; i < sequence; i++) {
			action.sendKeys(Keys.ENTER).build().perform();
			Thread.sleep(2000);
		}
	}

	/*
	 * @description :Method to get the sheet of xlsx file
	 * 
	 * @param :filePath,sheet
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	@SuppressWarnings("resource")
	public static XSSFSheet readExcel(String filePath, String sheet) {
		/* code to get the xssfSheet */
		try {
			File strFilePath = new File(filePath);
			FileInputStream inputStream1 = new FileInputStream(strFilePath);
			XSSFWorkbook worktbook = new XSSFWorkbook(inputStream1);
			XSSFSheet irpSheet = worktbook.getSheet(sheet);
			return irpSheet;
		} catch (Exception e) {
			// TODO: handle exception
		}
		return null;
	}

	/*
	 * @description :Method to get the row number from xlsx file
	 * 
	 * @param :cellContent,sheet
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static int getRowNumber(String cellContent, XSSFSheet sheet) {
		/* code to get the row number */
		int rowNum = 0;
		try {
			int rowCount1 = sheet.getLastRowNum() - sheet.getFirstRowNum();

			for (int i = 1; i <= rowCount1; i++) {
				Row row1 = sheet.getRow(i);
				if (!(row1 == null) && !(row1.getCell(0) == null) && !(row1.getCell(0).getCellType() == CellType.BLANK)
						&& row1.getCell(0).getCellType() == CellType.STRING
						&& row1.getCell(0).getStringCellValue().trim().equalsIgnoreCase(cellContent.trim())) {
					rowNum = row1.getRowNum();
					return rowNum + 1;
				}

			}
		} catch (Exception e) {
			// TODO: handle exception
			e.printStackTrace();
		}
		return rowNum;

	}

	public static String[] removeAllEmpty(String[] arr) {
		if (arr == null)
			return arr;

		String[] result = new String[arr.length];
		int amountOfValidStrings = 0;

		for (int i = 0; i < arr.length; i++) {
			if (!arr[i].equals(""))
				result[amountOfValidStrings++] = arr[i];
		}

		result = Arrays.copyOf(result, amountOfValidStrings);

		return result;
	}

	public static int getIndex(List<String> strList, String content) {
		for (int i = 0; i < strList.size(); i++) {
			if (strList.get(i).trim().toLowerCase().contains(content.trim().toLowerCase())) {
				return i;
			}
		}
		return -1;

	}

	/*
	 * @description : Capture the screen short and create a folder screenshot
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public  static String captureScreenshotReporter(WebDriver driver) {

		try {
			String Seperator = System.getProperty("file.separator");
			LocalDateTime myDateObj = LocalDateTime.now();
			DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
			String formattedDate = myDateObj.format(myFormatObj);
			formattedDate = formattedDate.replace(":", "").replace(" ", "");			
			String strFilePath = "file:results" + Seperator + "screenshots" + Seperator
					+ "LatestResults"+Seperator+ WebDriverManager.strTCName;
			String sFileName = WebDriverManager.strMethodName+ formattedDate + ".png";
			File file = new File(strFilePath);
			if (!file.exists()) {
				System.out.println("File created somewhere" + file);
				file.mkdir();
			}
			
			/* Capture the screenshots */
			File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			System.out.println("sourceFile" + sourceFile);
			File targetFile = new File(strFilePath + Seperator+sFileName);
			System.out.println("targetFile" + targetFile);
			
			/* Copy the screen shot to target file */
			FileUtils.copyFile(sourceFile, targetFile);
			System.out.println("copy");
			return targetFile.getAbsolutePath();
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
	
	
	/*
	 * @description : Method for reporter log
	 * 
	 * @param :WebDriver driver,String strReportLog
	 * 
	 * @return :NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public static void Reporter(WebDriver driver,String strReportLog) {		
		String Seperator = System.getProperty("file.separator");
		LocalDateTime myDateObj = LocalDateTime.now();		  
	    DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");
	    String formattedDate = myDateObj.format(myFormatObj);
	    formattedDate=formattedDate.replace(":", "");
		String imagePathInHost = "file:results"+Seperator+ "screenshots" + Seperator
				+ "LatestResults" + Seperator + WebDriverManager.strTCName+formattedDate.split(" ")[0] + Seperator
				+ captureScreenshot(driver, WebDriverManager.strTCName, WebDriverManager.strMethodName,formattedDate);
		System.out.println(imagePathInHost);
		String []strArr=strReportLog.split(",");
		String strStatus ="";
		if (strArr[3].toLowerCase().trim().contains("Pass".toLowerCase().trim())) {
			strStatus ="<a style=\"font-weight: bold;color: green;\" href=" + imagePathInHost + "> Pass </a>";
			strArr[3]=strStatus;
		} else {
			strStatus ="<a style=\"font-weight: bold;color: red;\"  href=" + imagePathInHost + "> Fail </a>";
			strArr[3]=strStatus;
		}
		
		/*Get all values with comma seperated */
		String strJoined = String.join(",", strArr);
		Reporter.log(strJoined);
	}

	/*
	 * @description : Capture the screen short and create a folder screenshot
	 * 
	 * @param : driver,  sTestClassName, sFileName,formattedDate
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static String captureScreenshot(WebDriver driver, String sTestClassName, String sFileName,String formattedDate) {		
		String Seperator = System.getProperty("file.separator");		
	    sFileName = sFileName+formattedDate.replace(" ", "")+ ".png";
		try {			  
			File file = new File("test-output"+Seperator+"results"+Seperator+"screenshots" + Seperator + "LatestResults" + Seperator + sTestClassName+formattedDate.split(" ")[0]);
			if (!file.exists()) {
				System.out.println("File created somewhere" + file);
				file.mkdir();
			}
			/* Capture the screenshots */
			File sourceFile = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			System.out.println("sourceFile" + sourceFile);
			File targetFile = new File("test-output"+Seperator+"results"+Seperator+"screenshots" + Seperator + "LatestResults" + Seperator + sTestClassName+formattedDate.split(" ")[0],
					sFileName);
			System.out.println("targetFile" + targetFile);
			/* Copy the screen shot to target file */
			FileUtils.copyFile(sourceFile, targetFile);
			System.out.println("copy");
			return sFileName;
		} catch (Exception e) {
			e.printStackTrace();
			return null;
		}
	}
}
