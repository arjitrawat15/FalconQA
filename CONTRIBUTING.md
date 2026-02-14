# 🤝 Contributing to FalconQA

Thank you for your interest in contributing to FalconQA! This guide will help you extend and enhance the framework.

---

## 📋 Table of Contents
1. [Getting Started](#getting-started)
2. [Adding New Tests](#adding-new-tests)
3. [Adding New Page Objects](#adding-new-page-objects)
4. [Adding Utilities](#adding-utilities)
5. [Adding Test Data](#adding-test-data)
6. [Code Standards](#code-standards)
7. [Testing Your Changes](#testing-your-changes)
8. [Pull Request Process](#pull-request-process)

---

## Getting Started

### Prerequisites:
- Java 11 or higher
- Maven 3.8+
- IDE (IntelliJ IDEA recommended)
- Git
- Basic Selenium knowledge

### Setup:
```bash
# Clone repository
git clone https://github.com/your-username/FalconQA.git
cd FalconQA

# Install dependencies
mvn clean install -DskipTests

# Run tests to verify setup
mvn clean test -DsuiteXmlFile=src/test/resources/smoke-tests.xml
```

---

## Adding New Tests

### Step 1: Identify Test Scenarios
```
Example: Testing user registration flow
Scenarios:
1. Valid registration
2. Duplicate email validation
3. Password strength validation
4. Terms & conditions checkbox
```

### Step 2: Create Test Class
```java
package com.falconqa.tests;

import com.falconqa.core.BaseTest;
import com.falconqa.pages.RegistrationPage;
import com.falconqa.utils.ExtentReportManager;
import org.testng.Assert;
import org.testng.annotations.Test;

/**
 * RegistrationTests - Test cases for user registration
 * 
 * @author Your Name
 * @version 1.0
 */
public class RegistrationTests extends BaseTest {
    
    @Test(priority = 1, description = "Verify successful registration with valid data")
    public void testValidRegistration() {
        ExtentReportManager.logInfo("Test: Valid User Registration");
        
        RegistrationPage registrationPage = new RegistrationPage(driver);
        
        // Perform registration
        registrationPage.enterEmail("test@example.com");
        registrationPage.enterPassword("SecurePass123!");
        registrationPage.acceptTerms();
        registrationPage.clickRegister();
        
        // Verify success
        Assert.assertTrue(registrationPage.isRegistrationSuccessful(),
            "Registration should be successful with valid data");
        
        ExtentReportManager.logPass("Registration completed successfully");
    }
    
    @Test(priority = 2, description = "Verify duplicate email validation")
    public void testDuplicateEmail() {
        ExtentReportManager.logInfo("Test: Duplicate Email Validation");
        
        RegistrationPage registrationPage = new RegistrationPage(driver);
        
        // Attempt registration with existing email
        registrationPage.enterEmail("existing@example.com");
        registrationPage.enterPassword("SecurePass123!");
        registrationPage.acceptTerms();
        registrationPage.clickRegister();
        
        // Verify error
        Assert.assertTrue(registrationPage.isErrorDisplayed(),
            "Error should be displayed for duplicate email");
        
        String errorMessage = registrationPage.getErrorMessage();
        Assert.assertTrue(errorMessage.contains("already exists"),
            "Error should mention email already exists");
        
        ExtentReportManager.logPass("Duplicate email validation working correctly");
    }
}
```

### Step 3: Add Test to Suite
```xml
<!-- testng.xml -->
<test name="Registration Tests">
    <classes>
        <class name="com.falconqa.tests.RegistrationTests"/>
    </classes>
</test>
```

### Step 4: Run and Verify
```bash
mvn clean test -Dtest=RegistrationTests
```

---

## Adding New Page Objects

### Step 1: Analyze the Page
```
Example: Registration Page
Elements:
- Email field (id: email)
- Password field (id: password)
- Confirm password (id: confirmPassword)
- Terms checkbox (id: terms)
- Register button (id: registerBtn)
- Success message (class: success-msg)
- Error message (class: error-msg)
```

### Step 2: Create Page Object Class
```java
package com.falconqa.pages;

import com.falconqa.core.BasePage;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;

/**
 * RegistrationPage - Page Object for Registration functionality
 * 
 * @author Your Name
 * @version 1.0
 */
public class RegistrationPage extends BasePage {
    
    // Locators
    private final By emailField = By.id("email");
    private final By passwordField = By.id("password");
    private final By confirmPasswordField = By.id("confirmPassword");
    private final By termsCheckbox = By.id("terms");
    private final By registerButton = By.id("registerBtn");
    private final By successMessage = By.className("success-msg");
    private final By errorMessage = By.className("error-msg");
    
    /**
     * Constructor
     * 
     * @param driver WebDriver instance
     */
    public RegistrationPage(WebDriver driver) {
        super(driver);
    }
    
    /**
     * Enter email address
     * 
     * @param email Email address
     * @return RegistrationPage for method chaining
     */
    public RegistrationPage enterEmail(String email) {
        type(emailField, email);
        logger.info("Entered email: {}", email);
        return this;
    }
    
    /**
     * Enter password
     * 
     * @param password Password
     * @return RegistrationPage for method chaining
     */
    public RegistrationPage enterPassword(String password) {
        type(passwordField, password);
        logger.info("Entered password");
        return this;
    }
    
    /**
     * Enter confirm password
     * 
     * @param password Confirm password
     * @return RegistrationPage for method chaining
     */
    public RegistrationPage enterConfirmPassword(String password) {
        type(confirmPasswordField, password);
        logger.info("Entered confirm password");
        return this;
    }
    
    /**
     * Accept terms and conditions
     * 
     * @return RegistrationPage for method chaining
     */
    public RegistrationPage acceptTerms() {
        click(termsCheckbox);
        logger.info("Accepted terms and conditions");
        return this;
    }
    
    /**
     * Click register button
     */
    public void clickRegister() {
        click(registerButton);
        logger.info("Clicked register button");
    }
    
    /**
     * Check if registration was successful
     * 
     * @return true if success message displayed, false otherwise
     */
    public boolean isRegistrationSuccessful() {
        return isDisplayed(successMessage);
    }
    
    /**
     * Get success message text
     * 
     * @return Success message
     */
    public String getSuccessMessage() {
        return getText(successMessage);
    }
    
    /**
     * Check if error message is displayed
     * 
     * @return true if error displayed, false otherwise
     */
    public boolean isErrorDisplayed() {
        return isDisplayed(errorMessage);
    }
    
    /**
     * Get error message text
     * 
     * @return Error message
     */
    public String getErrorMessage() {
        return getText(errorMessage);
    }
    
    /**
     * Perform complete registration
     * 
     * @param email Email address
     * @param password Password
     */
    public void register(String email, String password) {
        enterEmail(email);
        enterPassword(password);
        enterConfirmPassword(password);
        acceptTerms();
        clickRegister();
    }
}
```

### Best Practices for Page Objects:
1. ✅ Extend `BasePage` to inherit common methods
2. ✅ Make locators `private final`
3. ✅ Use descriptive method names (verb + noun)
4. ✅ Return `this` for method chaining
5. ✅ Return next page object when navigating
6. ✅ Add JavaDoc comments for all public methods
7. ✅ Log actions for debugging
8. ✅ Use logger, not System.out.println

---

## Adding Utilities

### Example: Adding Email Utility
```java
package com.falconqa.utils;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import javax.mail.*;
import javax.mail.internet.*;
import java.util.Properties;

/**
 * EmailUtils - Utility for sending email notifications
 * 
 * @author Your Name
 * @version 1.0
 */
public class EmailUtils {
    
    private static final Logger logger = LogManager.getLogger(EmailUtils.class);
    
    /**
     * Send email with test results
     * 
     * @param to Recipient email
     * @param subject Email subject
     * @param body Email body
     * @return true if sent successfully, false otherwise
     */
    public static boolean sendEmail(String to, String subject, String body) {
        try {
            // Configure email properties
            Properties props = new Properties();
            props.put("mail.smtp.host", "smtp.gmail.com");
            props.put("mail.smtp.port", "587");
            props.put("mail.smtp.auth", "true");
            props.put("mail.smtp.starttls.enable", "true");
            
            // Create session
            Session session = Session.getInstance(props, new Authenticator() {
                @Override
                protected PasswordAuthentication getPasswordAuthentication() {
                    return new PasswordAuthentication("your-email@gmail.com", "your-password");
                }
            });
            
            // Create message
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress("your-email@gmail.com"));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(to));
            message.setSubject(subject);
            message.setText(body);
            
            // Send
            Transport.send(message);
            
            logger.info("Email sent successfully to: {}", to);
            return true;
            
        } catch (MessagingException e) {
            logger.error("Failed to send email", e);
            return false;
        }
    }
}
```

---

## Adding Test Data

### For Excel:
```
1. Open: src/test/resources/testdata/testdata.xlsx
2. Go to existing sheet or create new sheet
3. Add header row: TestCaseID | Field1 | Field2 | ExpectedResult
4. Add data rows
5. Save file
```

### For JSON:
```
1. Create file: src/test/resources/testdata/my-data.json
2. Add structure:
```
```json
[
  {
    "testCaseId": "TC_001",
    "field1": "value1",
    "field2": "value2",
    "expectedResult": "PASS"
  }
]
```
```
3. Save file
4. Use in test with JsonUtils
```

---

## Code Standards

### Naming Conventions:
```java
// Classes: PascalCase
public class LoginPage { }
public class ExcelUtils { }

// Methods: camelCase (verb + noun)
public void clickLoginButton() { }
public String getUserName() { }

// Variables: camelCase
private String userName;
private int retryCount;

// Constants: UPPER_SNAKE_CASE
private static final String BASE_URL = "https://...";
private static final int MAX_RETRY = 3;

// Test methods: test + description
@Test
public void testValidLogin() { }
```

### Code Structure:
```java
public class MyClass {
    // 1. Static variables
    private static final Logger logger = ...;
    
    // 2. Instance variables
    private WebDriver driver;
    private String username;
    
    // 3. Constructor
    public MyClass(WebDriver driver) {
        this.driver = driver;
    }
    
    // 4. Public methods
    public void doSomething() { }
    
    // 5. Protected methods
    protected void helperMethod() { }
    
    // 6. Private methods
    private void privateHelper() { }
}
```

### JavaDoc:
```java
/**
 * Brief description of what the method does
 * 
 * @param param1 Description of parameter
 * @param param2 Description of parameter
 * @return Description of return value
 * @throws ExceptionType When this exception is thrown
 */
public String myMethod(String param1, int param2) throws ExceptionType {
    // Implementation
}
```

### Logging:
```java
// Use logger levels appropriately
logger.debug("Detailed debugging info");
logger.info("General information");
logger.warn("Warning - potential issue");
logger.error("Error occurred", exception);
```

---

## Testing Your Changes

### 1. Unit Test Your Code
```java
// If adding utility, create unit test
@Test
public void testExcelUtilsReadData() {
    ExcelUtils excel = new ExcelUtils("test.xlsx", "Sheet1");
    String data = excel.getCellData(1, "Username");
    Assert.assertNotNull(data);
    excel.close();
}
```

### 2. Run Affected Tests
```bash
# Run tests that use your changes
mvn clean test -Dtest=LoginTests
```

### 3. Run Full Suite
```bash
# Ensure no regression
mvn clean test
```

### 4. Check Reports
```bash
# Verify all tests passed
open test-output/reports/ExtentReport_*.html
```

---

## Pull Request Process

### 1. Create Branch
```bash
git checkout -b feature/add-registration-tests
```

### 2. Make Changes
```bash
# Add your changes
# Follow code standards
# Add tests
# Update documentation
```

### 3. Commit Changes
```bash
git add .
git commit -m "Add registration tests with page object

- Added RegistrationPage with all locators and actions
- Added RegistrationTests with 5 test scenarios
- Updated testng.xml to include new tests
- Added test data to testdata.xlsx"
```

### 4. Push to Remote
```bash
git push origin feature/add-registration-tests
```

### 5. Create Pull Request
```
1. Go to GitHub repository
2. Click "New Pull Request"
3. Select your branch
4. Fill in description:
   - What changes were made
   - Why these changes are needed
   - How to test the changes
5. Submit for review
```

### 6. Address Review Comments
```bash
# Make requested changes
git add .
git commit -m "Address review comments"
git push origin feature/add-registration-tests
```

---

## Checklist Before Submitting

- [ ] Code follows project style and conventions
- [ ] All tests pass locally
- [ ] New tests added for new functionality
- [ ] JavaDoc added for public methods
- [ ] No System.out.println (use logger)
- [ ] No hardcoded values (use config.properties)
- [ ] No commented-out code
- [ ] Meaningful commit messages
- [ ] README updated if needed
- [ ] TROUBLESHOOTING updated if adding common issue

---

## Questions?

- Check existing documentation (README, ARCHITECTURE, TROUBLESHOOTING)
- Look at existing code for examples
- Ask in GitHub Discussions
- Create an issue if you found a bug

---

## Thank You!

Your contributions help make FalconQA better for everyone! 🎉
