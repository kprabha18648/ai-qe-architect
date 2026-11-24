package com.aiqe.logging;

import io.restassured.filter.Filter;
import io.restassured.filter.FilterContext;
import io.restassured.response.Response;
import io.restassured.specification.FilterableRequestSpecification;
import io.restassured.specification.FilterableResponseSpecification;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class ApiLoggingFilter implements Filter {

    private static final Logger logger =
            LoggerFactory.getLogger(ApiLoggingFilter.class);


    @Override
    public Response filter(
            FilterableRequestSpecification requestSpec,
            FilterableResponseSpecification responseSpec,
            FilterContext ctx) {
        logger.info("Request Method: {}", requestSpec.getMethod());
        logger.info("Request URL: {}", requestSpec.getURI());
        logger.info("Request Content Type: {}", requestSpec.getContentType());
        logger.info("Request Body Present: {}", requestSpec.getBody() != null);
        long startTime = System.currentTimeMillis();

        Object requestBody = requestSpec.getBody();

        if (requestBody != null) {
            logger.info("Request Body: {}", requestBody);
        } else {
            logger.info("Request Body: <empty>");
        }
        Response response = ctx.next(requestSpec, responseSpec);
        long duration = System.currentTimeMillis() - startTime;

        logger.info("Response Time: {} ms", duration);

        logger.info("Response Status Code: {}", response.getStatusCode());
        logger.info("Response Content Type: {}", response.getContentType());
        logger.info("Response Body: {}", response.asString());
        Object responseBody = requestSpec.getBody();
        if (responseBody != null) {
            logger.info("Response Body: {}", responseBody);
        } else {
            logger.info("Response Body: <empty>");
        }


        return response;
    }
}
