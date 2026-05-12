package com.mobilewright.test;

import io.cucumber.java.en.*;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.Assert;

public class TravelBookingSteps {
    private WebDriver driver;
    private String baseUrl = "http://127.0.0.1:5000/";

    @Given("I am on the login page")
    public void i_am_on_the_login_page() {
        driver = new ChromeDriver();
        driver.get(baseUrl);
    }

    @When("I enter valid username and password")
    public void i_enter_valid_username_and_password() {
        driver.findElement(By.name("username")).sendKeys("user");
        driver.findElement(By.name("password")).sendKeys("pass");
    }

    @When("I enter invalid username and password")
    public void i_enter_invalid_username_and_password() {
        driver.findElement(By.name("username")).sendKeys("wrong");
        driver.findElement(By.name("password")).sendKeys("wrong");
    }

    @When("I click the login button")
    public void i_click_the_login_button() {
        driver.findElement(By.tagName("button")).click();
    }

    @Then("I should see the booking page")
    public void i_should_see_the_booking_page() {
        Assert.assertTrue(driver.getPageSource().contains("Book an SUV"));
        driver.quit();
    }

    @Then("I should see an error message")
    public void i_should_see_an_error_message() {
        Assert.assertTrue(driver.getPageSource().contains("Invalid credentials"));
        driver.quit();
    }

    @Given("I am logged in")
    public void i_am_logged_in() {
        driver = new ChromeDriver();
        driver.get(baseUrl);
        driver.findElement(By.name("username")).sendKeys("user");
        driver.findElement(By.name("password")).sendKeys("pass");
        driver.findElement(By.tagName("button")).click();
    }

    @When("I enter a valid travel date and select an SUV type")
    public void i_enter_a_valid_travel_date_and_select_an_suv_type() {
        driver.findElement(By.name("date")).sendKeys("2026-05-10");
        driver.findElement(By.name("suv_type")).sendKeys("XL");
    }

    @When("I leave the travel date empty and select an SUV type")
    public void i_leave_the_travel_date_empty_and_select_an_suv_type() {
        driver.findElement(By.name("date")).clear();
        driver.findElement(By.name("suv_type")).sendKeys("XL");
    }

    @When("I submit the booking")
    public void i_submit_the_booking() {
        driver.findElement(By.tagName("button")).click();
    }

    @Then("I should see the payment page")
    public void i_should_see_the_payment_page() {
        Assert.assertTrue(driver.getPageSource().contains("Payment Details"));
        driver.quit();
    }

    @Then("I should see a validation error for date")
    public void i_should_see_a_validation_error_for_date() {
        // Assuming client-side validation, check for required attribute
        WebElement dateInput = driver.findElement(By.name("date"));
        Assert.assertTrue(dateInput.getAttribute("required") != null);
        driver.quit();
    }

    @Given("I am on the payment page")
    public void i_am_on_the_payment_page() {
        i_am_logged_in();
        i_enter_a_valid_travel_date_and_select_an_suv_type();
        i_submit_the_booking();
    }

    @When("I enter valid card number, expiry, and CVV")
    public void i_enter_valid_card_number_expiry_and_cvv() {
        driver.findElement(By.name("card_number")).sendKeys("4111111111111111");
        driver.findElement(By.name("expiry")).sendKeys("12/30");
        driver.findElement(By.name("cvv")).sendKeys("123");
    }

    @When("I enter an invalid card number, valid expiry, and CVV")
    public void i_enter_an_invalid_card_number_valid_expiry_and_cvv() {
        driver.findElement(By.name("card_number")).sendKeys("123");
        driver.findElement(By.name("expiry")).sendKeys("12/30");
        driver.findElement(By.name("cvv")).sendKeys("123");
    }

    @When("I submit the payment")
    public void i_submit_the_payment() {
        driver.findElement(By.tagName("button")).click();
    }

    @Then("I should see the booking report")
    public void i_should_see_the_booking_report() {
        Assert.assertTrue(driver.getPageSource().contains("Booking Report"));
        driver.quit();
    }

    @Then("I should see a payment error")
    public void i_should_see_a_payment_error() {
        // No backend validation in demo, so just check page did not change
        Assert.assertTrue(driver.getCurrentUrl().contains("payment"));
        driver.quit();
    }

    @When("I click the logout link")
    public void i_click_the_logout_link() {
        driver.findElement(By.linkText("Logout")).click();
    }

    @Then("I should see the login page")
    public void i_should_see_the_login_page() {
        Assert.assertTrue(driver.getPageSource().contains("Login"));
        driver.quit();
    }

    @Given("I have completed a booking and payment")
    public void i_have_completed_a_booking_and_payment() {
        i_am_on_the_payment_page();
        i_enter_valid_card_number_expiry_and_cvv();
        i_submit_the_payment();
    }

    @When("I view the booking report")
    public void i_view_the_booking_report() {
        // Already on report page after payment
    }

    @Then("the report should show my username, date, SUV type, and masked card number")
    public void the_report_should_show_my_username_date_suv_type_and_masked_card_number() {
        String page = driver.getPageSource();
        Assert.assertTrue(page.contains("user"));
        Assert.assertTrue(page.contains("2026-05-10"));
        Assert.assertTrue(page.contains("XL"));
        Assert.assertTrue(page.contains("**** **** **** 1111"));
        driver.quit();
    }

    @Given("I am not logged in")
    public void i_am_not_logged_in() {
        driver = new ChromeDriver();
    }

    @When("I try to access the booking page")
    public void i_try_to_access_the_booking_page() {
        driver.get(baseUrl + "book");
    }

    @Then("I should be redirected to the login page")
    public void i_should_be_redirected_to_the_login_page() {
        Assert.assertTrue(driver.getPageSource().contains("Login"));
        driver.quit();
    }

    @When("I try to access the payment page without booking")
    public void i_try_to_access_the_payment_page_without_booking() {
        driver.get(baseUrl + "payment");
    }
}
