package tests;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import io.qameta.allure.Description;
import io.qameta.allure.Epic;
import io.qameta.allure.Feature;
import io.qameta.allure.Severity;
import io.qameta.allure.SeverityLevel;
import io.qameta.allure.Story;
import pages.LoginPage;
import utils.ConfigReader;

@Epic("OpenEMR Application")
@Feature("Login Functionality")
public class LoginPageTest extends BaseTest {

	private static final Logger logger = LogManager.getLogger(LoginPageTest.class);
	private LoginPage loginPage;

	@BeforeMethod
	public void initializePage() {
		loginPage = new LoginPage(driver);
		logger.info("LoginPage object created for test");
	}

//	@Test(priority = 1, description = "Verify login page is displayed")
//	@Story("Login Page Display")
//	@Severity(SeverityLevel.CRITICAL)
//	@Description("Verify that the login page is displayed correctly when accessing the application")
//	public void testLoginPageDisplay() {
//		logger.info("========== Starting Test: Verify Login Page Display ==========");
//
//		logTestStep("Verify login page is displayed");
//		boolean isDisplayed = loginPage.isLoginPageDisplayed();
//
//		Assert.assertTrue(isDisplayed, "Login page should be displayed");
//		logger.info("Test Passed: Login page is displayed successfully");
//	}

	@Test(priority = 2, description = "Verify successful login with valid credentials")
	@Story("Valid Login")
	@Severity(SeverityLevel.BLOCKER)
	@Description("Verify that user can successfully login with valid username and password")
	public void testValidLogin() {
		logger.info("========== Starting Test: Valid Login ==========");

		String username = ConfigReader.getProperty("valid.username");
		String password = ConfigReader.getProperty("valid.password");

		logTestStep("Enter valid username and password");
		loginPage.login(username, password);

		logTestStep("Wait for page to load after login");
		try {
			Thread.sleep(3000); // Wait for redirect
		} catch (InterruptedException e) {
			logger.error("Thread sleep interrupted", e);
		}

		logTestStep("Verify user is logged in successfully");
		String currentUrl = driver.getCurrentUrl();
		logger.info("Current URL after login: " + currentUrl);

		Assert.assertFalse(currentUrl.contains("login"), "User should be redirected away from login page");
		logger.info("Test Passed: User logged in successfully");
	}

//	@Test(priority = 3, description = "Verify login fails with invalid credentials")
//	@Story("Invalid Login")
//	@Severity(SeverityLevel.CRITICAL)
//	@Description("Verify that login fails when invalid credentials are provided")
//	public void testInvalidLogin() {
//		logger.info("========== Starting Test: Invalid Login ==========");
//
//		String invalidUsername = "invalidUser";
//		String invalidPassword = "invalidPass";
//
//		logTestStep("Enter invalid username and password");
//		loginPage.login(invalidUsername, invalidPassword);
//
//		logTestStep("Wait for error message to appear");
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			logger.error("Thread sleep interrupted", e);
//		}
//
//		logTestStep("Verify error message is displayed or user stays on login page");
//		String currentUrl = driver.getCurrentUrl();
//		logger.info("Current URL after invalid login: " + currentUrl);
//
//		boolean testPassed = currentUrl.contains("login") || loginPage.isErrorMessageDisplayed();
//		Assert.assertTrue(testPassed, "Login should fail with invalid credentials");
//		logger.info("Test Passed: Invalid login handled correctly");
//	}
//
//	@Test(priority = 4, description = "Verify login with empty credentials")
//	@Story("Empty Credentials")
//	@Severity(SeverityLevel.NORMAL)
//	@Description("Verify that appropriate validation occurs when user tries to login without credentials")
//	public void testEmptyCredentials() {
//		logger.info("========== Starting Test: Empty Credentials ==========");
//
//		logTestStep("Click login button without entering credentials");
//		loginPage.clickLoginButton();
//
//		logTestStep("Wait for validation");
//		try {
//			Thread.sleep(2000);
//		} catch (InterruptedException e) {
//			logger.error("Thread sleep interrupted", e);
//		}
//
//		logTestStep("Verify user remains on login page");
//		String currentUrl = driver.getCurrentUrl();
//		logger.info("Current URL after empty login attempt: " + currentUrl);
//
//		Assert.assertTrue(currentUrl.contains("login"), "User should remain on login page with empty credentials");
//		logger.info("Test Passed: Empty credentials validation working");
//	}

//	@Test(priority = 5, description = "Verify page title")
//	@Story("Page Verification")
//	@Severity(SeverityLevel.MINOR)
//	@Description("Verify that the login page has correct title")
//	public void testPageTitle() {
//		logger.info("========== Starting Test: Verify Page Title ==========");
//
//		logTestStep("Get page title");
//		String pageTitle = loginPage.getPageTitle();
//
//		logTestStep("Verify page title contains 'OpenEMR'");
//		Assert.assertTrue(pageTitle.contains("OpenEMR"), "Page title should contain 'OpenEMR'");
//		logger.info("Test Passed: Page title is correct - " + pageTitle);
//	}
}