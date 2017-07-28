package first.com.qait.tatoc;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;
import com.jayway.restassured.RestAssured;

/**
 * Advanced TATOC
 * 
 * @author nachiketatripathi
 *
 */
public class TatocAdvancedDemoTest {

	WebDriver driver = new ChromeDriver();
	WebDriverWait wait = new WebDriverWait(driver, 10);

	@BeforeClass
	public void initializeDriver() {
		driver.manage().window().maximize();
		System.setProperty("webdriver.chrome.driver", ".//chromedriver.exe");
		driver.get("http://10.0.1.86/tatoc/advanced");
	}

	@Test(priority = 1)
	public void testHoverMenu() {
		WebElement element = wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[@class='menutitle' and contains(text(),'Menu 2')]")));
		Actions action = new Actions(driver);
		action.moveToElement(element).build().perform();
		wait.until(ExpectedConditions
				.visibilityOfElementLocated(By.xpath("//*[@class='menuitem' and contains(text(),'Go Next')]"))).click();
	}

	@Test(priority = 2)
	public void testQueryGate() throws SQLException, ClassNotFoundException {
		String value = null, name = null, passkey = null;
		String dbUrl = "jdbc:mysql://10.0.1.86/tatoc";
		String symbol = driver.findElement(By.xpath("//*[@id='symboldisplay']")).getText();
		Class.forName("com.mysql.jdbc.Driver");
		Connection connection = DriverManager.getConnection(dbUrl, "tatocuser", "tatoc01");
		Statement statement = connection.createStatement();
		ResultSet resultSet1 = statement.executeQuery("select *  from identity;");
		while (resultSet1.next()) {
			if (symbol.toLowerCase().equals(resultSet1.getString(2))) {
				value = resultSet1.getString(1);
			}
		}
		ResultSet resultSet2 = statement.executeQuery("select *  from credentials;");
		while (resultSet2.next()) {
			if (value.equals(resultSet2.getString(1))) {
				name = resultSet2.getString(2);
				passkey = resultSet2.getString(3);
			}
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='name']"))).sendKeys(name);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='passkey']"))).sendKeys(passkey);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='submit']"))).click();
	}

	/*
	 * @Test(priority = 3) public void testOoyalaVideoPlayer() {
	 * driver.get("http://10.0.1.86/tatoc/advanced/video/player");
	 * 
	 * }
	 */

	@Test(priority = 4)
	public void testRestful() throws ParseException {
		driver.get("http://10.0.1.86/tatoc/advanced/rest/#");
		String session = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='session_id']")))
				.getText();
		String sessionId = session.substring(session.indexOf(':') + 1);
		sessionId = sessionId.trim();
		System.out.println(sessionId);
		String newSession = RestAssured.given().when()
				.get("http://10.0.1.86/tatoc/advanced/rest/service/token/" + sessionId).getBody().asString();
		System.out.println(newSession);
		JSONParser parser = new JSONParser();
		JSONObject obj = (JSONObject) parser.parse(newSession);
		String token = (String) obj.get("token");
		System.out.println(token);
		RestAssured.given().parameters("id", sessionId, "signature", token, "allow_access", "1").when()
				.post("http://10.0.1.86/tatoc/advanced/rest/service/register").then().statusCode(200);

		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Proceed')]"))).click();
	}

	@Test(priority = 5)
	public void testFileHandle() throws IOException, InterruptedException {
		//driver.get("http://10.0.1.86/tatoc/advanced/file/handle");
		File exixtingFile = new File("C:/Users/nachiketatripathi/Downloads/file_handle_test.dat");
		if (exixtingFile.exists()) {
			exixtingFile.delete();
		}
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Download File')]")))
				.click();
		Thread.sleep(3000);
		Properties properties = new Properties();
		InputStream input = null;
		Thread.sleep(3000);
		input = new FileInputStream("C:/Users/nachiketatripathi/Downloads/file_handle_test.dat");
		properties.load(input);
		String signature = properties.getProperty("Signature");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='signature']"))).sendKeys(signature);
		Thread.sleep(1000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@value='Proceed']"))).click();
	}

	@AfterClass
	public void closeWindow() throws InterruptedException {
		Thread.sleep(3000);
		driver.close();
	}
}
