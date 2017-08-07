package first.com.qait.tatoc;

import java.util.Set;
import org.openqa.selenium.By;
import org.openqa.selenium.Cookie;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.testng.annotations.AfterClass;
import org.testng.annotations.BeforeClass;
import org.testng.annotations.Test;

/**
 * Without using FindElement() or FindElements()
 * 
 * @author nachiketatripathi
 *
 */
public class TatocBasicDemoTest {
	WebDriver driver = new ChromeDriver();

	WebDriverWait wait = new WebDriverWait(driver, 10);

	@BeforeClass
	public void initializeDriver() {
		driver.manage().window().maximize();
		System.setProperty("webdriver.chrome.driver", ".//chromedriver.exe");
		driver.get("http://10.0.1.86/tatoc/basic");
	}

	@Test(priority = 1)
	public void testGridGate() {
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[contains(@onclick,'passthru()')]")))
				.click();
	}

	@Test(priority = 2)
	public void testFrameDungeon() throws InterruptedException {
		driver.switchTo().frame("main");
		String box1 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#answer")))
				.getAttribute("class");
		driver.switchTo().frame("child");
		String box2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#answer")))
				.getAttribute("class");
		driver.switchTo().defaultContent();

		while (!box1.equals(box2)) {
			driver.switchTo().frame("main");
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//center/a[1]"))).click();
			driver.switchTo().frame("child");
			Thread.sleep(1000);
			box2 = wait.until(ExpectedConditions.visibilityOfElementLocated(By.cssSelector("#answer")))
					.getAttribute("class");
			driver.switchTo().defaultContent();
		}
		driver.switchTo().frame("main");
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//center/a[2]"))).click();
	}

	@Test(priority = 3)
	public void testDragAround() throws InterruptedException {
		Thread.sleep(2000);
		WebElement from = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='dragbox']")));
		WebElement to = wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//div[@id='dropbox']")));
		Actions action = new Actions(driver);
		action.dragAndDrop(from, to).build().perform();
		Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Proceed')]"))).click();
	}

	@Test(priority = 4)
	public void testPopupWindows() throws InterruptedException {
		String first = driver.getWindowHandle();
		Thread.sleep(2000);
		wait.until(
				ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Launch Popup Window')]")))
				.click();
		Set<String> handles = driver.getWindowHandles();
		handles.remove(first);
		String second = handles.iterator().next().toString();
		driver.switchTo().window(second);
		if (driver.getWindowHandle().equals(second)) {
			Thread.sleep(2000);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='name']")))
					.sendKeys("Nachiketa Tripathi");
			Thread.sleep(2000);
			wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='submit']"))).click();
		}
		Thread.sleep(2000);
		driver.switchTo().window(first);
		Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Proceed')]"))).click();
		System.out.println(driver.getWindowHandle());
	}

	@Test(priority = 5)
	public void testCookieHandling() throws InterruptedException {
		Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Generate Token')]")))
				.click();
		String initialToken = driver.findElement(By.xpath("//*[@id='token']")).getText();
		String token = initialToken.substring(initialToken.indexOf(' ') + 1);
		Cookie newCookie = new Cookie("Token", token);
		driver.manage().addCookie(newCookie);
		Thread.sleep(2000);
		wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//a[contains(text(),'Proceed')]"))).click();
	}

	@AfterClass
	public void closeWindow() throws InterruptedException {
		Thread.sleep(2000);
		driver.close();
	}

}
