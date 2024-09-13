package com.wbg.selenium.qa.pageobject;

import java.awt.Robot;
import java.awt.event.KeyEvent;

import org.apache.log4j.Logger;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.How;
import org.openqa.selenium.support.PageFactory;
import org.testng.Assert;
import org.testng.Reporter;

import com.wbg.selenium.qa.configReader.ConfigFileReader;
import com.wbg.selenium.qa.configReader.Xlsx_FileReader;
import com.wbg.selenium.qa.manager.WebDriverManager;
import com.wbg.selenium.qa.utils.LoggerHelper;
import com.wbg.selenium.qa.utils.WebElementWrappers;

public class LoginPageObject extends WebDriverManager {
	Logger log = LoggerHelper.getLogger(LoginPageObject.class);
	WebElementWrappers ele;
	ConfigFileReader filereader;
	Xlsx_FileReader xlsx_FileReader = PageFactory.initElements(driver, Xlsx_FileReader.class);

	/* Locators for web page */
	@FindBy(how = How.XPATH, using = "//input[@name='loginfmt']")
	WebElement txtUserId;
	@FindBy(how = How.XPATH, using = "//input[@type='submit']")
	WebElement btnNext;
	@FindBy(how = How.CSS, using = "a#logout")
	WebElement logOut;
	@FindBy(how = How.XPATH, using = "//*[@class='csip-web-PrtnrMgmtContent']//*[contains(text(),'logout')]")
	WebElement logOutMsg;

	/*
	 * @description : Login to CSIP Application
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 23 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public void loginCSIPApplication() throws InterruptedException {
		try {
			/* Enter the user details for login */
			driver.navigate().to(Xlsx_FileReader.excelreader.getCellData("CommonData", "appUrl", 2));
			WebElementWrappers.waitForElementVisible(driver, logOut);

			/* Verifying the home screen is displayed */
			if (logOut.isDisplayed()) {
				WebElementWrappers.Reporter(driver,
						"Validate Login into application, UserShould be able to login, User is able to login wrappers ,Pass");
			} else
				WebElementWrappers.Reporter(driver,
						"Validate Login into application, UserShould be able to login, User is not able to login ,Fail");
			Assert.assertEquals(logOut.isDisplayed(), true,
					"Verify that the user is able to login into application successfully");

		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Validate Login into application, User Should be able to login, User is not able to login ,Fail");
			Assert.fail();
		}
	}

	/*
	 * @description : Logout from CSIP Application
	 * 
	 * @param : NA
	 * 
	 * @return : NA
	 * 
	 * @date : 23 Dec 2020
	 * 
	 * @author : Anjali Kumari
	 */
	public void logout() throws InterruptedException {
		try {
			WebElementWrappers.waitForElementVisible(driver, logOut);
			WebElementWrappers.clickElement(logOut);
			Thread.sleep(1000);
			Robot robot = new Robot();
			robot.keyPress(KeyEvent.VK_ENTER);
			Thread.sleep(1000);
			robot.keyRelease(KeyEvent.VK_ENTER);
			Thread.sleep(1000);
			WebElementWrappers.waitForElementVisible(driver, logOutMsg);
			if (logOutMsg.isDisplayed()) {
				WebElementWrappers.Reporter(driver,
						"Click Logout button in HomePage, Logout button should be clicked, Logout button is clicked ,Pass");
				WebElementWrappers.Reporter(driver,"Validate the Logout messgae, Logout Mesage should appear, Message:" + logOutMsg.getText()
						+ " ,Pass");
			} else {
				WebElementWrappers.Reporter(driver,
						"Click Logout button in HomePage, Logout button should be clicked, Logout button is not clicked ,Fail");
				Assert.assertEquals(logOutMsg.isDisplayed(), true, "CSIP application is logged out");
			}
		} catch (Exception e) {
			WebElementWrappers.Reporter(driver,
					"Click Logout button in HomePage, Logout button should be clicked, Logout button is not clicked ,Fail");
			Assert.fail();
		}

	}

}
