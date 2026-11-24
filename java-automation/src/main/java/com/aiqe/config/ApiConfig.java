
package com.aiqe.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Properties;

public class ApiConfig {

    private static final Properties services = new Properties();

    private static final String ENV =
            System.getProperty("env", "dev");

    static {

        // Load default service configuration
        loadProperties("config/services.properties");

        // Load environment-specific configuration
        loadProperties(
                "config/environments/" + ENV + ".properties"
        );
    }

    private static void loadProperties(String filePath) {

        try (InputStream input = ApiConfig.class
                .getClassLoader()
                .getResourceAsStream(filePath)) {

            if (input == null) {
                throw new IllegalStateException(
                        "Configuration file not found: " + filePath
                );
            }

            services.load(input);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load configuration: " + filePath, e
            );
        }
    }

    public static String getBaseUrl(String serviceName) {

        String configuredUrl = services.getProperty(serviceName);

        if (configuredUrl == null || configuredUrl.isBlank()) {
            throw new IllegalArgumentException(
                    "Unknown API service: " + serviceName
            );
        }

        return System.getProperty(
                serviceName + ".baseUrl",
                configuredUrl
        );
    }

    public static String getEnvironment() {
        return ENV;
    }

    // Backward compatibility
    public static String getBaseUrl() {
        return System.getProperty(
                "baseUrl",
                getBaseUrl("booking")
        );
    }
}
