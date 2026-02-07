package com.falconqa.listeners;

import com.falconqa.utils.ExtentReportManager;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.testng.*;

import java.util.Arrays;

/**
 * TestListener - Custom TestNG listener for enhanced reporting and logging
 * Implements multiple listener interfaces for comprehensive test tracking
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class TestListener implements ITestListener, ISuiteListener, IInvokedMethodListener {
    
    private static final Logger logger = LogManager.getLogger(TestListener.class);
    
    private int totalTests = 0;
    private int passedTests = 0;
    private int failedTests = 0;
    private int skippedTests = 0;
    private long suiteStartTime = 0;
    
    // ==================== SUITE LEVEL LISTENERS ====================
    
    @Override
    public void onStart(ISuite suite) {
        suiteStartTime = System.currentTimeMillis();
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("     TEST SUITE STARTED: {}", suite.getName());
        logger.info("═══════════════════════════════════════════════════════════");
        
        System.out.println("\n🚀 Starting Test Suite: " + suite.getName());
        System.out.println("━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━━");
    }
    
    @Override
    public void onFinish(ISuite suite) {
        long duration = System.currentTimeMillis() - suiteStartTime;
        long minutes = duration / 60000;
        long seconds = (duration % 60000) / 1000;
        
        logger.info("═══════════════════════════════════════════════════════════");
        logger.info("     TEST SUITE FINISHED: {}", suite.getName());
        logger.info("     Total Tests: {}", totalTests);
        logger.info("     ✅ Passed: {}", passedTests);
        logger.info("     ❌ Failed: {}", failedTests);
        logger.info("     ⏭️ Skipped: {}", skippedTests);
        logger.info("     ⏱️ Duration: {}m {}s", minutes, seconds);
        logger.info("═══════════════════════════════════════════════════════════");
        
        // Console summary
        System.out.println("\n" + "━".repeat(60));
        System.out.println("🏁 TEST SUITE COMPLETED: " + suite.getName());
        System.out.println("━".repeat(60));
        System.out.println("📊 Test Summary:");
        System.out.println("   Total:   " + totalTests);
        System.out.println("   ✅ Passed:  " + passedTests + " (" + getPercentage(passedTests, totalTests) + "%)");
        System.out.println("   ❌ Failed:  " + failedTests + " (" + getPercentage(failedTests, totalTests) + "%)");
        System.out.println("   ⏭️ Skipped: " + skippedTests + " (" + getPercentage(skippedTests, totalTests) + "%)");
        System.out.println("   ⏱️ Duration: " + minutes + "m " + seconds + "s");
        System.out.println("━".repeat(60) + "\n");
    }
    
    // ==================== TEST LEVEL LISTENERS ====================
    
    @Override
    public void onTestStart(ITestResult result) {
        totalTests++;
        String testName = result.getMethod().getMethodName();
        String className = result.getTestClass().getRealClass().getSimpleName();
        
        logger.info("▶️ Starting Test: {}.{}", className, testName);
        System.out.println("\n▶️ Test Started: " + className + "." + testName);
        
        // Log data provider parameters if present
        Object[] parameters = result.getParameters();
        if (parameters.length > 0) {
            logger.info("   Parameters: {}", Arrays.toString(parameters));
            System.out.println("   📝 Parameters: " + Arrays.toString(parameters));
        }
    }
    
    @Override
    public void onTestSuccess(ITestResult result) {
        passedTests++;
        String testName = result.getMethod().getMethodName();
        long duration = result.getEndMillis() - result.getStartMillis();
        
        logger.info("✅ Test PASSED: {} (Duration: {}ms)", testName, duration);
        System.out.println("✅ Test PASSED: " + testName + " (" + duration + "ms)");
    }
    
    @Override
    public void onTestFailure(ITestResult result) {
        failedTests++;
        String testName = result.getMethod().getMethodName();
        Throwable throwable = result.getThrowable();
        
        logger.error("❌ Test FAILED: {}", testName);
        logger.error("   Failure Reason: {}", throwable.getMessage());
        logger.error("   Stack Trace: ", throwable);
        
        System.out.println("❌ Test FAILED: " + testName);
        System.out.println("   Reason: " + throwable.getMessage());
        
        // Check if test will be retried
        if (result.getMethod().getRetryAnalyzer(result) != null) {
            RetryAnalyzer retryAnalyzer = (RetryAnalyzer) result.getMethod().getRetryAnalyzer(result);
            if (retryAnalyzer.getRetryCount() < 1) {  // Will retry
                System.out.println("   ⚠️ Test will be retried...");
            }
        }
    }
    
    @Override
    public void onTestSkipped(ITestResult result) {
        skippedTests++;
        String testName = result.getMethod().getMethodName();
        
        logger.warn("⏭️ Test SKIPPED: {}", testName);
        System.out.println("⏭️ Test SKIPPED: " + testName);
        
        if (result.getThrowable() != null) {
            logger.warn("   Skip Reason: {}", result.getThrowable().getMessage());
            System.out.println("   Reason: " + result.getThrowable().getMessage());
        }
    }
    
    @Override
    public void onTestFailedButWithinSuccessPercentage(ITestResult result) {
        logger.info("⚠️ Test failed but within success percentage: {}", 
                   result.getMethod().getMethodName());
    }
    
    @Override
    public void onTestFailedWithTimeout(ITestResult result) {
        failedTests++;
        String testName = result.getMethod().getMethodName();
        
        logger.error("⏱️ Test FAILED (Timeout): {}", testName);
        System.out.println("⏱️ Test FAILED (Timeout): " + testName);
    }
    
    // ==================== METHOD LEVEL LISTENERS ====================
    
    @Override
    public void beforeInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            logger.debug("🔧 Before Test Method: {}", methodName);
        }
    }
    
    @Override
    public void afterInvocation(IInvokedMethod method, ITestResult testResult) {
        if (method.isTestMethod()) {
            String methodName = method.getTestMethod().getMethodName();
            long duration = testResult.getEndMillis() - testResult.getStartMillis();
            logger.debug("🔧 After Test Method: {} (Duration: {}ms)", methodName, duration);
        }
    }
    
    // ==================== UTILITY METHODS ====================
    
    /**
     * Calculate percentage
     * 
     * @param value Numerator
     * @param total Denominator
     * @return Percentage (0-100)
     */
    private int getPercentage(int value, int total) {
        if (total == 0) return 0;
        return (value * 100) / total;
    }
    
    /**
     * Get test statistics
     * 
     * @return Test statistics string
     */
    public String getTestStatistics() {
        return String.format("Total: %d | Passed: %d | Failed: %d | Skipped: %d", 
                           totalTests, passedTests, failedTests, skippedTests);
    }
}
