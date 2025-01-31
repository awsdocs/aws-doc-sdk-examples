// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.example;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchClient;
import software.amazon.awssdk.services.cloudwatch.model.ListMetricsResponse;

import java.time.Duration;
import java.util.Random;

class GreetingsSenderTest {
    private final static Logger LOGGER = LoggerFactory.getLogger(GreetingsSenderTest.class);
    private final static Region REGION = Region.US_EAST_1;
    private final static CloudWatchClient cloudWatchClient = CloudWatchClient.builder().region(REGION).build();

    @Test
    @Tag("IntegrationTest")
    void sendGreetings() {
        String namespace = "DynamoDBMetricsExample" + "Text" + new Random(1000);

        GreetingsSender greetingsSender = new GreetingsSender(REGION, Duration.ofSeconds(15L), namespace);
        greetingsSender.createTable();
        try {
            greetingsSender.sendGreetings(5, 2500L);
            Thread.sleep(Duration.ofSeconds(30L).toMillis());
            ListMetricsResponse listMetricsResponse = cloudWatchClient.listMetrics(b -> b
                    .namespace(namespace)
                    .metricName(CoreMetric.API_CALL_DURATION.name()));

            Assertions.assertFalse(listMetricsResponse.metrics().isEmpty(), "No ApiCallDuration metric received by CloudWatch");

        } catch (InterruptedException | SdkException e) {
            LOGGER.error(e.getMessage(), e);
        } finally {
            greetingsSender.deleteTable();
            greetingsSender.close();
        }
    }
}