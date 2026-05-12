package com.mobilewright.data;

public final class CheckoutCustomer {
    private final String key;
    private final String firstName;
    private final String lastName;
    private final String zipCode;

    public CheckoutCustomer(String key, String firstName, String lastName, String zipCode) {
        this.key = key;
        this.firstName = firstName;
        this.lastName = lastName;
        this.zipCode = zipCode;
    }

    public String key() {
        return key;
    }

    public String firstName() {
        return firstName;
    }

    public String lastName() {
        return lastName;
    }

    public String zipCode() {
        return zipCode;
    }
}
