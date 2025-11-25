package com.example.demo;

import org.junit.jupiter.api.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.chrome.ChromeOptions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.nio.file.*;
import java.time.Duration;

import static org.junit.jupiter.api.Assertions.assertTrue;

@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
public class LoginTest {

    private WebDriver driver;
    private WebDriverWait wait;
    private Path tempProfile;

   @BeforeAll
    void setUpAll() throws Exception {
    tempProfile = Files.createTempDirectory("chromium-profile-");

    ChromeOptions options = new ChromeOptions();
    options.addArguments(
            "--user-data-dir=" + tempProfile,
            // "--headless=new",   //para ver a janela
            "--no-sandbox",
            "--disable-dev-shm-usage",
            "--window-size=1200,900"
    );
    driver = new ChromeDriver(options);
    wait   = new WebDriverWait(driver, Duration.ofSeconds(5));

    String pagePath = Paths.get("src/main/resources/static/index.html")
            .toAbsolutePath().toString().replace("\\", "/");
    String url = "file:///" + pagePath;
    driver.get(url);
}


    @AfterAll
    void tearDownAll() throws Exception {
        if (driver != null) driver.quit();
        // Limpa o perfil temporário
        if (tempProfile != null) {
            try (var walk = Files.walk(tempProfile)) {
                walk.sorted((a, b) -> b.compareTo(a))
                        .forEach(p -> { try { Files.deleteIfExists(p); } catch (Exception ignored) {} });
            }
        }
    }

    private WebElement el(By locator) {
        return wait.until(ExpectedConditions.visibilityOfElementLocated(locator));
    }

    private void doLogin(String user, String pass) {
        WebElement username = el(By.id("username"));
        WebElement password = el(By.id("password"));
        WebElement button   = el(By.tagName("button"));

        username.clear();
        password.clear();
        if (user != null) username.sendKeys(user);
        if (pass != null) password.sendKeys(pass);
        button.click();
    }

    private void snap(String name) throws Exception {
        byte[] bytes = ((TakesScreenshot) driver).getScreenshotAs(OutputType.BYTES);
        Path out = Paths.get("target", "screenshots", name + ".png");
        Files.createDirectories(out.getParent());
        Files.write(out, bytes);
    }

    @Test @Order(1)
    void testLoginSuccess() throws Exception {
        doLogin("admin", "1234");
        WebElement message = el(By.id("message"));
        assertTrue(message.getText().contains("Login bem-sucedido!"));
        snap("login_success");
    }

    @Test @Order(2)
    void testLoginFailure() throws Exception {
        doLogin("user", "wrong");
        WebElement message = el(By.id("message"));
        assertTrue(message.getText().contains("Usuário ou senha inválidos."));
        snap("login_failure");
    }

    @Test @Order(3)
    void testWrongPasswordOnly() throws Exception {
        doLogin("admin", "9999");
        WebElement message = el(By.id("message"));
        assertTrue(message.getText().contains("Usuário ou senha inválidos."));
        snap("wrong_password_only");
    }

    @Test @Order(4)
    void testEmptyFields() throws Exception {
        doLogin(null, null);
        WebElement message = el(By.id("message"));
        assertTrue(message.getText().contains("Usuário ou senha inválidos."));
        snap("empty_fields");
    }

    @Test @Order(5)
    void testLongInputs() throws Exception {
        doLogin("a".repeat(50), "b".repeat(50));
        WebElement message = el(By.id("message"));
        assertTrue(message.getText().contains("Usuário ou senha inválidos."));
        snap("long_inputs");
    }
}
