package com.falconqa.core;

import com.falconqa.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.*;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.List;

/**
 * BasePage - Parent class for all Page Objects
 * Contains reusable methods for WebElement interactions and waits
 * Implements Template Method pattern
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class BasePage {
    
    protected static final Logger logger = LogManager.getLogger(BasePage.class);
    protected WebDriver driver;
    protected WebDriverWait wait;
    protected Actions actions;
    protected JavascriptExecutor jsExecutor;
    private static final ConfigReader config = ConfigReader.getInstance();
    
    /**
     * Constructor - Initializes driver and utilities
     * 
     * @param driver WebDriver instance
     */
    public BasePage(WebDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, Duration.ofSeconds(config.getExplicitWait()));
        this.actions = new Actions(driver);
        this.jsExecutor = (JavascriptExecutor) driver;
    }
    
    // ==================== WAIT METHODS ====================
    
    /**
     * Wait for element to be visible
     * 
     * @param locator Element locator
     * @return WebElement
     */
    protected WebElement waitForElementVisible(By locator) {
        try {
            logger.debug("Waiting for element to be visible: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element not visible within timeout: {}", locator);
            throw new RuntimeException("Element not visible: " + locator, e);
        }
    }
    
    /**
     * Wait for element to be clickable
     * 
     * @param locator Element locator
     * @return WebElement
     */
    protected WebElement waitForElementClickable(By locator) {
        try {
            logger.debug("Waiting for element to be clickable: {}", locator);
            return wait.until(ExpectedConditions.elementToBeClickable(locator));
        } catch (TimeoutException e) {
            logger.error("Element not clickable within timeout: {}", locator);
            throw new RuntimeException("Element not clickable: " + locator, e);
        }
    }
    
    /**
     * Wait for element to be present in DOM
     * 
     * @param locator Element locator
     * @return WebElement
     */
    protected WebElement waitForElementPresent(By locator) {
        try {
            logger.debug("Waiting for element to be present: {}", locator);
            return wait.until(ExpectedConditions.presenceOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element not present within timeout: {}", locator);
            throw new RuntimeException("Element not present: " + locator, e);
        }
    }
    
    /**
     * Wait for all elements to be visible
     * 
     * @param locator Element locator
     * @return List of WebElements
     */
    protected List<WebElement> waitForElementsVisible(By locator) {
        try {
            logger.debug("Waiting for elements to be visible: {}", locator);
            return wait.until(ExpectedConditions.visibilityOfAllElementsLocatedBy(locator));
        } catch (TimeoutException e) {
            logger.error("Elements not visible within timeout: {}", locator);
            throw new RuntimeException("Elements not visible: " + locator, e);
        }
    }
    
    /**
     * Wait for element to be invisible
     * 
     * @param locator Element locator
     * @return boolean
     */
    protected boolean waitForElementInvisible(By locator) {
        try {
            logger.debug("Waiting for element to be invisible: {}", locator);
            return wait.until(ExpectedConditions.invisibilityOfElementLocated(locator));
        } catch (TimeoutException e) {
            logger.error("Element still visible after timeout: {}", locator);
            return false;
        }
    }
    
    /**
     * Wait for text to be present in element
     * 
     * @param locator Element locator
     * @param text Expected text
     * @return boolean
     */
    protected boolean waitForTextPresent(By locator, String text) {
        try {
            logger.debug("Waiting for text '{}' in element: {}", text, locator);
            return wait.until(ExpectedConditions.textToBePresentInElementLocated(locator, text));
        } catch (TimeoutException e) {
            logger.error("Text '{}' not present in element: {}", text, locator);
            return false;
        }
    }
    
    // ==================== ELEMENT INTERACTION METHODS ====================
    
    /**
     * Click element with wait
     * 
     * @param locator Element locator
     */
    protected void click(By locator) {
        try {
            WebElement element = waitForElementClickable(locator);
            element.click();
            logger.info("Clicked element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to click element: {}", locator, e);
            throw new RuntimeException("Click failed: " + locator, e);
        }
    }
    
    /**
     * Click using JavaScript executor (for stubborn elements)
     * 
     * @param locator Element locator
     */
    protected void clickByJS(By locator) {
        try {
            WebElement element = waitForElementVisible(locator);
            jsExecutor.executeScript("arguments[0].click();", element);
            logger.info("Clicked element using JavaScript: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to click element using JavaScript: {}", locator, e);
            throw new RuntimeException("JS click failed: " + locator, e);
        }
    }
    
    /**
     * Type text into element with clear
     * 
     * @param locator Element locator
     * @param text Text to type
     */
    protected void type(By locator, String text) {
        try {
            WebElement element = waitForElementVisible(locator);
            element.clear();
            element.sendKeys(text);
            logger.info("Typed text '{}' into element: {}", text, locator);
        } catch (Exception e) {
            logger.error("Failed to type into element: {}", locator, e);
            throw new RuntimeException("Type failed: " + locator, e);
        }
    }
    
    /**
     * Type text using JavaScript (bypasses Angular/React validation)
     * 
     * @param locator Element locator
     * @param text Text to type
     */
    protected void typeByJS(By locator, String text) {
        try {
            WebElement element = waitForElementVisible(locator);
            jsExecutor.executeScript("arguments[0].value=arguments[1];", element, text);
            logger.info("Typed text using JavaScript '{}' into element: {}", text, locator);
        } catch (Exception e) {
            logger.error("Failed to type using JavaScript: {}", locator, e);
            throw new RuntimeException("JS type failed: " + locator, e);
        }
    }
    
    /**
     * Get text from element
     * 
     * @param locator Element locator
     * @return Element text
     */
    protected String getText(By locator) {
        try {
            String text = waitForElementVisible(locator).getText();
            logger.debug("Retrieved text '{}' from element: {}", text, locator);
            return text;
        } catch (Exception e) {
            logger.error("Failed to get text from element: {}", locator, e);
            throw new RuntimeException("Get text failed: " + locator, e);
        }
    }
    
    /**
     * Get attribute value from element
     * 
     * @param locator Element locator
     * @param attribute Attribute name
     * @return Attribute value
     */
    protected String getAttribute(By locator, String attribute) {
        try {
            String value = waitForElementVisible(locator).getAttribute(attribute);
            logger.debug("Retrieved attribute '{}' = '{}' from element: {}", attribute, value, locator);
            return value;
        } catch (Exception e) {
            logger.error("Failed to get attribute '{}' from element: {}", attribute, locator, e);
            throw new RuntimeException("Get attribute failed: " + locator, e);
        }
    }
    
    /**
     * Check if element is displayed
     * 
     * @param locator Element locator
     * @return true if displayed, false otherwise
     */
    protected boolean isDisplayed(By locator) {
        try {
            boolean displayed = driver.findElement(locator).isDisplayed();
            logger.debug("Element displayed status: {} for {}", displayed, locator);
            return displayed;
        } catch (NoSuchElementException e) {
            logger.debug("Element not found: {}", locator);
            return false;
        }
    }
    
    /**
     * Check if element is enabled
     * 
     * @param locator Element locator
     * @return true if enabled, false otherwise
     */
    protected boolean isEnabled(By locator) {
        try {
            boolean enabled = waitForElementVisible(locator).isEnabled();
            logger.debug("Element enabled status: {} for {}", enabled, locator);
            return enabled;
        } catch (Exception e) {
            logger.debug("Element not enabled: {}", locator);
            return false;
        }
    }
    
    // ==================== DROPDOWN METHODS ====================
    
    /**
     * Select dropdown option by visible text
     * 
     * @param locator Dropdown locator
     * @param visibleText Option text
     */
    protected void selectByVisibleText(By locator, String visibleText) {
        try {
            Select select = new Select(waitForElementVisible(locator));
            select.selectByVisibleText(visibleText);
            logger.info("Selected option '{}' from dropdown: {}", visibleText, locator);
        } catch (Exception e) {
            logger.error("Failed to select option '{}' from dropdown: {}", visibleText, locator, e);
            throw new RuntimeException("Select by text failed: " + locator, e);
        }
    }
    
    /**
     * Select dropdown option by value
     * 
     * @param locator Dropdown locator
     * @param value Option value
     */
    protected void selectByValue(By locator, String value) {
        try {
            Select select = new Select(waitForElementVisible(locator));
            select.selectByValue(value);
            logger.info("Selected option with value '{}' from dropdown: {}", value, locator);
        } catch (Exception e) {
            logger.error("Failed to select option with value '{}' from dropdown: {}", value, locator, e);
            throw new RuntimeException("Select by value failed: " + locator, e);
        }
    }
    
    // ==================== MOUSE ACTIONS ====================
    
    /**
     * Hover over element
     * 
     * @param locator Element locator
     */
    protected void hover(By locator) {
        try {
            WebElement element = waitForElementVisible(locator);
            actions.moveToElement(element).perform();
            logger.info("Hovered over element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to hover over element: {}", locator, e);
            throw new RuntimeException("Hover failed: " + locator, e);
        }
    }
    
    /**
     * Double click element
     * 
     * @param locator Element locator
     */
    protected void doubleClick(By locator) {
        try {
            WebElement element = waitForElementVisible(locator);
            actions.doubleClick(element).perform();
            logger.info("Double clicked element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to double click element: {}", locator, e);
            throw new RuntimeException("Double click failed: " + locator, e);
        }
    }
    
    // ==================== SCROLL METHODS ====================
    
    /**
     * Scroll to element
     * 
     * @param locator Element locator
     */
    protected void scrollToElement(By locator) {
        try {
            WebElement element = waitForElementVisible(locator);
            jsExecutor.executeScript("arguments[0].scrollIntoView(true);", element);
            logger.info("Scrolled to element: {}", locator);
        } catch (Exception e) {
            logger.error("Failed to scroll to element: {}", locator, e);
            throw new RuntimeException("Scroll failed: " + locator, e);
        }
    }
    
    /**
     * Scroll to bottom of page
     */
    protected void scrollToBottom() {
        jsExecutor.executeScript("window.scrollTo(0, document.body.scrollHeight);");
        logger.info("Scrolled to bottom of page");
    }
    
    // ==================== ALERT METHODS ====================
    
    /**
     * Accept alert
     */
    protected void acceptAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().accept();
            logger.info("Accepted alert");
        } catch (Exception e) {
            logger.error("Failed to accept alert", e);
            throw new RuntimeException("Accept alert failed", e);
        }
    }
    
    /**
     * Dismiss alert
     */
    protected void dismissAlert() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            driver.switchTo().alert().dismiss();
            logger.info("Dismissed alert");
        } catch (Exception e) {
            logger.error("Failed to dismiss alert", e);
            throw new RuntimeException("Dismiss alert failed", e);
        }
    }
    
    /**
     * Get alert text
     * 
     * @return Alert text
     */
    protected String getAlertText() {
        try {
            wait.until(ExpectedConditions.alertIsPresent());
            String alertText = driver.switchTo().alert().getText();
            logger.info("Alert text: {}", alertText);
            return alertText;
        } catch (Exception e) {
            logger.error("Failed to get alert text", e);
            throw new RuntimeException("Get alert text failed", e);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Get current page title
     * 
     * @return Page title
     */
    protected String getPageTitle() {
        String title = driver.getTitle();
        logger.info("Current page title: {}", title);
        return title;
    }
    
    /**
     * Get current URL
     * 
     * @return Current URL
     */
    protected String getCurrentUrl() {
        String url = driver.getCurrentUrl();
        logger.info("Current URL: {}", url);
        return url;
    }
    
    /**
     * Refresh page
     */
    protected void refreshPage() {
        driver.navigate().refresh();
        logger.info("Page refreshed");
    }
    
    /**
     * Navigate back
     */
    protected void navigateBack() {
        driver.navigate().back();
        logger.info("Navigated back");
    }
    
    /**
     * Take a short pause (use sparingly)
     * 
     * @param milliseconds Milliseconds to wait
     */
    protected void pause(long milliseconds) {
        try {
            Thread.sleep(milliseconds);
            logger.debug("Paused for {} milliseconds", milliseconds);
        } catch (InterruptedException e) {
            logger.error("Pause interrupted", e);
            Thread.currentThread().interrupt();
        }
    }
}
