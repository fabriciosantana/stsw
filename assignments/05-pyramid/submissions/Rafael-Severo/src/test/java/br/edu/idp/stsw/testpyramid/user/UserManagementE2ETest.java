package br.edu.idp.stsw.testpyramid.user;

import io.github.bonigarcia.wdm.WebDriverManager;
import org.junit.jupiter.api.*;
import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.TestPropertySource;

import java.time.Duration;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@Disabled("Chrome not available in this environment")
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@TestPropertySource(properties = {
        "spring.jpa.hibernate.ddl-auto=create-drop",
        "spring.datasource.url=jdbc:h2:mem:testdb"
})
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@DisplayName("E2E Tests - User Management Web Interface")
class UserManagementE2ETest {

    private static WebDriver driver;
    private static WebDriverWait wait;

    @LocalServerPort
    private int port;

    private String baseUrl;

    @BeforeAll
    static void setupWebDriver() {
        WebDriverManager.chromedriver().setup();
        ChromeOptions options = new ChromeOptions();
        options.addArguments("--headless");
        options.addArguments("--no-sandbox");
        options.addArguments("--disable-dev-shm-usage");
        options.addArguments("--disable-gpu");
        options.addArguments("--window-size=1920,1080");

        driver = new ChromeDriver(options);
        wait = new WebDriverWait(driver, Duration.ofSeconds(10));
    }

    @AfterAll
    static void tearDownWebDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @BeforeEach
    void setup() {
        baseUrl = "http://localhost:" + port;
    }

    // ========== E2E TESTS - TOP OF THE PYRAMID ==========

    @Test
    @Order(1)
    @DisplayName("Should load user management page")
    void shouldLoadUserManagementPage() {
        // Given
        driver.get(baseUrl + "/users.html");

        // When & Then
        assertEquals("User Management", driver.getTitle());
        assertTrue(driver.getPageSource().contains("User Management System"));
    }

    @Test
    @Order(2)
    @DisplayName("Should create new user through web interface")
    void shouldCreateNewUserThroughWebInterface() {
        // Given
        driver.get(baseUrl + "/users.html");

        // Fill form
        WebElement nameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("userName")));
        WebElement emailField = driver.findElement(By.id("userEmail"));
        WebElement passwordField = driver.findElement(By.id("userPassword"));
        WebElement submitButton = driver.findElement(By.id("createUserBtn"));

        nameField.clear();
        nameField.sendKeys("E2E Test User");

        emailField.clear();
        emailField.sendKeys("e2e@example.com");

        passwordField.clear();
        passwordField.sendKeys("password123");

        // When
        submitButton.click();

        // Then
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "User created successfully"));
        WebElement message = driver.findElement(By.id("message"));
        assertTrue(message.getText().contains("User created successfully"));
    }

    @Test
    @Order(3)
    @DisplayName("Should list users in the interface")
    void shouldListUsersInTheInterface() {
        // Given
        driver.get(baseUrl + "/users.html");

        // When
        WebElement refreshButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("refreshUsersBtn")));
        refreshButton.click();

        // Then
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#usersTable tbody tr")));
        List<WebElement> userRows = driver.findElements(By.cssSelector("#usersTable tbody tr"));

        assertFalse(userRows.isEmpty(), "Should have at least one user in the list");

        // Check if our created user is in the list
        boolean foundUser = userRows.stream()
                .anyMatch(row -> row.getText().contains("E2E Test User") && row.getText().contains("e2e@example.com"));

        assertTrue(foundUser, "Created user should be visible in the users list");
    }

    @Test
    @Order(4)
    @DisplayName("Should validate form inputs")
    void shouldValidateFormInputs() {
        // Given
        driver.get(baseUrl + "/users.html");

        // Test empty name
        WebElement nameField = driver.findElement(By.id("userName"));
        WebElement emailField = driver.findElement(By.id("userEmail"));
        WebElement passwordField = driver.findElement(By.id("userPassword"));
        WebElement submitButton = driver.findElement(By.id("createUserBtn"));

        nameField.clear();
        emailField.clear();
        emailField.sendKeys("test@example.com");
        passwordField.clear();
        passwordField.sendKeys("password123");

        // When
        submitButton.click();

        // Then
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "Name is required"));
        WebElement message = driver.findElement(By.id("message"));
        assertTrue(message.getText().contains("Name is required"));
    }

    @Test
    @Order(5)
    @DisplayName("Should handle duplicate email error")
    void shouldHandleDuplicateEmailError() {
        // Given
        driver.get(baseUrl + "/users.html");

        // Try to create user with existing email
        WebElement nameField = driver.findElement(By.id("userName"));
        WebElement emailField = driver.findElement(By.id("userEmail"));
        WebElement passwordField = driver.findElement(By.id("userPassword"));
        WebElement submitButton = driver.findElement(By.id("createUserBtn"));

        nameField.clear();
        nameField.sendKeys("Another User");

        emailField.clear();
        emailField.sendKeys("e2e@example.com"); // Same email as before

        passwordField.clear();
        passwordField.sendKeys("password456");

        // When
        submitButton.click();

        // Then
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "Email already exists"));
        WebElement message = driver.findElement(By.id("message"));
        assertTrue(message.getText().contains("Email already exists"));
    }

    @Test
    @Order(6)
    @DisplayName("Should update user information")
    void shouldUpdateUserInformation() {
        // Given
        driver.get(baseUrl + "/users.html");

        // Wait for users to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#usersTable tbody tr")));

        // Find the edit button for our test user
        List<WebElement> editButtons = driver.findElements(By.cssSelector(".edit-btn"));
        assertFalse(editButtons.isEmpty(), "Should have at least one edit button");

        // Click edit on the first user (assuming it's our test user)
        editButtons.get(0).click();

        // Fill update form
        WebElement updateNameField = wait.until(ExpectedConditions.elementToBeClickable(By.id("updateUserName")));
        WebElement updateEmailField = driver.findElement(By.id("updateUserEmail"));
        WebElement updateButton = driver.findElement(By.id("updateUserBtn"));

        updateNameField.clear();
        updateNameField.sendKeys("Updated E2E Test User");

        updateEmailField.clear();
        updateEmailField.sendKeys("updated-e2e@example.com");

        // When
        updateButton.click();

        // Then
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "User updated successfully"));
        WebElement message = driver.findElement(By.id("message"));
        assertTrue(message.getText().contains("User updated successfully"));
    }

    @Test
    @Order(7)
    @DisplayName("Should delete user")
    void shouldDeleteUser() {
        // Given
        driver.get(baseUrl + "/users.html");

        // Wait for users to load
        wait.until(ExpectedConditions.presenceOfElementLocated(By.cssSelector("#usersTable tbody tr")));

        // Get initial count
        List<WebElement> initialRows = driver.findElements(By.cssSelector("#usersTable tbody tr"));
        int initialCount = initialRows.size();

        // Find and click delete button
        List<WebElement> deleteButtons = driver.findElements(By.cssSelector(".delete-btn"));
        assertFalse(deleteButtons.isEmpty(), "Should have at least one delete button");

        // When
        deleteButtons.get(0).click();

        // Confirm deletion (assuming there's a confirmation dialog)
        try {
            WebElement confirmButton = wait.until(ExpectedConditions.elementToBeClickable(By.id("confirmDeleteBtn")));
            confirmButton.click();
        } catch (Exception e) {
            // If no confirmation dialog, continue
        }

        // Then
        wait.until(ExpectedConditions.textToBePresentInElementLocated(By.id("message"), "User deleted successfully"));
        WebElement message = driver.findElement(By.id("message"));
        assertTrue(message.getText().contains("User deleted successfully"));

        // Verify user was removed from list
        WebElement refreshButton = driver.findElement(By.id("refreshUsersBtn"));
        refreshButton.click();

        wait.until(ExpectedConditions.or(
                ExpectedConditions.textToBePresentInElementLocated(By.id("usersTable"), "No users found"),
                ExpectedConditions.numberOfElementsToBeLessThan(By.cssSelector("#usersTable tbody tr"), initialCount)
        ));

        List<WebElement> finalRows = driver.findElements(By.cssSelector("#usersTable tbody tr"));
        assertTrue(finalRows.size() < initialCount, "User count should decrease after deletion");
    }
}
