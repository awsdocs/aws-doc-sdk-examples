// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis.basics;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamSummaryResponse;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.ResourceInUseException;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamDescriptionSummary;
import software.amazon.awssdk.services.kinesis.model.Tag;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletionException;

// snippet-start:[kinesis.java2.scenario.main]
/**
 * Demonstrates the full lifecycle of a Kinesis data stream:
 * create, describe, tag, write, read, and delete — using 10 distinct API operations.
 * <p>
 * All API calls go through {@link KinesisActions} methods. No interactive input is used.
 * </p>
 */
public class KinesisScenario {

    public static void main(String[] args) {
        String randomSuffix = generateRandomSuffix(6);
        String streamName = "kinesis-basics-" + randomSuffix;

        System.out.println("==========================================================");
        System.out.println("  Amazon Kinesis Data Streams — Basics Scenario");
        System.out.println("==========================================================");
        System.out.println("Stream name: " + streamName);
        System.out.println();

        try {
            // Step 1 — Create the data stream
            System.out.println("Step 1: Creating stream: " + streamName + "...");
            try {
                KinesisActions.createStreamAsync(streamName).join();
            } catch (CompletionException e) {
                if (e.getCause() instanceof ResourceInUseException) {
                    System.out.println("Stream already exists. Proceeding...");
                } else {
                    throw e;
                }
            }

            // Wait for stream to become ACTIVE using the SDK waiter.
            // The waiter polls DescribeStream (requires DescribeStreamRequest).
            System.out.println("Waiting for stream to become ACTIVE...");
            DescribeStreamRequest describeStreamRequest = DescribeStreamRequest.builder()
                .streamName(streamName)
                .build();

            // Access the client's waiter through the actions setter is not available;
            // we build a temporary client reference through KinesisActions for the waiter.
            // The waiter is on the async client: waitUntilStreamExists(DescribeStreamRequest).
            waitForStreamExists(streamName);
            System.out.println("Stream " + streamName + " is now ACTIVE.");
            System.out.println();

            // Step 2 — Describe the stream
            System.out.println("Step 2: Describing stream...");
            DescribeStreamSummaryResponse describeResponse =
                KinesisActions.describeStreamSummaryAsync(streamName).join();
            StreamDescriptionSummary summary = describeResponse.streamDescriptionSummary();
            String streamArn = summary.streamARN();
            System.out.println("  Stream ARN       : " + streamArn);
            System.out.println("  Stream Status    : " + summary.streamStatus());
            System.out.println("  Stream Mode      : " + summary.streamModeDetails().streamMode());
            System.out.println("  Retention (hours): " + summary.retentionPeriodHours());
            System.out.println("  Open Shard Count : " + summary.openShardCount());
            System.out.println();

            // Step 3 — Tag the stream
            System.out.println("Step 3: Adding tags to stream...");
            Map<String, String> tags = new LinkedHashMap<>();
            tags.put("Environment", "Development");
            tags.put("Project", "KinesisBasics");
            KinesisActions.addTagsToStreamAsync(streamName, tags).join();
            System.out.println("Added tags to stream " + streamName + ".");
            System.out.println();

            // Step 4 — List the stream's tags
            System.out.println("Step 4: Listing tags for stream...");
            List<Tag> tagList = KinesisActions.listTagsForStreamAsync(streamName).join();
            for (Tag tag : tagList) {
                System.out.println("  Tag: " + tag.key() + " = " + tag.value());
            }
            System.out.println();

            // Step 5 — Put a single record
            System.out.println("Step 5: Putting a single record...");
            SdkBytes singleData = SdkBytes.fromUtf8String("Hello Kinesis - single record");
            PutRecordResponse putRecordResponse =
                KinesisActions.putRecordAsync(streamName, "partitionKey-1", singleData).join();
            System.out.println("  Shard ID       : " + putRecordResponse.shardId());
            System.out.println("  Sequence Number: " + putRecordResponse.sequenceNumber());
            System.out.println();

            // Step 6 — Put multiple records in a batch
            System.out.println("Step 6: Putting batch records (5 records)...");
            List<PutRecordsRequestEntry> batchRecords = new ArrayList<>();
            for (int i = 1; i <= 5; i++) {
                batchRecords.add(PutRecordsRequestEntry.builder()
                    .data(SdkBytes.fromUtf8String("Batch record #" + i))
                    .partitionKey("partitionKey-" + i)
                    .build());
            }
            PutRecordsResponse putRecordsResponse =
                KinesisActions.putRecordsAsync(streamName, batchRecords).join();
            if (putRecordsResponse.failedRecordCount() > 0) {
                System.out.println("  WARNING: " + putRecordsResponse.failedRecordCount()
                    + " record(s) failed.");
            }
            System.out.println("  Successfully wrote "
                + (putRecordsResponse.records().size() - putRecordsResponse.failedRecordCount())
                + " record(s).");
            System.out.println();

            // Step 7 — List shards (manual pagination)
            System.out.println("Step 7: Listing shards...");
            List<Shard> shards = KinesisActions.listShardsAsync(streamName).join();
            for (Shard shard : shards) {
                System.out.println("  Shard: " + shard.shardId()
                    + " | Hash range: " + shard.hashKeyRange().startingHashKey()
                    + " - " + shard.hashKeyRange().endingHashKey());
            }
            String firstShardId = shards.get(0).shardId();
            System.out.println();

            // Step 8 — Get a shard iterator
            System.out.println("Step 8: Getting shard iterator for shard " + firstShardId + "...");
            GetShardIteratorResponse shardIteratorResponse =
                KinesisActions.getShardIteratorAsync(streamName, firstShardId,
                    ShardIteratorType.TRIM_HORIZON).join();
            String shardIterator = shardIteratorResponse.shardIterator();
            System.out.println("Obtained shard iterator for shard " + firstShardId + ".");
            System.out.println();

            // Step 9 — Get records (retry up to 3 times if empty)
            System.out.println("Step 9: Reading records from shard...");
            int maxAttempts = 3;
            String currentIterator = shardIterator;
            for (int attempt = 1; attempt <= maxAttempts; attempt++) {
                GetRecordsResponse getRecordsResponse =
                    KinesisActions.getRecordsAsync(currentIterator, 25).join();
                List<Record> records = getRecordsResponse.records();

                if (!records.isEmpty()) {
                    for (Record record : records) {
                        System.out.println("  Record: partitionKey=" + record.partitionKey()
                            + ", data=" + record.data().asUtf8String()
                            + ", seq=" + record.sequenceNumber()
                            + ", arrival=" + record.approximateArrivalTimestamp());
                    }
                    System.out.println("  Total records retrieved: " + records.size());
                    System.out.println("  Millis behind latest: "
                        + getRecordsResponse.millisBehindLatest());
                    break;
                } else {
                    System.out.println("  Attempt " + attempt + ": No records yet. Waiting 1 second...");
                    if (attempt < maxAttempts) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException ie) {
                            Thread.currentThread().interrupt();
                            break;
                        }
                        currentIterator = getRecordsResponse.nextShardIterator();
                    }
                }
            }
            System.out.println();

        } finally {
            // Cleanup — Step 10: Delete the stream
            System.out.println("Step 10: Deleting stream " + streamName + "...");
            try {
                KinesisActions.deleteStreamAsync(streamName).join();
                System.out.println("Waiting for stream to be deleted...");
                waitForStreamNotExists(streamName);
                System.out.println("Stream " + streamName + " has been deleted.");
            } catch (CompletionException e) {
                if (e.getCause() instanceof ResourceNotFoundException) {
                    System.out.println("Stream already deleted.");
                } else {
                    System.err.println("Error during cleanup: " + e.getCause().getMessage());
                }
            }
        }

        System.out.println();
        System.out.println("==========================================================");
        System.out.println("  Kinesis Basics Scenario completed successfully.");
        System.out.println("==========================================================");
    }

    /**
     * Waits for the stream to reach ACTIVE state using a polling loop with
     * DescribeStreamSummary. The SDK waiter {@code waitUntilStreamExists}
     * requires {@link DescribeStreamRequest}.
     */
    private static void waitForStreamExists(String streamName) {
        int maxWaitSeconds = 120;
        int pollIntervalSeconds = 5;
        int elapsed = 0;
        while (elapsed < maxWaitSeconds) {
            try {
                DescribeStreamSummaryResponse response =
                    KinesisActions.describeStreamSummaryAsync(streamName).join();
                String status = response.streamDescriptionSummary().streamStatus().toString();
                if ("ACTIVE".equals(status)) {
                    return;
                }
                System.out.println("  Stream status: " + status + " — waiting...");
            } catch (CompletionException e) {
                if (e.getCause() instanceof ResourceNotFoundException) {
                    System.out.println("  Stream not yet visible — waiting...");
                } else {
                    throw e;
                }
            }
            try {
                Thread.sleep(pollIntervalSeconds * 1000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
            elapsed += pollIntervalSeconds;
        }
        throw new RuntimeException("Stream " + streamName + " did not become ACTIVE within "
            + maxWaitSeconds + " seconds.");
    }

    /**
     * Waits for the stream to be fully deleted using a polling loop with
     * DescribeStreamSummary. When the stream no longer exists,
     * a {@link ResourceNotFoundException} is thrown, indicating deletion is complete.
     */
    private static void waitForStreamNotExists(String streamName) {
        int maxWaitSeconds = 120;
        int pollIntervalSeconds = 5;
        int elapsed = 0;
        while (elapsed < maxWaitSeconds) {
            try {
                DescribeStreamSummaryResponse response =
                    KinesisActions.describeStreamSummaryAsync(streamName).join();
                String status = response.streamDescriptionSummary().streamStatus().toString();
                System.out.println("  Stream status: " + status + " — waiting for deletion...");
            } catch (CompletionException e) {
                if (e.getCause() instanceof ResourceNotFoundException) {
                    return; // Stream is gone
                }
                throw e;
            }
            try {
                Thread.sleep(pollIntervalSeconds * 1000L);
            } catch (InterruptedException ie) {
                Thread.currentThread().interrupt();
                return;
            }
            elapsed += pollIntervalSeconds;
        }
        throw new RuntimeException("Stream " + streamName + " was not deleted within "
            + maxWaitSeconds + " seconds.");
    }

    private static String generateRandomSuffix(int length) {
        String chars = "abcdefghijklmnopqrstuvwxyz0123456789";
        Random random = new Random();
        StringBuilder sb = new StringBuilder(length);
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }
}
// snippet-end:[kinesis.java2.scenario.main]
