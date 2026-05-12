package com.mobilewright.pages;

import com.mobilewright.config.TestConfig;
import com.mobilewright.data.CheckoutCustomer;
import com.mobilewright.driver.PopupHandler;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.TimeoutException;
import org.openqa.selenium.WebElement;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

public class SauceDemoInventoryPage {
    private final AndroidDriver driver;
    private final WebDriverWait wait;

    private final By menuButton = By.id("react-burger-menu-btn");
    private final By logoutLink = By.id("logout_sidebar_link");
    private final By inventoryContainer = By.id("inventory_container");
    private final By inventoryItems = By.cssSelector(".inventory_item");
    private final By inventoryItemName = By.cssSelector(".inventory_item_name");
    private final By inventoryItemPrice = By.cssSelector(".inventory_item_price");
    private final By addToCartButton = By.cssSelector("button.btn_inventory");
    private final By cartLink = By.cssSelector(".shopping_cart_link");
    private final By checkoutButton = By.id("checkout");
    private final By cartContainer = By.id("cart_contents_container");
    private final By firstName = By.id("first-name");
    private final By lastName = By.id("last-name");
    private final By postalCode = By.id("postal-code");
    private final By continueButton = By.id("continue");
    private final By cancelButton = By.id("cancel");
    private final By checkoutInfoContainer = By.id("checkout_info_container");
    private final By checkoutError = By.cssSelector("[data-test='error']");
    private final By checkoutSummary = By.id("checkout_summary_container");
    private final By overviewItems = By.cssSelector(".cart_item");
    private final By paymentInfo = By.cssSelector("[data-test='payment-info-value']");
    private final By shippingInfo = By.cssSelector("[data-test='shipping-info-value']");
    private final By subtotalLabel = By.cssSelector("[data-test='subtotal-label']");
    private final By taxLabel = By.cssSelector("[data-test='tax-label']");
    private final By totalLabel = By.cssSelector("[data-test='total-label']");
    private final By finishButton = By.id("finish");
    private final By completeHeader = By.cssSelector("[data-test='complete-header']");

    public SauceDemoInventoryPage(AndroidDriver driver) {
        this.driver = driver;
        this.wait = new WebDriverWait(driver, TestConfig.defaultTimeout());
    }

    public boolean isLoaded() {
        try {
            wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
            return true;
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void logout() {
        wait.until(ExpectedConditions.elementToBeClickable(menuButton)).click();
        wait.until(ExpectedConditions.elementToBeClickable(logoutLink)).click();
        PopupHandler.handleAny(driver);
    }

    public List<Product> addEveryNthProductToCart(int interval) {
        if (interval < 1) {
            throw new IllegalArgumentException("Product interval must be greater than zero.");
        }

        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
        List<WebElement> items = wait.until(ExpectedConditions.numberOfElementsToBeMoreThan(inventoryItems, 0));
        List<Product> selectedProducts = new ArrayList<>();

        for (int index = 0; index < items.size(); index++) {
            int productPosition = index + 1;
            if (productPosition % interval != 0) {
                continue;
            }

            WebElement item = items.get(index);
            scrollIntoView(item);
            Product product = new Product(
                    item.findElement(inventoryItemName).getText(),
                    money(item.findElement(inventoryItemPrice).getText())
            );
            item.findElement(addToCartButton).click();
            selectedProducts.add(product);
        }

        return selectedProducts;
    }

    public void checkoutWith(CheckoutCustomer customer) {
        openCheckoutInformation();
        submitCheckoutInformation(customer);
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutSummary));
    }

    public void openCheckoutInformation() {
        wait.until(ExpectedConditions.elementToBeClickable(cartLink)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartContainer));
        wait.until(ExpectedConditions.elementToBeClickable(checkoutButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutInfoContainer));
    }

    public void submitCheckoutInformation(CheckoutCustomer customer) {
        clearAndType(firstName, customer.firstName());
        clearAndType(lastName, customer.lastName());
        clearAndType(postalCode, customer.zipCode());
        wait.until(ExpectedConditions.elementToBeClickable(continueButton)).click();
    }

    public boolean overviewHasProducts(List<Product> expectedProducts) {
        List<WebElement> items = wait.until(ExpectedConditions.numberOfElementsToBe(overviewItems, expectedProducts.size()));
        for (Product expectedProduct : expectedProducts) {
            boolean found = false;
            for (WebElement item : items) {
                if (expectedProduct.name().equals(item.findElement(inventoryItemName).getText())
                        && expectedProduct.price().compareTo(money(item.findElement(inventoryItemPrice).getText())) == 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    public boolean hasPaymentShippingAndPriceSummary() {
        return hasText(paymentInfo)
                && hasText(shippingInfo)
                && hasText(subtotalLabel)
                && hasText(taxLabel)
                && hasText(totalLabel);
    }

    public boolean overviewHasNoProductsAndZeroTotals() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutSummary));
        return driver.findElements(overviewItems).isEmpty()
                && BigDecimal.ZERO.compareTo(labelMoney(subtotalLabel)) == 0
                && BigDecimal.ZERO.compareTo(labelMoney(taxLabel)) == 0
                && BigDecimal.ZERO.compareTo(labelMoney(totalLabel)) == 0;
    }

    public boolean priceTotalMatches(List<Product> expectedProducts) {
        BigDecimal expectedSubtotal = BigDecimal.ZERO;
        for (Product product : expectedProducts) {
            expectedSubtotal = expectedSubtotal.add(product.price());
        }

        BigDecimal displayedSubtotal = labelMoney(subtotalLabel);
        BigDecimal displayedTax = labelMoney(taxLabel);
        BigDecimal displayedTotal = labelMoney(totalLabel);

        return expectedSubtotal.compareTo(displayedSubtotal) == 0
                && displayedSubtotal.add(displayedTax).compareTo(displayedTotal) == 0;
    }

    public void finishCheckout() {
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(finishButton));
        scrollIntoView(button);
        wait.until(ExpectedConditions.elementToBeClickable(finishButton)).click();
    }

    public boolean checkoutComplete() {
        try {
            return wait.until(ExpectedConditions.visibilityOfElementLocated(completeHeader)).isDisplayed();
        } catch (TimeoutException e) {
            return false;
        }
    }

    public boolean checkoutErrorContains(String expectedError) {
        try {
            String actualError = wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutError)).getText();
            return actualError.toLowerCase().contains(expectedError.toLowerCase());
        } catch (TimeoutException e) {
            return false;
        }
    }

    public void cancelFromCheckoutInformation() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutInfoContainer));
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartContainer));
    }

    public void cancelFromCheckoutOverview() {
        wait.until(ExpectedConditions.visibilityOfElementLocated(checkoutSummary));
        WebElement button = wait.until(ExpectedConditions.presenceOfElementLocated(cancelButton));
        scrollIntoView(button);
        wait.until(ExpectedConditions.elementToBeClickable(cancelButton)).click();
        wait.until(ExpectedConditions.visibilityOfElementLocated(inventoryContainer));
    }

    public boolean cartHasProducts(List<Product> expectedProducts) {
        wait.until(ExpectedConditions.visibilityOfElementLocated(cartContainer));
        List<WebElement> items = driver.findElements(overviewItems);
        if (items.size() != expectedProducts.size()) {
            return false;
        }

        for (Product expectedProduct : expectedProducts) {
            boolean found = false;
            for (WebElement item : items) {
                if (expectedProduct.name().equals(item.findElement(inventoryItemName).getText())
                        && expectedProduct.price().compareTo(money(item.findElement(inventoryItemPrice).getText())) == 0) {
                    found = true;
                    break;
                }
            }
            if (!found) {
                return false;
            }
        }
        return true;
    }

    private boolean hasText(By locator) {
        return !wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText().trim().isEmpty();
    }

    private void clearAndType(By locator, String value) {
        WebElement element = wait.until(ExpectedConditions.elementToBeClickable(locator));
        element.clear();
        element.sendKeys(value);
    }

    private BigDecimal labelMoney(By locator) {
        String text = wait.until(ExpectedConditions.visibilityOfElementLocated(locator)).getText();
        int separator = text.indexOf('$');
        if (separator < 0) {
            throw new IllegalStateException("No money value found in label: " + text);
        }
        return money(text.substring(separator));
    }

    private BigDecimal money(String text) {
        return new BigDecimal(text.replace("$", "").trim());
    }

    private void scrollIntoView(WebElement element) {
        ((JavascriptExecutor) driver).executeScript("arguments[0].scrollIntoView({block: 'center'});", element);
    }

    public static final class Product {
        private final String name;
        private final BigDecimal price;

        public Product(String name, BigDecimal price) {
            this.name = name;
            this.price = price;
        }

        public String name() {
            return name;
        }

        public BigDecimal price() {
            return price;
        }
    }
}
