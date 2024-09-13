package com.wbg.selenium.qa.manager;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.ie.InternetExplorerDriver;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterTest;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.BeforeTest;
import org.testng.internal.TestResult;

import com.wbg.selenium.qa.enums.Browser;

public class WebDriverManager implements ITest {
	public static WebDriver driver;
	private static Browser browser;
	private static final String chrome_Browser_property = "webdriver.chrome.driver";
	private static final String firefox_Browser_property = "webdriver.gecko.driver";
	private static final String IE_Browser_property = "webdriver.ie.driver";
	private static HashMap<String, WebDriver> driverObjMap = new HashMap<String, WebDriver>();
	public ThreadLocal<String> testName = new ThreadLocal<>();
	protected String testCaseName = "";
	public ArrayList<String> allTests = new ArrayList<String>();
	public static String strTCName;
	public static String strMethodName;

	/*
	 * @description : Method to get the details of driver
	 * 
	 * @param :
	 * 
	 * @return : driverObjMap
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	public static WebDriver gerDriverDetails(String className) {
		return driverObjMap.get(className);
	}

	/*
	 * @description : Method to get the details of browser
	 * 
	 * @param :
	 * 
	 * @return : browser
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public WebDriverManager() {
		browser = FileReaderManager.getInstance().getConfigReader().getBrowser();
	}

	/*
	 * @description : Method to create the driver
	 * 
	 * @param :
	 * 
	 * @return : WebDriver
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	@BeforeTest
	public WebDriver getDriver() {
		strTCName=this.getClass().getSimpleName();
		return createDriver();
	}

	/*
	 * @description : Method to create the driver
	 * 
	 * @param :
	 * 
	 * @return : WebDriver
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private WebDriver createDriver() {

		switch (browser) {
		case FIREFOX:
			File folder = new File("C:\\WBG\\TRS\\ExceptionProjectSetupFiles");
			folder.mkdir();
			System.setProperty(firefox_Browser_property,
					FileReaderManager.getInstance().getConfigReader().getDriverPath());
			driver = new FirefoxDriver();
			break;
		case CHROME:
			System.setProperty(chrome_Browser_property,
					FileReaderManager.getInstance().getConfigReader().getDriverPath());
			ChromeOptions options = new ChromeOptions();
			options.setExperimentalOption("useAutomationExtension", false);
			Map<String, Object> prefs = new HashMap<String, Object>();
			prefs.put("download.prompt_for_download", false);
			prefs.put("download.default_directory", "C:\\WBG\\TRS\\ExceptionProjectSetupFiles");
			options.setExperimentalOption("prefs", prefs);
			driver = new ChromeDriver(options);
			driverObjMap.put(getClass().getName(), driver);

			break;
		case IE:
			System.setProperty(IE_Browser_property, FileReaderManager.getInstance().getConfigReader().getDriverPath());
			driver = new InternetExplorerDriver();
			break;

		}
		driver.manage().window().maximize();
		driver.manage().timeouts().implicitlyWait(
				FileReaderManager.getInstance().getConfigReader().getCustomWait("ImplicitWait"), TimeUnit.SECONDS);
		return driver;
	}

	/*
	 * @description : Method to close the open driver
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	@AfterTest
	public void closeDriver() {
		driver.manage().deleteAllCookies();
		driver.close();
		driver.quit();

	}

	
	/*
	 * @description : Method to set custom name for testcase
	 * 
	 * @param :Method method, Object[] testData
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	@BeforeMethod(alwaysRun = true)
	public void setCustomTestcaseName(Method method, Object[] testData) {
		/* Set the default name */
		this.testName.set(method.getName());
		strMethodName=method.getName();

		/* Change the test name only if Dataprovider is used */
		if (testData != null && testData.length > 0) {
			System.out.println("\n\nParameters " + testData[0] + " are passed to the test - " + method.getName());
			System.out.println("I'm setting custom name to the test as " + method.getName() + "_" + testData[1]);
			this.testName.set(method.getName() + "_" + testData[1]);
		}

		/* Add the name to the collection that stores all list names */
		allTests.add(testName.get());

	}

	/*
	 * @description : Method to get test case name
	 * 
	 * @param :NA
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	@Override
	public String getTestName() {
		this.testCaseName = testName.get();
		return this.testCaseName;
	}

	
	/*
	 * @description : Method to set result name for testcase
	 * 
	 * @param :ITestResult result, Method method
	 * 
	 * @return : NA
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result, Method method) {
		if (result.getParameters().length > 0) {
			System.out.println("Updating the name as Parameters are passed to the test-" + method.getName());
			try {
				/*
				 * This helps in setting unique name to method for each test instance for a data
				 * provider
				 */
				Field resultMethod = TestResult.class.getDeclaredField("m_method");
				resultMethod.setAccessible(true);
				resultMethod.set(result, result.getMethod().clone());

				Field methodName = org.testng.internal.BaseTestMethod.class.getDeclaredField("m_methodName");
				methodName.setAccessible(true);
				methodName.set(result.getMethod(), this.getTestName());
			} catch (Exception e) {
				e.printStackTrace();
			}
			System.out.println("New Name is - " + result.getMethod().getMethodName());
		}
	}

}
