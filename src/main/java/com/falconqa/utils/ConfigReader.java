package com.falconqa.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.IOException;
import java.util.Properties;

/**
 * ConfigReader - Singleton class for reading configuration properties
 * Implements thread-safe lazy initialization
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class ConfigReader {
    
    private static final Logger logger = LogManager.getLogger(ConfigReader.class);
    private static ConfigReader instance;
    private Properties properties;
    private static final String CONFIG_FILE_PATH = "src/test/resources/config/config.properties";
    
    /**
     * Private constructor to prevent instantiation
     */
    private ConfigReader() {
        loadProperties();
    }
    
    /**
     * Thread-safe singleton instance getter
     * 
     * @return ConfigReader instance
     */
    public static synchronized ConfigReader getInstance() {
        if (instance == null) {
            instance = new ConfigReader();
        }
        return instance;
    }
    
    /**
     * Load properties from config file
     */
    private void loadProperties() {
        properties = new Properties();
        try (FileInputStream fis = new FileInputStream(CONFIG_FILE_PATH)) {
            properties.load(fis);
            logger.info("Configuration properties loaded successfully from: {}", CONFIG_FILE_PATH);
        } catch (IOException e) {
            logger.error("Failed to load configuration file: {}", CONFIG_FILE_PATH, e);
            throw new RuntimeException("Configuration file not found: " + CONFIG_FILE_PATH, e);
        }
    }
    
    /**
     * Get property value by key
     * 
     * @param key Property key
     * @return Property value
     */
    public String getProperty(String key) {
        String value = properties.getProperty(key);
        if (value != null) {
            return value.trim();
        }
        logger.warn("Property '{}' not found in configuration file", key);
        return null;
    }
    
    /**
     * Get property with default value
     * 
     * @param key Property key
     * @param defaultValue Default value if property not found
     * @return Property value or default value
     */
    public String getProperty(String key, String defaultValue) {
        return properties.getProperty(key, defaultValue).trim();
    }
    
    /**
     * Get integer property value
     * 
     * @param key Property key
     * @return Integer value
     */
    public int getIntProperty(String key) {
        String value = getProperty(key);
        try {
            return Integer.parseInt(value);
        } catch (NumberFormatException e) {
            logger.error("Failed to parse integer property: {}", key, e);
            throw new RuntimeException("Invalid integer property: " + key, e);
        }
    }
    
    /**
     * Get boolean property value
     * 
     * @param key Property key
     * @return Boolean value
     */
    public boolean getBooleanProperty(String key) {
        String value = getProperty(key);
        return Boolean.parseBoolean(value);
    }
    
    // Convenience methods for commonly used properties
    
    public String getBrowser() {
        return getProperty("browser", "chrome");
    }
    
    public String getBaseUrl() {
        String env = getProperty("environment", "qa");
        return switch (env.toLowerCase()) {
            case "staging" -> getProperty("staging.url");
            case "prod" -> getProperty("prod.url");
            default -> getProperty("base.url");
        };
    }
    
    public boolean isHeadless() {
        return getBooleanProperty("headless");
    }
    
    public int getImplicitWait() {
        return getIntProperty("implicit.wait");
    }
    
    public int getExplicitWait() {
        return getIntProperty("explicit.wait");
    }
    
    public int getPageLoadTimeout() {
        return getIntProperty("page.load.timeout");
    }
    
    public boolean isTakeScreenshotOnFailure() {
        return getBooleanProperty("take.screenshot.on.failure");
    }
    
    public boolean isParallelExecution() {
        return getBooleanProperty("parallel.execution");
    }
    
    public int getThreadCount() {
        return getIntProperty("thread.count");
    }
    
    public String getTestDataPath() {
        return getProperty("test.data.path");
    }
}
