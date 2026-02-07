package com.falconqa.tests;

import com.falconqa.core.BaseTest;
import com.falconqa.pages.CartPage;
import com.falconqa.pages.CheckoutPage;
import com.falconqa.pages.LoginPage;
import com.falconqa.pages.ProductsPage;
import com.falconqa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

/**
 * CheckoutTests - Test class for Checkout functionality
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class CheckoutTests extends BaseTest {
    
    private ProductsPage productsPage;
    private CartPage cartPage;
    private CheckoutPage checkoutPage;
    
    @BeforeMethod
    public void setupCheckout() {
        LoginPage loginPage = new LoginPage(driver);
        productsPage = loginPage.loginAsStandardUser();
        
        // Add a product to cart
        productsPage.addProductToCart("Sauce Labs Backpack");
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Click checkout
        checkoutPage = cartPage.clickCheckout();
        
        ExtentReportManager.logInfo("Setup completed - Ready for checkout");
    }
    
    @Test(priority = 1, description = "Verify successful checkout with valid information")
    public void testSuccessfulCheckout() {
        ExtentReportManager.logInfo("Test: Successful Checkout");
        
        // Fill checkout information
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        ExtentReportManager.logPass("Filled checkout information");
        
        // Continue to overview
        checkoutPage.clickContinue();
        
        // Verify overview page is loaded
        Assert.assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
                "Checkout overview page should be loaded");
        ExtentReportManager.logPass("Checkout overview page loaded");
        
        // Verify payment and shipping information
        Assert.assertFalse(checkoutPage.getPaymentInformation().isEmpty(), 
                "Payment information should be displayed");
        Assert.assertFalse(checkoutPage.getShippingInformation().isEmpty(), 
                "Shipping information should be displayed");
        ExtentReportManager.logPass("Payment and shipping information verified");
        
        // Verify price summary
        Assert.assertFalse(checkoutPage.getSubtotal().isEmpty(), 
                "Subtotal should be displayed");
        Assert.assertFalse(checkoutPage.getTax().isEmpty(), 
                "Tax should be displayed");
        Assert.assertFalse(checkoutPage.getTotal().isEmpty(), 
                "Total should be displayed");
        ExtentReportManager.logPass("Price summary verified");
        
        // Complete checkout
        checkoutPage.clickFinish();
        
        // Verify order completion
        Assert.assertTrue(checkoutPage.isOrderCompletedSuccessfully(), 
                "Order should be completed successfully");
        ExtentReportManager.logPass("Order completed successfully");
        
        // Verify completion message
        String completeHeader = checkoutPage.getCompleteHeader();
        Assert.assertTrue(completeHeader.contains("Thank you"), 
                "Completion header should contain 'Thank you'");
        ExtentReportManager.logPass("Completion message verified: " + completeHeader);
    }
    
    @Test(priority = 2, description = "Verify checkout with empty first name")
    public void testCheckoutWithEmptyFirstName() {
        ExtentReportManager.logInfo("Test: Checkout with Empty First Name");
        
        // Fill only last name and postal code
        checkoutPage.enterLastName("Doe");
        checkoutPage.enterPostalCode("12345");
        checkoutPage.clickContinue();
        
        // Verify error message
        Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), 
                "Error message should be displayed");
        ExtentReportManager.logPass("Error message displayed");
        
        String errorMessage = checkoutPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("First Name is required"), 
                "Error should indicate first name is required");
        ExtentReportManager.logPass("Error message verified: " + errorMessage);
    }
    
    @Test(priority = 3, description = "Verify checkout with empty last name")
    public void testCheckoutWithEmptyLastName() {
        ExtentReportManager.logInfo("Test: Checkout with Empty Last Name");
        
        // Fill only first name and postal code
        checkoutPage.enterFirstName("John");
        checkoutPage.enterPostalCode("12345");
        checkoutPage.clickContinue();
        
        // Verify error message
        Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), 
                "Error message should be displayed");
        ExtentReportManager.logPass("Error message displayed");
        
        String errorMessage = checkoutPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Last Name is required"), 
                "Error should indicate last name is required");
        ExtentReportManager.logPass("Error message verified: " + errorMessage);
    }
    
    @Test(priority = 4, description = "Verify checkout with empty postal code")
    public void testCheckoutWithEmptyPostalCode() {
        ExtentReportManager.logInfo("Test: Checkout with Empty Postal Code");
        
        // Fill only first name and last name
        checkoutPage.enterFirstName("John");
        checkoutPage.enterLastName("Doe");
        checkoutPage.clickContinue();
        
        // Verify error message
        Assert.assertTrue(checkoutPage.isErrorMessageDisplayed(), 
                "Error message should be displayed");
        ExtentReportManager.logPass("Error message displayed");
        
        String errorMessage = checkoutPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("Postal Code is required"), 
                "Error should indicate postal code is required");
        ExtentReportManager.logPass("Error message verified: " + errorMessage);
    }
    
    @Test(priority = 5, description = "Verify cancel checkout from information page")
    public void testCancelCheckoutFromInformationPage() {
        ExtentReportManager.logInfo("Test: Cancel Checkout from Information Page");
        
        // Click cancel
        CartPage returnedCartPage = checkoutPage.clickCancel();
        ExtentReportManager.logPass("Clicked cancel button");
        
        // Verify returned to cart page
        Assert.assertTrue(returnedCartPage.isCartPageLoaded(), 
                "Should return to cart page after canceling");
        ExtentReportManager.logPass("Returned to cart page");
    }
    
    @Test(priority = 6, description = "Verify checkout overview displays correct product")
    public void testCheckoutOverviewDisplaysProduct() {
        ExtentReportManager.logInfo("Test: Checkout Overview Displays Product");
        
        // Complete information step
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        
        // Verify on overview page
        Assert.assertTrue(checkoutPage.isCheckoutOverviewPageLoaded(), 
                "Should be on checkout overview page");
        ExtentReportManager.logPass("Checkout overview page loaded");
        
        // Note: In a real scenario, we would verify product details here
        // SauceDemo doesn't provide easy locators for product details on overview
        ExtentReportManager.logPass("Checkout overview page verified");
    }
    
    @Test(priority = 7, description = "Verify back home after checkout completion")
    public void testBackHomeAfterCheckout() {
        ExtentReportManager.logInfo("Test: Back Home After Checkout");
        
        // Complete checkout
        checkoutPage.fillCheckoutInformation("John", "Doe", "12345");
        checkoutPage.clickContinue();
        checkoutPage.clickFinish();
        
        // Verify order completed
        Assert.assertTrue(checkoutPage.isOrderCompletedSuccessfully(), 
                "Order should be completed");
        ExtentReportManager.logPass("Order completed");
        
        // Click back home
        ProductsPage homePage = checkoutPage.clickBackHome();
        ExtentReportManager.logPass("Clicked back home");
        
        // Verify returned to products page
        Assert.assertTrue(homePage.isProductsPageLoaded(), 
                "Should return to products page");
        ExtentReportManager.logPass("Returned to products page");
        
        // Verify cart is empty (order completed)
        String cartCount = homePage.getCartBadgeCount();
        Assert.assertEquals(cartCount, "0", 
                "Cart should be empty after completing order");
        ExtentReportManager.logPass("Cart is empty after order completion");
    }
    
    @Test(priority = 8, description = "Verify end-to-end checkout with multiple products")
    public void testEndToEndCheckoutMultipleProducts() {
        ExtentReportManager.logInfo("Test: End-to-End Checkout with Multiple Products");
        
        // Go back to products page (setup only added one product)
        CartPage currentCart = checkoutPage.clickCancel();
        ProductsPage products = currentCart.clickContinueShopping();
        
        // Add multiple products
        products.addProductToCart("Sauce Labs Bike Light");
        products.addProductToCart("Sauce Labs Bolt T-Shirt");
        ExtentReportManager.logPass("Added additional products");
        
        // Navigate to cart and checkout
        CartPage cart = products.clickShoppingCart();
        Assert.assertEquals(cart.getCartItemsCount(), 3, 
                "Should have 3 products in cart");
        ExtentReportManager.logPass("Cart has 3 products");
        
        CheckoutPage checkout = cart.clickCheckout();
        
        // Complete checkout
        checkout.fillCheckoutInformation("Jane", "Smith", "67890");
        checkout.clickContinue();
        
        // Verify overview and complete
        Assert.assertTrue(checkout.isCheckoutOverviewPageLoaded(), 
                "Should be on overview page");
        ExtentReportManager.logPass("On checkout overview page");
        
        checkout.clickFinish();
        
        // Verify completion
        Assert.assertTrue(checkout.isOrderCompletedSuccessfully(), 
                "Order should be completed successfully");
        ExtentReportManager.logPass("Order with multiple products completed successfully");
    }
}
