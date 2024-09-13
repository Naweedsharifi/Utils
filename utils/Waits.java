package com.wbg.selenium.qa.utils;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;


public class Waits {
	static Logger log = LoggerHelper.getLogger(Waits.class);

	/*
	 * @description :Wait for Element to clickable
	 * @param :driver,element,timeOut
	 * @return : boolean
	 * @date : 28 Dec 2020
	 * @author : Infosys Limited
	 */
	public boolean waitForElementClickable(WebDriver driver, WebElement element, long timeOut) {

		try {
			final WebDriverWait webDriverWait;
			webDriverWait = new WebDriverWait(driver, 10);
			/*Wait for element to be clickable*/
			webDriverWait.until(ExpectedConditions.elementToBeClickable(element));
			log.info("Element is clickable , " + "<" + element + ">" + ", TimeOut :" + timeOut);
			return true;
		} catch (Exception e) {
			log.info("Unable to click Element  , " + "<" + element.getClass() + ">" + ", TimeOut :" + timeOut);
			return false;

		}
	}
	
	/*
	 * @description :Wait for Elements to Visible
	 * @param :driver,element,timeOut
	 * @return :boolean 
	 * @date : 28 Dec 2020
	 * @author : Infosys Limited
	 */

	public boolean waitForElementVisible(WebDriver driver, WebElement element, long timeOut) {

		try {
			final WebDriverWait webDriverWait;
			webDriverWait = new WebDriverWait(driver, 10);
			/*Wait for element to be Visible*/
			webDriverWait.until(ExpectedConditions.visibilityOf(element));
			log.info("Element is visible by locator , " + "<" + element + ">" + ", TimeOut :" + timeOut);
			return true;
		} catch (Exception e) {
			log.info("Unable to find Element  , " + "<" + element.getClass() + ">" + ", TimeOut :" + timeOut);
			return false;

		}

	}

}
