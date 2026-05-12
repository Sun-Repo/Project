package com.mobilewright.data;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

public final class SauceUserData {
    private static final String DATA_RESOURCE = "data/sauce-users.csv";
    private static final Map<String, SauceUser> USERS = load();

    private SauceUserData() {
    }

    public static SauceUser byKey(String key) {
        SauceUser user = USERS.get(key);
        if (user == null) {
            throw new IllegalArgumentException("No SauceDemo user data found for key: " + key);
        }
        return user;
    }

    private static Map<String, SauceUser> load() {
        Map<String, SauceUser> users = new HashMap<>();
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
                    if (columns.length < 5) {
                        throw new IllegalStateException("Invalid SauceDemo data row: " + line);
                    }

                    SauceUser user = new SauceUser(
                            columns[0].trim(),
                            columns[1].trim(),
                            columns[2].trim(),
                            columns[3].trim(),
                            columns[4].trim()
                    );
                    users.put(user.key(), user);
                }
            }
        } catch (IOException e) {
            throw new IllegalStateException("Unable to read SauceDemo user data.", e);
        }
        return users;
    }
}
