package com.mobilewright.data;

public final class SauceUser {
    private final String key;
    private final String username;
    private final String password;
    private final String expectedResult;
    private final String expectedMessage;

    public SauceUser(String key, String username, String password, String expectedResult, String expectedMessage) {
        this.key = key;
        this.username = username;
        this.password = password;
        this.expectedResult = expectedResult;
        this.expectedMessage = expectedMessage;
    }

    public String key() {
        return key;
    }

    public String username() {
        return username;
    }

    public String password() {
        return password;
    }

    public String expectedResult() {
        return expectedResult;
    }

    public String expectedMessage() {
        return expectedMessage;
    }

    public boolean expectsSuccess() {
        return "success".equalsIgnoreCase(expectedResult);
    }
}
