// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package org.example;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.exception.SdkException;
import software.amazon.awssdk.core.exception.SdkServiceException;
import software.amazon.awssdk.core.metrics.CoreMetric;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbEnhancedClient;
import software.amazon.awssdk.enhanced.dynamodb.DynamoDbTable;
import software.amazon.awssdk.enhanced.dynamodb.TableSchema;
import software.amazon.awssdk.metrics.MetricPublisher;
import software.amazon.awssdk.metrics.publishers.cloudwatch.CloudWatchMetricPublisher;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.cloudwatch.CloudWatchAsyncClient;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.dynamodb.waiters.DynamoDbWaiter;

import java.time.Duration;
import java.util.Objects;
import java.util.Optional;

public class GreetingsSender implements AutoCloseable {
    public static final Logger LOGGER = LoggerFactory.getLogger(GreetingsSender.class);
    static final String TABLE_NAME = "Greetings";
    private final DynamoDbTable<Greeting> greetingsTable;
    private final DynamoDbClient dynamoDbClient;
    private final CloudWatchAsyncClient cloudWatchAsyncClient;
    private final MetricPublisher metricPublisher;

    public GreetingsSender(final Region region, Duration uploadFrequency, String namespace) {
        cloudWatchAsyncClient = CloudWatchAsyncClient.builder()
                .region(region)
                .build();

        metricPublisher = CloudWatchMetricPublisher.builder()
                .cloudWatchClient(cloudWatchAsyncClient)
                .uploadFrequency(uploadFrequency)
                .namespace(namespace)
                .detailedMetrics(
                        CoreMetric.API_CALL_DURATION)
                .build();

        dynamoDbClient = DynamoDbClient.builder()
                .region(region)
                .overrideConfiguration(c -> c.addMetricPublisher(metricPublisher))
                .build();

        DynamoDbEnhancedClient enhancedClient = DynamoDbEnhancedClient.builder()
                .dynamoDbClient(dynamoDbClient)
                .build();

        greetingsTable = enhancedClient.table(TABLE_NAME, TableSchema.fromBean(Greeting.class));
    }

    public void sendGreetings(Integer numberOfGreetings, Long timeBetweenSendsInMillis) throws InterruptedException {
        final String greetingZeroId = "greeting-00";
        final Greeting greetingZero = new Greeting();
        greetingZero.setId(greetingZeroId);
        greetingZero.setName(greetingZeroId);

        for (int i = 0; i < numberOfGreetings; i++) {
            LOGGER.info("Sending greeting #{}", i + 1);
            final String greetingId = String.format("greeting-%2d", i);
            final Greeting greeting = new Greeting();
            greeting.setId(greetingId);
            greeting.setName(greetingId);

            putGreeting(greeting);
            putGreeting(greetingZero);
            getGreeting(greetingId);
            getGreeting(greetingZeroId);

            // Briefly pause the loop to extend the requests over a longer period to generate metrics
            // for this example.
            Thread.sleep(timeBetweenSendsInMillis);
        }
    }

    private void putGreeting(final Greeting greeting) {
        if (Objects.isNull(greeting)) {
            throw new NullPointerException("Attempted to put a null greeting.");
        }

        LOGGER.debug("Persisting the following greeting to DynamoDB. {}.", greeting);

        try {
            greetingsTable.putItem(greeting);
        } catch (SdkException exception) {
            // The SDK exception will be an `SdkServiceException` in the case that the exception returned from the
            // service, in this case DynamoDB. Otherwise, e.g. in the case of connection timeouts, there will not be
            // a request ID.
            String requestId = "NONE";
            if (exception instanceof SdkServiceException) {
                requestId = ((SdkServiceException) exception).requestId();
            }
            LOGGER.error(
                    "Encountered an exception with DynamoDB when attempting to put the following greeting. {}."
                            + "Request ID: {}.", greeting, requestId, exception
            );
            throw exception;
        }
    }

    private Optional<Greeting> getGreeting(final String greetingId) {
        if (Objects.isNull(greetingId)) {
            throw new NullPointerException("Attempted to get a greeting with a null ID.");
        }

        LOGGER.debug("Looking up the following greeting from DynamoDB. {}.", greetingId);

        final Greeting lookup = new Greeting();
        lookup.setId(greetingId);

        try {
            final Greeting returned = greetingsTable.getItem(lookup);
            return Optional.ofNullable(returned);
        } catch (SdkException exception) {
            String requestId = "NONE";
            if (exception instanceof SdkServiceException) {
                requestId = ((SdkServiceException) exception).requestId();
            }
            LOGGER.error(
                    "Encountered an exception with DynamoDB when attempting to retrieve the following greeting. {}."
                            + "Request ID: {}", greetingId, requestId, exception
            );
            throw exception;
        }
    }

    public void createTable() {
        LOGGER.info("Creating {} table.", TABLE_NAME);
        greetingsTable.createTable();
        try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build()) {
            waiter.waitUntilTableExists(b -> b.tableName(TABLE_NAME));
        }
        LOGGER.info("{} table created.", TABLE_NAME);

    }

    public void deleteTable() {
        LOGGER.info("Deleting {} table.", TABLE_NAME);
        greetingsTable.deleteTable();
        try (DynamoDbWaiter waiter = DynamoDbWaiter.builder().client(dynamoDbClient).build()) {
            waiter.waitUntilTableNotExists(b -> b.tableName(TABLE_NAME));
        }
        LOGGER.info("{} table deleted.", TABLE_NAME);
    }

    @Override
    public void close() {
        metricPublisher.close();
        cloudWatchAsyncClient.close();
        dynamoDbClient.close();
    }
}
