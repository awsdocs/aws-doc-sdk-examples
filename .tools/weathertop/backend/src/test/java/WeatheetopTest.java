// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.weathertop.service.GetLogMessages;
import com.weathertop.service.HistoricalSDK;
import com.weathertop.service.QueryLatestFromS3;
import com.weathertop.service.SDKStats;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class WeatheetopTest {

    @Test
    @Order(1)
    public void testStats() throws com.fasterxml.jackson.core.JsonProcessingException {
        SDKStats langStats = new SDKStats();
        String[] langs = {"java", "kotlin", "dotnetv4", "php", "javascriptv3", "python", "gov2", "rustv1"};
        String JSON = assertDoesNotThrow(() -> langStats.getCoverageSummary(langs));
        System.out.println(JSON);
    }

    @Test
    @Order(2)
    public void testGetLogs() throws com.fasterxml.jackson.core.JsonProcessingException {
        GetLogMessages logs = new GetLogMessages();
        String lang = "python";

        // Ensure the method does not throw an exception
        String json = assertDoesNotThrow(() -> logs.getHistoricalSummary(lang));

        // Print the result
        System.out.println(json);
    }


    @Test
    @Order(3)
    public void testS3Query() throws com.fasterxml.jackson.core.JsonProcessingException {
        QueryLatestFromS3 wt = new QueryLatestFromS3();
        String json = assertDoesNotThrow(() -> wt.readJsonFromS3("python"));
        System.out.println(json);
    }

    @Test
    @Order(4)
    public void testGetHistoryData() throws com.fasterxml.jackson.core.JsonProcessingException {
        HistoricalSDK history = new HistoricalSDK();
        String json = assertDoesNotThrow(() ->  history.getHistoricalSummary("java"));
        System.out.println(json);
    }
}
