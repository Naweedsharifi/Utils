package com.wbg.selenium.qa.utils;

import org.testng.*;
import org.testng.collections.Lists;
import org.testng.internal.Utils;
import org.testng.log4testng.Logger;
import org.testng.xml.XmlSuite;

import java.io.*;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;

public class CustomizedEmailableReport implements IReporter {

	private static final Logger L = Logger.getLogger(CustomizedEmailableReport.class);
	String Seperator = System.getProperty("file.separator");
	/* Instance fields */

	private PrintWriter out;
	private int row;
	private Integer testIndex;
	private int methodIndex;
	private Scanner scanner;

	/*
	 * @description : Method to Generate Report
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 29 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	@Override
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outdir) {
		/* Creates summary of the run */
		// outdir="file:///" + System.getProperty("user.dir") + Seperator
		// +"test-output"+Seperator+"results";
		try {
			out = createWriter(outdir, suites);
		} catch (IOException e) {
			L.error("output file", e);
			return;
		}

		startHtml(out);
		generateSuiteSummaryReport(suites);
		// generateMethodSummaryReport(suites);
		generateMethodDetailReport(suites);
		endHtml(out);
		out.flush();
		out.close();
	}

	/*
	 * @description : Method to Create a HTML file Report
	 * 
	 * @param :outdir
	 * 
	 * @return : PrintWriter
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	protected PrintWriter createWriter(String outdir, List<ISuite> suites) throws IOException {
		/* Creates HTML file Report */
		new File(outdir).mkdirs();
		LocalDateTime myDateObj = LocalDateTime.now();

		DateTimeFormatter myFormatObj = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

		String formattedDate = myDateObj.format(myFormatObj);
		formattedDate = formattedDate.replace(":", " ");

		return new PrintWriter(new BufferedWriter(
				new FileWriter(new File(outdir, suites.get(0).getName() + formattedDate + ".html"))));

	}
	/*
	 * @description : Method to fetch all the test results
	 * 
	 * @param :suite,testsOnly
	 * 
	 * @return : testsOnly
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	public List<ITestResult> getAllTestResults(ISuite suite, boolean testsOnly) {
		/* fetch all the test results */
		List<ITestResult> result = Lists.newArrayList();
		for (ISuiteResult sr : suite.getResults().values()) {
			result.addAll(sr.getTestContext().getPassedTests().getAllResults());
			result.addAll(sr.getTestContext().getFailedTests().getAllResults());
			result.addAll(sr.getTestContext().getSkippedTests().getAllResults());
			if (!testsOnly) {
				result.addAll(sr.getTestContext().getPassedConfigurations().getAllResults());
				result.addAll(sr.getTestContext().getFailedConfigurations().getAllResults());
				result.addAll(sr.getTestContext().getSkippedConfigurations().getAllResults());
			}
		}
		return result;
	}

	/*
	 * @description : Creates a table showing the highlights of each test method
	 * with links to the method details
	 * 
	 * @param :suites
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	protected void generateMethodSummaryReport(List<ISuite> suites) {

		/*
		 * Creates a table showing the highlights of each test method with links to the
		 * method detail
		 */
		methodIndex = 0;
		int pass = 0;
		startResultSummaryTable("methodOverview");
		int testIndex = 1;
		for (ISuite suite : suites) {

			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				ITestContext testContext = r2.getTestContext();

				String testName = testContext.getName();
				this.testIndex = testIndex;

				int fail = resultSummary(suite, testContext.getFailedTests(), testName, "", "");
				resultSummary(suite, testContext.getSkippedTests(), "", "", "");
				if (fail == 0) {
					pass = resultSummary(suite, testContext.getPassedTests(), testName, "", "");

				} else {
					pass = resultSummary(suite, testContext.getPassedTests(), "", "", "");
				}
				testIndex++;
			}
		}
		out.println("</table>");
	}

	/*
	 * @description : Creates a section showing known results for each method
	 * 
	 * @param :suites
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	protected void generateMethodDetailReport(List<ISuite> suites) {

		/* Creates a section showing known results for each method */
		methodIndex = 0;
		int testIndex = 1;
		for (ISuite suite : suites) {
			Map<String, ISuiteResult> r = suite.getResults();
			for (ISuiteResult r2 : r.values()) {
				this.testIndex = testIndex;
				ITestContext testContext = r2.getTestContext();

				if (r.values().size() > 0) {

					out.println("<h1 id=\"t" + testIndex + "\">" + testContext.getName() + "</h1>");
				}
				resultDetail(testContext.getPassedTests());
				resultDetail(testContext.getFailedTests());
				testIndex++;
			}
		}
	}

	/*
	 * @description : Creates a method to fetch the result details
	 * 
	 * @param :suites,tests.testname,style,details
	 * 
	 * @return : int
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	private int resultSummary(ISuite suite, IResultMap tests, String testname, String style, String details) {

		int count = 0;
		if (tests.getAllResults().size() > 0) {
			StringBuffer buff = new StringBuffer();
			String lastClassName = "";
			int mq = 0;
			int cq = 0;
			for (ITestNGMethod method : getMethodSet(tests, suite)) {
				row += 1;
				methodIndex += 1;
				ITestClass testClass = method.getTestClass();
				String className = testClass.getName();

				if (mq == 0) {
					String id = (testIndex == null ? null : "t" + Integer.toString(testIndex));
					if (testname != "") {
						titleRow(testname + details, 5, id);
					}
					testIndex = null;
				}
				if (!className.equalsIgnoreCase(lastClassName)) {
					if (mq > 0) {
						cq += 1;
						out.print("<tr height=\"1px\">" + "<td");
						if (mq > 1) {
							out.print(" rowspan=\"" + mq + "\"");

						}
						out.println("height=\"2px\"></td>" + buff);
					}
					mq = 0;
					buff.setLength(0);
					lastClassName = className;
				}
				Set<ITestResult> resultSet = tests.getResults(method);
				long end = Long.MIN_VALUE;
				long start = Long.MAX_VALUE;
				long startMS = 0;
				String firstLine = "";
				String screenshotLnk = "";
				for (ITestResult testResult : tests.getResults(method)) {
					if (testResult.getEndMillis() > end) {
						end = testResult.getEndMillis() / 1000;
					}
					if (testResult.getStartMillis() < start) {
						startMS = testResult.getStartMillis();
						start = startMS / 1000;
					}

					Throwable exception = testResult.getThrowable();

					boolean hasThrowable = exception != null;
					if (hasThrowable) {

						/* for link in exception */
						List<String> msgs = Reporter.getOutput(testResult);
						boolean hasReporterOutput = msgs.size() > 0;
						if (hasReporterOutput) {
							for (String line : msgs) {
								if (line.contains("</a")) {
									// out.println(line + "<br/>");
									screenshotLnk += line + "<br/>";
								} else {
									if (line.contains("Skipped")) {
										// out.println("<h4 class=\"pass\">Pass</h4><br/>");
										screenshotLnk += line + "<br/>";
									}
								}
							}
						}

					} else

					{
						/* for link in exception */
						List<String> msgs = Reporter.getOutput(testResult);
						boolean hasReporterOutput = msgs.size() > 0;
						if (hasReporterOutput) {
							for (String line : msgs) {
								if (line.contains("</a")) {
									// out.println("<h4 class=\"pass\">Pass</h4><br/>");
									screenshotLnk += line + "<br/>";
								} else {
									if (line.contains("Skipped")) {
										// out.println("<h4 class=\"pass\">Pass</h4><br/>");
										screenshotLnk += line + "<br/>";
									}
								}
							}
						}

					}
				}

				DateFormat formatter = new SimpleDateFormat("kk:mm:ss");
				Calendar calendar = Calendar.getInstance();
				calendar.setTimeInMillis(startMS);

				mq += 1;
				if (mq > 1) {
					buff.append("<tr>");
				}
				String description = method.getDescription();
				String testInstanceName = resultSet.toArray(new ITestResult[] {})[0].getTestName();
				buff.append("<td><a href=\"#m" + methodIndex + "\">" + qualifiedName(method) + " "
						+ (description != null && description.length() > 0 ? "(\"" + description + "\")" : "") + "</a>"
						+ (null == testInstanceName ? "" : "<br>(" + testInstanceName + ")") + "</td>"
						+ "<td  style=\"text-align:left;padding-right:2em\">" + screenshotLnk + "<br/></td>"
						+ "<td style=\"text-align:right\">" + formatter.format(calendar.getTime()) + "</td>" + "<td>"
						+ millisToTimeConversion(end - start) + "</td>" + "</tr>");
				count = count + 1;
				// resultSet.size()
			}
			if (mq > 0) {
				cq += 1;

				if (mq > 1) {
					out.print("<tr height=\"1px\"><td");
					out.print(" rowspan=\"" + mq + "\"");
					out.print("<td height=\"2px\">" + lastClassName + "</td>" + buff);
				} else {
					out.print("<tr height=\"1px\"><td height=\"2px\">" + lastClassName + "</td>" + buff);
				}
			}
		}
		return count;

	}
	/*
	 * @description : Creates a Convert the mili to seconds
	 * 
	 * @param :seconf]d
	 * 
	 * @return : String
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private String millisToTimeConversion(long seconds) {

		final int MINUTES_IN_AN_HOUR = 60;
		final int SECONDS_IN_A_MINUTE = 60;

		int minutes = (int) (seconds / SECONDS_IN_A_MINUTE);
		seconds -= minutes * SECONDS_IN_A_MINUTE;

		int hours = minutes / MINUTES_IN_AN_HOUR;
		minutes -= hours * MINUTES_IN_AN_HOUR;

		return prefixZeroToDigit(hours) + ":" + prefixZeroToDigit(minutes) + ":" + prefixZeroToDigit((int) seconds);
	}

	private String prefixZeroToDigit(int num) {
		int number = num;
		if (number <= 9) {
			String sNumber = "0" + number;
			return sNumber;
		} else
			return "" + number;

	}

	/*
	 * @description : Starts and defines columns result summary table
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private void startResultSummaryTable(String style) {
		/* Starts and defines columns result summary table */
		out.println("<tr class=\"heading\"><th>Test Case Name</th>"
				+ "<th>Method</th><th>Exception & screenshot</th><th>Start Time </th><th>Time<br/>(hh:mm:ss)</th></tr>");
		row = 0;
	}

	private String qualifiedName(ITestNGMethod method) {
		StringBuilder addon = new StringBuilder();
		String[] groups = method.getGroups();
		int length = groups.length;
		if (length > 0 && !"basic".equalsIgnoreCase(groups[0])) {
			addon.append("(");
			for (int i = 0; i < length; i++) {
				if (i > 0) {
					addon.append(", ");
				}
				addon.append(groups[i]);
			}
			addon.append(")");
		}

		return "<b>" + method.getMethodName() + "</b> " + addon;
	}

	/*
	 * @description :Result details-Enter the data for result
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private void resultDetail(IResultMap tests) {

		Set<ITestResult> testResults = tests.getAllResults();

		List<ITestResult> list = new ArrayList<ITestResult>(testResults);
		Collections.sort(list, new TestResultsSorter());
		for (ITestResult result : list) {

			ITestNGMethod method = result.getMethod();
			methodIndex++;
			String cname = method.getTestClass().getName();
			out.println("<h2 id=\"m" + methodIndex + "\">" + method.getMethodName() + "</h2>");
			Set<ITestResult> resultSet = tests.getResults(method);
			generateForResult(result, method, resultSet.size());

			out.println("<p class=\"totop\"><a href=\"#summary\">back to summary</a></p>");

		}
	}

	/*
	 * @description : Create a structure for Summary result
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private void generateForResult(ITestResult ans, ITestNGMethod method, int resultSetSize) {
		Object[] parameters = ans.getParameters();
		boolean hasParameters = parameters != null && parameters.length > 0;
		List<String> msgs = Reporter.getOutput(ans);
		boolean hasReporterOutput = msgs.size() > 0;
		Throwable exception = ans.getThrowable();
		boolean hasThrowable = exception != null;
		if (hasReporterOutput || hasThrowable) {
			if (msgs != null) {
				if (hasReporterOutput) {
					out.println("<html>" + "<body>" + "<table class=\"summary\" border='1' align=Center>"
							+ "<tr class=\"heading\">" + "<td>Step Description</td>" + "<td>Step Expected</td>"
							+ "<td>Step Actual</td>" + "<td>Result</td>" + "</tr>");

					for (String line : msgs) {
						if (!line.contains("<a")) {
							String splitstring = (Utils.toString(line));
							out.println("<tr>");
							String[] straraay = splitstring.split(",");
							for (int i = 0; i < straraay.length; i++) {
								if (straraay[i].equals("Fail")) {
									out.println("<td class=\"fail\">" + straraay[i] + "</td>");
								} else if (straraay[i].equals("Pass")) {
									out.println("<td class=\"pass\">" + straraay[i] + "</td>");
								} else {
									out.println("<td>" + straraay[i] + "</td>");
								}
							}
							out.println("</tr>");

						} else if (line.contains("<a")) {
							String splitstring = (Utils.toString(line));
							out.println("<tr>");
							String[] straraay = splitstring.split(",");
							for (int i = 0; i < straraay.length; i++) {
								if (straraay[i].equals("Fail") || straraay[i].equals("Pass")) {
									out.println(straraay[i]);
								} else {
									out.println("<td>" + straraay[i] + "</td>");
								}
							}
							out.println("</tr>");

						}

					}

					out.println("</table></body></html>");
				}
				if (hasThrowable) {
					boolean wantsMinimalOutput = ans.getStatus() == ITestResult.SUCCESS;

				}
			}

		}
		if (hasParameters) {
			out.println("</table>");
		}
	}

	/*
	 * @description : generate ExceptionReport
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	protected void generateExceptionReport(Throwable exception, ITestNGMethod method) {
		out.print("<div class=\"stacktrace\">");
		out.print(Utils.stackTrace(exception, true)[0]);
		out.println("</div>");
	}

	/*
	 * @description : Since the methods will be sorted chronologically, we want to
	 * return the ITestNGMethod from the invoked methods.
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private Collection<ITestNGMethod> getMethodSet(IResultMap tests, ISuite suite) {
		/**
		 * Since the methods will be sorted chronologically, we want to return the
		 * ITestNGMethod from the invoked methods.
		 */
		List<IInvokedMethod> r = Lists.newArrayList();
		List<IInvokedMethod> invokedMethods = suite.getAllInvokedMethods();
		for (IInvokedMethod im : invokedMethods) {
			// System.out.println("suite.getAllInvokedMethods() .."+im);
			if (tests.getAllMethods().contains(im.getTestMethod())) {
				r.add(im);
			}
		}

		//
		// /*Change method Name */
		// int count=0;
		// String testBase=r.get(0).getTestMethod().getMethodName();
		// for (int i = 1; i < r.size(); i++) {
		// if (testBase.equals(r.get(i).getTestMethod().getMethodName())) {
		// String strConvertedName=r.get(i)+"Iteration"+count;
		// r.get(i).getTestMethod().getMethodName().replace(testBase, strConvertedName);
		// }
		// }

		Collections.sort(r, new TestSorter());

		List<ITestNGMethod> result = Lists.newArrayList();

		/* Add all the invoked methods */
		for (IInvokedMethod m : r) {
			for (ITestNGMethod temp : result) {
				if (!temp.equals(m.getTestMethod()))
					result.add(m.getTestMethod());
			}
		}

		/*
		 * Add all the methods that weren't invoked (e.g. skipped) that we haven't added
		 * yet
		 */

		Collection<ITestNGMethod> allMethodsCollection = tests.getAllMethods();
		List<ITestNGMethod> allMethods = new ArrayList<ITestNGMethod>(allMethodsCollection);
		// System.out.println("All methods before sort" + allMethods.toString());
		Collections.sort(allMethods, new TestMethodSorter());
		// System.out.println("After sorting " + allMethods.toString());

		for (ITestNGMethod m : allMethods) {

			if (!result.contains(m)) {
				result.add(m);
			}
		}

		return result;
	}

	/*
	 * @description :Create the structure for Summary Report
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	@SuppressWarnings("unused")
	public void generateSuiteSummaryReport(List<ISuite> suites) {
		int passnumber = 0;
		int failnumber = 0;
		tableStart("testOverview", null);
		out.print("<h1 class=left>" + suites.get(0).getName() + "</h1>");
		// titleRow(suites.get(0).getName(), 8);
		out.print("<tr class=\"heading\">");
		tableColumnStart("Test Case");
		tableColumnStart("Methods<br/>Passed");

		tableColumnStart("Methods<br/> skipped");
		tableColumnStart("Methods<br/>failed");
		tableColumnStart("Status");

		tableColumnStart("Start<br/>Time");
		tableColumnStart("End<br/>Time");
		tableColumnStart("Total<br/>Time");

		out.println("</tr>");
		NumberFormat formatter = new DecimalFormat("#,##0.0");
		int qty_tests = 0;
		int qty_pass_m = 0;
		int qty_pass_s = 0;
		int qty_skip = 0;
		int qty_fail = 0;
		long time_start = Long.MAX_VALUE;
		long time_end = Long.MIN_VALUE;
		testIndex = 1;
		for (ISuite suite : suites) {
			if (suites.size() > 1) {
				titleRow(suite.getName(), 8);
			}
			Map<String, ISuiteResult> tests = suite.getResults();
			for (ISuiteResult r : tests.values()) {
				qty_tests += 1;
				ITestContext overview = r.getTestContext();

				startSummaryRow(overview.getName());
				int q = getMethodSet(overview.getPassedTests(), suite).size();
				qty_pass_m += q;
				summaryCell(q, Integer.MAX_VALUE);
				/*
				 * q = overview.getPassedTests().size(); qty_pass_s += q; summaryCell(q,
				 * Integer.MAX_VALUE);
				 */
				q = getMethodSet(overview.getSkippedTests(), suite).size();
				qty_skip += q;
				summaryCell(q, 0);
				q = getMethodSet(overview.getFailedTests(), suite).size();
				qty_fail += q;
				summaryCell(q, 0);

				/* NEW Insert error found */
				out.print("<td class=\"numi" + (true ? "" : "_attn") + "\">");
				int fail = getShortException(overview.getFailedTests());

				int pass = getShortException(overview.getPassedTests());

				int skip = getShortException(overview.getSkippedTests());
				if (fail > 0) {
					failnumber = failnumber + 1;
					out.print("Failed");
				} else if (fail == 0) {
					passnumber = passnumber + 1;
					out.print("Success");
				}
				out.println("</td>");

				SimpleDateFormat summaryFormat = new SimpleDateFormat("kk:mm:ss");
				summaryCell(summaryFormat.format(overview.getStartDate()), true);
				out.println("</td>");

				summaryCell(summaryFormat.format(overview.getEndDate()), true);
				out.println("</td>");

				time_start = Math.min(overview.getStartDate().getTime(), time_start);
				time_end = Math.max(overview.getEndDate().getTime(), time_end);

				summaryCell(millisToTimeConversion(
						(overview.getEndDate().getTime() - overview.getStartDate().getTime()) / 1000), true);

				out.println("</tr>");
				testIndex++;
			}
		}
		if (qty_tests > 1) {
			out.println("<tr class=\"total\"><td>Total</td>");
			summaryCell(qty_pass_m, Integer.MAX_VALUE);

			summaryCell(qty_skip, 0);
			summaryCell(qty_fail, 0);
			summaryCell(" ", true);
			summaryCell(" ", true);
			summaryCell(" ", true);

			summaryCell(formatter.format(((time_end - time_start) / 1000.) / 60.) + " minutes", true);
			out.println("</tr>");
			String val = "Total- " + passnumber + " Passed & " + failnumber + " Failed";

			out.println(
					"<tr class=\"total\"><th class=\"pass\"colspan=1 style=\"text-align:Center\">Total Passed : </th>");
			out.println("<th class=\"pass\" colspan=3 style=\"text-align:Center\">" + passnumber + "</th>");
			out.println("<th class=\"fail\" colspan=3 style=\"text-align:Center\">Total Failed : </th>");
			out.println("<th class=\"fail\"colspan=1 style=\"text-align:Center\">" + failnumber + "</th></tr>");

		}
		out.println("</table>");
	}

	/*
	 * @description : append the values in Summary cell
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private void summaryCell(String[] val) {
		StringBuffer b = new StringBuffer();
		for (String v : val) {
			b.append(v + " ");
		}
		summaryCell(b.toString(), true);
	}

	/*
	 * @description : Format the in Summary cell
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private void summaryCell(String v, boolean isgood) {
		out.print("<td class=left" + (isgood ? "" : "_attn") + "\">" + v + "</td>");
	}

	/*
	 * @description : Adding Summary start row
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private void startSummaryRow(String label) {
		row += 1;
		out.print("<tr" + (row % 2 == 0 ? " class=\"stripe\"" : "")
				+ "><td style=\"text-align:left;padding-right:2em\"><a href=\"#t" + testIndex + "\"><b>" + label
				+ "</b></a>" + "</td>");

	}

	private void summaryCell(int v, int maxexpected) {
		summaryCell(String.valueOf(v), v <= maxexpected);
	}

	/*
	 * @description : Adding Table
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	private void tableStart(String cssclass, String id) {
		out.println("<table id=summary>");
		row = 0;
	}

	private void tableColumnStart(String label) {
		out.print("<th>" + label + "</th>");
	}

	private void titleRow(String label, int cq) {
		titleRow(label, cq, null);
	}

	private void titleRow(String label, int cq, String id) {
		out.print("<tr");
		if (id != null) {
			out.print(" id=\"" + id + "\"");
		}
		out.println("><th colspan=\"" + cq + "\">" + label + "</th></tr>");
		row = 0;
	}

	/*
	 * @description :Providing the css style to HTML
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */
	protected void startHtml(PrintWriter out) {
		/** Starts HTML stream */
		out.println(
				"<!DOCTYPE html PUBLIC \"-//W3C//DTD XHTML 1.1//EN\" \"http://www.w3.org/TR/xhtml11/DTD/xhtml11.dtd\">");
		out.println("<html xmlns=\"http://www.w3.org/1999/xhtml\">");
		out.println("<head>");
		out.println("<title>TestNG Report</title>");
		out.println("<style type=\"text/css\">");
		out.println(
				"table {border: 2px solid #000000;border-collapse: collapse;border-spacing: 0px;width: 1000px;margin-left: auto;margin-right: auto;background-color: #dfeceb;}");
		out.println(
				"table.summary {border: 2px solid #000000;border-collapse: collapse;border-spacing: 0px;width: 1000px;margin-left: auto;margin-right: auto;table-layout: fixed;width: 100%;background-color: #dfeceb;}");
		out.println("tr.heading {background-color: #89B6B5;color: #000000;font-size: 0.9em;font-weight: bold;}");
		out.println(
				"tr.subheading {background-color: #EFFBF8;color: #000000;font-weight: bold;font-size: 0.9em;text-align: left;}");
		out.println("td.left {text-align: Center;}");
		out.println("th.left {text-align: Center;}");
		out.println("td.pass {font-weight: bold;color: green;}");
		out.println("td.fail {font-weight: bold;color: red;}");
		out.println("tr.ratio {height:10px;}");
		out.println("td.ratio {height:5px;}");
		out.println("h1.left {text-align: Center;}");
		out.println("th.pass {font-weight: bold;color: green;}");
		out.println("th.fail {font-weight: bold;color: red;}");

		out.println(
				"td,th {padding: 4px;text-align: inherit\\0/;display: table-cell;border: 1px solid #000000;frame:vsides;overflow: hidden;text-overflow: ellipsis;word-wrap: break-word;\r\n"
						+ "}");
		out.println(".total td {font-weight:bold}");

		out.println(".totop {font-size:85%;text-align:center;border-bottom:2px solid #000}");
		out.println("</style>");
		out.println("</head>");
		out.println("<body>");
	}

	/* Finishes HTML stream */
	protected void endHtml(PrintWriter out) {
		// out.println("<center> Report customized </center>");
		out.println("</body></html>");
	}

	/*
	 * @description : Sorting of Test Method
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	/* Inner Classes */
	/* Arranges methods by classname and method name */
	private class TestSorter implements Comparator<IInvokedMethod> {
		/* Methods */

		/* Arranges methods by classname and method name */
		@Override
		public int compare(IInvokedMethod o1, IInvokedMethod o2) {

			int r = o1.getTestMethod().getTestClass().getName().compareTo(o2.getTestMethod().getTestClass().getName());

			if (r == 0) {

				r = o1.getTestMethod().getMethodName().compareTo(o2.getTestMethod().getMethodName());

			}
			return r;
		}

	}

	private class TestMethodSorter implements Comparator<ITestNGMethod> {
		@Override
		public int compare(ITestNGMethod o1, ITestNGMethod o2) {
			int r = 0;
			return r = (int) (o1.getDate() - o2.getDate());

		}
	}
	/*
	 * @description : Sorting of Test Result
	 * 
	 * @param :
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private class TestResultsSorter implements Comparator<ITestResult> {
		@Override
		public int compare(ITestResult o1, ITestResult o2) {
			int r = 0;

			return (int) (o1.getEndMillis() - o2.getEndMillis());

		}
	}
	/*
	 * @description : Write all parameters
	 * 
	 * @param :tests
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private void getParameters(IResultMap tests) {
		/* Write all parameters */
		for (ITestResult result : tests.getAllResults()) {
			methodIndex++;
			Object[] parameters = result.getParameters();
			boolean hasParameters = parameters != null && parameters.length > 0;
			if (hasParameters) {

				for (Object p : parameters) {
					out.println(Utils.escapeHtml(Utils.toString(p)) + " | ");
				}
			}

		}
	}

	/*
	 * @description : Write the first line of the stack trace
	 * 
	 * @param :tests
	 * 
	 * @return :
	 * 
	 * @date : 28 Dec 2020
	 * 
	 * @author : Infosys Limited
	 */

	private int getShortException(IResultMap tests) {
		/* Write the first line of the stack trace */
		int failcount = 0;

		int rescount = tests.getAllResults().size();

		outerloop: for (ITestResult result : tests.getAllResults()) {

			methodIndex++;
			Throwable exception = result.getThrowable();
			List<String> msgs = Reporter.getOutput(result);
			boolean hasReporterOutput = msgs.size() > 0;
			boolean hasThrowable = exception != null;
			if (hasThrowable) {

				boolean wantsMinimalOutput = result.getStatus() == ITestResult.SUCCESS;
				if (hasReporterOutput) {
					failcount = failcount + 1;

				}

				// Getting first line of the stack trace
				String str = Utils.stackTrace(exception, true)[0];
				scanner = new Scanner(str);
				String firstLine = scanner.nextLine();
				// out.println(firstLine);
			} else if (result.getStatus() == ITestResult.SUCCESS) {
				failcount = failcount + 1;

			}

		}

		return failcount;

	}

}