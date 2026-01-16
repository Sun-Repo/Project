@ -1,83 +0,0 @@
// OTP verification
// Search
// Cart
// Checkout

OTP flows- Controlled Retry Logic
import org.testng.IRetryAnalyzer;
import org.testng.ITestResult;

public class RetryAnalyzer implements IRetryAnalyzer {

    private int retryCount = 0;
    private static final int MAX_RETRY = 2;

    @Override
    public boolean retry(ITestResult result) {
        if (retryCount < MAX_RETRY) {
            retryCount++;
            return true;
        }
        return false;
    }
}

// Flaky Test Tracker
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.util.concurrent.ConcurrentHashMap;

public class RetailRetryListener implements ITestListener {

    private static ConcurrentHashMap<String, Integer> flakyTests =
            new ConcurrentHashMap<>();

    @Override
    public void onTestFailure(ITestResult result) {
        String testName = result.getMethod().getMethodName();
        int retryAttempt = result.getMethod().getCurrentInvocationCount();
        Throwable error = result.getThrowable();

        flakyTests.put(testName, retryAttempt);

        System.out.println("📱 Retail Test Failed");
        System.out.println("Test: " + testName);
        System.out.println("Retry Attempt: " + retryAttempt);
        System.out.println("Failure Reason: " + error.getMessage());
    }

    @Override
    public void onFinish(org.testng.ITestContext context) {
        System.out.println("\n🚨 Flaky Test Summary (Retail Mobile App)");
        flakyTests.forEach((test, retries) -> {
            System.out.println("Test: " + test +
                    " | Retry Count: " + retries);
        });
    }
}
// Listener Globally
<listeners>
    <listener class-name="RetryTransformer"/>
    <listener class-name="RetailRetryListener"/>
</listeners>

// example
import org.testng.Assert;
import org.testng.annotations.Test;

public class AddToCartTest {

    @Test(retryAnalyzer = RetryAnalyzer.class)
    public void addProductToCart() {
        // Simulate flaky retail behavior (network / animation delay)
        Assert.assertTrue(Math.random() > 0.6,
                "Add to cart failed due to UI sync issue");
    }
}
// advantages
// Separates Product Bugs vs Automation Issues
// Network latency
// App animation timing
// Third-party payment gateways
