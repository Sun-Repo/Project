package com.mobilewright.steps;

import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.ExtentTest;
import com.aventstack.extentreports.Status;
import com.mobilewright.config.TestConfig;
import com.mobilewright.data.CheckoutCustomer;
import com.mobilewright.data.CheckoutCustomerData;
import com.mobilewright.data.SauceUser;
import com.mobilewright.data.SauceUserData;
import com.mobilewright.driver.AndroidChromeDriverFactory;
import com.mobilewright.pages.SauceDemoInventoryPage;
import com.mobilewright.pages.SauceDemoLoginPage;
import com.mobilewright.reporting.ReportManager;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.android.AndroidStartScreenRecordingOptions;
import io.cucumber.java.After;
import io.cucumber.java.AfterAll;
import io.cucumber.java.Before;
import io.cucumber.java.Scenario;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import io.qameta.allure.Allure;
import org.openqa.selenium.OutputType;
import org.openqa.selenium.TakesScreenshot;
import org.testng.Assert;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.util.List;

public class SauceDemoSteps {
    private AndroidDriver driver;
    private SauceDemoLoginPage loginPage;
    private SauceDemoInventoryPage inventoryPage;
    private ExtentTest report;
    private Scenario scenario;
    private SauceUser activeUser;
    private SauceDemoLoginPage.LoginOutcome loginOutcome;
    private List<SauceDemoInventoryPage.Product> selectedProducts;
    private boolean screenRecordingStarted;

    private static final DateTimeFormatter ARTIFACT_TIMESTAMP =
            DateTimeFormatter.ofPattern("yyyyMMdd-HHmmss-SSS");

    @Before
    public void beforeScenario(Scenario scenario) {
        this.scenario = scenario;
        ExtentReports extent = ReportManager.getReport();
        report = extent.createTest(scenario.getName());
        driver = AndroidChromeDriverFactory.create();
        loginPage = new SauceDemoLoginPage(driver);
        inventoryPage = new SauceDemoInventoryPage(driver);
        startScenarioRecording();
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

    @When("I add every {int}th product to the cart")
    public void addEveryNthProductToCart(int interval) {
        selectedProducts = inventoryPage.addEveryNthProductToCart(interval);
        Assert.assertFalse(selectedProducts.isEmpty(), "At least one product should be added to the cart.");
        report.pass("Added " + selectedProducts.size() + " product(s) using every " + interval + "th product rule.");
    }

    @When("I add all products to the cart")
    public void addAllProductsToCart() {
        selectedProducts = inventoryPage.addEveryNthProductToCart(1);
        Assert.assertFalse(selectedProducts.isEmpty(), "At least one product should be added to the cart.");
        report.pass("Added all visible products to the cart. Product count: " + selectedProducts.size() + ".");
    }

    @When("I open checkout from the cart without adding products")
    public void openCheckoutFromEmptyCart() {
        selectedProducts = java.util.Collections.emptyList();
        inventoryPage.openCheckoutInformation();
        report.pass("Opened checkout information from an empty cart.");
    }

    @And("I open the checkout information page")
    public void openCheckoutInformationPage() {
        inventoryPage.openCheckoutInformation();
        report.pass("Checkout information page opened.");
    }

    @And("I checkout using customer data {string}")
    public void checkoutUsingCustomerData(String customerKey) {
        CheckoutCustomer customer = CheckoutCustomerData.byKey(customerKey);
        inventoryPage.checkoutWith(customer);
        report.pass("Checkout information entered for " + customer.firstName() + " " + customer.lastName() + ".");
    }

    @And("I submit checkout information using customer data {string}")
    public void submitCheckoutInformationUsingCustomerData(String customerKey) {
        CheckoutCustomer customer = CheckoutCustomerData.byKey(customerKey);
        inventoryPage.submitCheckoutInformation(customer);
        report.info("Submitted checkout information for data key: " + customer.key() + ".");
    }

    @Then("the checkout overview should show the selected products and valid payment shipping and price totals")
    public void checkoutOverviewShouldShowProductsAndSummary() {
        Assert.assertNotNull(selectedProducts, "No selected products were stored before checkout.");
        Assert.assertTrue(inventoryPage.overviewHasProducts(selectedProducts),
                "Checkout overview should show every selected product with its price.");
        Assert.assertTrue(inventoryPage.hasPaymentShippingAndPriceSummary(),
                "Checkout overview should show payment, shipping, subtotal, tax, and total information.");
        Assert.assertTrue(inventoryPage.priceTotalMatches(selectedProducts),
                "Checkout subtotal, tax, and total should match the selected product prices.");
        report.pass("Checkout overview product, payment, shipping, and price total validations passed.");
    }

    @Then("the checkout overview should show no products and zero price totals")
    public void checkoutOverviewShouldShowNoProductsAndZeroTotals() {
        Assert.assertTrue(inventoryPage.overviewHasNoProductsAndZeroTotals(),
                "Empty cart checkout overview should show no products and zero subtotal, tax, and total.");
        report.pass("Empty cart checkout overview showed no products and zero totals.");
    }

    @Then("the checkout information page should show required field error {string}")
    public void checkoutInformationShouldShowRequiredFieldError(String expectedError) {
        Assert.assertTrue(inventoryPage.checkoutErrorContains(expectedError),
                "Checkout information error should contain: " + expectedError);
        report.pass("Checkout information validation displayed: " + expectedError + ".");
    }

    @And("a checkout failure screenshot is stored as {string}")
    public void checkoutFailureScreenshotIsStoredAs(String screenshotName) {
        File screenshot = captureScreenshot(screenshotName);
        report.addScreenCaptureFromPath(relativeToOutput(screenshot));
        scenario.attach(readBytes(screenshot), "image/png", screenshot.getName());
        attachFileToAllure(screenshot, "Checkout failure screenshot - " + screenshotName, "image/png", ".png");
        report.pass("Checkout failure screenshot stored as " + screenshot.getName() + ".");
    }

    @And("I cancel checkout from the information page")
    public void cancelCheckoutFromInformationPage() {
        inventoryPage.cancelFromCheckoutInformation();
        report.pass("Checkout was canceled from the information page.");
    }

    @And("I cancel checkout from the overview page")
    public void cancelCheckoutFromOverviewPage() {
        inventoryPage.cancelFromCheckoutOverview();
        report.pass("Checkout was canceled from the overview page.");
    }

    @Then("the sauce cart should still show the selected products")
    public void cartShouldStillShowSelectedProducts() {
        Assert.assertNotNull(selectedProducts, "No selected products were stored before cart validation.");
        Assert.assertTrue(inventoryPage.cartHasProducts(selectedProducts),
                "Cart should still show the selected products after canceling checkout.");
        report.pass("Cart retained selected products after checkout cancel.");
    }

    @When("I finish the checkout")
    public void finishTheCheckout() {
        inventoryPage.finishCheckout();
        report.pass("Finish checkout was submitted.");
    }

    @Then("the checkout should complete successfully")
    public void checkoutShouldCompleteSuccessfully() {
        Assert.assertTrue(inventoryPage.checkoutComplete(), "Checkout complete confirmation should be displayed.");
        report.pass("Checkout completed successfully.");
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
                attachFileToAllure(screenshot, "Failure screenshot", "image/png", ".png");
            }
            report.fail("Scenario failed: " + scenario.getName());
        } else {
            report.pass("Scenario passed: " + scenario.getName());
        }

        File finalScreenshot = captureAllureScreenshot((scenario.isFailed() ? "failed-" : "passed-") + scenario.getName());
        if (finalScreenshot.exists()) {
            attachFileToAllure(finalScreenshot, "Timestamped final screenshot", "image/png", ".png");
        }

        File video = stopScenarioRecording(scenario.getName());
        if (video.exists()) {
            attachFileToAllure(video, "Scenario video", "video/mp4", ".mp4");
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

    private File captureAllureScreenshot(String name) {
        File destination = artifactFile("screenshots", name, ".png");
        try {
            Files.createDirectories(destination.getParentFile().toPath());
            File source = ((TakesScreenshot) driver).getScreenshotAs(OutputType.FILE);
            Files.copy(source.toPath(), destination.toPath(), StandardCopyOption.REPLACE_EXISTING);
        } catch (IOException | RuntimeException e) {
            report.warning("Allure screenshot capture failed: " + e.getMessage());
        }
        return destination;
    }

    private void startScenarioRecording() {
        try {
            driver.startRecordingScreen(new AndroidStartScreenRecordingOptions()
                    .withTimeLimit(Duration.ofMinutes(10)));
            screenRecordingStarted = true;
            report.info("Android screen recording started for Allure video attachment.");
        } catch (RuntimeException e) {
            screenRecordingStarted = false;
            report.warning("Screen recording could not start: " + e.getMessage());
        }
    }

    private File stopScenarioRecording(String scenarioName) {
        File destination = artifactFile("videos", scenarioName, ".mp4");
        if (!screenRecordingStarted) {
            return destination;
        }

        try {
            String recording = driver.stopRecordingScreen();
            if (recording == null || recording.trim().isEmpty()) {
                report.warning("Screen recording finished without video data.");
                return destination;
            }

            Files.createDirectories(destination.getParentFile().toPath());
            Files.write(destination.toPath(), Base64.getDecoder().decode(recording));
        } catch (IOException | RuntimeException e) {
            report.warning("Screen recording could not be saved: " + e.getMessage());
        } finally {
            screenRecordingStarted = false;
        }
        return destination;
    }

    private File artifactFile(String artifactType, String scenarioName, String extension) {
        File root = new File(TestConfig.allureArtifactsDir(), TestConfig.allureRunTimestamp());
        String fileName = sanitize(scenarioName) + "-" + ARTIFACT_TIMESTAMP.format(LocalDateTime.now()) + extension;
        return new File(new File(root, artifactType), fileName);
    }

    private void attachFileToAllure(File file, String title, String contentType, String extension) {
        if (!file.exists()) {
            return;
        }

        try (InputStream stream = Files.newInputStream(file.toPath())) {
            Allure.addAttachment(title, contentType, stream, extension);
        } catch (IOException | RuntimeException e) {
            report.warning("Allure attachment failed for " + file.getName() + ": " + e.getMessage());
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
