package pageObjects;

import java.io.FileInputStream;
import java.util.Properties;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;

import io.restassured.path.json.JsonPath;

public class PresentationLayer {

	public void UI(JsonPath jsonUser, ExtentTest test) throws Exception {
		System.setProperty("webdriver.chrome.driver", "./Drivers/chromedriver86.exe");
		WebDriver driver = new ChromeDriver();
		FileInputStream fis = new FileInputStream(
				"D:\\selenium demo\\OpenCollegeTest\\src\\test\\resources\\config\\projectData.properties");
		Properties prop = new Properties();
		prop.load(fis);
		driver.get(prop.getProperty("URL") + prop.getProperty("login"));
		test.log(Status.INFO, "Navigate to URL successful");
		String[] UI_validation = prop.getProperty("Check_UI_Responses").split(",");
		for (String item : UI_validation) {
			String UI_Element = driver.findElement(By.xpath(prop.getProperty(item + "_xpath"))).getText();
			Assert.assertEquals(UI_Element, jsonUser.get(item).toString());
			test.log(Status.PASS, "UI Element verified with API response" + item + "=" + UI_Element);
			// System.out.println("UI_Element verified " + item + "=" + UI_Element);
		}
		driver.close();
	}
}
