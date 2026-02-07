package com.falconqa.utils;

import org.apache.commons.io.FileUtils;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.openqa.selenium.WebDriver;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 * ScreenshotUtils - Utility class for capturing screenshots
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class ScreenshotUtils {
    
    private static final Logger logger = LogManager.getLogger(ScreenshotUtils.class);
    private static final String SCREENSHOT_PATH = "test-output/screenshots/";
    
    /**
     * Capture screenshot and save to file
     * 
     * @param driver WebDriver instance
     * @param screenshotName Screenshot name
     * @return Screenshot file path
     */
    public static String captureScreenshot(WebDriver driver, String screenshotName) {
        try {
            // Create screenshots directory if not exists
            File screenshotDir = new File(SCREENSHOT_PATH);
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }
            
            // Generate timestamp
            String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
            
            // Create filename
            String fileName = screenshotName + "_" + timestamp + ".png";
            String filePath = SCREENSHOT_PATH + fileName;
            
            // Take screenshot
            TakesScreenshot takesScreenshot = (TakesScreenshot) driver;
            File sourceFile = takesScreenshot.getScreenshotAs(OutputType.FILE);
            File destinationFile = new File(filePath);
            
            // Copy file to destination
            FileUtils.copyFile(sourceFile, destinationFile);
            
            logger.info("Screenshot captured successfully: {}", filePath);
            return filePath;
            
        } catch (Exception e) {
            logger.error("Failed to capture screenshot", e);
            return null;
        }
    }
    
    /**
     * Capture screenshot with auto-generated name
     * 
     * @param driver WebDriver instance
     * @return Screenshot file path
     */
    public static String captureScreenshot(WebDriver driver) {
        String timestamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        return captureScreenshot(driver, "screenshot_" + timestamp);
    }
}
