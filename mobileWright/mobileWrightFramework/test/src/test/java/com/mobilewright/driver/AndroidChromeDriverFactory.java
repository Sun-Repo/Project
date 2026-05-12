package com.mobilewright.driver;

import com.mobilewright.config.TestConfig;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.options.UiAutomator2Options;

import java.net.MalformedURLException;
import java.net.URL;
import java.time.Duration;

public final class AndroidChromeDriverFactory {
    private AndroidChromeDriverFactory() {
    }

    public static AndroidDriver create() {
        UiAutomator2Options options = new UiAutomator2Options()
                .setPlatformName("Android")
                .setAutomationName("UiAutomator2")
                .setUdid(TestConfig.deviceUdid())
                .setNewCommandTimeout(Duration.ofSeconds(120));

        options.setCapability("browserName", "Chrome");
        if (!TestConfig.platformVersion().isEmpty()) {
            options.setPlatformVersion(TestConfig.platformVersion());
        }

        options.setCapability("appium:autoGrantPermissions", true);
        options.setCapability("appium:chromedriverAutodownload", true);
        options.setCapability("appium:nativeWebScreenshot", true);
        options.setCapability("appium:ensureWebviewsHavePages", true);

        try {
            AndroidDriver driver = new AndroidDriver(new URL(TestConfig.appiumUrl()), options);
            driver.manage().timeouts().implicitlyWait(Duration.ofSeconds(1));
            return driver;
        } catch (MalformedURLException e) {
            throw new IllegalArgumentException("Invalid Appium URL: " + TestConfig.appiumUrl(), e);
        }
    }
}
