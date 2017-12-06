package Refined;

//import java.io.IOException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.TimeUnit;

import org.openqa.selenium.By;
import org.openqa.selenium.Dimension;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import com.galenframework.api.Galen;
import com.galenframework.reports.GalenTestInfo;
import com.galenframework.reports.HtmlReportBuilder;
import com.galenframework.reports.model.LayoutReport;

public class GalenLibrary {

	DateFormat dateFormat = new SimpleDateFormat("dd-MM-yy-hh-mm");
	Date date = new Date();
	WebDriver webDriver = new ChromeDriver();
	WebDriverWait Explicitwait = new WebDriverWait(webDriver, 10);

	List<String> Urls = new ArrayList<String>();

	public int GalenUICheck(String TestName, String SpecFile, String Resolution) throws Exception {

		List<GalenTestInfo> tests = new LinkedList<GalenTestInfo>();
		GalenTestInfo test = GalenTestInfo.fromString(TestName);
		int total, passed, failed = 0;
		getdimension(Resolution);

		try {
			for (String temp : Urls) {
				webDriver.get(temp);
				webDriver.manage().timeouts().implicitlyWait(30, TimeUnit.SECONDS);
				String Feeds = getfeedscount(Resolution, "Xpath of parent element which has all children");
				LayoutReport report = Galen.checkLayout(webDriver, SpecFile, Arrays.asList(Resolution, Feeds));
				test.getReport().layout(report,
						TestName + " Resolution " + Resolution + " EmailAddress " + temp + " Feeds" + Feeds);
				tests.add(test);
				test.getTest().getGroups();
				total = test.getReport().fetchStatistic().getTotal();
				passed = test.getReport().fetchStatistic().getPassed();
				failed = total - passed;
			}
			webDriver.close();
			new HtmlReportBuilder().build(tests, "target/galen-html-reports/" + " " + TestName);
		} catch (Exception e) {
			e.printStackTrace();
		}
		return failed;
	}

	String getfeedscount(String Resolution, String locator) {
		String Feedcount;
		int feedcount = 0;
		List<WebElement> Feedscount = webDriver.findElements(By.xpath(locator));
		for (WebElement temp : Feedscount) {
			feedcount = feedcount + 1;
		}
		Feedcount = Resolution + "_" + feedcount;
		return Feedcount;
	}

	void getdimension(String Resolution) {
		String dimension[] = Resolution.split("x");
		int width = Integer.parseInt(dimension[0]);
		int height = Integer.parseInt(dimension[1]);
		Dimension change = new Dimension(width, height);
		webDriver.manage().window().setSize(change);
	}

	public void getemailurl(String Temp) {
		Urls.add(Temp);
	}
}
