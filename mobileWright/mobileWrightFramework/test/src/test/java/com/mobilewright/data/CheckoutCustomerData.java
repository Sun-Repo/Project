package com.mobilewright.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class CheckoutCustomerData {
    private static final String DATA_RESOURCE = "data/checkout-customers.csv";
    private static final Map<String, CheckoutCustomer> CUSTOMERS = load();

    private CheckoutCustomerData() {
    }

    public static CheckoutCustomer byKey(String key) {
        CheckoutCustomer customer = CUSTOMERS.get(key);
        if (customer == null) {
            throw new IllegalArgumentException("No SauceDemo checkout customer data found for key: " + key);
        }
        return customer;
    }

    private static Map<String, CheckoutCustomer> load() {
        Map<String, CheckoutCustomer> customers = new HashMap<>();
        ClassLoader loader = Thread.currentThread().getContextClassLoader();

        try (InputStream stream = loader.getResourceAsStream(DATA_RESOURCE)) {
            if (stream == null) {
                throw new IllegalStateException("Missing test data resource: " + DATA_RESOURCE);
            }

            try (BufferedReader reader = new BufferedReader(new InputStreamReader(stream, StandardCharsets.UTF_8))) {
                String line;
                boolean header = true;
                while ((line = reader.readLine()) != null) {
                    if (header) {
                        header = false;
                        continue;
                    }
                    if (line.trim().isEmpty()) {
                        continue;
                    }

                    String[] columns = line.split(",", -1);
                    if (columns.length < 4) {
                        throw new IllegalStateException("Invalid checkout customer data row: " + line);
                    }

                    CheckoutCustomer customer = new CheckoutCustomer(
                            columns[0].trim(),
                            columns[1].trim(),
                            columns[2].trim(),
                            columns[3].trim()
                    );
                    customers.put(customer.key(), customer);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read SauceDemo checkout customer data.", e);
        }
        return customers;
    }
}
