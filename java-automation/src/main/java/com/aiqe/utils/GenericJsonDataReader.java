
package com.aiqe.utils;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.io.InputStream;

public class GenericJsonDataReader {

    public static JsonNode readJson(String fileName)
            throws IOException {

        ObjectMapper mapper = new ObjectMapper();

        String filePath = "testdata/" + fileName;

        try (InputStream inputStream =
                     GenericJsonDataReader.class
                             .getClassLoader()
                             .getResourceAsStream(filePath)) {

            if (inputStream == null) {
                throw new IOException(
                        "JSON file not found: " + filePath
                );
            }

            return mapper.readTree(inputStream);
        }
    }
}
