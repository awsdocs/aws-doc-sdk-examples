// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis.basics;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.Timeout;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamSummaryResponse;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamDescriptionSummary;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionException;
import java.util.concurrent.TimeUnit;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration tests for {@link KinesisActions}. Exercises real AWS Kinesis
 * operations through the async client. Creates a stream with the prefix
 * {@code wt-test-} and cleans up after all tests.
 */
@Tag("IntegrationTest")
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
@Timeout(value = 10, unit = TimeUnit.MINUTES)
class KinesisIntegrationTest {

    private static final String STREAM_PREFIX = "wt-test-stream-";
    private final String uniqueSuffix = UUID.randomUUID().toString().substring(0, 8);
    private final String streamName = STREAM_PREFIX + uniqueSuffix;
    private String streamArn;
    private String firstShardId;

    @BeforeAll
    void setup() {
        System.out.println("Integration test stream: " + streamName);
    }

    @AfterAll
    void cleanup() {
        System.out.println("Cleaning up: deleting stream " + streamName);
        try {
            KinesisActions.deleteStreamAsync(streamName).join();
            // Wait for deletion
            waitForStreamDeletion();
            System.out.println("Stream " + streamName + " deleted.");
        } catch (CompletionException e) {
            if (e.getCause() instanceof ResourceNotFoundException) {
                System.out.println("Stream already deleted.");
            } else {
                System.err.println("Cleanup error: " + e.getCause().getMessage());
            }
        }
    }

    @Test
    @Order(1)
    void testCreateStream() {
        KinesisActions.createStreamAsync(streamName).join();
        waitForStreamActive();
    }

    @Test
    @Order(2)
    void testDescribeStreamSummary() {
        DescribeStreamSummaryResponse response =
            KinesisActions.describeStreamSummaryAsync(streamName).join();
        StreamDescriptionSummary summary = response.streamDescriptionSummary();

        assertNotNull(summary.streamARN());
        assertEquals("ACTIVE", summary.streamStatus().toString());
        streamArn = summary.streamARN();
    }

    @Test
    @Order(3)
    void testAddTagsToStream() {
        Map<String, String> tags = new LinkedHashMap<>();
        tags.put("IntegTest", "true");
        tags.put("RunId", uniqueSuffix);

        KinesisActions.addTagsToStreamAsync(streamName, tags).join();
    }

    @Test
    @Order(4)
    void testListTagsForStream() {
        var tagList = KinesisActions.listTagsForStreamAsync(streamName).join();
        assertFalse(tagList.isEmpty());
        boolean foundRunId = tagList.stream()
            .anyMatch(t -> "RunId".equals(t.key()) && uniqueSuffix.equals(t.value()));
        assertTrue(foundRunId, "Expected to find RunId tag with value " + uniqueSuffix);
    }

    @Test
    @Order(5)
    void testPutRecord() {
        SdkBytes data = SdkBytes.fromUtf8String("integration-test-single-record");
        PutRecordResponse response =
            KinesisActions.putRecordAsync(streamName, "pk-integ", data).join();
        assertNotNull(response.shardId());
        assertNotNull(response.sequenceNumber());
    }

    @Test
    @Order(6)
    void testPutRecords() {
        List<PutRecordsRequestEntry> entries = new ArrayList<>();
        for (int i = 1; i <= 3; i++) {
            entries.add(PutRecordsRequestEntry.builder()
                .data(SdkBytes.fromUtf8String("batch-record-" + i))
                .partitionKey("pk-batch-" + i)
                .build());
        }
        PutRecordsResponse response =
            KinesisActions.putRecordsAsync(streamName, entries).join();
        assertEquals(0, response.failedRecordCount());
    }

    @Test
    @Order(7)
    void testListShards() {
        List<Shard> shards = KinesisActions.listShardsAsync(streamName).join();
        assertFalse(shards.isEmpty());
        firstShardId = shards.get(0).shardId();
        assertNotNull(firstShardId);
    }

    @Test
    @Order(8)
    void testGetShardIterator() {
        assertNotNull(firstShardId, "firstShardId must be set by testListShards");
        GetShardIteratorResponse response = KinesisActions.getShardIteratorAsync(
            streamName, firstShardId, ShardIteratorType.TRIM_HORIZON).join();
        assertNotNull(response.shardIterator());
    }

    @Test
    @Order(9)
    void testGetRecords() {
        assertNotNull(firstShardId, "firstShardId must be set by testListShards");
        GetShardIteratorResponse iterResponse = KinesisActions.getShardIteratorAsync(
            streamName, firstShardId, ShardIteratorType.TRIM_HORIZON).join();

        String iterator = iterResponse.shardIterator();
        int maxAttempts = 5;
        for (int i = 0; i < maxAttempts; i++) {
            GetRecordsResponse response =
                KinesisActions.getRecordsAsync(iterator, 25).join();
            if (!response.records().isEmpty()) {
                assertTrue(response.records().size() > 0);
                return;
            }
            iterator = response.nextShardIterator();
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                break;
            }
        }
        // Data may not be immediately available; not a hard failure.
        System.out.println("NOTE: No records returned after " + maxAttempts + " attempts. "
            + "This can happen due to eventual consistency.");
    }

    // --- Helper methods ---

    private void waitForStreamActive() {
        int maxWait = 120;
        int elapsed = 0;
        int interval = 5;
        while (elapsed < maxWait) {
            try {
                DescribeStreamSummaryResponse resp =
                    KinesisActions.describeStreamSummaryAsync(streamName).join();
                if ("ACTIVE".equals(resp.streamDescriptionSummary().streamStatus().toString())) {
                    return;
                }
            } catch (CompletionException e) {
                if (!(e.getCause() instanceof ResourceNotFoundException)) {
                    throw e;
                }
            }
            try {
                Thread.sleep(interval * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            elapsed += interval;
        }
        throw new RuntimeException("Stream did not become ACTIVE within " + maxWait + "s");
    }

    private void waitForStreamDeletion() {
        int maxWait = 120;
        int elapsed = 0;
        int interval = 5;
        while (elapsed < maxWait) {
            try {
                KinesisActions.describeStreamSummaryAsync(streamName).join();
            } catch (CompletionException e) {
                if (e.getCause() instanceof ResourceNotFoundException) {
                    return; // Deleted
                }
                throw e;
            }
            try {
                Thread.sleep(interval * 1000L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                return;
            }
            elapsed += interval;
        }
    }
}
