
package com.aiqe.dataproviders;

import com.aiqe.annotations.TestDataFile;
import com.aiqe.utils.GenericJsonDataReader;
import com.fasterxml.jackson.databind.JsonNode;
import org.testng.annotations.DataProvider;

import java.io.IOException;
import java.lang.reflect.Method;

public class GenericJsonDataProvider {

    @DataProvider(name = "jsonData")
    public static Object[][] getJsonData(Method method)
            throws IOException {

        TestDataFile annotation =
                method.getAnnotation(TestDataFile.class);

        if (annotation == null) {
            throw new IllegalArgumentException(
                    "Missing @TestDataFile on " + method.getName()
            );
        }

        String fileName = annotation.value();

        JsonNode rootNode =
                GenericJsonDataReader.readJson(fileName);

        if (!rootNode.isArray()) {
            throw new IllegalArgumentException(
                    "Expected JSON array in: " + fileName
            );
        }

        int totalRecords = rootNode.size();

        Object[][] testData =
                new Object[totalRecords][1];

        for (int i = 0; i < totalRecords; i++) {
            testData[i][0] = rootNode.get(i);
        }

        return testData;
    }
}
