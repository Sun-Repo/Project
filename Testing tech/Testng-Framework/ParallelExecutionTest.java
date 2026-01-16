@ -1,60 +0,0 @@
// ├── ParallelExecutionTest.java
// ├── ThreadLocalDriverTest.java
// ├── DeviceBasedParallelTest.java
// ├── AppStateIsolationTest.java
// ├── NetworkConditionTest.java
// ├── RetryFlakyMobileTest.java


// ThreadLocalDriverTest.java
import io.appium.java_client.AppiumDriver;
import io.appium.java_client.MobileElement;
import org.testng.annotations.*;

public class ThreadLocalDriverTest {

    private static ThreadLocal<AppiumDriver<MobileElement>> driver = new ThreadLocal<>();

    @BeforeMethod
    public void setUp() {
        AppiumDriver<MobileElement> appiumDriver =
                MobileDriverFactory.createDriver();
        driver.set(appiumDriver);
    }

    @Test
    public void verifyHomeScreenLoads() {
        driver.get().findElementById("home_title").isDisplayed();
    }

    @AfterMethod
    public void tearDown() {
        driver.get().quit();
        driver.remove();
    }
}
// AppStateIsolationTest.java
// (Avoid app state leakage in parallel runs)
import org.testng.annotations.Test;

public class AppStateIsolationTest extends BaseMobileTest {

    @Test
    public void verifyFreshInstallState() {
        driver.resetApp();
        assert driver.findElementById("welcome_screen").isDisplayed();
    }
}
// NetworkConditionTest.java
import io.appium.java_client.android.AndroidDriver;
import org.testng.annotations.Test;

public class NetworkConditionTest extends BaseMobileTest {

    @Test
    public void verifyAppBehaviorOnOfflineMode() {
        AndroidDriver androidDriver = (AndroidDriver) driver;
        androidDriver.setConnection(Connection.AIRPLANE);
        // Validate offline message
    }
}