package com.aiqe.tests;

import com.aiqe.annotations.TestDataFile;
import com.aiqe.client.ApiClient;
import com.aiqe.dataproviders.GenericJsonDataProvider;
import com.fasterxml.jackson.databind.JsonNode;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;


public class AuthApiTest {

    @TestDataFile("auth-data.json")
    @Test(dataProvider = "jsonData",
            dataProviderClass = GenericJsonDataProvider.class)
    public void authTest(JsonNode authtoken){

        JsonNode requestBody = authtoken.get("requestBody");
        Response authResponse = ApiClient.post("/auth", requestBody.toString());
        Assert.assertEquals(authResponse.statusCode(), 200);
        String token = authResponse.jsonPath().getString("token");
        Assert.assertNotNull(token, "Token should not be null");
        Assert.assertFalse(token.isBlank(),
                "Token should not be blank");
    }

    @TestDataFile("authNegative-data.json")
    @Test(dataProvider = "jsonData",
            dataProviderClass = GenericJsonDataProvider.class)
    public void authNegativeTest(JsonNode authTokenNegative){
        JsonNode requestBody = authTokenNegative.get("requestBody");
        Response authResponse = ApiClient.post("/auth", requestBody.toString());
        Assert.assertEquals(authResponse.statusCode(), 200);
        Assert.assertEquals(
                authResponse.jsonPath().getString("reason"),
                "Bad credentials"
        );
        Assert.assertNull(
                authResponse.jsonPath().getString("token"),
                "Token should not be generated for invalid credentials"
        );

    }
}
