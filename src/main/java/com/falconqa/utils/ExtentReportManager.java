package com.falconqa.utils;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ExtentReportManager - Manages Extent Reports for test execution
 * Thread-safe implementation for parallel execution
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class ExtentReportManager {
    
    private static final Logger logger = LogManager.getLogger(ExtentReportManager.class);
    private static ExtentReports extentReports;
    private static ThreadLocal<ExtentTest> extentTest = new ThreadLocal<>();
    private static final ConfigReader config = ConfigReader.getInstance();
    
    private static final String REPORT_PATH = "test-output/reports/";
    private static final String REPORT_NAME = "ExtentReport_" + 
            new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date()) + ".html";
    
    /**
     * Initialize Extent Reports
     */
    public static void initReports() {
        if (extentReports == null) {
            // Create report directory if not exists
            File reportDir = new File(REPORT_PATH);
            if (!reportDir.exists()) {
                reportDir.mkdirs();
            }
            
            // Initialize ExtentSparkReporter
            ExtentSparkReporter sparkReporter = new ExtentSparkReporter(REPORT_PATH + REPORT_NAME);
            
            // Configure report
            sparkReporter.config().setDocumentTitle(config.getProperty("extent.report.title", 
                    "FalconQA Automation Report"));
            sparkReporter.config().setReportName(config.getProperty("extent.report.name", 
                    "Test Execution Report"));
            sparkReporter.config().setTheme(Theme.DARK);
            sparkReporter.config().setTimeStampFormat("MMM dd, yyyy HH:mm:ss");
            
            // Initialize ExtentReports
            extentReports = new ExtentReports();
            extentReports.attachReporter(sparkReporter);
            
            // Set system info
            extentReports.setSystemInfo("Application", "FalconQA Framework");
            extentReports.setSystemInfo("Environment", config.getProperty("environment", "QA"));
            extentReports.setSystemInfo("Browser", config.getBrowser());
            extentReports.setSystemInfo("OS", System.getProperty("os.name"));
            extentReports.setSystemInfo("Java Version", System.getProperty("java.version"));
            extentReports.setSystemInfo("User", System.getProperty("user.name"));
            
            logger.info("Extent Reports initialized: {}", REPORT_PATH + REPORT_NAME);
        }
    }
    
    /**
     * Create a test in the report
     * 
     * @param testName Test name
     * @param description Test description
     */
    public static void createTest(String testName, String description) {
        ExtentTest test = extentReports.createTest(testName, description);
        extentTest.set(test);
        logger.debug("Test created in report: {}", testName);
    }
    
    /**
     * Get current test instance
     * 
     * @return ExtentTest instance
     */
    public static ExtentTest getTest() {
        return extentTest.get();
    }
    
    /**
     * Log info message
     * 
     * @param message Message to log
     */
    public static void logInfo(String message) {
        if (getTest() != null) {
            getTest().log(Status.INFO, message);
        }
    }
    
    /**
     * Log pass message
     * 
     * @param message Message to log
     */
    public static void logPass(String message) {
        if (getTest() != null) {
            getTest().log(Status.PASS, message);
        }
    }
    
    /**
     * Log fail message
     * 
     * @param message Message to log
     */
    public static void logFail(String message) {
        if (getTest() != null) {
            getTest().log(Status.FAIL, message);
        }
    }
    
    /**
     * Log skip message
     * 
     * @param message Message to log
     */
    public static void logSkip(String message) {
        if (getTest() != null) {
            getTest().log(Status.SKIP, message);
        }
    }
    
    /**
     * Log warning message
     * 
     * @param message Message to log
     */
    public static void logWarning(String message) {
        if (getTest() != null) {
            getTest().log(Status.WARNING, message);
        }
    }
    
    /**
     * Attach screenshot to report
     * 
     * @param screenshotPath Path to screenshot
     */
    public static void attachScreenshot(String screenshotPath) {
        if (getTest() != null && screenshotPath != null) {
            try {
                getTest().addScreenCaptureFromPath(screenshotPath);
                logger.debug("Screenshot attached to report: {}", screenshotPath);
            } catch (Exception e) {
                logger.error("Failed to attach screenshot to report", e);
            }
        }
    }
    
    /**
     * Flush reports - write to disk
     */
    public static void flushReports() {
        if (extentReports != null) {
            extentReports.flush();
            logger.info("Extent Reports flushed successfully");
            logger.info("Report location: {}", REPORT_PATH + REPORT_NAME);
        }
    }
    
    /**
     * Get report file path
     * 
     * @return Report file path
     */
    public static String getReportPath() {
        return REPORT_PATH + REPORT_NAME;
    }
}
