package com.aiqe.utils;

import io.restassured.response.Response;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;

public class SchemaValidator {

    public static void validate(Response response, String schemaFileName ){

        String schemaPath = "schemas/" +  schemaFileName;

        response.then().body(matchesJsonSchemaInClasspath(schemaPath));

    }
}
