
package com.aiqe.client;

import com.aiqe.config.ApiConfig;
import com.aiqe.logging.ApiLoggingFilter;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import io.restassured.specification.RequestSpecification;
import java.util.Map;

public class ApiClient {

    public static Response get(String endpoint) {
        return get("booking", endpoint);
    }

    public static Response get(
            String serviceName,
            String endpoint) {

        return createRequest(serviceName)
                .when()
                .get(endpoint);
    }

    public static Response post(
            String endpoint,
            Object requestBody) {

        return post("booking", endpoint, requestBody);
    }

    public static Response post(
            String serviceName,
            String endpoint,
            Object requestBody) {

        return createRequest(serviceName)
                .contentType("application/json")
                .body(requestBody)
                .when()
                .post(endpoint);
    }

    public static Response put(
            String endpoint,
            Object requestBody,
            String token) {

        return put("booking", endpoint, requestBody, token);
    }

    public static Response put(
            String serviceName,
            String endpoint,
            Object requestBody,
            String token) {

        return createRequest(serviceName)
                .contentType("application/json")
                .cookie("token", token)
                .body(requestBody)
                .when()
                .put(endpoint);
    }

    public static Response delete(
            String endpoint,
            String token) {

        return delete("booking", endpoint, token);
    }

    public static Response delete(
            String serviceName,
            String endpoint,
            String token) {

        return createRequest(serviceName)
                .contentType("application/json")
                .cookie("token", token)
                .when()
                .delete(endpoint);
    }

    public static String getAuthToken(
            String serviceName,
            String authEndpoint,
            Map<String, String> credentials) {

        Response response = post(
                serviceName,
                authEndpoint,
                credentials
        );

        if (response.getStatusCode() != 200) {
            throw new IllegalStateException(
                    "Authentication failed: HTTP "
                            + response.getStatusCode()
            );
        }

        String token = response.jsonPath()
                .getString("token");

        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Auth token is missing"
            );
        }

        return token;
    }


    private static RequestSpecification createRequest(
            String serviceName) {

        return RestAssured
                .given()
                .filter(new ApiLoggingFilter())
                .baseUri(
                        ApiConfig.getBaseUrl(serviceName)
                );
    }
}
