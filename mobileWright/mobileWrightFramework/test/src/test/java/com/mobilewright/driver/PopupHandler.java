package com.mobilewright.driver;

import io.appium.java_client.AppiumBy;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.Alert;
import org.openqa.selenium.NoAlertPresentException;
import org.openqa.selenium.NoSuchElementException;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebDriverException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.time.Duration;
import java.util.Arrays;
import java.util.List;

public final class PopupHandler {
    private static final List<String> POPUP_BUTTON_TEXT = Arrays.asList(
            "Allow", "While using the app", "Only this time", "OK", "Accept", "Agree",
            "Continue", "Got it", "No thanks", "Not now", "Dismiss", "Close"
    );

    private PopupHandler() {
    }

    public static void handleAny(AndroidDriver driver) {
        acceptWebAlert(driver);
        String originalContext = currentContext(driver);
        handleNativePopups(driver);
        if (originalContext != null) {
            try {
                driver.context(originalContext);
            } catch (WebDriverException ignored) {
                switchToWebContext(driver);
            }
        }
    }

    public static void switchToWebContext(AndroidDriver driver) {
        long end = System.currentTimeMillis() + Duration.ofSeconds(20).toMillis();
        while (System.currentTimeMillis() < end) {
            for (String context : driver.getContextHandles()) {
                if (context.toUpperCase().contains("CHROMIUM") || context.toUpperCase().contains("WEBVIEW")) {
                    driver.context(context);
                    return;
                }
            }
            sleep(500);
        }
    }

    private static void acceptWebAlert(AndroidDriver driver) {
        try {
            Alert alert = driver.switchTo().alert();
            alert.accept();
        } catch (WebDriverException ignored) {
        }
    }

    private static void handleNativePopups(AndroidDriver driver) {
        try {
            driver.context("NATIVE_APP");
        } catch (WebDriverException ignored) {
            return;
        }

        for (int pass = 0; pass < 4; pass++) {
            boolean clicked = false;
            for (String text : POPUP_BUTTON_TEXT) {
                clicked = clickIfPresent(driver, text);
                if (clicked) {
                    sleep(700);
                    break;
                }
            }
            if (!clicked) {
                break;
            }
        }
    }

    private static boolean clickIfPresent(AndroidDriver driver, String text) {
        try {
            WebElement element = new WebDriverWait(driver, Duration.ofSeconds(2))
                    .until(ExpectedConditions.elementToBeClickable(AppiumBy.androidUIAutomator(
                            "new UiSelector().text(\"" + text + "\")"
                    )));
            element.click();
            return true;
        } catch (WebDriverException ignored) {
            return false;
        }
    }

    private static String currentContext(AndroidDriver driver) {
        try {
            return driver.getContext();
        } catch (WebDriverException e) {
            return null;
        }
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
