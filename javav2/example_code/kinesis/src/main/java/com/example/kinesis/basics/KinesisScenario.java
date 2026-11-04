// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis.basics;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.Consumer;
import software.amazon.awssdk.services.kinesis.model.ConsumerStatus;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResultEntry;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamDescriptionSummary;
import software.amazon.awssdk.services.kinesis.model.StreamSummary;
import software.amazon.awssdk.services.kinesis.model.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.CompletionException;

// snippet-start:[kinesis.java2.scenario.main]
/**
 * Demonstrates Amazon Kinesis Data Streams basics: list and inspect streams
 * and shards, write a batch of records, read them back with a shard iterator,
 * manage tags, and register and deregister an enhanced fan-out consumer.
 * <p>
 * The scenario creates a stream during setup and deletes it during cleanup.
 * </p>
 */
public class KinesisScenario {

    private static final Logger logger = LoggerFactory.getLogger(KinesisScenario.class);
    private static final String DASHES = "-".repeat(80);
    private static final String CONSUMER_NAME = "sdk-example-consumer";

    public static void main(String[] args) {
        try (KinesisActions actions = new KinesisActions()) {
            runScenario(actions);
        } catch (RuntimeException e) {
            logger.error("The Kinesis scenario failed: {}", e.getMessage());
        }
    }

    /**
     * Runs the scenario steps in order and always cleans up the resources it created.
     *
     * @param actions the {@link KinesisActions} wrapper used to call Kinesis
     */
    public static void runScenario(KinesisActions actions) {
        String streamName = "sdk-example-stream-" + UUID.randomUUID().toString().substring(0, 8);
        boolean streamCreated = false;
        String consumerArn = null;

        logger.info(DASHES);
        logger.info("Welcome to the Amazon Kinesis Data Streams Scenario.");
        logger.info("This scenario demonstrates advanced stream management, batch data");
        logger.info("ingestion, shard inspection, tagging, and enhanced fan-out consumers.");
        logger.info(DASHES);

        try {
            // Setup: create a stream with one shard and wait for it to become ACTIVE.
            logger.info("Setting up resources...");
            logger.info("Creating Kinesis stream: {}", streamName);
            actions.createStreamAsync(streamName, 1).join();
            streamCreated = true;
            logger.info("Waiting for stream to become ACTIVE...");
            actions.waitForStreamActiveAsync(streamName).join();
            logger.info("Stream is now ACTIVE.");
            logger.info(DASHES);

            // Step 1: List streams and verify that the new stream is present.
            logger.info("Step 1: Listing streams in your account...");
            List<StreamSummary> streams = actions.listStreamsAsync().join();
            boolean found = false;
            for (StreamSummary stream : streams) {
                logger.info("  Stream: {} (Status: {}, Mode: {})", stream.streamName(),
                    stream.streamStatus(), stream.streamModeDetails().streamMode());
                found = found || streamName.equals(stream.streamName());
            }
            logger.info("Found {} stream(s). Our scenario stream is {}.", streams.size(),
                found ? "present" : "not yet listed");
            logger.info(DASHES);

            // Step 2: Describe the stream summary and save the stream ARN.
            logger.info("Step 2: Describing stream summary...");
            StreamDescriptionSummary summary = actions.describeStreamSummaryAsync(streamName).join();
            String streamArn = summary.streamARN();
            logger.info("  Stream Name: {}", summary.streamName());
            logger.info("  Stream ARN: {}", streamArn);
            logger.info("  Status: {}", summary.streamStatus());
            logger.info("  Retention Period: {} hours", summary.retentionPeriodHours());
            logger.info("  Open Shard Count: {}", summary.openShardCount());
            logger.info("  Encryption: {}", summary.encryptionType());
            logger.info("  Stream Mode: {}", summary.streamModeDetails().streamMode());
            logger.info(DASHES);

            // Step 3: List the shards and save the first shard ID.
            logger.info("Step 3: Listing shards in the stream...");
            List<Shard> shards = actions.listShardsAsync(streamName).join();
            for (Shard shard : shards) {
                logger.info("  Shard ID: {}", shard.shardId());
                logger.info("    Hash Key Range: {} - {}", shard.hashKeyRange().startingHashKey(),
                    shard.hashKeyRange().endingHashKey());
                logger.info("    Starting Sequence Number: {}",
                    shard.sequenceNumberRange().startingSequenceNumber());
            }
            logger.info("Found {} shard(s).", shards.size());
            String shardId = shards.get(0).shardId();
            logger.info(DASHES);

            // Step 4: Write a batch of simulated sensor readings with PutRecords.
            logger.info("Step 4: Putting 5 records into the stream using PutRecords...");
            List<PutRecordsRequestEntry> entries = new ArrayList<>();
            for (String[] reading : sensorReadings()) {
                entries.add(PutRecordsRequestEntry.builder()
                    .partitionKey(reading[0])
                    .data(SdkBytes.fromUtf8String(reading[1]))
                    .build());
            }
            PutRecordsResponse putResponse = actions.putRecordsAsync(streamName, entries).join();
            List<PutRecordsResultEntry> results = putResponse.records();
            for (int i = 0; i < results.size(); i++) {
                PutRecordsResultEntry result = results.get(i);
                if (result.errorCode() == null) {
                    logger.info("  Record {}: ShardId={}, SequenceNumber={}", i + 1, result.shardId(),
                        result.sequenceNumber());
                } else {
                    logger.warn("  Record {} failed: {} - {}", i + 1, result.errorCode(),
                        result.errorMessage());
                }
            }
            logger.info("Successfully put {} record(s) with {} failures.",
                results.size() - putResponse.failedRecordCount(), putResponse.failedRecordCount());
            logger.info(DASHES);

            // Step 5: Get a shard iterator and read the records back.
            logger.info("Step 5: Getting shard iterator (TRIM_HORIZON) and reading records...");
            String shardIterator = actions.getShardIteratorAsync(streamName, shardId,
                ShardIteratorType.TRIM_HORIZON).join();
            logger.info("  Obtained shard iterator for {}.", shardId);
            logger.info("  Reading records from the stream...");
            int recordsRead = readRecords(actions, shardIterator, entries.size());
            logger.info("Read {} record(s) from the stream.", recordsRead);
            logger.info(DASHES);

            // Step 6: Add tags to the stream.
            logger.info("Step 6: Adding tags to the stream...");
            Map<String, String> tags = new LinkedHashMap<>();
            tags.put("Project", "kinesis-demo");
            tags.put("Environment", "development");
            tags.put("CostCenter", "12345");
            actions.addTagsToStreamAsync(streamName, tags).join();
            tags.forEach((key, value) -> logger.info("  Added tag: {} = {}", key, value));
            logger.info("Tags added successfully. Tags help you organize and track costs for streams.");
            logger.info(DASHES);

            // Step 7: List the tags on the stream.
            logger.info("Step 7: Listing tags for the stream...");
            List<Tag> streamTags = actions.listTagsForStreamAsync(streamName).join();
            streamTags.forEach(tag -> logger.info("  Tag: {} = {}", tag.key(), tag.value()));
            logger.info("Found {} tag(s).", streamTags.size());
            logger.info(DASHES);

            // Step 8: Remove a tag and verify the remaining tags.
            logger.info("Step 8: Removing the CostCenter tag from the stream...");
            actions.removeTagsFromStreamAsync(streamName, List.of("CostCenter")).join();
            logger.info("Tag removed successfully.");
            logger.info("Verifying remaining tags...");
            List<Tag> remainingTags = actions.listTagsForStreamAsync(streamName).join();
            remainingTags.forEach(tag -> logger.info("  Tag: {} = {}", tag.key(), tag.value()));
            logger.info("Confirmed: {} tag(s) remaining after removal.", remainingTags.size());
            logger.info(DASHES);

            // Step 9: Register an enhanced fan-out consumer and wait for it to become ACTIVE.
            logger.info("Step 9: Registering an enhanced fan-out consumer...");
            Consumer consumer = actions.registerStreamConsumerAsync(streamArn, CONSUMER_NAME).join();
            consumerArn = consumer.consumerARN();
            logger.info("  Consumer Name: {}", consumer.consumerName());
            logger.info("  Consumer ARN: {}", consumerArn);
            logger.info("  Status: {}", consumer.consumerStatus());
            logger.info("Waiting for consumer to become ACTIVE...");
            waitForConsumerActive(actions, consumerArn);
            logger.info("Enhanced fan-out consumer registered successfully.");
            logger.info("Each enhanced fan-out consumer gets dedicated read throughput of "
                + "2 MiB/second per shard.");
            logger.info(DASHES);

            // Step 10: Deregister the consumer.
            logger.info("Step 10: Deregistering the enhanced fan-out consumer...");
            actions.deregisterStreamConsumerAsync(consumerArn).join();
            consumerArn = null;
            logger.info("Consumer '{}' deregistered successfully.", CONSUMER_NAME);
            logger.info("Enhanced fan-out charges will stop for this consumer.");
            logger.info(DASHES);

        } catch (RuntimeException e) {
            logger.error("The scenario encountered an error: {}", e.getMessage());
            throw e;
        } finally {
            cleanUp(actions, streamName, streamCreated, consumerArn);
        }

        logger.info("Amazon Kinesis Data Streams scenario complete!");
    }

    /**
     * Reads records with GetRecords until the expected number of records is
     * returned or the attempts are used up.
     */
    private static int readRecords(KinesisActions actions, String shardIterator, int expected) {
        int recordsRead = 0;
        String iterator = shardIterator;
        for (int attempt = 0; attempt < 10 && recordsRead < expected && iterator != null; attempt++) {
            GetRecordsResponse response = actions.getRecordsAsync(iterator, 25).join();
            for (Record record : response.records()) {
                recordsRead++;
                logger.info("  Record {}: {}", recordsRead, record.data().asUtf8String());
            }
            iterator = response.nextShardIterator();
            if (recordsRead < expected) {
                // Newly written records can take a moment to become readable, and a
                // GetRecords call can return an empty batch, so pause before polling again.
                sleep(1000);
            }
        }
        return recordsRead;
    }

    /**
     * Polls DescribeStreamConsumer until the consumer is ACTIVE. The SDK has no
     * waiter for stream consumers.
     */
    private static void waitForConsumerActive(KinesisActions actions, String consumerArn) {
        for (int attempt = 0; attempt < 30; attempt++) {
            ConsumerStatus status = actions.describeStreamConsumerAsync(consumerArn).join().consumerStatus();
            if (status == ConsumerStatus.ACTIVE) {
                logger.info("  Consumer status: {}", status);
                return;
            }
            // Consumer registration takes several seconds, so wait between status checks.
            sleep(2000);
        }
        throw new IllegalStateException("Consumer did not become ACTIVE within 60 seconds.");
    }

    /**
     * Deletes the resources that the scenario created. Each step has its own
     * try/catch so that a failure does not hide the original error.
     */
    private static void cleanUp(KinesisActions actions, String streamName, boolean streamCreated,
                                String consumerArn) {
        logger.info(DASHES);
        logger.info("Cleaning up resources...");
        if (consumerArn != null) {
            logger.info("Ensuring consumer is deregistered...");
            try {
                actions.deregisterStreamConsumerAsync(consumerArn).join();
                logger.info("  Consumer deregistered.");
            } catch (CompletionException e) {
                if (e.getCause() instanceof ResourceNotFoundException) {
                    logger.info("  Consumer already deregistered.");
                } else {
                    logger.warn("  Failed to deregister consumer: {}", e.getMessage());
                }
            }
        }
        if (streamCreated) {
            try {
                logger.info("Deleting stream: {}", streamName);
                actions.deleteStreamAsync(streamName).join();
                logger.info("Waiting for stream deletion to complete...");
                actions.waitForStreamDeletedAsync(streamName).join();
                logger.info("Stream deleted successfully.");
            } catch (CompletionException e) {
                logger.warn("Failed to delete stream {}: {}", streamName, e.getMessage());
            }
        }
        logger.info(DASHES);
    }

    /**
     * Returns static sample IoT sensor readings (not live data) as
     * {partition key, JSON payload} pairs.
     * The sensor ID is the partition key so that readings from the same sensor
     * go to the same shard.
     */
    private static List<String[]> sensorReadings() {
        return List.of(
            new String[]{"sensor-001",
                "{\"sensor_id\": \"sensor-001\", \"temperature\": 72.5, \"timestamp\": \"2024-01-15T10:00:00Z\"}"},
            new String[]{"sensor-002",
                "{\"sensor_id\": \"sensor-002\", \"temperature\": 68.3, \"timestamp\": \"2024-01-15T10:00:01Z\"}"},
            new String[]{"sensor-001",
                "{\"sensor_id\": \"sensor-001\", \"temperature\": 73.1, \"timestamp\": \"2024-01-15T10:00:02Z\"}"},
            new String[]{"sensor-003",
                "{\"sensor_id\": \"sensor-003\", \"temperature\": 75.8, \"timestamp\": \"2024-01-15T10:00:03Z\"}"},
            new String[]{"sensor-002",
                "{\"sensor_id\": \"sensor-002\", \"temperature\": 67.9, \"timestamp\": \"2024-01-15T10:00:04Z\"}"});
    }

    private static void sleep(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
            throw new IllegalStateException("Interrupted while waiting.", e);
        }
    }
}
// snippet-end:[kinesis.java2.scenario.main]
