package com.aiqe.tests;

import com.aiqe.client.ApiClient;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

public class MultiServiceApiTest {

    @Test
    public void testMultipleServices(){

        // Service 1: Booking API
        Response bookingResponse =
                ApiClient.get("/booking");

        Assert.assertEquals(
                bookingResponse.getStatusCode(), 200
        );

        // Service 2: Products API
        Response productResponse =
                ApiClient.get("products", "/products/1");

        Assert.assertEquals(
                productResponse.getStatusCode(), 200
        );

        Assert.assertEquals(
                productResponse.jsonPath().getInt("id"), 1
        );
    }
}
