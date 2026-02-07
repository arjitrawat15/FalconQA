package com.falconqa.pages;

import com.falconqa.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.util.List;

/**
 * ProductsPage - Page Object for Products listing page
 * URL: https://www.saucedemo.com/inventory.html
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class ProductsPage extends BasePage {
    
    // Locators
    private final By pageTitle = By.className("title");
    private final By productContainer = By.className("inventory_list");
    private final By productItems = By.className("inventory_item");
    private final By productName = By.className("inventory_item_name");
    private final By productPrice = By.className("inventory_item_price");
    private final By shoppingCartBadge = By.className("shopping_cart_badge");
    private final By shoppingCartLink = By.className("shopping_cart_link");
    private final By hamburgerMenu = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By sortDropdown = By.className("product_sort_container");
    
    // Dynamic locators
    private String addToCartButtonTemplate = "//div[text()='%s']/ancestor::div[@class='inventory_item']//button";
    private String removeButtonTemplate = "//div[text()='%s']/ancestor::div[@class='inventory_item']//button[contains(text(),'Remove')]";
    
    /**
     * Constructor
     * 
     * @param driver WebDriver instance
     */
    public ProductsPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Verify products page is loaded
     * 
     * @return true if page loaded, false otherwise
     */
    public boolean isProductsPageLoaded() {
        boolean loaded = isDisplayed(pageTitle) && 
                        getText(pageTitle).equals("Products");
        logger.info("Products page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Get page title text
     * 
     * @return Page title
     */
    public String getPageTitleText() {
        return getText(pageTitle);
    }
    
    /**
     * Get count of products displayed
     * 
     * @return Number of products
     */
    public int getProductCount() {
        List<WebElement> products = driver.findElements(productItems);
        int count = products.size();
        logger.info("Product count: {}", count);
        return count;
    }
    
    /**
     * Add product to cart by name
     * 
     * @param productName Product name
     */
    public void addProductToCart(String productName) {
        By addToCartButton = By.xpath(String.format(addToCartButtonTemplate, productName));
        click(addToCartButton);
        logger.info("Added product to cart: {}", productName);
    }
    
    /**
     * Remove product from cart by name
     * 
     * @param productName Product name
     */
    public void removeProductFromCart(String productName) {
        By removeButton = By.xpath(String.format(removeButtonTemplate, productName));
        click(removeButton);
        logger.info("Removed product from cart: {}", productName);
    }
    
    /**
     * Get shopping cart badge count
     * 
     * @return Cart item count
     */
    public String getCartBadgeCount() {
        if (isDisplayed(shoppingCartBadge)) {
            String count = getText(shoppingCartBadge);
            logger.info("Cart badge count: {}", count);
            return count;
        }
        logger.info("Cart is empty");
        return "0";
    }
    
    /**
     * Click shopping cart icon
     * 
     * @return CartPage instance
     */
    public CartPage clickShoppingCart() {
        click(shoppingCartLink);
        logger.info("Clicked shopping cart");
        return new CartPage(driver);
    }
    
    /**
     * Open hamburger menu
     */
    public void openMenu() {
        click(hamburgerMenu);
        logger.info("Opened hamburger menu");
        pause(500); // Small pause for menu animation
    }
    
    /**
     * Logout from application
     * 
     * @return LoginPage instance
     */
    public LoginPage logout() {
        openMenu();
        click(logoutLink);
        logger.info("Logged out successfully");
        return new LoginPage(driver);
    }
    
    /**
     * Sort products by option
     * 
     * @param sortOption Sort option (Name (A to Z), Name (Z to A), Price (low to high), Price (high to low))
     */
    public void sortProductsBy(String sortOption) {
        selectByVisibleText(sortDropdown, sortOption);
        logger.info("Sorted products by: {}", sortOption);
    }
    
    /**
     * Get all product names
     * 
     * @return List of product names
     */
    public List<String> getAllProductNames() {
        List<WebElement> products = driver.findElements(productName);
        List<String> names = products.stream()
                .map(WebElement::getText)
                .toList();
        logger.info("Retrieved {} product names", names.size());
        return names;
    }
    
    /**
     * Get product price by name
     * 
     * @param productName Product name
     * @return Product price
     */
    public String getProductPrice(String productName) {
        By priceLocator = By.xpath(String.format(
                "//div[text()='%s']/ancestor::div[@class='inventory_item']//div[@class='inventory_item_price']", 
                productName));
        String price = getText(priceLocator);
        logger.info("Product '{}' price: {}", productName, price);
        return price;
    }
    
    /**
     * Click on product name to view details
     * 
     * @param productName Product name
     * @return ProductDetailsPage instance
     */
    public ProductDetailsPage clickProductName(String productName) {
        By productNameLocator = By.xpath(String.format("//div[text()='%s']", productName));
        click(productNameLocator);
        logger.info("Clicked on product: {}", productName);
        return new ProductDetailsPage(driver);
    }
    
    /**
     * Verify if product exists
     * 
     * @param productName Product name
     * @return true if product exists, false otherwise
     */
    public boolean isProductDisplayed(String productName) {
        By productLocator = By.xpath(String.format("//div[text()='%s']", productName));
        boolean displayed = isDisplayed(productLocator);
        logger.info("Product '{}' displayed: {}", productName, displayed);
        return displayed;
    }
}
