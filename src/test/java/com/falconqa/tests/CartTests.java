package com.falconqa.tests;

import com.falconqa.core.BaseTest;
import com.falconqa.pages.CartPage;
import com.falconqa.pages.LoginPage;
import com.falconqa.pages.ProductsPage;
import com.falconqa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.Test;

import java.util.List;

/**
 * CartTests - Test class for Shopping Cart functionality
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class CartTests extends BaseTest {
    
    private ProductsPage productsPage;
    private CartPage cartPage;
    
    @BeforeMethod
    public void loginAndNavigateToCart() {
        LoginPage loginPage = new LoginPage(driver);
        productsPage = loginPage.loginAsStandardUser();
        ExtentReportManager.logInfo("Logged in as standard user");
    }
    
    @Test(priority = 1, description = "Verify cart page navigation")
    public void testCartPageNavigation() {
        ExtentReportManager.logInfo("Test: Cart Page Navigation");
        
        // Click shopping cart
        cartPage = productsPage.clickShoppingCart();
        ExtentReportManager.logPass("Clicked shopping cart icon");
        
        // Verify cart page is loaded
        Assert.assertTrue(cartPage.isCartPageLoaded(), 
                "Cart page should be loaded");
        ExtentReportManager.logPass("Cart page loaded successfully");
    }
    
    @Test(priority = 2, description = "Verify empty cart displays correctly")
    public void testEmptyCart() {
        ExtentReportManager.logInfo("Test: Empty Cart Display");
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Verify cart is empty
        Assert.assertTrue(cartPage.isCartEmpty(), 
                "Cart should be empty initially");
        ExtentReportManager.logPass("Cart is empty as expected");
        
        // Verify cart items count is 0
        Assert.assertEquals(cartPage.getCartItemsCount(), 0, 
                "Cart items count should be 0");
        ExtentReportManager.logPass("Cart items count verified: 0");
    }
    
    @Test(priority = 3, description = "Verify product appears in cart after adding")
    public void testProductInCart() {
        ExtentReportManager.logInfo("Test: Product in Cart");
        
        String productName = "Sauce Labs Backpack";
        
        // Add product to cart
        productsPage.addProductToCart(productName);
        ExtentReportManager.logPass("Added product: " + productName);
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Verify product is in cart
        Assert.assertTrue(cartPage.isProductInCart(productName), 
                "Product should be present in cart");
        ExtentReportManager.logPass("Product found in cart: " + productName);
        
        // Verify cart items count
        Assert.assertEquals(cartPage.getCartItemsCount(), 1, 
                "Cart should have 1 item");
        ExtentReportManager.logPass("Cart items count verified: 1");
    }
    
    @Test(priority = 4, description = "Verify multiple products in cart")
    public void testMultipleProductsInCart() {
        ExtentReportManager.logInfo("Test: Multiple Products in Cart");
        
        // Add multiple products
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        ExtentReportManager.logPass("Added 3 products to cart");
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Verify cart has 3 items
        Assert.assertEquals(cartPage.getCartItemsCount(), 3, 
                "Cart should have 3 items");
        ExtentReportManager.logPass("Cart has 3 items");
        
        // Verify all products are in cart
        List<String> cartItems = cartPage.getCartItemNames();
        Assert.assertTrue(cartItems.contains("Sauce Labs Backpack"), 
                "Cart should contain Sauce Labs Backpack");
        Assert.assertTrue(cartItems.contains("Sauce Labs Bike Light"), 
                "Cart should contain Sauce Labs Bike Light");
        Assert.assertTrue(cartItems.contains("Sauce Labs Bolt T-Shirt"), 
                "Cart should contain Sauce Labs Bolt T-Shirt");
        ExtentReportManager.logPass("All products verified in cart");
    }
    
    @Test(priority = 5, description = "Verify remove product from cart")
    public void testRemoveProductFromCart() {
        ExtentReportManager.logInfo("Test: Remove Product from Cart");
        
        String productName = "Sauce Labs Backpack";
        
        // Add product and navigate to cart
        productsPage.addProductToCart(productName);
        cartPage = productsPage.clickShoppingCart();
        
        // Verify product is in cart
        Assert.assertTrue(cartPage.isProductInCart(productName), 
                "Product should be in cart");
        ExtentReportManager.logPass("Product in cart before removal");
        
        // Remove product
        cartPage.removeProductFromCart(productName);
        ExtentReportManager.logPass("Removed product from cart");
        
        // Verify cart is empty
        Assert.assertTrue(cartPage.isCartEmpty(), 
                "Cart should be empty after removing product");
        ExtentReportManager.logPass("Cart is empty after removal");
    }
    
    @Test(priority = 6, description = "Verify product price in cart")
    public void testProductPriceInCart() {
        ExtentReportManager.logInfo("Test: Product Price in Cart");
        
        String productName = "Sauce Labs Backpack";
        
        // Get product price from products page
        String priceOnProductsPage = productsPage.getProductPrice(productName);
        ExtentReportManager.logInfo("Price on products page: " + priceOnProductsPage);
        
        // Add product to cart
        productsPage.addProductToCart(productName);
        cartPage = productsPage.clickShoppingCart();
        
        // Get product price in cart
        String priceInCart = cartPage.getProductPriceInCart(productName);
        ExtentReportManager.logInfo("Price in cart: " + priceInCart);
        
        // Verify prices match
        Assert.assertEquals(priceInCart, priceOnProductsPage, 
                "Product price should match between products page and cart");
        ExtentReportManager.logPass("Product price verified in cart");
    }
    
    @Test(priority = 7, description = "Verify continue shopping functionality")
    public void testContinueShopping() {
        ExtentReportManager.logInfo("Test: Continue Shopping");
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Click continue shopping
        ProductsPage backToProducts = cartPage.clickContinueShopping();
        ExtentReportManager.logPass("Clicked continue shopping");
        
        // Verify navigated back to products page
        Assert.assertTrue(backToProducts.isProductsPageLoaded(), 
                "Should navigate back to products page");
        ExtentReportManager.logPass("Navigated back to products page");
    }
    
    @Test(priority = 8, description = "Verify remove all items from cart")
    public void testRemoveAllItemsFromCart() {
        ExtentReportManager.logInfo("Test: Remove All Items from Cart");
        
        // Add multiple products
        productsPage.addProductToCart("Sauce Labs Backpack");
        productsPage.addProductToCart("Sauce Labs Bike Light");
        productsPage.addProductToCart("Sauce Labs Bolt T-Shirt");
        ExtentReportManager.logPass("Added 3 products");
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Verify cart has items
        Assert.assertFalse(cartPage.isCartEmpty(), "Cart should have items");
        ExtentReportManager.logPass("Cart has items before removal");
        
        // Remove all items
        cartPage.removeAllItems();
        ExtentReportManager.logPass("Removed all items from cart");
        
        // Verify cart is empty
        Assert.assertTrue(cartPage.isCartEmpty(), 
                "Cart should be empty after removing all items");
        ExtentReportManager.logPass("Cart is empty after removing all items");
    }
    
    @Test(priority = 9, description = "Verify cart persistence across navigation")
    public void testCartPersistence() {
        ExtentReportManager.logInfo("Test: Cart Persistence");
        
        String productName = "Sauce Labs Backpack";
        
        // Add product to cart
        productsPage.addProductToCart(productName);
        String cartCountBefore = productsPage.getCartBadgeCount();
        ExtentReportManager.logPass("Cart count before navigation: " + cartCountBefore);
        
        // Navigate to cart
        cartPage = productsPage.clickShoppingCart();
        
        // Navigate back to products
        ProductsPage backToProducts = cartPage.clickContinueShopping();
        
        // Verify cart badge still shows same count
        String cartCountAfter = backToProducts.getCartBadgeCount();
        Assert.assertEquals(cartCountAfter, cartCountBefore, 
                "Cart count should persist across navigation");
        ExtentReportManager.logPass("Cart count persisted: " + cartCountAfter);
        
        // Navigate to cart again
        cartPage = backToProducts.clickShoppingCart();
        
        // Verify product still in cart
        Assert.assertTrue(cartPage.isProductInCart(productName), 
                "Product should still be in cart");
        ExtentReportManager.logPass("Product persisted in cart");
    }
}
