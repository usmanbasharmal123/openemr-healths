package listeners;

import java.lang.reflect.Field;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ISuite;
import org.testng.ISuiteListener;
import org.testng.ITestListener;
import org.testng.ITestResult;

import io.qameta.allure.Allure;
import tests.BaseTest;
import utils.EmailUtil;
import utils.ScreenshotUtil;

public class TestListener implements ITestListener, ISuiteListener {

	private static final Logger logger = LogManager.getLogger(TestListener.class);
	private int totalTests = 0;
	private int passedTests = 0;
	private int failedTests = 0;
	private int skippedTests = 0;

	// This will hold the Jenkins Allure URL
	private String allureUrl = "";

	@Override
	public void onStart(ISuite suite) {
		logger.info("========================================");
		logger.info("Test Suite Started: " + suite.getName());
		logger.info("========================================");

		// Read Allure URL passed from Jenkins
		allureUrl = System.getProperty("allure.url", "");
		logger.info("Received Allure URL from Jenkins: " + allureUrl);
	}

	@Override
	public void onFinish(ISuite suite) {
		logger.info("========================================");
		logger.info("Test Suite Finished: " + suite.getName());
		logger.info("Total Tests: " + totalTests);
		logger.info("Passed: " + passedTests);
		logger.info("Failed: " + failedTests);
		logger.info("Skipped: " + skippedTests);
		logger.info("========================================");

		// Send email with results
		sendEmailReport();
	}

	@Override
	public void onTestStart(ITestResult result) {
		totalTests++;
		logger.info("========================================");
		logger.info("Test Started: " + result.getMethod().getMethodName());
		logger.info("Test Description: " + result.getMethod().getDescription());
		logger.info("========================================");

		Allure.step("Test Started: " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestSuccess(ITestResult result) {
		passedTests++;
		logger.info("✓ Test Passed: " + result.getMethod().getMethodName());
		Allure.step("Test Passed: " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestFailure(ITestResult result) {
		failedTests++;
		logger.error("✗ Test Failed: " + result.getMethod().getMethodName());
		logger.error("Failure Reason: " + result.getThrowable());

		// Capture screenshot on failure
		WebDriver driver = getDriverFromTest(result);
		if (driver != null) {
			String testName = result.getMethod().getMethodName();
			ScreenshotUtil.captureFailureScreenshot(driver, testName);
		}

		Allure.step("Test Failed: " + result.getMethod().getMethodName());
	}

	@Override
	public void onTestSkipped(ITestResult result) {
		skippedTests++;
		logger.warn("⊘ Test Skipped: " + result.getMethod().getMethodName());
		Allure.step("Test Skipped: " + result.getMethod().getMethodName());
	}

	/**
	 * Get WebDriver instance from test class using reflection
	 */
	private WebDriver getDriverFromTest(ITestResult result) {
		Object testInstance = result.getInstance();

		try {
			if (testInstance instanceof BaseTest) {
				Field driverField = BaseTest.class.getDeclaredField("driver");
				driverField.setAccessible(true);
				return (WebDriver) driverField.get(testInstance);
			}
		} catch (Exception e) {
			logger.error("Failed to get driver from test: " + e.getMessage());
		}

		return null;
	}

	/**
	 * Send email with test results
	 */
	private void sendEmailReport() {
		String subject = "OpenEMR Test Execution Report - "
				+ (failedTests == 0 ? "✓ ALL PASSED" : "✗ FAILURES DETECTED");

		// Pass Allure URL into email body
		String body = EmailUtil.createTestResultEmailBody(totalTests, passedTests, failedTests, skippedTests,
				allureUrl);

		EmailUtil.sendEmailWithReport(subject, body, null);
	}
}
