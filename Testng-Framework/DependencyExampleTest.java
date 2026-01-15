package mobile.tests;

import io.appium.java_client.MobileElement;
import io.appium.java_client.android.AndroidDriver;
import org.openqa.selenium.remote.DesiredCapabilities;
import org.testng.Assert;
import org.testng.annotations.*;

import java.net.MalformedURLException;
import java.net.URL;

public class DependencyExampleTest {

    private AndroidDriver<MobileElement> driver;

    @BeforeClass
    public void setup() throws MalformedURLException {
        DesiredCapabilities caps = new DesiredCapabilities();
        caps.setCapability("platformName", "Android");
        caps.setCapability("deviceName", "Pixel_4");
        caps.setCapability("app", "/path/to/app.apk");
        
        driver = new AndroidDriver<>(new URL("http://localhost:4723/wd/hub"), caps);
    }

    // 1️⃣ Login Test (prerequisite for all other tests)
    @Test
    public void loginTest() {
        MobileElement username = driver.findElementById("com.example:id/username");
        MobileElement password = driver.findElementById("com.example:id/password");
        MobileElement loginBtn = driver.findElementById("com.example:id/loginBtn");

        username.sendKeys("testuser");
        password.sendKeys("password123");
        loginBtn.click();

        // Assert login success
        MobileElement homeTitle = driver.findElementById("com.example:id/homeTitle");
        Assert.assertTrue(homeTitle.isDisplayed(), "Home screen is not displayed");
    }

    // 2️⃣ Navigate to Profile (depends on successful login)
    @Test(dependsOnMethods = "loginTest")
    public void navigateToProfileTest() {
        MobileElement profileBtn = driver.findElementById("com.example:id/profileBtn");
        profileBtn.click();

        MobileElement profileHeader = driver.findElementById("com.example:id/profileHeader");
        Assert.assertTrue(profileHeader.isDisplayed(), "Profile screen not visible");
    }

    // 3️⃣ Update Profile (depends on navigation to profile)
    @Test(dependsOnMethods = "navigateToProfileTest")
    public void updateProfileTest() {
        MobileElement editBtn = driver.findElementById("com.example:id/editProfile");
        editBtn.click();

        MobileElement nameField = driver.findElementById("com.example:id/name");
        nameField.clear();
        nameField.sendKeys("New Name");

        MobileElement saveBtn = driver.findElementById("com.example:id/saveBtn");
        saveBtn.click();

        MobileElement toastMsg = driver.findElementByXPath("//android.widget.Toast[1]");
        Assert.assertTrue(toastMsg.getText().contains("Profile updated"), "Profile not updated");
    }
