package com.falconqa.pages;

import com.falconqa.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * CartPage - Page Object for Shopping Cart page
 * URL: https://www.saucedemo.com/cart.html
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class CartPage extends BasePage {
    
    // Locators
    private final By pageTitle = By.className("title");
    private final By cartItems = By.className("cart_item");
    private final By cartItemName = By.className("inventory_item_name");
    private final By cartItemPrice = By.className("inventory_item_price");
    private final By continueShoppingButton = By.id("continue-shopping");
    private final By checkoutButton = By.id("checkout");
    private final By removeButtonTemplate = By.cssSelector("button[id^='remove']");
    
    /**
     * Constructor
     * 
     * @param driver WebDriver instance
     */
    public CartPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Verify cart page is loaded
     * 
     * @return true if loaded, false otherwise
     */
    public boolean isCartPageLoaded() {
        boolean loaded = isDisplayed(pageTitle) && 
                        getText(pageTitle).equals("Your Cart");
        logger.info("Cart page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Get cart items count
     * 
     * @return Number of items in cart
     */
    public int getCartItemsCount() {
        List<WebElement> items = driver.findElements(cartItems);
        int count = items.size();
        logger.info("Cart items count: {}", count);
        return count;
    }
    
    /**
     * Get all cart item names
     * 
     * @return List of item names
     */
    public List<String> getCartItemNames() {
        List<WebElement> items = driver.findElements(cartItemName);
        List<String> names = items.stream()
                .map(WebElement::getText)
                .toList();
        logger.info("Cart item names: {}", names);
        return names;
    }
    
    /**
     * Verify if product is in cart
     * 
     * @param productName Product name
     * @return true if product in cart, false otherwise
     */
    public boolean isProductInCart(String productName) {
        List<String> itemNames = getCartItemNames();
        boolean inCart = itemNames.contains(productName);
        logger.info("Product '{}' in cart: {}", productName, inCart);
        return inCart;
    }
    
    /**
     * Get product price in cart
     * 
     * @param productName Product name
     * @return Product price
     */
    public String getProductPriceInCart(String productName) {
        By priceLocator = By.xpath(String.format(
                "//div[text()='%s']/ancestor::div[@class='cart_item']//div[@class='inventory_item_price']", 
                productName));
        String price = getText(priceLocator);
        logger.info("Product '{}' price in cart: {}", productName, price);
        return price;
    }
    
    /**
     * Remove product from cart by name
     * 
     * @param productName Product name
     */
    public void removeProductFromCart(String productName) {
        By removeButton = By.xpath(String.format(
                "//div[text()='%s']/ancestor::div[@class='cart_item']//button", 
                productName));
        click(removeButton);
        logger.info("Removed product '{}' from cart", productName);
    }
    
    /**
     * Click continue shopping button
     * 
     * @return ProductsPage instance
     */
    public ProductsPage clickContinueShopping() {
        click(continueShoppingButton);
        logger.info("Clicked continue shopping");
        return new ProductsPage(driver);
    }
    
    /**
     * Click checkout button
     * 
     * @return CheckoutPage instance
     */
    public CheckoutPage clickCheckout() {
        click(checkoutButton);
        logger.info("Clicked checkout");
        return new CheckoutPage(driver);
    }
    
    /**
     * Remove all items from cart
     */
    public void removeAllItems() {
        int itemCount = getCartItemsCount();
        for (int i = 0; i < itemCount; i++) {
            List<WebElement> removeButtons = driver.findElements(removeButtonTemplate);
            if (!removeButtons.isEmpty()) {
                removeButtons.get(0).click();
                logger.info("Removed item {} from cart", i + 1);
            }
        }
        logger.info("Removed all items from cart");
    }
    
    /**
     * Check if cart is empty
     * 
     * @return true if cart empty, false otherwise
     */
    public boolean isCartEmpty() {
        boolean empty = getCartItemsCount() == 0;
        logger.info("Cart is empty: {}", empty);
        return empty;
    }
}
