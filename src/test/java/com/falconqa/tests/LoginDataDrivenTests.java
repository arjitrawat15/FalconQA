package com.falconqa.tests;

import com.falconqa.core.BaseTest;
import com.falconqa.pages.LoginPage;
import com.falconqa.pages.ProductsPage;
import com.falconqa.utils.ConfigReader;
import com.falconqa.utils.ExcelUtils;
import com.falconqa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

/**
 * LoginDataDrivenTests - Data-driven login tests using Excel
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class LoginDataDrivenTests extends BaseTest {
    
    private static final String EXCEL_PATH = "src/test/resources/testdata/testdata.xlsx";
    private static final String SHEET_NAME = "LoginData";
    
    /**
     * DataProvider for login test data from Excel
     * 
     * @return 2D array with test data [username, password, expectedResult, description]
     */
    @DataProvider(name = "loginDataFromExcel")
    public Object[][] getLoginDataFromExcel() {
        ExcelUtils excel = new ExcelUtils(EXCEL_PATH, SHEET_NAME);
        
        // Get all test data (skip TestCaseID column, start from column 1)
        int rowCount = excel.getRowCount();
        Object[][] testData = new Object[rowCount][4];
        
        for (int i = 0; i < rowCount; i++) {
            testData[i][0] = excel.getCellData(i + 1, "Username");      // Column 1
            testData[i][1] = excel.getCellData(i + 1, "Password");      // Column 2
            testData[i][2] = excel.getCellData(i + 1, "ExpectedResult"); // Column 3
            testData[i][3] = excel.getCellData(i + 1, "Description");   // Column 4
        }
        
        excel.close();
        logger.info("Loaded {} login test cases from Excel", rowCount);
        return testData;
    }
    
    /**
     * Data-driven login test
     * Tests multiple login scenarios from Excel file
     * 
     * @param username Username to test
     * @param password Password to test
     * @param expectedResult Expected result (PASS/FAIL)
     * @param description Test description
     */
    @Test(dataProvider = "loginDataFromExcel", 
          description = "Data-driven login test from Excel")
    public void testLoginWithExcelData(String username, 
                                       String password, 
                                       String expectedResult, 
                                       String description) {
        
        ExtentReportManager.logInfo("Test Case: " + description);
        ExtentReportManager.logInfo("Username: " + username);
        ExtentReportManager.logInfo("Password: " + (password.isEmpty() ? "[EMPTY]" : "[MASKED]"));
        ExtentReportManager.logInfo("Expected Result: " + expectedResult);
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Perform login
        if (username.isEmpty() || password.isEmpty()) {
            // For empty field scenarios
            if (!username.isEmpty()) loginPage.enterUsername(username);
            if (!password.isEmpty()) loginPage.enterPassword(password);
            loginPage.clickLoginButton();
        } else {
            loginPage.login(username, password);
        }
        
        // Verify based on expected result
        if (expectedResult.equalsIgnoreCase("PASS")) {
            // Expected: Successful login
            ProductsPage productsPage = new ProductsPage(driver);
            
            try {
                Assert.assertTrue(productsPage.isProductsPageLoaded(), 
                    "Login should be successful - Products page should load");
                ExtentReportManager.logPass("✅ Login successful as expected");
                
            } catch (AssertionError e) {
                ExtentReportManager.logFail("❌ Expected successful login but failed: " + e.getMessage());
                throw e;
            }
            
        } else {
            // Expected: Login failure with error message
            try {
                Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
                    "Login should fail - Error message should be displayed");
                
                String errorMessage = loginPage.getErrorMessage();
                ExtentReportManager.logPass("✅ Login failed as expected. Error: " + errorMessage);
                
            } catch (AssertionError e) {
                ExtentReportManager.logFail("❌ Expected login failure but succeeded: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * DataProvider for positive login scenarios only
     * 
     * @return Test data with only valid credentials
     */
    @DataProvider(name = "positiveLoginData")
    public Object[][] getPositiveLoginData() {
        ExcelUtils excel = new ExcelUtils(EXCEL_PATH, SHEET_NAME);
        
        // Get only rows where ExpectedResult = "PASS"
        int totalRows = excel.getRowCount();
        int positiveCount = 0;
        
        // First, count positive test cases
        for (int i = 1; i <= totalRows; i++) {
            String result = excel.getCellData(i, "ExpectedResult");
            if (result.equalsIgnoreCase("PASS")) {
                positiveCount++;
            }
        }
        
        // Now, extract positive test cases
        Object[][] testData = new Object[positiveCount][3];
        int index = 0;
        
        for (int i = 1; i <= totalRows; i++) {
            String result = excel.getCellData(i, "ExpectedResult");
            if (result.equalsIgnoreCase("PASS")) {
                testData[index][0] = excel.getCellData(i, "Username");
                testData[index][1] = excel.getCellData(i, "Password");
                testData[index][2] = excel.getCellData(i, "Description");
                index++;
            }
        }
        
        excel.close();
        logger.info("Loaded {} positive login test cases", positiveCount);
        return testData;
    }
    
    /**
     * Test only valid login scenarios
     * 
     * @param username Valid username
     * @param password Valid password
     * @param description Test description
     */
    @Test(dataProvider = "positiveLoginData",
          description = "Test valid login scenarios only")
    public void testValidLogins(String username, String password, String description) {
        
        ExtentReportManager.logInfo("Valid Login Test: " + description);
        
        LoginPage loginPage = new LoginPage(driver);
        ProductsPage productsPage = loginPage.login(username, password);
        
        // Verify successful login
        Assert.assertTrue(productsPage.isProductsPageLoaded(), 
            "Should successfully login and reach products page");
        
        Assert.assertEquals(productsPage.getPageTitleText(), "Products", 
            "Products page title should be 'Products'");
        
        ExtentReportManager.logPass("✅ Valid login successful for user: " + username);
    }
    
    /**
     * DataProvider for negative login scenarios only
     * 
     * @return Test data with only invalid credentials
     */
    @DataProvider(name = "negativeLoginData")
    public Object[][] getNegativeLoginData() {
        ExcelUtils excel = new ExcelUtils(EXCEL_PATH, SHEET_NAME);
        
        int totalRows = excel.getRowCount();
        int negativeCount = 0;
        
        // Count negative test cases
        for (int i = 1; i <= totalRows; i++) {
            String result = excel.getCellData(i, "ExpectedResult");
            if (result.equalsIgnoreCase("FAIL")) {
                negativeCount++;
            }
        }
        
        // Extract negative test cases
        Object[][] testData = new Object[negativeCount][3];
        int index = 0;
        
        for (int i = 1; i <= totalRows; i++) {
            String result = excel.getCellData(i, "ExpectedResult");
            if (result.equalsIgnoreCase("FAIL")) {
                testData[index][0] = excel.getCellData(i, "Username");
                testData[index][1] = excel.getCellData(i, "Password");
                testData[index][2] = excel.getCellData(i, "Description");
                index++;
            }
        }
        
        excel.close();
        logger.info("Loaded {} negative login test cases", negativeCount);
        return testData;
    }
    
    /**
     * Test only invalid login scenarios
     * 
     * @param username Invalid username
     * @param password Invalid password
     * @param description Test description
     */
    @Test(dataProvider = "negativeLoginData",
          description = "Test invalid login scenarios only")
    public void testInvalidLogins(String username, String password, String description) {
        
        ExtentReportManager.logInfo("Invalid Login Test: " + description);
        
        LoginPage loginPage = new LoginPage(driver);
        
        // Perform login
        if (username.isEmpty() || password.isEmpty()) {
            if (!username.isEmpty()) loginPage.enterUsername(username);
            if (!password.isEmpty()) loginPage.enterPassword(password);
            loginPage.clickLoginButton();
        } else {
            loginPage.login(username, password);
        }
        
        // Verify error message is displayed
        Assert.assertTrue(loginPage.isErrorMessageDisplayed(), 
            "Error message should be displayed for invalid credentials");
        
        String errorMessage = loginPage.getErrorMessage();
        Assert.assertFalse(errorMessage.isEmpty(), 
            "Error message should not be empty");
        
        ExtentReportManager.logPass("✅ Login correctly failed with error: " + errorMessage);
    }
}
