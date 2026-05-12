package com.mobilewright.config;

import java.time.Duration;

public final class TestConfig {
    private TestConfig() {
    }

    public static String appiumUrl() {
        return prop("appiumUrl", "http://127.0.0.1:4723/");
    }

    public static String deviceUdid() {
        return prop("deviceUdid", "R5CR82MHA1T");
    }

    public static String platformVersion() {
        return System.getProperty("platformVersion", "").trim();
    }

    public static String targetUrl() {
        return prop("targetUrl", "https://www.saucedemo.com/?utm_source=chatgpt.com");
    }

    public static int maxLoginRetries() {
        return Integer.parseInt(prop("maxLoginRetries", "3"));
    }

    public static Duration defaultTimeout() {
        return Duration.ofSeconds(Long.parseLong(prop("defaultTimeoutSeconds", "20")));
    }

    public static String outputDir() {
        return prop("outputDir", "output");
    }

    private static String prop(String key, String fallback) {
        String value = System.getProperty(key);
        return value == null || value.trim().isEmpty() ? fallback : value.trim();
    }
}
