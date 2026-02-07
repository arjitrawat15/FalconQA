package com.falconqa.tests;

import com.falconqa.core.BaseTest;
import com.falconqa.pages.LoginPage;
import com.falconqa.pages.ProductsPage;
import com.falconqa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * LoginTests - Test class for Login functionality
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class LoginTests extends BaseTest {
    
    @Test(priority = 1, description = "Verify successful login with valid credentials")
    public void testValidLogin() {
        ExtentReportManager.logInfo("Test: Valid Login");
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Verify login page is loaded
        Assert.assertTrue(loginPage.isLoginPageLoaded(), 
                "Login page should be loaded");
        ExtentReportManager.logPass("Login page loaded successfully");
        
        // Login with standard user
        ProductsPage productsPage = loginPage.login(
                LoginPage.STANDARD_USER, 
                LoginPage.VALID_PASSWORD);
        
        // Verify navigation to products page
        Assert.assertTrue(productsPage.isProductsPageLoaded(), 
                "Should navigate to products page after successful login");
        ExtentReportManager.logPass("Login successful - Products page loaded");
        
        // Verify page title
        Assert.assertEquals(productsPage.getPageTitleText(), "Products", 
                "Products page title should be 'Products'");
        ExtentReportManager.logPass("Products page title verified");
    }
    
    @Test(priority = 2, description = "Verify login fails with invalid username")
    public void testInvalidUsername() {
        ExtentReportManager.logInfo("Test: Invalid Username Login");
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Attempt login with invalid username
        loginPage.enterUsername("invalid_user");
        loginPage.enterPassword(LoginPage.VALID_PASSWORD);
        loginPage.clickLoginButton();
        
        // Verify error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for invalid credentials");
        ExtentReportManager.logPass("Error message displayed for invalid username");
        
        // Verify error message text
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Username and password do not match"), 
                "Error message should indicate credentials mismatch");
        ExtentReportManager.logPass("Error message text verified: " + errorMessage);
    }
    
    @Test(priority = 3, description = "Verify login fails with invalid password")
    public void testInvalidPassword() {
        ExtentReportManager.logInfo("Test: Invalid Password Login");
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Attempt login with invalid password
        loginPage.enterUsername(LoginPage.STANDARD_USER);
        loginPage.enterPassword("wrong_password");
        loginPage.clickLoginButton();
        
        // Verify error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for invalid password");
        ExtentReportManager.logPass("Error message displayed for invalid password");
    }
    
    @Test(priority = 4, description = "Verify login fails with empty username")
    public void testEmptyUsername() {
        ExtentReportManager.logInfo("Test: Empty Username Login");
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Attempt login with empty username
        loginPage.enterPassword(LoginPage.VALID_PASSWORD);
        loginPage.clickLoginButton();
        
        // Verify error message
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for empty username");
        ExtentReportManager.logPass("Error message displayed for empty username");
        
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Username is required"), 
                "Error message should indicate username is required");
        ExtentReportManager.logPass("Error message verified: " + errorMessage);
    }
    
    @Test(priority = 5, description = "Verify login fails with empty password")
    public void testEmptyPassword() {
        ExtentReportManager.logInfo("Test: Empty Password Login");
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Attempt login with empty password
        loginPage.enterUsername(LoginPage.STANDARD_USER);
        loginPage.clickLoginButton();
        
        // Verify error message
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for empty password");
        ExtentReportManager.logPass("Error message displayed for empty password");
        
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Password is required"), 
                "Error message should indicate password is required");
        ExtentReportManager.logPass("Error message verified: " + errorMessage);
    }
    
    @Test(priority = 6, description = "Verify login with locked out user")
    public void testLockedOutUser() {
        ExtentReportManager.logInfo("Test: Locked Out User Login");
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Attempt login with locked out user
        loginPage.login(LoginPage.LOCKED_OUT_USER, LoginPage.VALID_PASSWORD);
        
        // Verify error message for locked out user
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                "Error message should be displayed for locked out user");
        ExtentReportManager.logPass("Error message displayed for locked out user");
        
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("locked out"), 
                "Error message should indicate user is locked out");
        ExtentReportManager.logPass("Locked out error message verified: " + errorMessage);
    }
    
    @Test(priority = 7, description = "Verify logout functionality")
    public void testLogout() {
        ExtentReportManager.logInfo("Test: Logout Functionality");
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Login
        ProductsPage productsPage = loginPage.loginAsStandardUser();
        Assert.assertTrue(productsPage.isProductsPageLoaded(), 
                "Should be on products page after login");
        ExtentReportManager.logPass("Login successful");
        
        // Logout
        LoginPage logoutPage = productsPage.logout();
        
        // Verify redirected to login page
        Assert.assertTrue(logoutPage.isLoginPageLoaded(), 
                "Should be redirected to login page after logout");
        ExtentReportManager.logPass("Logout successful - Redirected to login page");
    }
}
