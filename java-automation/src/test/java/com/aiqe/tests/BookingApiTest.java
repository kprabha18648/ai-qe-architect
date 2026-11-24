
package com.aiqe.tests;

import com.aiqe.annotations.TestDataFile;
import com.aiqe.client.ApiClient;
import com.aiqe.config.EndpointConfig;
import com.aiqe.dataproviders.GenericJsonDataProvider;
import com.aiqe.utils.ApiValidator;
import com.aiqe.utils.ResponseTimeValidator;
import com.aiqe.utils.RetryHandler;
import com.aiqe.utils.SchemaValidator;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import java.util.HashMap;
import java.util.Map;

public class BookingApiTest {

    private static final String SERVICE = "booking";

    @Test(
            dataProvider = "jsonData",
            dataProviderClass = GenericJsonDataProvider.class
    )
    @TestDataFile("booking-data.json")
    public void createBookingTest(JsonNode bookingData) {

        JsonNode requestBody = bookingData.get("requestBody");

        // Step 1: Create Booking
        Response createResponse = ApiClient.post(
                SERVICE,
                EndpointConfig.getEndpoint("booking.create"),
                requestBody.toString()
        );

        ApiValidator.statusCodeValidation(createResponse, 200);

        ResponseTimeValidator.validateResposeTime(
                createResponse, 3000
        );

        SchemaValidator.validate(
                createResponse,
                "booking-response-schema.json"
        );

        int bookingId = createResponse.jsonPath()
                .getInt("bookingid");

        Assert.assertTrue(bookingId > 0);

        Assert.assertEquals(
                createResponse.as(JsonNode.class).path("booking"),
                requestBody
        );

        // Prepare reusable path parameters
        Map<String, Integer> pathParams = Map.of(
                "bookingId", bookingId
        );

        // Step 2: Get Booking
        String bookingEndpoint = EndpointConfig.getEndpoint(
                "booking.get",
                pathParams
        );

        Response getResponse = RetryHandler.getRetry(
                () -> ApiClient.get(SERVICE, bookingEndpoint),
                3
        );

        ApiValidator.statusCodeValidation(getResponse, 200);

        ApiValidator.responseBodyAssertion(
                getResponse,
                "lastname",
                requestBody.get("lastname").asText()
        );

        ApiValidator.responseBodyAssertion(
                getResponse,
                "totalprice",
                requestBody.get("totalprice").asInt()
        );

        ApiValidator.responseBodyAssertion(
                getResponse,
                "depositpaid",
                requestBody.get("depositpaid").asBoolean()
        );

        Assert.assertEquals(
                getResponse.as(JsonNode.class),
                requestBody
        );

        // Step 3: Generate Authentication Token
        Map<String, String> credentials = new HashMap<>();

        credentials.put(
                "username",
                System.getProperty("booking.username", "admin")
        );

        credentials.put(
                "password",
                System.getProperty("booking.password", "password123")
        );

        String token = ApiClient.getAuthToken(
                SERVICE,
                EndpointConfig.getEndpoint("booking.auth"),
                credentials
        );

        Assert.assertNotNull(token);
        Assert.assertFalse(token.isBlank());

        // Step 4: Prepare Updated Request Body
        ObjectNode updatedBody = ((ObjectNode) requestBody)
                .deepCopy();

        updatedBody.put(
                "totalprice",
                bookingData.get("testData")
                        .get("updatedPrice").asInt()
        );

        // Step 5: Update Booking
        Response putResponse = ApiClient.put(
                SERVICE,
                EndpointConfig.getEndpoint(
                        "booking.update",
                        pathParams
                ),
                updatedBody.toString(),
                token
        );

        ApiValidator.statusCodeValidation(putResponse, 200);

        Assert.assertEquals(
                putResponse.as(JsonNode.class),
                updatedBody
        );

        // Step 6: Verify Updated Booking
        Response getAfterPut = ApiClient.get(
                SERVICE,
                bookingEndpoint
        );

        ApiValidator.statusCodeValidation(getAfterPut, 200);

        Assert.assertEquals(
                getAfterPut.as(JsonNode.class),
                updatedBody
        );

        // Step 7: Delete Booking
        Response deleteResponse = ApiClient.delete(
                SERVICE,
                EndpointConfig.getEndpoint(
                        "booking.delete",
                        pathParams
                ),
                token
        );

        ApiValidator.statusCodeValidation(deleteResponse, 201);

        // Step 8: Verify Booking Is Deleted
        Response checkDeleteIsWorking = ApiClient.get(
                SERVICE,
                bookingEndpoint
        );

        ApiValidator.statusCodeValidation(
                checkDeleteIsWorking,
                404
        );
    }

    @Test
    public void getProductTest(){

        String endPoint = EndpointConfig.getEndpoint("products.get", Map.of("productId",1));

        Response response = ApiClient.get("products", endPoint);
        ApiValidator.statusCodeValidation(response,200);
        ApiValidator.responseBodyAssertion(
                response, "id", 1
        );
    }
}
