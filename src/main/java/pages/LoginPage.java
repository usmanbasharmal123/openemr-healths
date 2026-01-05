package pages;

import java.time.Duration;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.FindBy;
import org.openqa.selenium.support.PageFactory;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

public class LoginPage {

	private WebDriver driver;
	private WebDriverWait wait;
	private static final Logger logger = LogManager.getLogger(LoginPage.class);

	// Page Elements
	@FindBy(id = "authUser")
	private WebElement usernameField;

	@FindBy(id = "clearPass")
	private WebElement passwordField;

	@FindBy(xpath = "//select[@name='languageChoice']")
	private WebElement languageDropdown;

	@FindBy(xpath = "//button[@type='submit']")
	private WebElement loginButton;

	@FindBy(xpath = "//div[contains(@class,'alert') or contains(@class,'error')]")
	private WebElement errorMessage;

	@FindBy(xpath = "//p[contains(text(),'OpenEMR')]")
	private WebElement openEMRLogo;

	// Constructor
	public LoginPage(WebDriver driver) {
		this.driver = driver;
		this.wait = new WebDriverWait(driver, Duration.ofSeconds(10));
		PageFactory.initElements(driver, this);
		logger.info("LoginPage initialized");
	}

	// Page Actions
	public void enterUsername(String username) {
		try {
			wait.until(ExpectedConditions.visibilityOf(usernameField));
			usernameField.clear();
			usernameField.sendKeys(username);
			logger.info("Entered username: " + username);
		} catch (Exception e) {
			logger.error("Failed to enter username: " + e.getMessage(), e);
			throw e;
		}
	}

	public void enterPassword(String password) {
		try {
			wait.until(ExpectedConditions.visibilityOf(passwordField));
			passwordField.clear();
			passwordField.sendKeys(password);
			logger.info("Entered password");
		} catch (Exception e) {
			logger.error("Failed to enter password: " + e.getMessage(), e);
			throw e;
		}
	}

	public void selectLanguage(String language) {
		try {
			wait.until(ExpectedConditions.visibilityOf(languageDropdown));
			Select select = new Select(languageDropdown);
			select.selectByVisibleText(language);
			logger.info("Selected language: " + language);
		} catch (Exception e) {
			logger.error("Failed to select language: " + e.getMessage(), e);
			throw e;
		}
	}

	public void clickLoginButton() {
		try {
			wait.until(ExpectedConditions.elementToBeClickable(loginButton));
			loginButton.click();
			logger.info("Clicked on Login button");
		} catch (Exception e) {
			logger.error("Failed to click login button: " + e.getMessage(), e);
			throw e;
		}
	}

	public void login(String username, String password) {
		logger.info("Performing login with username: " + username);
		enterUsername(username);
		enterPassword(password);
		clickLoginButton();
		logger.info("Login attempt completed");
	}

	public void loginWithLanguage(String username, String password, String language) {
		logger.info("Performing login with username: " + username + " and language: " + language);
		enterUsername(username);
		enterPassword(password);
		selectLanguage(language);
		clickLoginButton();
		logger.info("Login attempt with language selection completed");
	}

	public boolean isLoginPageDisplayed() {
		try {
			boolean isDisplayed = wait.until(ExpectedConditions.visibilityOf(openEMRLogo)).isDisplayed();
			logger.info("Login page display status: " + isDisplayed);
			return isDisplayed;
		} catch (Exception e) {
			logger.error("Failed to verify login page: " + e.getMessage(), e);
			return false;
		}
	}

	public boolean isErrorMessageDisplayed() {
		try {
			boolean isDisplayed = errorMessage.isDisplayed();
			logger.info("Error message display status: " + isDisplayed);
			return isDisplayed;
		} catch (Exception e) {
			logger.info("No error message displayed");
			return false;
		}
	}

	public String getErrorMessage() {
		try {
			String message = errorMessage.getText();
			logger.info("Error message text: " + message);
			return message;
		} catch (Exception e) {
			logger.error("Failed to get error message: " + e.getMessage(), e);
			return "";
		}
	}

	public String getPageTitle() {
		String title = driver.getTitle();
		logger.info("Page title: " + title);
		return title;
	}
}