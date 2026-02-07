package com.falconqa.core;

import com.falconqa.utils.ConfigReader;
import io.github.bonigarcia.wdm.WebDriverManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.edge.EdgeDriver;
import org.openqa.selenium.edge.EdgeOptions;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.openqa.selenium.firefox.FirefoxOptions;

import java.time.Duration;

/**
 * DriverFactory - Manages WebDriver lifecycle with ThreadLocal for parallel execution
 * Implements Factory and Singleton patterns
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class DriverFactory {
    
    private static final Logger logger = LogManager.getLogger(DriverFactory.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    
    // ThreadLocal for thread-safe parallel execution
    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();
    
    /**
     * Initialize WebDriver based on browser type from config
     * 
     * @return WebDriver instance
     */
    public static WebDriver initializeDriver() {
        return initializeDriver(config.getBrowser());
    }
    
    /**
     * Initialize WebDriver with specified browser
     * 
     * @param browserName Browser name (chrome, firefox, edge)
     * @return WebDriver instance
     */
    public static WebDriver initializeDriver(String browserName) {
        if (getDriver() != null) {
            logger.warn("Driver already initialized for this thread. Returning existing driver.");
            return getDriver();
        }
        
        logger.info("Initializing {} driver...", browserName);
        
        WebDriver webDriver;
        
        try {
            switch (browserName.toLowerCase()) {
                case "chrome":
                    webDriver = initializeChromeDriver();
                    break;
                case "firefox":
                    webDriver = initializeFirefoxDriver();
                    break;
                case "edge":
                    webDriver = initializeEdgeDriver();
                    break;
                default:
                    logger.warn("Unknown browser: {}. Defaulting to Chrome.", browserName);
                    webDriver = initializeChromeDriver();
            }
            
            // Set timeouts
            webDriver.manage().timeouts()
                    .implicitlyWait(Duration.ofSeconds(config.getImplicitWait()));
            webDriver.manage().timeouts()
                    .pageLoadTimeout(Duration.ofSeconds(config.getPageLoadTimeout()));
            webDriver.manage().timeouts()
                    .scriptTimeout(Duration.ofSeconds(30));
            
            // Maximize window
            webDriver.manage().window().maximize();
            
            // Set driver in ThreadLocal
            driver.set(webDriver);
            
            logger.info("Driver initialized successfully: {}", browserName);
            
            return webDriver;
            
        } catch (Exception e) {
            logger.error("Failed to initialize driver: {}", browserName, e);
            throw new RuntimeException("Driver initialization failed: " + e.getMessage(), e);
        }
    }
    
    /**
     * Initialize Chrome driver with options
     * 
     * @return ChromeDriver instance
     */
    private static WebDriver initializeChromeDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless=new");
        }
        
        // Performance and stability options
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-gpu");
        options.addArguments("--disable-extensions");
        options.addArguments("--disable-popup-blocking");
        options.addArguments("--start-maximized");
        options.addArguments("--remote-allow-origins=*");
        
        // Disable notifications
        options.addArguments("--disable-notifications");
        
        // Set page load strategy
        options.setPageLoadStrategy(org.openqa.selenium.PageLoadStrategy.NORMAL);
        
        logger.debug("Chrome options configured: headless={}", config.isHeadless());
        
        return new ChromeDriver(options);
    }
    
    /**
     * Initialize Firefox driver with options
     * 
     * @return FirefoxDriver instance
     */
    private static WebDriver initializeFirefoxDriver() {
        WebDriverManager.firefoxdriver().setup();
        FirefoxOptions options = new FirefoxOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--width=1920");
        options.addArguments("--height=1080");
        
        logger.debug("Firefox options configured: headless={}", config.isHeadless());
        
        return new FirefoxDriver(options);
    }
    
    /**
     * Initialize Edge driver with options
     * 
     * @return EdgeDriver instance
     */
    private static WebDriver initializeEdgeDriver() {
        WebDriverManager.edgedriver().setup();
        EdgeOptions options = new EdgeOptions();
        
        if (config.isHeadless()) {
            options.addArguments("--headless");
        }
        
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--no-sandbox");
        options.addArguments("--start-maximized");
        
        logger.debug("Edge options configured: headless={}", config.isHeadless());
        
        return new EdgeDriver(options);
    }
    
    /**
     * Get current thread's WebDriver instance
     * 
     * @return WebDriver instance
     */
    public static WebDriver getDriver() {
        return driver.get();
    }
    
    /**
     * Quit driver and remove from ThreadLocal
     */
    public static void quitDriver() {
        WebDriver webDriver = getDriver();
        if (webDriver != null) {
            try {
                logger.info("Quitting driver...");
                webDriver.quit();
                driver.remove();
                logger.info("Driver quit successfully");
            } catch (Exception e) {
                logger.error("Error while quitting driver", e);
            }
        }
    }
    
    /**
     * Check if driver is initialized for current thread
     * 
     * @return true if driver exists, false otherwise
     */
    public static boolean isDriverInitialized() {
        return getDriver() != null;
    }
}
