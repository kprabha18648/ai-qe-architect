
package com.aiqe.tests;

import com.aiqe.utils.RetryHandler;
import com.github.tomakehurst.wiremock.WireMockServer;
import io.restassured.RestAssured;
import io.restassured.response.Response;
import org.testng.Assert;
import org.testng.annotations.Test;

import static com.github.tomakehurst.wiremock.client.WireMock.*;
import static com.github.tomakehurst.wiremock.core.WireMockConfiguration.options;
import static com.github.tomakehurst.wiremock.stubbing.Scenario.STARTED;

public class RetryHandlerTest {

    @Test
    public void testRetryMechanism() {

        // Step 1: Start WireMock on a random port
        WireMockServer mockServer =
                new WireMockServer(options().dynamicPort());

        mockServer.start();

        try {

            // Step 2: Mock API always returns HTTP 503
            mockServer.stubFor(
                    get(urlEqualTo("/booking/5"))
                            .willReturn(
                                    aResponse().withStatus(503)
                            )
            );

            System.out.println(
                    "Mock Server URL: " + mockServer.baseUrl()
            );

            // Step 3: Call RetryHandler
            Response response = RetryHandler.getRetry(
                    () -> RestAssured.given()
                            .baseUri(mockServer.baseUrl())
                            .get("/booking/5"),
                    3
            );

            // Step 4: Validate final response
            Assert.assertEquals(
                    response.getStatusCode(),
                    503,
                    "Final response should be 503"
            );

            // Step 5: Verify exactly 3 API requests
            mockServer.verify(
                    3,
                    getRequestedFor(urlEqualTo("/booking/5"))
            );

            System.out.println(
                    "Retry mechanism verified successfully!"
            );

        } finally {

            // Step 6: Stop WireMock server
            mockServer.stop();
        }
    }

    @Test
    public void testRetryUntilSuccess() {

        WireMockServer mockServer =
                new WireMockServer(options().dynamicPort());

        mockServer.start();

        try {
            // First two requests return 503
            // Third and subsequent requests return 200
            mockServer.stubFor(
                    get(urlEqualTo("/booking/5"))
                            .inScenario("Retry Scenario")
                            .whenScenarioStateIs(STARTED)
                            .willReturn(aResponse().withStatus(503))
                            .willSetStateTo("Second Attempt")
            );

            mockServer.stubFor(
                    get(urlEqualTo("/booking/5"))
                            .inScenario("Retry Scenario")
                            .whenScenarioStateIs("Second Attempt")
                            .willReturn(aResponse().withStatus(503))
                            .willSetStateTo("Success")
            );

            mockServer.stubFor(
                    get(urlEqualTo("/booking/5"))
                            .inScenario("Retry Scenario")
                            .whenScenarioStateIs("Success")
                            .willReturn(aResponse().withStatus(200))
            );

            Response response = RetryHandler.getRetry(
                    () -> RestAssured.given()
                            .baseUri(mockServer.baseUrl())
                            .get("/booking/5"),
                    3
            );

            Assert.assertEquals(response.getStatusCode(), 200);

            mockServer.verify(
                    3,
                    getRequestedFor(urlEqualTo("/booking/5"))
            );

        } finally {
            mockServer.stop();
        }
    }

}
