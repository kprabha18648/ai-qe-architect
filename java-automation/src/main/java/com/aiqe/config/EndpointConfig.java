
package com.aiqe.config;

import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Properties;

public class EndpointConfig {

    private static final Properties endpoints = new Properties();

    static {
        try (InputStream input = EndpointConfig.class
                .getClassLoader()
                .getResourceAsStream("config/endpoints.properties")) {

            if (input == null) {
                throw new IllegalStateException(
                        "endpoints.properties file not found"
                );
            }

            endpoints.load(input);

        } catch (IOException e) {
            throw new IllegalStateException(
                    "Failed to load endpoints.properties", e
            );
        }
    }

    public static String getEndpoint(String endpointKey) {

        String endpoint = endpoints.getProperty(endpointKey);

        if (endpoint == null || endpoint.isBlank()) {
            throw new IllegalArgumentException(
                    "Unknown endpoint: " + endpointKey
            );
        }

        return endpoint;
    }

    public static String getEndpoint(
            String endpointKey,
            Map<String, ?> pathParams) {

        String endpoint = getEndpoint(endpointKey);

        for (Map.Entry<String, ?> entry : pathParams.entrySet()) {

            String placeholder = "{" + entry.getKey() + "}";

            endpoint = endpoint.replace(
                    placeholder,
                    String.valueOf(entry.getValue())
            );
        }

        if (endpoint.matches(".*\\{[^{}]+\\}.*")) {
            throw new IllegalArgumentException(
                    "Missing path parameter for endpoint: " + endpointKey
            );
        }

        return endpoint;
    }
}
