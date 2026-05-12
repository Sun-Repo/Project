package com.mobilewright.pages;

import com.mobilewright.config.TestConfig;
import com.mobilewright.driver.PopupHandler;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

public class SauceDemoLoginPage {
    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By username = By.id("user-name");
    private final By password = By.id("password");
    private final By loginButton = By.id("login-button");
    private final By errorMessage = By.cssSelector("[data-test='error']");

    public SauceDemoLoginPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TestConfig.defaultTimeout());
    }

    public void open() {
        driver.get(TestConfig.targetUrl());
        PopupHandler.handleAny(driver);
        PopupHandler.switchToWebContext(driver);
        wait.until(ExpectedConditions.visibilityOfElementLocated(username));
    }

    public LoginOutcome login(String user, String secret) {
        clearAndType(username, user);
        clearAndType(password, secret);
        wait.until(ExpectedConditions.elementToBeClickable(loginButton)).click();
        PopupHandler.handleAny(driver);
        return waitForOutcome();
    }

    public boolean isDisplayed() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(username)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    private LoginOutcome waitForOutcome() {
        long end = System.currentTimeMillis() + TestConfig.defaultTimeout().toMillis();
        while (System.currentTimeMillis() < end) {
            if (driver.getCurrentUrl().contains("inventory.html") || isPresent(By.id("react-burger-menu-btn"))) {
                return LoginOutcome.success();
            }
            if (isPresent(errorMessage)) {
                return LoginOutcome.failure(driver.findElement(errorMessage).getText());
            }
            sleep(500);
        }
        return LoginOutcome.failure("Timed out waiting for SauceDemo login result.");
    }

    private boolean isPresent(By locator) {
        try {
            return !driver.findElements(locator).isEmpty();
        } catch (WebDriverException e) {
            return false;
        }
    }

    private void clearAndType(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(value);
    }

    private void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    public static final class LoginOutcome {
        private final boolean success;
        private final String message;

        private LoginOutcome(boolean success, String message) {
            this.success = success;
            this.message = message;
        }

        public static LoginOutcome success() {
            return new LoginOutcome(true, "Login succeeded.");
        }

        public static LoginOutcome failure(String message) {
            return new LoginOutcome(false, message);
        }

        public boolean isSuccess() {
            return success;
        }

        public String getMessage() {
            return message;
        }
    }
}
