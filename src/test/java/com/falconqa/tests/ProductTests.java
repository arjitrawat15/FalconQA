package com.falconqa.tests;

import com.falconqa.core.BaseTest;
import com.falconqa.pages.LoginPage;
import com.falconqa.pages.ProductDetailsPage;
import com.falconqa.pages.ProductsPage;
import com.falconqa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * ProductTests - Test class for Product functionality
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class ProductTests extends BaseTest {
    
    private ProductsPage productsPage;
    
    @BeforeMethod
    public void loginBeforeTest() {
        LoginPage loginPage = new LoginPage(driver);
        productsPage = loginPage.loginAsStandardUser();
        ExtentReportManager.logInfo("Logged in as standard user");
    }
    
    @Test(priority = 1, description = "Verify products are displayed on products page")
    public void testProductsDisplayed() {
        ExtentReportManager.logInfo("Test: Verify Products Displayed");
        
        // Verify products page is loaded
        Assert.assertTrue(productsPage.isProductsPageLoaded(), 
                "Products page should be loaded");
        ExtentReportManager.logPass("Products page loaded");
        
        // Verify products are displayed
        int productCount = productsPage.getProductCount();
        Assert.assertTrue(productCount > 0, 
                "At least one product should be displayed");
        ExtentReportManager.logPass("Products displayed: " + productCount);
        
        // Verify expected product count (SauceDemo has 6 products)
        Assert.assertEquals(productCount, 6, 
                "Should display 6 products");
        ExtentReportManager.logPass("Product count verified: 6");
    }
    
    @Test(priority = 2, description = "Verify add product to cart functionality")
    public void testAddProductToCart() {
        ExtentReportManager.logInfo("Test: Add Product to Cart");
        
        String productName = "Sauce Labs Backpack";
        
        // Add product to cart
        productsPage.addProductToCart(productName);
        ExtentReportManager.logPass("Product added to cart: " + productName);
        
        // Verify cart badge shows 1 item
        String cartCount = productsPage.getCartBadgeCount();
        Assert.assertEquals(cartCount, "1", 
                "Cart badge should show 1 item");
        ExtentReportManager.logPass("Cart badge updated: " + cartCount);
    }
    
    @Test(priority = 3, description = "Verify add multiple products to cart")
    public void testAddMultipleProductsToCart() {
        ExtentReportManager.logInfo("Test: Add Multiple Products to Cart");
        
        // Add first product
        productsPage.addProductToCart("Sauce Labs Backpack");
        ExtentReportManager.logInfo("Added product 1");
        
        // Add second product
        productsPage.addProductToCart("Sauce Labs Bike Light");
        ExtentReportManager.logInfo("Added product 2");
        
        // Add third product
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        ExtentReportManager.logInfo("Added product 3");
        
        // Verify cart badge shows 3 items
        String cartCount = productsPage.getCartBadgeCount();
        Assert.assertEquals(cartCount, "3", 
                "Cart badge should show 3 items");
        ExtentReportManager.logPass("Cart badge shows correct count: " + cartCount);
    }
    
    @Test(priority = 4, description = "Verify remove product from cart")
    public void testRemoveProductFromCart() {
        ExtentReportManager.logInfo("Test: Remove Product from Cart");
        
        String productName = "Sauce Labs Backpack";
        
        // Add product to cart
        productsPage.addProductToCart(productName);
        String cartCountAfterAdd = productsPage.getCartBadgeCount();
        Assert.assertEquals(cartCountAfterAdd, "1", "Cart should have 1 item");
        ExtentReportManager.logPass("Product added to cart");
        
        // Remove product from cart
        productsPage.removeProductFromCart(productName);
        ExtentReportManager.logPass("Product removed from cart");
        
        // Verify cart badge is removed (no items)
        String cartCountAfterRemove = productsPage.getCartBadgeCount();
        Assert.assertEquals(cartCountAfterRemove, "0", 
                "Cart should be empty after removing item");
        ExtentReportManager.logPass("Cart is empty after removal");
    }
    
    @Test(priority = 5, description = "Verify product details page navigation")
    public void testProductDetailsNavigation() {
        ExtentReportManager.logInfo("Test: Product Details Navigation");
        
        String productName = "Sauce Labs Backpack";
        
        // Click on product name
        ProductDetailsPage detailsPage = productsPage.clickProductName(productName);
        ExtentReportManager.logPass("Clicked on product: " + productName);
        
        // Verify product details page is loaded
        Assert.assertTrue(detailsPage.isProductDetailsPageLoaded(), 
                "Product details page should be loaded");
        ExtentReportManager.logPass("Product details page loaded");
        
        // Verify product name matches
        String detailsProductName = detailsPage.getProductName();
        Assert.assertEquals(detailsProductName, productName, 
                "Product name should match on details page");
        ExtentReportManager.logPass("Product name verified: " + detailsProductName);
    }
    
    @Test(priority = 6, description = "Verify add to cart from product details page")
    public void testAddToCartFromDetailsPage() {
        ExtentReportManager.logInfo("Test: Add to Cart from Details Page");
        
        String productName = "Sauce Labs Backpack";
        
        // Navigate to product details
        ProductDetailsPage detailsPage = productsPage.clickProductName(productName);
        ExtentReportManager.logPass("Navigated to product details");
        
        // Add to cart from details page
        detailsPage.addToCart();
        ExtentReportManager.logPass("Added product to cart from details page");
        
        // Navigate back to products
        ProductsPage backToProducts = detailsPage.clickBackToProducts();
        
        // Verify cart badge shows 1 item
        String cartCount = backToProducts.getCartBadgeCount();
        Assert.assertEquals(cartCount, "1", 
                "Cart badge should show 1 item");
        ExtentReportManager.logPass("Cart badge verified: " + cartCount);
    }
    
    @Test(priority = 7, description = "Verify product sorting by name A to Z")
    public void testSortProductsByNameAtoZ() {
        ExtentReportManager.logInfo("Test: Sort Products by Name (A to Z)");
        
        // Sort by name A to Z
        productsPage.sortProductsBy("Name (A to Z)");
        ExtentReportManager.logPass("Sorted products by Name (A to Z)");
        
        // Get product names
        List<String> productNames = productsPage.getAllProductNames();
        
        // Verify first product is "Sauce Labs Backpack" (alphabetically first)
        Assert.assertEquals(productNames.get(0), "Sauce Labs Backpack", 
                "First product should be 'Sauce Labs Backpack'");
        ExtentReportManager.logPass("Products sorted correctly A to Z");
    }
    
    @Test(priority = 8, description = "Verify product sorting by name Z to A")
    public void testSortProductsByNameZtoA() {
        ExtentReportManager.logInfo("Test: Sort Products by Name (Z to A)");
        
        // Sort by name Z to A
        productsPage.sortProductsBy("Name (Z to A)");
        ExtentReportManager.logPass("Sorted products by Name (Z to A)");
        
        // Get product names
        List<String> productNames = productsPage.getAllProductNames();
        
        // Verify first product starts with higher alphabetical order
        Assert.assertTrue(productNames.get(0).startsWith("Test.allTheThings()") || 
                         productNames.get(0).contains("T-Shirt (Red)"), 
                "First product should be alphabetically last");
        ExtentReportManager.logPass("Products sorted correctly Z to A");
    }
    
    @Test(priority = 9, description = "Verify product sorting by price low to high")
    public void testSortProductsByPriceLowToHigh() {
        ExtentReportManager.logInfo("Test: Sort Products by Price (Low to High)");
        
        // Sort by price low to high
        productsPage.sortProductsBy("Price (low to high)");
        ExtentReportManager.logPass("Sorted products by Price (low to high)");
        
        // Get first and last product prices
        List<String> productNames = productsPage.getAllProductNames();
        String firstProductPrice = productsPage.getProductPrice(productNames.get(0));
        String lastProductPrice = productsPage.getProductPrice(productNames.get(productNames.size() - 1));
        
        // Extract numeric values
        double firstPrice = Double.parseDouble(firstProductPrice.replace("$", ""));
        double lastPrice = Double.parseDouble(lastProductPrice.replace("$", ""));
        
        // Verify first price is less than or equal to last price
        Assert.assertTrue(firstPrice <= lastPrice, 
                "Products should be sorted by price low to high");
        ExtentReportManager.logPass("Products sorted correctly by price (low to high)");
    }
    
    @Test(priority = 10, description = "Verify all product details are displayed")
    public void testProductDetailsDisplayed() {
        ExtentReportManager.logInfo("Test: Verify Product Details Displayed");
        
        String productName = "Sauce Labs Backpack";
        
        // Navigate to product details
        ProductDetailsPage detailsPage = productsPage.clickProductName(productName);
        
        // Verify product name is displayed
        Assert.assertFalse(detailsPage.getProductName().isEmpty(), 
                "Product name should be displayed");
        ExtentReportManager.logPass("Product name displayed");
        
        // Verify product description is displayed
        Assert.assertFalse(detailsPage.getProductDescription().isEmpty(), 
                "Product description should be displayed");
        ExtentReportManager.logPass("Product description displayed");
        
        // Verify product price is displayed
        Assert.assertFalse(detailsPage.getProductPrice().isEmpty(), 
                "Product price should be displayed");
        ExtentReportManager.logPass("Product price displayed");
        
        // Verify product image is displayed
        Assert.assertTrue(detailsPage.isProductImageDisplayed(), 
                "Product image should be displayed");
        ExtentReportManager.logPass("Product image displayed");
    }
}
