package com.mobilewright.steps;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.mobilewright.config.TestConfig;
import com.mobilewright.data.SauceUser;
import com.mobilewright.data.SauceUserData;
import com.mobilewright.driver.AndroidChromeDriverFactory;
import com.mobilewright.pages.SauceDemoInventoryPage;
import com.mobilewright.pages.SauceDemoLoginPage;
import com.mobilewright.reporting.ReportManager;
import io.appium.java_client.android.AndroidDriver;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class SauceDemoSteps {
    private AndroidDriver driver;
    private SauceDemoLoginPage loginPage;
    private SauceDemoInventoryPage inventoryPage;
    private ExtentTest report;
    private Scenario scenario;
    private SauceUser activeUser;
    private SauceDemoLoginPage.LoginOutcome loginOutcome;

    @Before
    public void beforeScenario(Scenario scenario) {
        this.scenario = scenario;
        ExtentReports extent = ReportManager.getReport();
        report = extent.createTest(scenario.getName());
        driver = AndroidChromeDriverFactory.create();
        loginPage = new SauceDemoLoginPage(driver);
        inventoryPage = new SauceDemoInventoryPage(driver);
    }

    @Given("the SauceDemo login page is opened on Android Chrome")
    public void openSauceDemoLoginPage() {
        report.info("Opening SauceDemo in Android Chrome: " + TestConfig.targetUrl());
        loginPage.open();
        report.info("Current URL: " + driver.getCurrentUrl());
        report.info("Page title: " + driver.getTitle());
    }

    @And("the login page is readable")
    public void loginPageIsReadable() {
        Assert.assertTrue(loginPage.isDisplayed(), "SauceDemo login page should be readable.");
        report.pass("Login page loaded and username field is visible.");
    }

    @When("I login with retry using sauce user data {string}")
    public void loginWithRetryUsingData(String userKey) {
        activeUser = SauceUserData.byKey(userKey);
        loginOutcome = null;

        for (int attempt = 1; attempt <= TestConfig.maxLoginRetries(); attempt++) {
            report.info("Login attempt " + attempt + " for " + activeUser.username());
            loginOutcome = loginPage.login(activeUser.username(), activeUser.password());

            if (activeUser.expectsSuccess() && loginOutcome.isSuccess() && inventoryPage.isLoaded()) {
                report.pass("Login succeeded for " + activeUser.username() + " on attempt " + attempt + ".");
                return;
            }

            if (!activeUser.expectsSuccess() && isExpectedError(loginOutcome)) {
                report.pass("Expected login validation displayed for " + activeUser.username()
                        + ": " + loginOutcome.getMessage());
                return;
            }

            report.log(Status.WARNING, "Attempt " + attempt + " did not meet the expected outcome: "
                    + (loginOutcome == null ? "No result" : loginOutcome.getMessage()));
            loginPage.open();
        }
    }

    @Then("the sauce user should reach the inventory page")
    public void userShouldReachInventoryPage() {
        Assert.assertNotNull(activeUser, "No active user data was loaded.");
        Assert.assertTrue(activeUser.expectsSuccess(), activeUser.username() + " should be a positive scenario.");
        Assert.assertNotNull(loginOutcome, "No login outcome was returned.");
        Assert.assertTrue(loginOutcome.isSuccess(), loginOutcome.getMessage());
        Assert.assertTrue(inventoryPage.isLoaded(), "Inventory page should be loaded.");
    }

    @And("the sauce user logs out successfully")
    public void userLogsOutSuccessfully() {
        inventoryPage.logout();
        Assert.assertTrue(loginPage.isDisplayed(), "Login page should display after logout.");
        report.pass("Logout completed for " + activeUser.username() + ".");
    }

    @Then("the sauce user should see the expected login error")
    public void userShouldSeeExpectedLoginError() {
        Assert.assertNotNull(activeUser, "No active user data was loaded.");
        Assert.assertFalse(activeUser.expectsSuccess(), activeUser.username() + " should be a negative scenario.");
        Assert.assertNotNull(loginOutcome, "No login outcome was returned.");
        Assert.assertFalse(loginOutcome.isSuccess(), "Negative scenario should not log in.");
        Assert.assertTrue(isExpectedError(loginOutcome),
                "Expected error to contain '" + activeUser.expectedMessage()
                        + "' but was '" + loginOutcome.getMessage() + "'.");
    }

    @And("a negative scenario screenshot is stored with the user data name")
    public void storeNegativeScenarioScreenshot() {
        File screenshot = captureScreenshot(dataFileName(activeUser));
        report.addScreenCaptureFromPath(relativeToOutput(screenshot));
        scenario.attach(readBytes(screenshot), "image/png", screenshot.getName());
    }

    @After
    public void afterScenario(Scenario scenario) {
        if (scenario.isFailed()) {
            File screenshot = captureScreenshot("failure-" + scenario.getName());
            if (screenshot.exists()) {
                report.addScreenCaptureFromPath(relativeToOutput(screenshot));
                scenario.attach(readBytes(screenshot), "image/png", screenshot.getName());
            }
            report.fail("Scenario failed: " + scenario.getName());
        } else {
            report.pass("Scenario passed: " + scenario.getName());
        }

        if (driver != null) {
            driver.quit();
        }
    }

    @AfterAll
    public static void afterAll() {
        ReportManager.flush();
    }

    private boolean isExpectedError(SauceDemoLoginPage.LoginOutcome outcome) {
        return outcome != null
                && !outcome.isSuccess()
                && outcome.getMessage().toLowerCase().contains(activeUser.expectedMessage().toLowerCase());
    }

    private File captureScreenshot(String name) {
        try {
            File screenshotDir = new File(TestConfig.outputDir(), "screenshots");
            if (!screenshotDir.exists()) {
                screenshotDir.mkdirs();
            }

            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            File destination = new File(screenshotDir, sanitize(name) + ".png");
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
            return destination;
        } catch (IOException | RuntimeException e) {
            report.warning("Screenshot capture failed: " + e.getMessage());
            return new File(TestConfig.outputDir(), "screenshots" + File.separator + sanitize(name) + ".png");
        }
    }

    private String dataFileName(SauceUser user) {
        return user.username() + "_" + user.password();
    }

    private String sanitize(String value) {
        return value.replaceAll("[^A-Za-z0-9._-]", "_");
    }

    private String relativeToOutput(File screenshot) {
        return "screenshots/" + screenshot.getName();
    }

    private byte[] readBytes(File file) {
        try {
            return Files.readAllBytes(file.toPath());
        } catch (IOException e) {
            return new byte[0];
        }
    }
}
