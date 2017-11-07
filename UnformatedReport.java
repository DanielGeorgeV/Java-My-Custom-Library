package com.owler.report;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.testng.IReporter;
import org.testng.ISuite;
import org.testng.ITestNGMethod;
import org.testng.ITestResult;
import org.testng.xml.XmlSuite;

/*
 * Author : Daniel George Valsarajan
 * 
 * Description : 
 *  Report class will create a report file of all test plan and,
 *  will also include the information logged during the execution of the test 
 *  to understand and monitor the flow of the test.
 *  
 *  HTML file created by this class has doesn't have javascript or any styles.
 */

public class SlackReport implements IReporter {
	public void generateReport(List<XmlSuite> xmlSuites, List<ISuite> suites, String outputDirectory) {
		for (ISuite suite : suites) {
			String suiteName = suite.getName() + "-SlackReport.html";
			String testName = suite.getName();
			try {
				FileWriter writer = createFile(outputDirectory, suiteName);
				startHtml(writer, testName);
				overallReport(writer, suite);
				casewiseReport(writer, suite);
				endHtml(writer);
			} catch (IOException E) {
				System.out.println("Report is not generator. " + E);
			}
		}
	}

	FileWriter createFile(String outputDirectory, String suiteName) throws IOException {
		return new FileWriter(new File(outputDirectory, suiteName));
	}

	void startHtml(FileWriter writer, String testName) throws IOException {
		writer.write("<!DOCTYPE html><html><head>");
		writer.write("<title>" + testName + "</title>");
		writer.write("</head>");
		writer.write("<h2 style=text-align:center>" + testName + " Automation Report</h2>");
		writer.write("<hr><body>");
	}

	void overallReport(FileWriter writer, ISuite suite) throws IOException {
		writer.write(
				"<table cellpadding=5, cellspacing=5, class='param'><tr><th class='numi'>Test Name</th><th class='numi'>Passed</th><th class='numi'>Failed</th><th class='numi'>Skipped</th><th class='numi'>Test Duration</th></tr>");
		for (String temp : suite.getResults().keySet()) {
			writer.write("<tr><td class='numi'>" + suite.getResults().get(temp).getTestContext().getName()
					+ "</strong></td><td class='passed'><b>"
					+ suite.getResults().get(temp).getTestContext().getPassedTests().size() + "</td>");
			writer.write("<td class='failed'>" + suite.getResults().get(temp).getTestContext().getFailedTests().size()
					+ "</td>");
			writer.write("<td class='skipped'>" + suite.getResults().get(temp).getTestContext().getSkippedTests().size()
					+ "</td>");
			long diffTime = suite.getResults().get(temp).getTestContext().getEndDate().getTime()
					- suite.getResults().get(temp).getTestContext().getStartDate().getTime();
			long time = TimeUnit.MILLISECONDS.toSeconds(diffTime);
			if (time >= 120) {
				time = TimeUnit.MILLISECONDS.toMinutes(diffTime);
				writer.write("<td class='numi'>" + time + " minutes</td>");
			} else {
				writer.write("<td class='numi'>" + time + " seconds</td>");
			}
			writer.write("</tr></table>");
		}
	}

	void casewiseReport(FileWriter writer, ISuite suite) throws IOException {
		Map<String, ITestResult> results = new HashMap<String, ITestResult>();
		Set<String> testNames = suite.getResults().keySet();
		for (String testName : testNames) {
			for (ITestResult iResult : suite.getResults().get(testName).getTestContext().getFailedTests()
					.getAllResults()) {
				results.put(iResult.getName(), iResult);
			}
			for (ITestResult iResult : suite.getResults().get(testName).getTestContext().getPassedTests()
					.getAllResults()) {
				results.put(iResult.getName(), iResult);
			}
			for (ITestResult iResult : suite.getResults().get(testName).getTestContext().getSkippedTests()
					.getAllResults()) {
				results.put(iResult.getName(), iResult);
			}
		}
		writer.write(
				"<table cellpadding=5, cellspacing=5, class='param'><tr><th class='numi'>Test Method</th><th class='numi'>Description</th><th class='numi'>Result</th></tr>");
		for (ITestNGMethod method : suite.getAllMethods()) {
			writer.write("<tr><td class='content'>" + method.getMethodName() + "</td><td class='description'>"
					+ method.getDescription() + "</td>");
			if ((results.get(method.getMethodName()).getStatus()) == 1) {
				writer.write("<td class='passed'>Pass</td>");
			} else {
				writer.write("<td class='failed'>Fail</td></tr>");
			}
		}
		writer.write("</table>");
	}

	void endHtml(FileWriter writer) throws IOException {
		writer.write("<hr>");
		writer.write("</body></html>");
		writer.flush();
		writer.close();
	}
}
