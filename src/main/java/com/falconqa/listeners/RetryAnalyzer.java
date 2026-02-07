package com.falconqa.listeners;

import com.falconqa.utils.ConfigReader;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

/**
 * RetryAnalyzer - Automatically retries failed tests
 * Helps reduce false negatives from flaky tests
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class RetryAnalyzer implements IRetryAnalyzer {
    
    private static final Logger logger = LogManager.getLogger(RetryAnalyzer.class);
    private static final ConfigReader config = ConfigReader.getInstance();
    
    private int retryCount = 0;
    private int maxRetryCount;
    
    /**
     * Constructor - reads max retry count from config
     */
    public RetryAnalyzer() {
        this.maxRetryCount = config.getIntProperty("max.retry.count");
    }
    
    /**
     * Determines if test should be retried
     * 
     * @param result Test result
     * @return true if test should be retried, false otherwise
     */
    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < maxRetryCount) {
            retryCount++;
            logger.warn("Retrying test '{}' - Attempt {} of {}", 
                       result.getName(), retryCount, maxRetryCount);
            logger.warn("Failure reason: {}", result.getThrowable().getMessage());
            
            // Log to console for visibility
            System.out.println("⚠️ RETRY: " + result.getName() + 
                             " (Attempt " + retryCount + "/" + maxRetryCount + ")");
            
            return true;  // Retry the test
        }
        
        logger.error("Test '{}' failed after {} retry attempts", 
                    result.getName(), maxRetryCount);
        return false;  // Don't retry anymore
    }
    
    /**
     * Reset retry count (useful for test management)
     */
    public void resetRetryCount() {
        retryCount = 0;
    }
    
    /**
     * Get current retry count
     * 
     * @return Current retry count
     */
    public int getRetryCount() {
        return retryCount;
    }
}
