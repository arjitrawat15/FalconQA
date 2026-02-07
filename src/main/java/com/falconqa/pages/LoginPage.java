package com.falconqa.pages;

import com.falconqa.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * LoginPage - Page Object for Login functionality
 * URL: https://www.saucedemo.com/
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class LoginPage extends BasePage {
    
    // Locators
    private final By usernameField = By.id("user-name");
    private final By passwordField = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");
    private final By errorButton = By.cssSelector(".error-button");
    
    // Valid credentials from SauceDemo
    public static final String STANDARD_USER = "standard_user";
    public static final String LOCKED_OUT_USER = "locked_out_user";
    public static final String PROBLEM_USER = "problem_user";
    public static final String PERFORMANCE_GLITCH_USER = "performance_glitch_user";
    public static final String VALID_PASSWORD = "secret_sauce";
    
    /**
     * Constructor
     * 
     * @param driver WebDriver instance
     */
    public LoginPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Enter username
     * 
     * @param username Username
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterUsername(String username) {
        type(usernameField, username);
        logger.info("Entered username: {}", username);
        return this;
    }
    
    /**
     * Enter password
     * 
     * @param password Password
     * @return LoginPage instance for method chaining
     */
    public LoginPage enterPassword(String password) {
        type(passwordField, password);
        logger.info("Entered password");
        return this;
    }
    
    /**
     * Click login button
     * 
     * @return ProductsPage instance
     */
    public ProductsPage clickLoginButton() {
        click(loginButton);
        logger.info("Clicked login button");
        return new ProductsPage(driver);
    }
    
    /**
     * Perform login with credentials
     * 
     * @param username Username
     * @param password Password
     * @return ProductsPage instance
     */
    public ProductsPage login(String username, String password) {
        logger.info("Attempting login with username: {}", username);
        enterUsername(username);
        enterPassword(password);
        return clickLoginButton();
    }
    
    /**
     * Perform login with standard user
     * 
     * @return ProductsPage instance
     */
    public ProductsPage loginAsStandardUser() {
        return login(STANDARD_USER, VALID_PASSWORD);
    }
    
    /**
     * Get error message text
     * 
     * @return Error message
     */
    public String getErrorMessage() {
        String error = getText(errorMessage);
        logger.info("Error message displayed: {}", error);
        return error;
    }
    
    /**
     * Check if error message is displayed
     * 
     * @return true if error displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }
    
    /**
     * Click error close button
     */
    public void closeErrorMessage() {
        if (isDisplayed(errorButton)) {
            click(errorButton);
            logger.info("Closed error message");
        }
    }
    
    /**
     * Verify login page is loaded
     * 
     * @return true if login page loaded, false otherwise
     */
    public boolean isLoginPageLoaded() {
        boolean loaded = isDisplayed(loginButton) && 
                        isDisplayed(usernameField) && 
                        isDisplayed(passwordField);
        logger.info("Login page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Get page title
     * 
     * @return Page title
     */
    public String getLoginPageTitle() {
        return getPageTitle();
    }
}
