// Login Test with Multiple User Roles (Mobile App)
import org.testng.annotations.DataProvider;

public class LoginDataProvider {

    @DataProvider(name = "userRoles")
    public Object[][] getUserRoles() {
        return new Object[][]{
            {"admin_user", "admin123", "Admin"},
            {"standard_user", "user123", "User"},
            {"guest_user", "guest123", "Guest"}
        };
    }
}
// LoginTest.java
import org.testng.Assert;
import org.testng.annotations.Test;

public class LoginTest extends BaseMobileTest {

    @Test(dataProvider = "userRoles", dataProviderClass = LoginDataProvider.class)
    public void loginWithDifferentRoles(String username, String password, String rewards) {
        loginPage.login(username, password);
        Assert.assertTrue(homePage.isRewardsDisplayed(rewards));
    }
}

// ==============================================================================================
// Cross-Device Testing (Android + iOS)
import org.testng.annotations.DataProvider;

public class DeviceDataProvider {

    @DataProvider(name = "Platform", parallel = true)
    public Object[][] getPlatform() {
        return new Object[][]{
            {"Android", "Pixel_7"},
            {"iOS", "iPhone_14"}
        };
    }
}

// ============================================================
import org.testng.annotations.Test;

public class CrossDeviceTest extends BaseMobileTest {

    @Test(dataProvider = "devices", dataProviderClass = DeviceDataProvider.class)
    public void verifyHomeScreen(String platform, String deviceName) {
        launchApp(platform, deviceName);
        homePage.verifyHomeScreen();
    }
}


// Advantages
// -----------DataProvider avoids code duplication
// -----------Improves coverage without increasing test count
// -----------Supports parallel execution
// -----------Ideal for cross-device, cross-locale testing
// -----------Essential for scalable mobile frameworks
