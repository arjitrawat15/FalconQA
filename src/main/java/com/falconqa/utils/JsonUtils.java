package com.falconqa.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.*;

/**
 * JsonUtils - Utility class for reading and parsing JSON test data files
 * Uses Jackson library for JSON processing
 * 
 * @author FalconQA Team
 * @version 1.0
 */
public class JsonUtils {
    
    private static final Logger logger = LogManager.getLogger(JsonUtils.class);
    private static final ObjectMapper objectMapper = new ObjectMapper();
    
    /**
     * Read JSON file and return as JsonNode
     * 
     * @param jsonFilePath Path to JSON file
     * @return JsonNode object
     */
    public static JsonNode readJsonFile(String jsonFilePath) {
        try {
            File jsonFile = new File(jsonFilePath);
            JsonNode rootNode = objectMapper.readTree(jsonFile);
            logger.info("JSON file read successfully: {}", jsonFilePath);
            return rootNode;
            
        } catch (IOException e) {
            logger.error("Failed to read JSON file: {}", jsonFilePath, e);
            throw new RuntimeException("JSON file not found or cannot be read: " + jsonFilePath, e);
        }
    }
    
    /**
     * Read JSON file and convert to Map
     * 
     * @param jsonFilePath Path to JSON file
     * @return Map containing JSON data
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> readJsonAsMap(String jsonFilePath) {
        try {
            File jsonFile = new File(jsonFilePath);
            Map<String, Object> jsonMap = objectMapper.readValue(jsonFile, Map.class);
            logger.info("JSON file read as Map: {}", jsonFilePath);
            return jsonMap;
            
        } catch (IOException e) {
            logger.error("Failed to read JSON file as Map: {}", jsonFilePath, e);
            throw new RuntimeException("JSON file cannot be converted to Map: " + jsonFilePath, e);
        }
    }
    
    /**
     * Read JSON array and convert to List of Maps
     * Useful for test data with multiple test cases
     * 
     * Example JSON:
     * [
     *   {"username": "user1", "password": "pass1", "expectedResult": "PASS"},
     *   {"username": "user2", "password": "pass2", "expectedResult": "FAIL"}
     * ]
     * 
     * @param jsonFilePath Path to JSON file
     * @return List of Maps containing test data
     */
    @SuppressWarnings("unchecked")
    public static List<Map<String, Object>> readJsonArrayAsList(String jsonFilePath) {
        try {
            File jsonFile = new File(jsonFilePath);
            List<Map<String, Object>> jsonList = objectMapper.readValue(jsonFile, List.class);
            logger.info("JSON array read as List: {} items", jsonList.size());
            return jsonList;
            
        } catch (IOException e) {
            logger.error("Failed to read JSON array as List: {}", jsonFilePath, e);
            throw new RuntimeException("JSON file cannot be converted to List: " + jsonFilePath, e);
        }
    }
    
    /**
     * Read JSON and convert to specific Java object
     * 
     * @param jsonFilePath Path to JSON file
     * @param valueType Class type to convert to
     * @param <T> Generic type
     * @return Object of specified type
     */
    public static <T> T readJsonAsObject(String jsonFilePath, Class<T> valueType) {
        try {
            File jsonFile = new File(jsonFilePath);
            T object = objectMapper.readValue(jsonFile, valueType);
            logger.info("JSON file read as {}: {}", valueType.getSimpleName(), jsonFilePath);
            return object;
            
        } catch (IOException e) {
            logger.error("Failed to read JSON as {}: {}", valueType.getSimpleName(), jsonFilePath, e);
            throw new RuntimeException("JSON file cannot be converted to " + valueType.getSimpleName(), e);
        }
    }
    
    /**
     * Get test data from JSON array for TestNG DataProvider
     * 
     * JSON format:
     * [
     *   {"field1": "value1", "field2": "value2"},
     *   {"field1": "value3", "field2": "value4"}
     * ]
     * 
     * @param jsonFilePath Path to JSON file
     * @param fieldNames Field names to extract (in order)
     * @return 2D Object array for DataProvider
     */
    public static Object[][] getTestDataFromJsonArray(String jsonFilePath, String... fieldNames) {
        try {
            List<Map<String, Object>> jsonList = readJsonArrayAsList(jsonFilePath);
            
            Object[][] testData = new Object[jsonList.size()][fieldNames.length];
            
            for (int i = 0; i < jsonList.size(); i++) {
                Map<String, Object> row = jsonList.get(i);
                for (int j = 0; j < fieldNames.length; j++) {
                    testData[i][j] = row.get(fieldNames[j]);
                }
            }
            
            logger.info("Test data extracted from JSON: {} rows x {} columns", 
                       testData.length, fieldNames.length);
            return testData;
            
        } catch (Exception e) {
            logger.error("Failed to extract test data from JSON: {}", jsonFilePath, e);
            throw new RuntimeException("Cannot extract test data from JSON", e);
        }
    }
    
    /**
     * Get specific test case from JSON array by index
     * 
     * @param jsonFilePath Path to JSON file
     * @param index Index of test case (0-based)
     * @return Map containing test case data
     */
    public static Map<String, Object> getTestCaseByIndex(String jsonFilePath, int index) {
        List<Map<String, Object>> jsonList = readJsonArrayAsList(jsonFilePath);
        
        if (index < 0 || index >= jsonList.size()) {
            throw new IndexOutOfBoundsException("Invalid test case index: " + index);
        }
        
        Map<String, Object> testCase = jsonList.get(index);
        logger.info("Retrieved test case at index {}: {}", index, testCase);
        return testCase;
    }
    
    /**
     * Get test cases filtered by field value
     * 
     * Example: Get all test cases where "type" = "positive"
     * 
     * @param jsonFilePath Path to JSON file
     * @param fieldName Field name to filter on
     * @param fieldValue Value to match
     * @return List of matching test cases
     */
    public static List<Map<String, Object>> getFilteredTestCases(String jsonFilePath, 
                                                                   String fieldName, 
                                                                   Object fieldValue) {
        List<Map<String, Object>> jsonList = readJsonArrayAsList(jsonFilePath);
        List<Map<String, Object>> filteredList = new ArrayList<>();
        
        for (Map<String, Object> testCase : jsonList) {
            if (testCase.get(fieldName) != null && testCase.get(fieldName).equals(fieldValue)) {
                filteredList.add(testCase);
            }
        }
        
        logger.info("Filtered test cases: {} items (filter: {}={})", 
                   filteredList.size(), fieldName, fieldValue);
        return filteredList;
    }
    
    /**
     * Get value from nested JSON
     * 
     * Example JSON:
     * {
     *   "user": {
     *     "credentials": {
     *       "username": "testuser"
     *     }
     *   }
     * }
     * 
     * Usage: getNestedValue(json, "user", "credentials", "username")
     * 
     * @param jsonFilePath Path to JSON file
     * @param keys Nested keys path
     * @return Value at the nested path
     */
    public static Object getNestedValue(String jsonFilePath, String... keys) {
        JsonNode rootNode = readJsonFile(jsonFilePath);
        JsonNode currentNode = rootNode;
        
        for (String key : keys) {
            currentNode = currentNode.get(key);
            if (currentNode == null) {
                logger.warn("Key '{}' not found in JSON path", key);
                return null;
            }
        }
        
        // Convert JsonNode to appropriate type
        if (currentNode.isTextual()) {
            return currentNode.asText();
        } else if (currentNode.isInt()) {
            return currentNode.asInt();
        } else if (currentNode.isBoolean()) {
            return currentNode.asBoolean();
        } else if (currentNode.isDouble()) {
            return currentNode.asDouble();
        } else {
            return currentNode.toString();
        }
    }
    
    /**
     * Write Object to JSON file
     * 
     * @param object Object to write
     * @param jsonFilePath Path to save JSON file
     */
    public static void writeJsonFile(Object object, String jsonFilePath) {
        try {
            objectMapper.writerWithDefaultPrettyPrinter()
                       .writeValue(new File(jsonFilePath), object);
            logger.info("JSON file written successfully: {}", jsonFilePath);
            
        } catch (IOException e) {
            logger.error("Failed to write JSON file: {}", jsonFilePath, e);
            throw new RuntimeException("Cannot write JSON file: " + jsonFilePath, e);
        }
    }
    
    /**
     * Convert JSON string to Map
     * 
     * @param jsonString JSON string
     * @return Map containing JSON data
     */
    @SuppressWarnings("unchecked")
    public static Map<String, Object> jsonStringToMap(String jsonString) {
        try {
            return objectMapper.readValue(jsonString, Map.class);
        } catch (IOException e) {
            logger.error("Failed to parse JSON string", e);
            throw new RuntimeException("Invalid JSON string", e);
        }
    }
    
    /**
     * Convert Object to JSON string
     * 
     * @param object Object to convert
     * @return JSON string
     */
    public static String objectToJsonString(Object object) {
        try {
            return objectMapper.writeValueAsString(object);
        } catch (IOException e) {
            logger.error("Failed to convert object to JSON string", e);
            throw new RuntimeException("Cannot convert object to JSON", e);
        }
    }
}
