// ========================(Single Thread / Non-Parallel)========================
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    @BeforeMethod
    public void setUp() {
        System.out.println("Browser setup");
    }

    @AfterMethod
    public void tearDown() {
        System.out.println("Browser cleanup");
    }
}
// ==========================(Parallel Execution Safe)======================
// ThreadLocal ensures one WebDriver instance per thread, making the framework safe for parallel execution
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.AfterMethod;
import org.testng.annotations.BeforeMethod;

public class BaseTest {

    private static ThreadLocal<WebDriver> driver = new ThreadLocal<>();

    @BeforeMethod
    public void setUp() {
        driver.set(new ChromeDriver());
        driver.get().manage().window().maximize();
    }

    protected WebDriver getDriver() {
        return driver.get();
    }

    @AfterMethod
    public void tearDown() {
        if (getDriver() != null) {
            getDriver().quit();
            driver.remove();
        }
    }
}
// =============Suite-Level Initialization + Method-Level Cleanup==========

// This approach separates environment setup from test-level browser management
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.annotations.*;

public class BaseTest {

    protected WebDriver driver;

    @BeforeSuite
    public void beforeSuite() {
        System.out.println("Initialize reporting and environment");
    }

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }

    @AfterSuite
    public void afterSuite() {
        System.out.println("Flush reports and cleanup");
    }
}

// ============Conditional Driver Creation (Cross-Browser Support)=============
// Using TestNG parameters allows browser selection without code changes, which is CI-friendly.
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.firefox.FirefoxDriver;
import org.testng.annotations.*;

public class BaseTest {

    protected WebDriver driver;

    @Parameters("browser")
    @BeforeMethod
    public void setUp(String browser) {
        if (browser.equalsIgnoreCase("chrome")) {
            driver = new ChromeDriver();
        } else if (browser.equalsIgnoreCase("firefox")) {
            driver = new FirefoxDriver();
        }
    }

    @AfterMethod
    public void tearDown() {
        if (driver != null) driver.quit();
    }
}
// ===============Resilient BaseTest with Failure Handling==========
// “ITestResult gives test outcome at runtime, allowing conditional actions like screenshots on failure.
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.testng.ITestResult;
import org.testng.annotations.*;

public class BaseTest {

    protected WebDriver driver;

    @BeforeMethod
    public void setUp() {
        driver = new ChromeDriver();
    }

    @AfterMethod
    public void tearDown(ITestResult result) {
        if (!result.isSuccess()) {
            System.out.println("Capture screenshot for failed test: " + result.getName());
        }
        if (driver != null) driver.quit();
    }
}
// ===========API + UI Hybrid BaseTest (Enterprise Pattern)==========
// In modern automation, UI tests often depend on backend state. A hybrid BaseTest allows API calls to prepare data or authenticate users before UI validation, improving speed and reliability
BaseTest
 ├── API Client (Auth, Test Data Setup)
 ├── ThreadLocal WebDriver (UI)
 ├── Shared Test Context
 └── Cleanup (API + UI)


 public class TestContext {

    private String authToken;
    private String userId;

    public String getAuthToken() {
        return authToken;
    }

    public void setAuthToken(String authToken) {
        this.authToken = authToken;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }
}


// BeforeMethod

// API creates test data

// Data stored in TestContext

// Test Execution

// UI test reads data from TestContext

// AfterMethod

// Cleanup uses TestContext values

// Context destroyed
// ======TestContext in BaseTest==============
private static ThreadLocal<TestContext> context = new ThreadLocal<>();

@BeforeMethod
public void setUp() {
    TestContext testContext = new TestContext();
    testContext.setAuthToken(ApiClient.login());
    testContext.setUserId(ApiClient.createUser());
    context.set(testContext);
}

protected TestContext getContext() {
    return context.get();
}
