
package com.aiqe.utils;

import com.aiqe.client.ApiClient;
import io.restassured.response.Response;

import java.util.function.Supplier;

public class RetryHandler {

    // Existing method for our Booking API tests
    public static Response getRetry(String endpoint, int maxLimit) {

        return getRetry(
                () -> ApiClient.get(endpoint),
                maxLimit
        );
    }

    // Generic retry method for any API request
    public static Response getRetry(
            Supplier<Response> apiRequest,
            int maxLimit) {

        if (maxLimit < 1) {
            throw new IllegalArgumentException(
                    "maxLimit must be at least 1");
        }

        Response response = null;

        for (int i = 1; i <= maxLimit; i++) {

            response = apiRequest.get();

            int statusCode = response.getStatusCode();

            System.out.println(
                    "Attempt " + i + ": " + statusCode
            );

            // Stop if the response is not 503
            if (statusCode != 503) {
                break;
            }
        }

        return response;
    }
}
