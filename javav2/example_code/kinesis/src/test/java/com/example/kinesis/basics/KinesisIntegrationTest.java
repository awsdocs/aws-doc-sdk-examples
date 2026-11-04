// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis.basics;

import com.example.kinesis.HelloKinesis;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamDescriptionSummary;

import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for the Kinesis Hello example, the {@link KinesisActions}
 * wrapper, and {@link KinesisScenario}. These tests call AWS and create
 * Kinesis streams, which they delete when they finish.
 */
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Timeout(value = 10, unit = TimeUnit.MINUTES)
public class KinesisIntegrationTest {

    private static final Logger logger = LoggerFactory.getLogger(KinesisIntegrationTest.class);
    private final KinesisActions actions = new KinesisActions();

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testHelloKinesis() {
        assertDoesNotThrow(() -> {
            try (KinesisAsyncClient client = KinesisAsyncClient.create()) {
                HelloKinesis.listStreams(client);
            }
        });
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testKinesisActionsIntegration() {
        String streamName = "sdk-integ-stream-" + UUID.randomUUID().toString().substring(0, 8);
        try {
            assertDoesNotThrow(() -> {
                actions.createStreamAsync(streamName, 1).join();
                actions.waitForStreamActiveAsync(streamName).join();

                StreamDescriptionSummary summary = actions.describeStreamSummaryAsync(streamName).join();
                assertEquals("ACTIVE", summary.streamStatusAsString());

                List<Shard> shards = actions.listShardsAsync(streamName).join();
                assertFalse(shards.isEmpty(), "The stream should have at least one shard.");

                PutRecordsResponse putResponse = actions.putRecordsAsync(streamName, List.of(
                    PutRecordsRequestEntry.builder()
                        .partitionKey("sensor-001")
                        .data(SdkBytes.fromUtf8String("{\"sensor_id\": \"sensor-001\"}"))
                        .build())).join();
                assertEquals(0, putResponse.failedRecordCount());

                String iterator = actions.getShardIteratorAsync(streamName, shards.get(0).shardId(),
                    ShardIteratorType.TRIM_HORIZON).join();
                assertNotNull(iterator);
                actions.getRecordsAsync(iterator, 10).join();

                actions.addTagsToStreamAsync(streamName, Map.of("Project", "kinesis-demo",
                    "CostCenter", "12345")).join();
                actions.removeTagsFromStreamAsync(streamName, List.of("CostCenter")).join();
                List<software.amazon.awssdk.services.kinesis.model.Tag> tags = actions.listTagsForStreamAsync(streamName).join();
                assertTrue(tags.stream().anyMatch(t -> "Project".equals(t.key())));
                assertFalse(tags.stream().anyMatch(t -> "CostCenter".equals(t.key())));

                assertTrue(actions.listStreamsAsync().join().stream()
                    .anyMatch(s -> streamName.equals(s.streamName())));

                String consumerArn = actions.registerStreamConsumerAsync(summary.streamARN(),
                    "sdk-integ-consumer").join().consumerARN();
                assertNotNull(consumerArn);
                actions.deregisterStreamConsumerAsync(consumerArn).join();
            });
        } finally {
            // Deleting the stream also deletes any consumer still registered with it.
            try {
                actions.deleteStreamAsync(streamName).join();
                actions.waitForStreamDeletedAsync(streamName).join();
            } catch (RuntimeException e) {
                logger.warn("Cleanup of stream {} failed: {}", streamName, e.getMessage());
            }
        }
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testKinesisScenario() {
        assertDoesNotThrow(() -> KinesisScenario.runScenario(actions));
        logger.info("Test 3 passed");
    }
}
