package com.falconqa.tests;

import com.falconqa.core.BaseTest;
import com.falconqa.pages.CartPage;
import com.falconqa.pages.CheckoutPage;
import com.falconqa.pages.LoginPage;
import com.falconqa.pages.ProductsPage;
import com.falconqa.utils.ExtentReportManager;
import com.falconqa.utils.JsonUtils;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.util.List;
import java.util.Map;

/**
 * CheckoutDataDrivenTests - Data-driven checkout tests using JSON
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class CheckoutDataDrivenTests extends BaseTest {
    
    private static final String JSON_PATH = "src/test/resources/testdata/checkout-data.json";
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    
    /**
     * Setup before each test - Login and add product to cart
     */
    @BeforeMethod
    public void setupCheckout() {
        // Login
        LoginPage loginPage = new LoginPage(driver);
        productsPage = loginPage.loginAsStandardUser();
        
        // Add product to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Start checkout
        checkoutPage = cartPage.clickCheckout();
        
        ExtentReportManager.logInfo("Setup completed - Ready for checkout test");
    }
    
    /**
     * DataProvider for checkout test data from JSON
     * 
     * @return 2D array with test data
     */
    @DataProvider(name = "checkoutDataFromJson")
    public Object[][] getCheckoutDataFromJson() {
        // Read JSON file
        List<Map<String, Object>> jsonData = JsonUtils.readJsonArrayAsList(JSON_PATH);
        
        // Convert to Object array for TestNG
        Object[][] testData = new Object[jsonData.size()][5];
        
        for (int i = 0; i < jsonData.size(); i++) {
            Map<String, Object> row = jsonData.get(i);
            testData[i][0] = row.get("firstName");
            testData[i][1] = row.get("lastName");
            testData[i][2] = row.get("postalCode");
            testData[i][3] = row.get("expectedResult");
            testData[i][4] = row.get("description");
        }
        
        logger.info("Loaded {} checkout test cases from JSON", testData.length);
        return testData;
    }
    
    /**
     * Data-driven checkout test
     * 
     * @param firstName First name
     * @param lastName Last name
     * @param postalCode Postal code
     * @param expectedResult Expected result (PASS/FAIL)
     * @param description Test description
     */
    @Test(dataProvider = "checkoutDataFromJson",
          description = "Data-driven checkout test from JSON")
    public void testCheckoutWithJsonData(String firstName, 
                                         String lastName, 
                                         String postalCode, 
                                         String expectedResult, 
                                         String description) {
        
        ExtentReportManager.logInfo("Test Case: " + description);
        ExtentReportManager.logInfo("First Name: " + (firstName.isEmpty() ? "[EMPTY]" : firstName));
        ExtentReportManager.logInfo("Last Name: " + (lastName.isEmpty() ? "[EMPTY]" : lastName));
        ExtentReportManager.logInfo("Postal Code: " + (postalCode.isEmpty() ? "[EMPTY]" : postalCode));
        ExtentReportManager.logInfo("Expected Result: " + expectedResult);
        
        // Fill checkout form
        if (!firstName.isEmpty()) {
            checkoutPage.enterFirstName(firstName);
        }
        if (!lastName.isEmpty()) {
            checkoutPage.enterLastName(lastName);
        }
        if (!postalCode.isEmpty()) {
            checkoutPage.enterPostalCode(postalCode);
        }
        
        // Click continue
        checkoutPage.clickContinue();
        
        // Verify based on expected result
        if (expectedResult.equalsIgnoreCase("PASS")) {
            // Expected: Successful navigation to overview page
            try {
                Assert.assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
                    "Should successfully navigate to checkout overview page");
                
                // Complete the checkout
                checkoutPage.clickFinish();
                
                Assert.assertTrue(checkoutPage.isOrderCompletedSuccessfully(), 
                    "Order should be completed successfully");
                
                ExtentReportManager.logPass("✅ Checkout successful as expected");
                
            } catch (AssertionError e) {
                ExtentReportManager.logFail("❌ Expected successful checkout but failed: " + e.getMessage());
                throw e;
            }
            
        } else {
            // Expected: Validation error
            try {
                Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), 
                    "Validation error should be displayed");
                
                String errorMessage = checkoutPage.getErrorMessage();
                ExtentReportManager.logPass("✅ Checkout validation failed as expected. Error: " + errorMessage);
                
                // Verify specific error messages
                if (firstName.isEmpty()) {
                    Assert.assertTrue(errorMessage.contains("First Name"), 
                        "Error should mention First Name");
                } else if (lastName.isEmpty()) {
                    Assert.assertTrue(errorMessage.contains("Last Name"), 
                        "Error should mention Last Name");
                } else if (postalCode.isEmpty()) {
                    Assert.assertTrue(errorMessage.contains("Postal Code"), 
                        "Error should mention Postal Code");
                }
                
            } catch (AssertionError e) {
                ExtentReportManager.logFail("❌ Expected validation error but checkout succeeded: " + e.getMessage());
                throw e;
            }
        }
    }
    
    /**
     * DataProvider for valid checkout scenarios only
     */
    @DataProvider(name = "validCheckoutData")
    public Object[][] getValidCheckoutData() {
        List<Map<String, Object>> allData = JsonUtils.readJsonArrayAsList(JSON_PATH);
        
        // Filter only PASS scenarios
        List<Map<String, Object>> validData = allData.stream()
            .filter(row -> "PASS".equalsIgnoreCase((String) row.get("expectedResult")))
            .toList();
        
        Object[][] testData = new Object[validData.size()][3];
        for (int i = 0; i < validData.size(); i++) {
            Map<String, Object> row = validData.get(i);
            testData[i][0] = row.get("firstName");
            testData[i][1] = row.get("lastName");
            testData[i][2] = row.get("postalCode");
        }
        
        logger.info("Loaded {} valid checkout test cases", testData.length);
        return testData;
    }
    
    /**
     * Test only valid checkout scenarios
     */
    @Test(dataProvider = "validCheckoutData",
          description = "Test valid checkout scenarios only")
    public void testValidCheckouts(String firstName, String lastName, String postalCode) {
        
        ExtentReportManager.logInfo("Valid Checkout Test");
        ExtentReportManager.logInfo(String.format("Name: %s %s, Postal Code: %s", 
            firstName, lastName, postalCode));
        
        // Fill and submit checkout form
        checkoutPage.fillCheckoutInformation(firstName, lastName, postalCode);
        checkoutPage.clickContinue();
        
        // Verify overview page
        Assert.assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
            "Should reach checkout overview page");
        ExtentReportManager.logPass("✅ Reached checkout overview page");
        
        // Verify price information is displayed
        Assert.assertFalse(checkoutPage.getSubtotal().isEmpty(), 
            "Subtotal should be displayed");
        Assert.assertFalse(checkoutPage.getTax().isEmpty(), 
            "Tax should be displayed");
        Assert.assertFalse(checkoutPage.getTotal().isEmpty(), 
            "Total should be displayed");
        ExtentReportManager.logPass("✅ Price information displayed");
        
        // Complete checkout
        checkoutPage.clickFinish();
        
        // Verify completion
        Assert.assertTrue(checkoutPage.isOrderCompletedSuccessfully(), 
            "Order should be completed");
        
        String completeHeader = checkoutPage.getCompleteHeader();
        Assert.assertTrue(completeHeader.contains("Thank you"), 
            "Completion message should contain 'Thank you'");
        
        ExtentReportManager.logPass("✅ Checkout completed successfully: " + completeHeader);
    }
    
    /**
     * Alternative DataProvider using filtered JSON data
     * Example: Get only test cases with specific testCaseId pattern
     */
    @DataProvider(name = "filteredCheckoutData")
    public Object[][] getFilteredCheckoutData() {
        // Example: Get only test cases 001-003 (valid scenarios)
        List<Map<String, Object>> allData = JsonUtils.readJsonArrayAsList(JSON_PATH);
        
        List<Map<String, Object>> filteredData = allData.stream()
            .filter(row -> {
                String testCaseId = (String) row.get("testCaseId");
                return testCaseId.matches(".*_00[1-3]");  // Only 001, 002, 003
            })
            .toList();
        
        Object[][] testData = new Object[filteredData.size()][5];
        for (int i = 0; i < filteredData.size(); i++) {
            Map<String, Object> row = filteredData.get(i);
            testData[i][0] = row.get("firstName");
            testData[i][1] = row.get("lastName");
            testData[i][2] = row.get("postalCode");
            testData[i][3] = row.get("testCaseId");
            testData[i][4] = row.get("description");
        }
        
        return testData;
    }
    
    /**
     * Test using filtered data
     */
    @Test(dataProvider = "filteredCheckoutData",
          description = "Filtered checkout tests", enabled = false)
    public void testFilteredCheckouts(String firstName, String lastName, String postalCode, 
                                      String testCaseId, String description) {
        
        ExtentReportManager.logInfo("Test Case ID: " + testCaseId);
        ExtentReportManager.logInfo("Description: " + description);
        
        // Complete checkout flow
        checkoutPage.fillCheckoutInformation(firstName, lastName, postalCode);
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();
        
        Assert.assertTrue(checkoutPage.isOrderCompletedSuccessfully(), 
            "Order should complete successfully");
        
        ExtentReportManager.logPass("✅ Test case " + testCaseId + " passed");
    }
}
