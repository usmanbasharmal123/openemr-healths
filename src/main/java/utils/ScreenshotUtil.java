package utils;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import io.qameta.allure.Allure;

public class ScreenshotUtil {

	private static final Logger logger = LogManager.getLogger(ScreenshotUtil.class);
	private static final String SCREENSHOT_DIR = "screenshots/";

	static {
		// Create screenshots directory if it doesn't exist
		File directory = new File(SCREENSHOT_DIR);
		if (!directory.exists()) {
			directory.mkdirs();
			logger.info("Created screenshots directory: " + SCREENSHOT_DIR);
		}
	}

	/**
	 * Capture screenshot and attach to Allure report
	 */
	public static void captureScreenshot(WebDriver driver, String screenshotName) {
		try {
			// Take screenshot as bytes
			byte[] screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);

			// Attach to Allure report
			Allure.addAttachment(screenshotName, "image/png", new ByteArrayInputStream(screenshot), "png");

			logger.info("Screenshot captured and attached to Allure: " + screenshotName);

		} catch (Exception e) {
			logger.error("Failed to capture screenshot: " + e.getMessage(), e);
		}
	}

	/**
	 * Capture screenshot and save to file
	 */
	public static String captureScreenshotAsFile(WebDriver driver, String testName) {
		String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
		String fileName = testName + "_" + timestamp + ".png";
		String filePath = SCREENSHOT_DIR + fileName;

		try {
			File screenshot = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
			File destination = new File(filePath);
			FileUtils.copyFile(screenshot, destination);

			logger.info("Screenshot saved to: " + filePath);
			return filePath;

		} catch (IOException e) {
			logger.error("Failed to save screenshot: " + e.getMessage(), e);
			return null;
		}
	}

	/**
	 * Capture screenshot on test failure
	 */
	public static void captureFailureScreenshot(WebDriver driver, String testName) {
		String screenshotName = "FAILURE_" + testName;
		captureScreenshot(driver, screenshotName);
		captureScreenshotAsFile(driver, screenshotName);
		logger.error("Test failed! Screenshot captured: " + screenshotName);
	}
}