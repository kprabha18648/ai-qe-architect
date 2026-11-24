package com.aiqe.utils;

import io.restassured.response.Response;
import org.testng.Assert;

public class ResponseTimeValidator {

    public static void validateResposeTime(Response response, long maxResponseTime){
        long actualResponseTime = response.time();
        Assert.assertTrue(actualResponseTime < maxResponseTime, "API response time exceeded 2000 ms. Actual: " + response + "ms");



    }
}
