package tests;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Optional;
import org.testng.annotations.Parameters;

import io.github.bonigarcia.wdm.WebDriverManager;
import io.qameta.allure.Allure;
import utils.ConfigReader;
import utils.ScreenshotUtil;

public class BaseTest {

	protected WebDriver driver;
	protected static final Logger logger = LogManager.getLogger(BaseTest.class);

	@BeforeMethod
	@Parameters({ "browser" })
	public void setUp(@Optional("chrome") String browser) {
		logger.info("========== Test Execution Started ==========");
		logger.info("Initializing WebDriver for browser: " + browser);

		try {
			driver = initializeDriver(browser);
			driver.manage().window().maximize();
			driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(10));
			driver.manage().timeouts().pageLoadTimeout(Duration.ofSeconds(30));

			String url = ConfigReader.getProperty("app.url");
			logger.info("Navigating to URL: " + url);
			driver.get(url);

			logger.info("Browser setup completed successfully");
		} catch (Exception e) {
			logger.error("Error during setup: " + e.getMessage(), e);
			throw e;
		}
	}

	private WebDriver initializeDriver(String browser) {
		WebDriver driver;

		switch (browser.toLowerCase()) {
		case "chrome":
			WebDriverManager.chromedriver().setup();
			ChromeOptions chromeOptions = new ChromeOptions();
			chromeOptions.addArguments("--remote-allow-origins=*");
			driver = new ChromeDriver(chromeOptions);
			logger.info("Chrome browser initialized");
			break;

		case "firefox":
			WebDriverManager.firefoxdriver().setup();
			driver = new FirefoxDriver();
			logger.info("Firefox browser initialized");
			break;

		case "edge":
			WebDriverManager.edgedriver().setup();
			driver = new EdgeDriver();
			logger.info("Edge browser initialized");
			break;

		default:
			logger.warn("Invalid browser specified. Defaulting to Chrome");
			WebDriverManager.chromedriver().setup();
			driver = new ChromeDriver();
			break;
		}

		return driver;
	}

	@AfterMethod
	public void tearDown() {
		logger.info("Closing browser and cleaning up");

		if (driver != null) {
			driver.quit();
			logger.info("Browser closed successfully");
		}

		logger.info("========== Test Execution Completed ==========\n");
	}

	protected void logTestStep(String stepDescription) {
		logger.info("TEST STEP: " + stepDescription);
		Allure.step(stepDescription);
	}

	protected void captureScreenshot(String screenshotName) {
		ScreenshotUtil.captureScreenshot(driver, screenshotName);
	}
}