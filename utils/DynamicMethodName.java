package com.wbg.selenium.qa.utils;

import java.lang.reflect.Field;
import org.testng.ITest;
import org.testng.ITestResult;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.internal.TestResult;

import java.lang.reflect.Method;
import java.util.ArrayList;

public class DynamicMethodName implements ITest {

	public ThreadLocal<String> testName = new ThreadLocal<>();
	protected String testCaseName = "";
	public ArrayList<String> allTests = new ArrayList<String>();

	@BeforeMethod(alwaysRun = true)
	public void setCustomTestcaseName(Method method, Object[] testData) {
		/* Set the default name */
		this.testName.set(method.getName());

		/* Change the test name only if Dataprovider is used */
		if (testData != null && testData.length > 0) {
			System.out.println("\n\nParameters " + testData[0] + " are passed to the test - " + method.getName());
			System.out.println("I'm setting custom name to the test as " + method.getName() + "_" + testData[0]);
			this.testName.set(method.getName() + "_" + testData[0]);
		}

	/* Add the name to the collection that stores all list names */
	allTests.add(testName.get());

}

	@Override
	public String getTestName() {
		this.testCaseName = testName.get();
		return this.testCaseName;
	}

	@AfterMethod(alwaysRun = true)
	public void setResultTestName(ITestResult result, Method method) {
		if (result.getParameters().length > 0) {
			if (result.getParameters()[0] instanceof String) {
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
}