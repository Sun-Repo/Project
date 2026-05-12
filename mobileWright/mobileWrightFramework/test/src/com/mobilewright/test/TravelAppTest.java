package com.mobilewright.test;

import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;
import org.testng.ITestResult;
import org.testng.Reporter;
import org.testng.annotations.*;
import com.aventstack.extentreports.*;
import com.aventstack.extentreports.reporter.ExtentHtmlReporter;

public class TravelAppTest {
    private WebDriver driver;
    private ExtentReports extent;
    private ExtentTest test;

    @BeforeSuite
    public void setUpReport() {
        ExtentHtmlReporter htmlReporter = new ExtentHtmlReporter("test/reports/TravelAppTestReport.html");
        extent = new ExtentReports();
        extent.attachReporter(htmlReporter);
    }

    @BeforeMethod
    public void setUp() {
        // Set path to chromedriver if needed
        driver = new ChromeDriver();
        driver.get("http://127.0.0.1:5000/");
    }

    @Test
    public void testLoginPositive() {
        test = extent.createTest("Login Positive Test");
        driver.findElement(By.name("username")).sendKeys("user");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("button")).click();
        Assert.assertTrue(driver.getPageSource().contains("Book an SUV"));
        test.pass("Login successful with valid credentials");
    }

    @Test
    public void testLoginNegative() {
        test = extent.createTest("Login Negative Test");
        driver.findElement(By.name("username")).sendKeys("wrong");
        driver.findElement(By.name("password")).sendKeys("wrong");
        driver.findElement(By.tagName("button")).click();
        Assert.assertTrue(driver.getPageSource().contains("Invalid credentials"));
        test.pass("Login failed with invalid credentials");
    }

    @Test
    public void testBookingAndPayment() {
        test = extent.createTest("Booking and Payment Test");
        // Login
        driver.findElement(By.name("username")).sendKeys("user");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("button")).click();
        // Book
        driver.findElement(By.name("date")).sendKeys("2026-05-10");
        driver.findElement(By.name("suv_type")).sendKeys("XL");
        driver.findElement(By.tagName("button")).click();
        // Payment
        driver.findElement(By.name("card_number")).sendKeys("4111111111111111");
        driver.findElement(By.name("expiry")).sendKeys("12/30");
        driver.findElement(By.name("cvv")).sendKeys("123");
        driver.findElement(By.tagName("button")).click();
        Assert.assertTrue(driver.getPageSource().contains("Booking Report"));
        test.pass("Booking and payment completed");
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (result.getStatus() == ITestResult.FAILURE) {
            test.fail(result.getThrowable());
        }
        driver.quit();
    }

    @AfterSuite
    public void tearDownReport() {
        extent.flush();
    }
}
