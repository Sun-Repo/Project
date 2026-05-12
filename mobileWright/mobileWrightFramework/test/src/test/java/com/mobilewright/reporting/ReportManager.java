package com.mobilewright.reporting;

import com.mobilewright.config.TestConfig;
import com.aventstack.extentreports.ExtentReports;
import com.aventstack.extentreports.reporter.ExtentSparkReporter;
import com.aventstack.extentreports.reporter.configuration.Theme;

import java.io.File;

public final class ReportManager {
    private static ExtentReports extent;

    private ReportManager() {
    }

    public static synchronized ExtentReports getReport() {
        if (extent == null) {
            File output = new File(TestConfig.outputDir());
            if (!output.exists()) {
                output.mkdirs();
            }

            ExtentSparkReporter spark = new ExtentSparkReporter(new File(output, "SauceDemoMobileReport.html"));
            spark.config().setDocumentTitle("SauceDemo Android Chrome Report");
            spark.config().setReportName("MobileWright MCP SauceDemo Android Chrome");
            spark.config().setTheme(Theme.STANDARD);

            extent = new ExtentReports();
            extent.attachReporter(spark);
            extent.setSystemInfo("Target", TestConfig.targetUrl());
            extent.setSystemInfo("Device UDID", TestConfig.deviceUdid());
            extent.setSystemInfo("Browser", "Android Chrome");
            extent.setSystemInfo("Automation", "Appium UiAutomator2");
        }
        return extent;
    }

    public static synchronized void flush() {
        if (extent != null) {
            extent.flush();
        }
    }
}
