“Mobile testing involves multiple devices, OS versions, and environments. Parameterization allows us to run the same test across different configurations without changing code.”

<!DOCTYPE suite SYSTEM "https://testng.org/testng-1.0.dtd">
<suite name="MobileRegressionSuite" parallel="tests" thread-count="2">

    <test name="AndroidTest">
        <parameter name="platformName" value="Android"/>
        <parameter name="deviceName" value="Pixel_6"/>
        <parameter name="platformVersion" value="13"/>
        <parameter name="appPackage" value="com.demo.app"/>
        <parameter name="appActivity" value=".MainActivity"/>
        <classes>
            <class name="tests.ParameterizationExampleTest"/>
        </classes>
    </test>

    <test name="iOSTest">
        <parameter name="platformName" value="iOS"/>
        <parameter name="deviceName" value="iPhone 14"/>
        <parameter name="platformVersion" value="16.0"/>
        <parameter name="bundleId" value="com.demo.iosapp"/>
        <classes>
            <class name="tests.ParameterizationExampleTest"/>
        </classes>
    </test>

</suite>
===========================================================================
// base Page

public class DriverFactory {

    private static ThreadLocal<AppiumDriver> driver = new ThreadLocal<>();

    public static void setDriver(AppiumDriver driverInstance) {
        driver.set(driverInstance);
    }

    public static AppiumDriver getDriver() {
        return driver.get();
    }

    public static void removeDriver() {
        driver.remove();
    }
}
=======================================================================================
// Example

package tests;

import io.appium.java_client.AppiumDriver;
import io.appium.java_client.android.AndroidDriver;
import io.appium.java_client.ios.IOSDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.URL;

public class ParameterizationExampleTest {

    @Parameters({
        "platformName",
        "deviceName",
        "platformVersion",
        "appPackage",
        "appActivity",
        "bundleId"
    })
    @BeforeMethod
    public void setUp(
            String platformName,
            String deviceName,
            String platformVersion,
            @Optional("") String appPackage,
            @Optional("") String appActivity,
            @Optional("") String bundleId
    ) throws Exception {

        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", platformName);
        caps.setCapability("deviceName", deviceName);
        caps.setCapability("platformVersion", platformVersion);
        caps.setCapability("automationName",
                platformName.equalsIgnoreCase("Android") ? "UiAutomator2" : "XCUITest");

        if (platformName.equalsIgnoreCase("Android")) {
            caps.setCapability("appPackage", appPackage);
            caps.setCapability("appActivity", appActivity);
            DriverFactory.setDriver(
                new AndroidDriver(new URL("http://localhost:4723/wd/hub"), caps)
            );
        } else {
            caps.setCapability("bundleId", bundleId);
            DriverFactory.setDriver(
                new IOSDriver(new URL("http://localhost:4723/wd/hub"), caps)
            );
        }
    }

    @Test
    public void verifyAppLaunch() {
        AppiumDriver driver = DriverFactory.getDriver();
        Assert.assertNotNull(driver.getSessionId(), "App did not launch successfully");
    }

    @AfterMethod
    public void tearDown() {
        DriverFactory.getDriver().quit();
        DriverFactory.removeDriver();
    }
}
