package com.falconqa.pages;

import com.falconqa.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * ProductDetailsPage - Page Object for Product Details page
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class ProductDetailsPage extends BasePage {
    
    // Locators
    private final By productName = By.className("inventory_details_name");
    private final By productDescription = By.className("inventory_details_desc");
    private final By productPrice = By.className("inventory_details_price");
    private final By productImage = By.className("inventory_details_img");
    private final By addToCartButton = By.cssSelector("button[id^='add-to-cart']");
    private final By removeButton = By.cssSelector("button[id^='remove']");
    private final By backToProductsButton = By.id("back-to-products");
    
    /**
     * Constructor
     * 
     * @param driver WebDriver instance
     */
    public ProductDetailsPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Get product name
     * 
     * @return Product name
     */
    public String getProductName() {
        return getText(productName);
    }
    
    /**
     * Get product description
     * 
     * @return Product description
     */
    public String getProductDescription() {
        return getText(productDescription);
    }
    
    /**
     * Get product price
     * 
     * @return Product price
     */
    public String getProductPrice() {
        return getText(productPrice);
    }
    
    /**
     * Add product to cart
     */
    public void addToCart() {
        click(addToCartButton);
        logger.info("Added product to cart from details page");
    }
    
    /**
     * Remove product from cart
     */
    public void removeFromCart() {
        click(removeButton);
        logger.info("Removed product from cart");
    }
    
    /**
     * Click back to products
     * 
     * @return ProductsPage instance
     */
    public ProductsPage clickBackToProducts() {
        click(backToProductsButton);
        logger.info("Clicked back to products");
        return new ProductsPage(driver);
    }
    
    /**
     * Verify product details page is loaded
     * 
     * @return true if loaded, false otherwise
     */
    public boolean isProductDetailsPageLoaded() {
        boolean loaded = isDisplayed(productName) && 
                        isDisplayed(productPrice);
        logger.info("Product details page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Check if product image is displayed
     * 
     * @return true if displayed, false otherwise
     */
    public boolean isProductImageDisplayed() {
        return isDisplayed(productImage);
    }
}
