// Execution Hooks
// Screenshot-on-Failure Listener
import org.testng.ITestListener;
import org.testng.ITestResult;

public class CustomListener implements ITestListener {

    public void onTestFailure(ITestResult result) {
        System.out.println("Test Failed: " + result.getName());
    }

    public void onTestSuccess(ITestResult result) {
        System.out.println("Test Passed: " + result.getName());
    }
}
// ========================================================================
import io.appium.java_client.AppiumDriver;
import org.testng.ITestListener;
import org.testng.ITestResult;

import java.io.File;

public class ScreenshotListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        try {
            AppiumDriver driver = DriverManager.getDriver();
            File screenshot = driver.getScreenshotAs(org.openqa.selenium.OutputType.FILE);
            System.out.println("Screenshot captured for: " + result.getName());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
// Test Execution Time Tracker Listener
// Track slow mobile tests (important for CI stability).
import org.testng.ITestListener;
import org.testng.ITestResult;

public class ExecutionTimeListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        long duration = result.getEndMillis() - result.getStartMillis();
        System.out.println(result.getName() + " executed in " + duration + " ms");
    }
}
// ========================================================
// Retry + Flaky Test Logger Listener
// Log flaky mobile tests that pass after retry.
import org.testng.ITestListener;
import org.testng.ITestResult;

public class FlakyTestListener implements ITestListener {

    @Override
    public void onTestSuccess(ITestResult result) {
        if (result.getMethod().getCurrentInvocationCount() > 1) {
            System.out.println("Flaky Test Detected: " + result.getName());
        }
    }
}
// ======================================
// App Lifecycle Listener (Launch / Close App)
import org.testng.ITestListener;
import org.testng.ITestResult;

public class AppLifecycleListener implements ITestListener {

    @Override
    public void onTestStart(ITestResult result) {
        DriverManager.getDriver().launchApp();
    }

    @Override
    public void onTestFinish(ITestResult result) {
        DriverManager.getDriver().closeApp();
    }
}
// ===========================================================
// Network Failure Diagnostic Listener
import org.testng.ITestListener;
import org.testng.ITestResult;

public class NetworkDiagnosticListener implements ITestListener {

    @Override
    public void onTestFailure(ITestResult result) {
        if (result.getThrowable() != null &&
            result.getThrowable().getMessage().contains("timeout")) {
            System.out.println("Network-related failure detected in test: " + result.getName());
        }
    }
}
// ==========================================
<listeners>
    <listener class-name="listeners.ScreenshotListener"/>
    <listener class-name="listeners.ExecutionTimeListener"/>
    <listener class-name="listeners.FlakyTestListener"/>
    <listener class-name="listeners.AppLifecycleListener"/>
    <listener class-name="listeners.NetworkDiagnosticListener"/>
</listeners>


