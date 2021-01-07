import java.io.FileInputStream;
import java.util.Properties;

import org.openqa.selenium.WebDriver;
import org.testng.Assert;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;

//import extentReport.ExtentReportNG;
import io.restassured.RestAssured;
import io.restassured.http.Method;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import pageObjects.PresentationLayer;

public class GitHubUsers extends PresentationLayer {
	WebDriver driver;
	Properties prop = new Properties();

	static ExtentTest test;
	static ExtentReports report;

	@BeforeClass
	public static void startTest() {
		
		String path = System.getProperty("user.dir") + "\\reports\\index.html";
		System.out.println("Path : --> " + path);
		
		ExtentSparkReporter sparkreporter = new ExtentSparkReporter(path);
		sparkreporter.config().setReportName("my Extent Report");
		sparkreporter.config().setDocumentTitle("ExtentReport Site");
		
		report = new ExtentReports();
		report.attachReporter(sparkreporter);
		report.setSystemInfo("Tester", "Suganya Sengodan");
		report.setSystemInfo("Email", "sugansengodan@gmail.com");
		
		test = report.createTest("GitHubUsers");
	}

	@Test
	public void GetUserDetails() throws Exception {

		FileInputStream fis = new FileInputStream(
				"D:\\selenium demo\\OpenCollegeTest\\src\\test\\resources\\config\\projectData.properties");
		prop.load(fis);

		PresentationLayer pLayer = new PresentationLayer();

		RestAssured.baseURI = prop.getProperty("URI");

		RequestSpecification httpRequest = RestAssured.given();

		Response response = httpRequest.request(Method.GET, prop.getProperty("login"));

		int status_code = response.getStatusCode();
		Assert.assertEquals(status_code, 200, "Received unexpected status code");
		test.log(Status.PASS, "Status code verified");

		String content_type = response.header("Content-Type");
		Assert.assertEquals(content_type, "application/json; charset=utf-8", "Received unexpected content type");

		test.log(Status.PASS, "Header content type verified");

		JsonPath jsonUser = response.jsonPath();

		if (prop.getProperty("local_value_check").equals("true")) {
			test.log(Status.INFO, "local property file value check required");
			CheckResponses(jsonUser);
		}
		
		test.log(Status.INFO, "Validating API response in Web UI");
		
		pLayer.UI(jsonUser, test);

	}

	/*
	 * Check Responses function validates the local property file elements with the
	 * corresponding API response elements. Also, this function is made optional
	 * with the flag local_value_check in order to proceed with different users
	 */

	void CheckResponses(JsonPath jsonUser) {

		String[] validate = prop.getProperty("Check_API_Responses").split(",");
		for (String item : validate) {
			test.log(Status.INFO, "Checking " + item);
			Assert.assertEquals(jsonUser.get(item).toString(), prop.getProperty(item),
					"Received incorrect user details");
			test.log(Status.PASS, "verified response from API");

		}

	}

	/*
	 * void CheckPresentationLayer(JsonPath jsonUser) {
	 * System.setProperty("webdriver.chrome.driver",
	 * "./Drivers/chromedriver86.exe"); driver = new ChromeDriver();
	 * driver.get(prop.getProperty("URL") + prop.getProperty("login")); String[]
	 * UI_validation = prop.getProperty("Check_UI_Responses").split(","); for
	 * (String item : UI_validation) { String UI_Element =
	 * driver.findElement(By.xpath(prop.getProperty(item + "_xpath"))).getText();
	 * Assert.assertEquals(UI_Element, jsonUser.get(item).toString());
	 * System.out.println("UI_Element verified  " + item + "=" + UI_Element); }
	 * driver.close(); }
	 */
	@AfterClass
	public static void endTest() {
		report.flush();
	}
}
