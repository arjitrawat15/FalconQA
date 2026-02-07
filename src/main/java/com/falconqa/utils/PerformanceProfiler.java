package com.falconqa.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileWriter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * PerformanceProfiler - Tracks and analyzes test execution performance
 * Identifies slow tests and performance bottlenecks
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class PerformanceProfiler {
    
    private static final Logger logger = LogManager.getLogger(PerformanceProfiler.class);
    
    // Thread-safe storage for performance metrics
    private static final Map<String, Long> testStartTimes = new ConcurrentHashMap<>();
    private static final Map<String, Long> testExecutionTimes = new ConcurrentHashMap<>();
    private static final Map<String, Integer> testExecutionCounts = new ConcurrentHashMap<>();
    
    // Performance thresholds (in milliseconds)
    private static final long SLOW_TEST_THRESHOLD = 5000;  // 5 seconds
    private static final long VERY_SLOW_TEST_THRESHOLD = 10000;  // 10 seconds
    
    /**
     * Start tracking a test
     * 
     * @param testName Test name
     */
    public static void startTest(String testName) {
        testStartTimes.put(testName, System.currentTimeMillis());
        logger.debug("Performance tracking started for: {}", testName);
    }
    
    /**
     * Stop tracking a test and record execution time
     * 
     * @param testName Test name
     * @return Execution time in milliseconds
     */
    public static long stopTest(String testName) {
        Long startTime = testStartTimes.get(testName);
        if (startTime == null) {
            logger.warn("No start time found for test: {}", testName);
            return 0;
        }
        
        long executionTime = System.currentTimeMillis() - startTime;
        
        // Store execution time
        testExecutionTimes.put(testName, executionTime);
        
        // Increment execution count
        testExecutionCounts.merge(testName, 1, Integer::sum);
        
        // Log if test is slow
        if (executionTime > VERY_SLOW_TEST_THRESHOLD) {
            logger.warn("⚠️ VERY SLOW TEST: {} took {}ms", testName, executionTime);
        } else if (executionTime > SLOW_TEST_THRESHOLD) {
            logger.warn("⚠️ SLOW TEST: {} took {}ms", testName, executionTime);
        } else {
            logger.debug("Test completed: {} in {}ms", testName, executionTime);
        }
        
        // Remove from tracking
        testStartTimes.remove(testName);
        
        return executionTime;
    }
    
    /**
     * Get execution time for a specific test
     * 
     * @param testName Test name
     * @return Execution time in milliseconds, or 0 if not found
     */
    public static long getExecutionTime(String testName) {
        return testExecutionTimes.getOrDefault(testName, 0L);
    }
    
    /**
     * Get average execution time for a test
     * 
     * @param testName Test name
     * @return Average execution time in milliseconds
     */
    public static long getAverageExecutionTime(String testName) {
        Long totalTime = testExecutionTimes.get(testName);
        Integer count = testExecutionCounts.get(testName);
        
        if (totalTime == null || count == null || count == 0) {
            return 0;
        }
        
        return totalTime / count;
    }
    
    /**
     * Get all slow tests (above threshold)
     * 
     * @return Map of slow tests with execution times
     */
    public static Map<String, Long> getSlowTests() {
        Map<String, Long> slowTests = new HashMap<>();
        
        for (Map.Entry<String, Long> entry : testExecutionTimes.entrySet()) {
            if (entry.getValue() > SLOW_TEST_THRESHOLD) {
                slowTests.put(entry.getKey(), entry.getValue());
            }
        }
        
        return slowTests;
    }
    
    /**
     * Get top N slowest tests
     * 
     * @param n Number of tests to return
     * @return List of test names sorted by execution time (slowest first)
     */
    public static List<Map.Entry<String, Long>> getTopSlowTests(int n) {
        return testExecutionTimes.entrySet().stream()
                .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                .limit(n)
                .toList();
    }
    
    /**
     * Get total execution time for all tests
     * 
     * @return Total time in milliseconds
     */
    public static long getTotalExecutionTime() {
        return testExecutionTimes.values().stream()
                .mapToLong(Long::longValue)
                .sum();
    }
    
    /**
     * Get average execution time for all tests
     * 
     * @return Average time in milliseconds
     */
    public static long getAverageExecutionTimeAllTests() {
        if (testExecutionTimes.isEmpty()) {
            return 0;
        }
        return getTotalExecutionTime() / testExecutionTimes.size();
    }
    
    /**
     * Generate performance report
     * 
     * @return Performance report as formatted string
     */
    public static String generateReport() {
        StringBuilder report = new StringBuilder();
        
        report.append("\n");
        report.append("════════════════════════════════════════════════════════════════\n");
        report.append("                 PERFORMANCE ANALYSIS REPORT\n");
        report.append("════════════════════════════════════════════════════════════════\n");
        report.append("\n");
        
        // Overall statistics
        report.append("📊 OVERALL STATISTICS:\n");
        report.append("   Total Tests Executed: ").append(testExecutionTimes.size()).append("\n");
        report.append("   Total Execution Time: ").append(formatTime(getTotalExecutionTime())).append("\n");
        report.append("   Average Test Time: ").append(formatTime(getAverageExecutionTimeAllTests())).append("\n");
        report.append("\n");
        
        // Slow tests
        Map<String, Long> slowTests = getSlowTests();
        if (!slowTests.isEmpty()) {
            report.append("⚠️ SLOW TESTS (> ").append(SLOW_TEST_THRESHOLD).append("ms):\n");
            slowTests.entrySet().stream()
                    .sorted(Map.Entry.<String, Long>comparingByValue().reversed())
                    .forEach(entry -> {
                        report.append("   ❌ ").append(entry.getKey())
                              .append(": ").append(formatTime(entry.getValue())).append("\n");
                    });
            report.append("\n");
        }
        
        // Top 10 slowest tests
        report.append("🐌 TOP 10 SLOWEST TESTS:\n");
        List<Map.Entry<String, Long>> topSlow = getTopSlowTests(10);
        int rank = 1;
        for (Map.Entry<String, Long> entry : topSlow) {
            report.append(String.format("   %2d. %-50s %s\n", 
                    rank++, entry.getKey(), formatTime(entry.getValue())));
        }
        report.append("\n");
        
        // Performance distribution
        report.append("📈 PERFORMANCE DISTRIBUTION:\n");
        long fast = testExecutionTimes.values().stream().filter(t -> t < 1000).count();
        long normal = testExecutionTimes.values().stream().filter(t -> t >= 1000 && t < SLOW_TEST_THRESHOLD).count();
        long slow = testExecutionTimes.values().stream().filter(t -> t >= SLOW_TEST_THRESHOLD && t < VERY_SLOW_TEST_THRESHOLD).count();
        long verySlow = testExecutionTimes.values().stream().filter(t -> t >= VERY_SLOW_TEST_THRESHOLD).count();
        
        report.append("   ⚡ Fast (< 1s):           ").append(fast).append(" tests\n");
        report.append("   ✅ Normal (1-5s):         ").append(normal).append(" tests\n");
        report.append("   ⚠️  Slow (5-10s):          ").append(slow).append(" tests\n");
        report.append("   🐌 Very Slow (> 10s):     ").append(verySlow).append(" tests\n");
        report.append("\n");
        
        report.append("════════════════════════════════════════════════════════════════\n");
        
        return report.toString();
    }
    
    /**
     * Save performance report to file
     * 
     * @param filename Filename to save report
     */
    public static void saveReport(String filename) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String reportFilename = filename.replace(".txt", "_" + timestamp + ".txt");
            
            FileWriter writer = new FileWriter(reportFilename);
            writer.write(generateReport());
            writer.close();
            
            logger.info("Performance report saved to: {}", reportFilename);
            
        } catch (IOException e) {
            logger.error("Failed to save performance report", e);
        }
    }
    
    /**
     * Generate CSV report for analysis in Excel
     * 
     * @param filename CSV filename
     */
    public static void saveCSVReport(String filename) {
        try {
            String timestamp = new SimpleDateFormat("yyyy-MM-dd_HH-mm-ss").format(new Date());
            String csvFilename = filename.replace(".csv", "_" + timestamp + ".csv");
            
            FileWriter writer = new FileWriter(csvFilename);
            
            // Header
            writer.write("Test Name,Execution Time (ms),Execution Time (s),Status\n");
            
            // Data rows
            for (Map.Entry<String, Long> entry : testExecutionTimes.entrySet()) {
                String testName = entry.getKey();
                long timeMs = entry.getValue();
                double timeSec = timeMs / 1000.0;
                String status;
                
                if (timeMs > VERY_SLOW_TEST_THRESHOLD) {
                    status = "VERY_SLOW";
                } else if (timeMs > SLOW_TEST_THRESHOLD) {
                    status = "SLOW";
                } else if (timeMs < 1000) {
                    status = "FAST";
                } else {
                    status = "NORMAL";
                }
                
                writer.write(String.format("%s,%d,%.2f,%s\n", 
                        testName, timeMs, timeSec, status));
            }
            
            writer.close();
            logger.info("CSV performance report saved to: {}", csvFilename);
            
        } catch (IOException e) {
            logger.error("Failed to save CSV report", e);
        }
    }
    
    /**
     * Format time in human-readable format
     * 
     * @param milliseconds Time in milliseconds
     * @return Formatted time string
     */
    private static String formatTime(long milliseconds) {
        if (milliseconds < 1000) {
            return milliseconds + "ms";
        } else if (milliseconds < 60000) {
            return String.format("%.2fs", milliseconds / 1000.0);
        } else {
            long minutes = milliseconds / 60000;
            long seconds = (milliseconds % 60000) / 1000;
            return String.format("%dm %ds", minutes, seconds);
        }
    }
    
    /**
     * Clear all performance data
     */
    public static void reset() {
        testStartTimes.clear();
        testExecutionTimes.clear();
        testExecutionCounts.clear();
        logger.info("Performance data cleared");
    }
    
    /**
     * Print performance report to console
     */
    public static void printReport() {
        System.out.println(generateReport());
    }
}
