// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.regions.Region;

import java.time.Duration;


public class DynamoDbMetricsExample {
    private static final Logger LOGGER = LoggerFactory.getLogger(DynamoDbMetricsExample.class);

    public static void main(String[] args) {
        String namespace = "DynamoDBMetricsExample";

        GreetingsSender greetingsSender = new GreetingsSender(Region.US_EAST_1,
                Duration.ofMinutes(1L),
                namespace);
        // Create the DynamoDB table, if it doesn't already exist.
        greetingsSender.createTable();

        try {
            greetingsSender.sendGreetings(999, 10000L);
        } catch (InterruptedException | SdkException e) {
            LOGGER.error("Error", e);
        } finally {
            // Comment out the following statement if you intend to review CloudWatch Contributor Insights.
            greetingsSender.deleteTable();
            greetingsSender.close();
        }
    }
}