package com.falconqa.pages;

import com.falconqa.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * CheckoutPage - Page Object for Checkout flow
 * Handles checkout information, overview, and completion
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class CheckoutPage extends BasePage {
    
    // Step 1: Your Information - Locators
    private final By firstNameField = By.id("first-name");
    private final By lastNameField = By.id("last-name");
    private final By postalCodeField = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By errorMessage = By.cssSelector("[data-test='error']");
    
    // Step 2: Overview - Locators
    private final By pageTitle = By.className("title");
    private final By paymentInformation = By.cssSelector("[data-test='payment-info-value']");
    private final By shippingInformation = By.cssSelector("[data-test='shipping-info-value']");
    private final By subtotalLabel = By.className("summary_subtotal_label");
    private final By taxLabel = By.className("summary_tax_label");
    private final By totalLabel = By.className("summary_total_label");
    private final By finishButton = By.id("finish");
    
    // Step 3: Complete - Locators
    private final By completeHeader = By.className("complete-header");
    private final By completeText = By.className("complete-text");
    private final By backHomeButton = By.id("back-to-products");
    
    /**
     * Constructor
     * 
     * @param driver WebDriver instance
     */
    public CheckoutPage(WebDriver driver) {
        super(driver);
    }
    
    // ==================== STEP 1: YOUR INFORMATION ====================
    
    /**
     * Enter first name
     * 
     * @param firstName First name
     * @return CheckoutPage instance
     */
    public CheckoutPage enterFirstName(String firstName) {
        type(firstNameField, firstName);
        logger.info("Entered first name: {}", firstName);
        return this;
    }
    
    /**
     * Enter last name
     * 
     * @param lastName Last name
     * @return CheckoutPage instance
     */
    public CheckoutPage enterLastName(String lastName) {
        type(lastNameField, lastName);
        logger.info("Entered last name: {}", lastName);
        return this;
    }
    
    /**
     * Enter postal code
     * 
     * @param postalCode Postal code
     * @return CheckoutPage instance
     */
    public CheckoutPage enterPostalCode(String postalCode) {
        type(postalCodeField, postalCode);
        logger.info("Entered postal code: {}", postalCode);
        return this;
    }
    
    /**
     * Fill checkout information
     * 
     * @param firstName First name
     * @param lastName Last name
     * @param postalCode Postal code
     */
    public void fillCheckoutInformation(String firstName, String lastName, String postalCode) {
        enterFirstName(firstName);
        enterLastName(lastName);
        enterPostalCode(postalCode);
        logger.info("Filled checkout information");
    }
    
    /**
     * Click continue button
     */
    public void clickContinue() {
        click(continueButton);
        logger.info("Clicked continue button");
    }
    
    /**
     * Click cancel button
     * 
     * @return CartPage instance
     */
    public CartPage clickCancel() {
        click(cancelButton);
        logger.info("Clicked cancel button");
        return new CartPage(driver);
    }
    
    /**
     * Get error message
     * 
     * @return Error message text
     */
    public String getErrorMessage() {
        String error = getText(errorMessage);
        logger.info("Error message: {}", error);
        return error;
    }
    
    /**
     * Check if error message is displayed
     * 
     * @return true if displayed, false otherwise
     */
    public boolean isErrorMessageDisplayed() {
        return isDisplayed(errorMessage);
    }
    
    // ==================== STEP 2: CHECKOUT OVERVIEW ====================
    
    /**
     * Verify checkout overview page is loaded
     * 
     * @return true if loaded, false otherwise
     */
    public boolean isCheckoutOverviewPageLoaded() {
        boolean loaded = isDisplayed(pageTitle) && 
                        getText(pageTitle).equals("Checkout: Overview");
        logger.info("Checkout overview page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Get payment information
     * 
     * @return Payment info text
     */
    public String getPaymentInformation() {
        String payment = getText(paymentInformation);
        logger.info("Payment information: {}", payment);
        return payment;
    }
    
    /**
     * Get shipping information
     * 
     * @return Shipping info text
     */
    public String getShippingInformation() {
        String shipping = getText(shippingInformation);
        logger.info("Shipping information: {}", shipping);
        return shipping;
    }
    
    /**
     * Get subtotal
     * 
     * @return Subtotal text
     */
    public String getSubtotal() {
        String subtotal = getText(subtotalLabel);
        logger.info("Subtotal: {}", subtotal);
        return subtotal;
    }
    
    /**
     * Get tax
     * 
     * @return Tax text
     */
    public String getTax() {
        String tax = getText(taxLabel);
        logger.info("Tax: {}", tax);
        return tax;
    }
    
    /**
     * Get total
     * 
     * @return Total text
     */
    public String getTotal() {
        String total = getText(totalLabel);
        logger.info("Total: {}", total);
        return total;
    }
    
    /**
     * Click finish button
     */
    public void clickFinish() {
        click(finishButton);
        logger.info("Clicked finish button");
    }
    
    // ==================== STEP 3: CHECKOUT COMPLETE ====================
    
    /**
     * Verify checkout complete page is loaded
     * 
     * @return true if loaded, false otherwise
     */
    public boolean isCheckoutCompletePageLoaded() {
        boolean loaded = isDisplayed(completeHeader);
        logger.info("Checkout complete page loaded: {}", loaded);
        return loaded;
    }
    
    /**
     * Get completion header text
     * 
     * @return Header text
     */
    public String getCompleteHeader() {
        String header = getText(completeHeader);
        logger.info("Complete header: {}", header);
        return header;
    }
    
    /**
     * Get completion message text
     * 
     * @return Message text
     */
    public String getCompleteText() {
        String text = getText(completeText);
        logger.info("Complete text: {}", text);
        return text;
    }
    
    /**
     * Click back home button
     * 
     * @return ProductsPage instance
     */
    public ProductsPage clickBackHome() {
        click(backHomeButton);
        logger.info("Clicked back home button");
        return new ProductsPage(driver);
    }
    
    /**
     * Verify order completion success
     * 
     * @return true if success, false otherwise
     */
    public boolean isOrderCompletedSuccessfully() {
        boolean success = isCheckoutCompletePageLoaded() && 
                         getCompleteHeader().contains("Thank you");
        logger.info("Order completed successfully: {}", success);
        return success;
    }
}
