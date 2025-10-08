// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.logs.CloudWatchLogQuery;
import com.example.logs.CloudWatchLogsSearch;
import com.example.logs.DescribeSubscriptionFilters;
import com.example.logs.FilterLogEvents;
import com.example.logs.GetLogEvents;
import com.example.logs.PutLogEvents;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatchlogs.CloudWatchLogsClient;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class CloudWatchLogsTest {
    private static final Logger logger = LoggerFactory.getLogger(CloudWatchLogsTest.class);
    private static CloudWatchLogsClient logsClient;

    private static String logGroupName = "";
    private static String logStreamName = "";
    private static String pattern = "";


    @BeforeAll
    public static void setUp() throws IOException {
       logsClient = CloudWatchLogsClient.builder()
                .region(Region.US_EAST_1)
                .build();

       logGroupName = "WeathertopJavaContainerLogs";
       logStreamName = "weathertop-java-stream";
       pattern = "INFO";
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testSearchLogStreams() {
        assertDoesNotThrow(() -> CloudWatchLogsSearch.searchLogStreamsAndFilterEvents(logsClient, logGroupName, logStreamName, pattern));
        logger.info(" Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeMostRecent() {
        assertDoesNotThrow(() -> CloudWatchLogQuery.describeMostRecentLogStream(logsClient, logGroupName));
        logger.info(" Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeSubscriptionFilters() {
        assertDoesNotThrow(() -> DescribeSubscriptionFilters.describeFilters(logsClient, logGroupName));
        logger.info(" Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testterFilterLogEvents() {
        Long startTime = Instant.now().minus(7, ChronoUnit.DAYS).toEpochMilli();
        Long endTime = Instant.now().toEpochMilli();
        assertDoesNotThrow(() -> FilterLogEvents.filterCWLogEvents(logsClient, logGroupName, startTime, endTime));
        logger.info(" Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testGetCWLogEvents() {
        assertDoesNotThrow(() -> GetLogEvents.getCWLogEvents(logsClient, logGroupName, logStreamName));
        logger.info(" Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testputCWLogEvents() {
        assertDoesNotThrow(() -> PutLogEvents.putCWLogEvents(logsClient, logGroupName, logStreamName));
        logger.info(" Test 6 passed");
    }
}
