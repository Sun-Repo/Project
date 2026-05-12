package com.mobilewright.tests;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.mobilewright.config.TestConfig;
import com.mobilewright.driver.AndroidChromeDriverFactory;
import com.mobilewright.pages.SauceDemoInventoryPage;
import com.mobilewright.pages.SauceDemoLoginPage;
import com.mobilewright.reporting.ReportManager;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.AfterSuite;
import org.testng.annotations.BeforeMethod;
import org.testng.annotations.DataProvider;
import org.testng.annotations.Test;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SauceDemoAndroidChromeTest {
    private AndroidDriver driver;
    private ExtentReports extent;
    private ExtentTest report;

    @BeforeMethod(alwaysRun = true)
    public void createDriver() {
        extent = ReportManager.getReport();
        driver = AndroidChromeDriverFactory.create();
    }

    @DataProvider(name = "sauceUsers")
    public Object[][] sauceUsers() {
        return new Object[][]{
                {"standard_user", true},
                {"locked_out_user", false},
                {"problem_user", true},
                {"performance_glitch_user", true},
                {"error_user", true},
                {"visual_user", true}
        };
    }

    @Test(dataProvider = "sauceUsers")
    public void loginAndLogoutOnAndroidChrome(String userName, boolean shouldLogin) {
        report = extent.createTest("SauceDemo Android Chrome - " + userName);
        SauceDemoLoginPage loginPage = new SauceDemoLoginPage(driver);
        SauceDemoInventoryPage inventoryPage = new SauceDemoInventoryPage(driver);

        report.info("Opening SauceDemo in Android Chrome: " + TestConfig.targetUrl());
        loginPage.open();
        Assert.assertTrue(loginPage.isDisplayed(), "SauceDemo login page should be readable.");
        report.pass("Login page loaded and username field is visible.");

        SauceDemoLoginPage.LoginOutcome outcome = null;
        for (int attempt = 1; attempt <= TestConfig.maxLoginRetries(); attempt++) {
            report.info("Login attempt " + attempt + " for " + userName);
            outcome = loginPage.login(userName, "secret_sauce");

            if (shouldLogin && outcome.isSuccess() && inventoryPage.isLoaded()) {
                report.pass("Login succeeded for " + userName + " on attempt " + attempt + ".");
                inventoryPage.logout();
                Assert.assertTrue(loginPage.isDisplayed(), "Login page should display after logout.");
                report.pass("Logout completed for " + userName + ".");
                attachScreenshot("logout-" + userName);
                return;
            }

            if (!shouldLogin && !outcome.isSuccess()
                    && outcome.getMessage().toLowerCase().contains("locked out")) {
                report.pass("Locked out validation displayed as expected: " + outcome.getMessage());
                attachScreenshot("locked-out-" + userName);
                return;
            }

            report.log(Status.WARNING, "Attempt " + attempt + " did not meet the expected outcome: "
                    + (outcome == null ? "No result" : outcome.getMessage()));
            loginPage.open();
        }

        attachScreenshot("failure-" + userName);
        String message = outcome == null ? "No login outcome was returned." : outcome.getMessage();
        Assert.fail("Use case did not pass for " + userName + " after "
                + TestConfig.maxLoginRetries() + " attempts. Last result: " + message);
    }

    @AfterMethod(alwaysRun = true)
    public void closeDriver() {
        if (driver != null) {
            driver.quit();
        }
    }

    @AfterSuite(alwaysRun = true)
    public void flushReport() {
        ReportManager.flush();
    }

    private void attachScreenshot(String name) {
        try {
            File screenshotDir = new File(TestConfig.outputDir(), "screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(screenshotDir, name + ".png");
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            report.addScreenCaptureFromPath("screenshots/" + destination.getName());
        } catch (IOException | RuntimeException e) {
            report.warning("Screenshot capture failed: " + e.getMessage());
        }
    }
}
