package com.aiqe.utils;

import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.testng.Assert;

public class ApiValidator {

    public static void statusCodeValidation (Response response, int expectedStatusCode){
        int actualStatusCode = response.getStatusCode();
        Assert.assertEquals(actualStatusCode, expectedStatusCode, "API status code mismatch");

    }

    public static void responseBodyAssertion(
            Response response,
            String jsonPath,
            Object expectedValue) {

        JsonNode root = response.as(JsonNode.class);
        JsonNode node = root.path(jsonPath);

        Assert.assertFalse(node.isMissingNode(),
                "JSON field not found: " + jsonPath);

        Object actualValue;

        if (node.isTextual()) {
            actualValue = node.asText();
        } else if (node.isIntegralNumber()) {
            actualValue = node.numberValue();
        } else if (node.isFloatingPointNumber()) {
            actualValue = node.numberValue();
        } else if (node.isBoolean()) {
            actualValue = node.asBoolean();
        } else if (node.isNull()) {
            actualValue = null;
        } else {
            actualValue = node;
        }

        Assert.assertEquals(actualValue, expectedValue,
                "JSON field mismatch at: " + jsonPath);
    }
}
