package com.falconqa.core;

import com.falconqa.utils.ConfigReader;
import com.falconqa.utils.ExtentReportManager;
import com.falconqa.utils.ScreenshotUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.WebDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

/**
 * BaseTest - Parent class for all test classes
 * Handles driver lifecycle, reporting, and screenshots
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class BaseTest {
    
    protected static final Logger logger = LogManager.getLogger(BaseTest.class);
    protected WebDriver driver;
    protected static final ConfigReader config = ConfigReader.getInstance();
    
    /**
     * Suite level setup - Initialize Extent Reports
     */
    @BeforeSuite(alwaysRun = true)
    public void suiteSetup() {
        logger.info("========== TEST SUITE EXECUTION STARTED ==========");
        ExtentReportManager.initReports();
        logger.info("Extent Reports initialized");
    }
    
    /**
     * Test level setup - Initialize driver and navigate to base URL
     */
    @BeforeMethod(alwaysRun = true)
    public void setUp(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        logger.info("========== TEST STARTED: {} ==========", testName);
        
        // Start performance tracking
        com.falconqa.utils.PerformanceProfiler.startTest(testName);
        
        // Initialize driver
        driver = DriverFactory.initializeDriver();
        logger.info("Driver initialized for test: {}", testName);
        
        // Create test in Extent Report
        ExtentReportManager.createTest(testName, 
                                       result.getMethod().getDescription());
        
        // Navigate to base URL
        String baseUrl = config.getBaseUrl();
        driver.get(baseUrl);
        logger.info("Navigated to base URL: {}", baseUrl);
        
        ExtentReportManager.logInfo("Test started - Browser: " + config.getBrowser());
        ExtentReportManager.logInfo("Navigated to: " + baseUrl);
    }
    
    /**
     * Test level teardown - Handle test results, screenshots, and quit driver
     */
    @AfterMethod(alwaysRun = true)
    public void tearDown(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        
        // Stop performance tracking
        long executionTime = com.falconqa.utils.PerformanceProfiler.stopTest(testName);
        
        // Handle test result
        if (result.getStatus() == ITestResult.FAILURE) {
            logger.error("TEST FAILED: {}", testName);
            logger.error("Failure reason: {}", result.getThrowable());
            
            // Take screenshot on failure
            if (config.isTakeScreenshotOnFailure()) {
                String screenshotPath = ScreenshotUtils.captureScreenshot(driver, testName);
                ExtentReportManager.logFail("Test Failed: " + result.getThrowable().getMessage());
                ExtentReportManager.attachScreenshot(screenshotPath);
                logger.info("Screenshot captured: {}", screenshotPath);
            }
            
        } else if (result.getStatus() == ITestResult.SUCCESS) {
            logger.info("TEST PASSED: {}", testName);
            ExtentReportManager.logPass("Test Passed (Duration: " + executionTime + "ms)");
            
        } else if (result.getStatus() == ITestResult.SKIP) {
            logger.warn("TEST SKIPPED: {}", testName);
            ExtentReportManager.logSkip("Test Skipped: " + result.getThrowable());
        }
        
        // Quit driver
        DriverFactory.quitDriver();
        logger.info("Driver quit successfully");
        logger.info("========== TEST ENDED: {} ==========", testName);
    }
    
    /**
     * Suite level teardown - Flush Extent Reports and generate performance report
     */
    @AfterSuite(alwaysRun = true)
    public void suiteTeardown() {
        logger.info("========== TEST SUITE EXECUTION COMPLETED ==========");
        
        // Flush Extent Reports
        ExtentReportManager.flushReports();
        logger.info("Extent Reports flushed successfully");
        
        // Generate and save performance report
        com.falconqa.utils.PerformanceProfiler.printReport();
        com.falconqa.utils.PerformanceProfiler.saveReport("test-output/reports/performance-report.txt");
        com.falconqa.utils.PerformanceProfiler.saveCSVReport("test-output/reports/performance-report.csv");
        logger.info("Performance reports generated successfully");
    }
    
    /**
     * Get current driver instance
     * 
     * @return WebDriver instance
     */
    protected WebDriver getDriver() {
        return DriverFactory.getDriver();
    }
}
