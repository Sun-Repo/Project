@ -1,80 +0,0 @@
package tests.mobile;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;

public class GroupingExampleTest {

    private AndroidDriver<MobileElement> driver;

    @BeforeClass
    @Parameters({"platformName", "deviceName", "appURL"})
    public void setUp(String platformName, String deviceName, String appURL) throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", platformName);
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("app", appURL);

        driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
    }

    @AfterClass
    public void tearDown() {
        if (driver != null) {
            driver.quit();
        }
    }

    // --- SMOKE TESTS ---
    @Test(groups = {"smoke"})
    public void loginTest() {
        MobileElement username = driver.findElementById("com.example:id/username");
        MobileElement password = driver.findElementById("com.example:id/password");
        MobileElement loginButton = driver.findElementById("com.example:id/login");

        username.sendKeys("testuser");
        password.sendKeys("password");
        loginButton.click();

        MobileElement homeTitle = driver.findElementById("com.example:id/homeTitle");
        Assert.assertEquals(homeTitle.getText(), "Welcome");
    }

    // --- REGRESSION TESTS ---
    @Test(groups = {"regression"})
    public void addItemToCartTest() {
        MobileElement product = driver.findElementById("com.example:id/product_1");
        product.click();

        MobileElement addToCart = driver.findElementById("com.example:id/addToCart");
        addToCart.click();

        MobileElement cartCount = driver.findElementById("com.example:id/cartCount");
        Assert.assertEquals(cartCount.getText(), "1");
    }

    @Test(groups = {"regression"})
    public void removeItemFromCartTest() {
        MobileElement cart = driver.findElementById("com.example:id/cart");
        cart.click();

        MobileElement removeButton = driver.findElementById("com.example:id/removeItem");
        removeButton.click();

        MobileElement cartCount = driver.findElementById("com.example:id/cartCount");
        Assert.assertEquals(cartCount.getText(), "0");
    }

}
// ===============================================================================================
<groups>
    <run>
        <include name="smoke"/>
    </run>
</groups>