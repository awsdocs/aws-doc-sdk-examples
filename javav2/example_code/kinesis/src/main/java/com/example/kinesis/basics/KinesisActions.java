// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis.basics;

import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.AddTagsToStreamRequest;
import software.amazon.awssdk.services.kinesis.model.AddTagsToStreamResponse;
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest;
import software.amazon.awssdk.services.kinesis.model.CreateStreamResponse;
import software.amazon.awssdk.services.kinesis.model.DeleteStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DeleteStreamResponse;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamSummaryRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamSummaryResponse;
import software.amazon.awssdk.services.kinesis.model.ExpiredIteratorException;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorResponse;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
import software.amazon.awssdk.services.kinesis.model.ListTagsForStreamRequest;
import software.amazon.awssdk.services.kinesis.model.ListTagsForStreamResponse;
import software.amazon.awssdk.services.kinesis.model.ProvisionedThroughputExceededException;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.ResourceInUseException;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamModeDetails;
import software.amazon.awssdk.services.kinesis.model.StreamMode;
import software.amazon.awssdk.services.kinesis.model.Tag;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// snippet-start:[kinesis.java2.actions.main]
/**
 * Wrapper class for Amazon Kinesis Data Streams async operations.
 * <p>
 * All methods are static and return {@link CompletableFuture}. Failures are
 * handled in {@code .whenComplete()} — the cause is unwrapped, checked for the
 * specific SDK v2 exception, logged, and rethrown as a
 * {@link CompletionException}.
 * </p>
 */
public class KinesisActions {

    private static volatile KinesisAsyncClient kinesisAsyncClient;

    private static KinesisAsyncClient getAsyncClient() {
        if (kinesisAsyncClient == null) {
            SdkAsyncHttpClient httpClient = NettyNioAsyncHttpClient.builder()
                .maxConcurrency(100)
                .connectionTimeout(Duration.ofSeconds(60))
                .readTimeout(Duration.ofSeconds(60))
                .writeTimeout(Duration.ofSeconds(60))
                .build();

            ClientOverrideConfiguration overrideConfig = ClientOverrideConfiguration.builder()
                .apiCallTimeout(Duration.ofMinutes(2))
                .apiCallAttemptTimeout(Duration.ofSeconds(90))
                .retryStrategy(RetryMode.STANDARD)
                .build();

            kinesisAsyncClient = KinesisAsyncClient.builder()
                .httpClient(httpClient)
                .overrideConfiguration(overrideConfig)
                .build();
        }
        return kinesisAsyncClient;
    }

    /**
     * Creates a Kinesis data stream in ON_DEMAND mode.
     *
     * @param streamName the name of the stream to create
     * @return a {@link CompletableFuture} containing the {@link CreateStreamResponse}
     */
    public static CompletableFuture<CreateStreamResponse> createStreamAsync(String streamName) {
        CreateStreamRequest request = CreateStreamRequest.builder()
            .streamName(streamName)
            .streamModeDetails(StreamModeDetails.builder()
                .streamMode(StreamMode.ON_DEMAND)
                .build())
            .build();

        return getAsyncClient().createStream(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ResourceInUseException) {
                        System.err.println("Stream '" + streamName + "' already exists: " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("CreateStream request accepted for: " + streamName);
            });
    }

    // snippet-start:[kinesis.java2.describe_stream.main]
    /**
     * Describes a Kinesis data stream summary.
     *
     * @param streamName the name of the stream to describe
     * @return a {@link CompletableFuture} containing the {@link DescribeStreamSummaryResponse}
     */
    public static CompletableFuture<DescribeStreamSummaryResponse> describeStreamSummaryAsync(String streamName) {
        DescribeStreamSummaryRequest request = DescribeStreamSummaryRequest.builder()
            .streamName(streamName)
            .build();

        return getAsyncClient().describeStreamSummary(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ResourceNotFoundException) {
                        System.err.println("Stream '" + streamName + "' not found: " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("Described stream: " + streamName);
            });
    }
    // snippet-end:[kinesis.java2.describe_stream.main]

    // snippet-start:[kinesis.java2.add_tags.main]
    /**
     * Adds tags to a Kinesis data stream.
     *
     * @param streamName the name of the stream to tag
     * @param tags       the map of tag key-value pairs
     * @return a {@link CompletableFuture} containing the {@link AddTagsToStreamResponse}
     */
    public static CompletableFuture<AddTagsToStreamResponse> addTagsToStreamAsync(String streamName,
                                                                                   Map<String, String> tags) {
        AddTagsToStreamRequest request = AddTagsToStreamRequest.builder()
            .streamName(streamName)
            .tags(tags)
            .build();

        return getAsyncClient().addTagsToStream(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ResourceNotFoundException) {
                        System.err.println("Stream '" + streamName + "' not found for tagging: " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("Added tags to stream: " + streamName);
            });
    }
    // snippet-end:[kinesis.java2.add_tags.main]

    // snippet-start:[kinesis.java2.list_tags.main]
    /**
     * Lists all tags on a Kinesis data stream using manual pagination
     * (chaining futures with {@code thenCompose}).
     *
     * @param streamName the name of the stream whose tags to list
     * @return a {@link CompletableFuture} containing the list of {@link Tag} objects
     */
    public static CompletableFuture<List<Tag>> listTagsForStreamAsync(String streamName) {
        List<Tag> allTags = new ArrayList<>();

        return listTagsPage(streamName, null, allTags);
    }

    private static CompletableFuture<List<Tag>> listTagsPage(String streamName,
                                                              String exclusiveStartTagKey,
                                                              List<Tag> accumulator) {
        ListTagsForStreamRequest.Builder requestBuilder = ListTagsForStreamRequest.builder()
            .streamName(streamName);

        if (exclusiveStartTagKey != null) {
            requestBuilder.exclusiveStartTagKey(exclusiveStartTagKey);
        }

        return getAsyncClient().listTagsForStream(requestBuilder.build())
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ResourceNotFoundException) {
                        System.err.println("Stream '" + streamName + "' not found for listing tags: "
                            + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
            })
            .thenCompose(response -> {
                if (response.tags() != null) {
                    accumulator.addAll(response.tags());
                }
                if (response.hasMoreTags() != null && response.hasMoreTags()) {
                    // Get the last tag key for the next page
                    String lastTagKey = response.tags().get(response.tags().size() - 1).key();
                    return listTagsPage(streamName, lastTagKey, accumulator);
                }
                return CompletableFuture.completedFuture(accumulator);
            });
    }
    // snippet-end:[kinesis.java2.list_tags.main]

    /**
     * Writes a single data record to a Kinesis data stream.
     *
     * @param streamName   the name of the stream
     * @param partitionKey the partition key for the record
     * @param data         the data payload as {@link SdkBytes}
     * @return a {@link CompletableFuture} containing the {@link PutRecordResponse}
     */
    public static CompletableFuture<PutRecordResponse> putRecordAsync(String streamName,
                                                                       String partitionKey,
                                                                       SdkBytes data) {
        PutRecordRequest request = PutRecordRequest.builder()
            .streamName(streamName)
            .partitionKey(partitionKey)
            .data(data)
            .build();

        return getAsyncClient().putRecord(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ProvisionedThroughputExceededException) {
                        System.err.println("Write throughput exceeded for stream '"
                            + streamName + "': " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("Put record to shard: " + response.shardId()
                    + ", seq: " + response.sequenceNumber());
            });
    }

    // snippet-start:[kinesis.java2.put_records.main]
    /**
     * Writes multiple data records in a batch to a Kinesis data stream.
     *
     * @param streamName the name of the stream
     * @param records    the list of {@link PutRecordsRequestEntry} to write
     * @return a {@link CompletableFuture} containing the {@link PutRecordsResponse}
     */
    public static CompletableFuture<PutRecordsResponse> putRecordsAsync(String streamName,
                                                                         List<PutRecordsRequestEntry> records) {
        PutRecordsRequest request = PutRecordsRequest.builder()
            .streamName(streamName)
            .records(records)
            .build();

        return getAsyncClient().putRecords(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ProvisionedThroughputExceededException) {
                        System.err.println("Batch write throughput exceeded for stream '"
                            + streamName + "': " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("PutRecords: " + response.records().size()
                    + " record(s), " + response.failedRecordCount() + " failed.");
            });
    }
    // snippet-end:[kinesis.java2.put_records.main]

    // snippet-start:[kinesis.java2.list_shards.main]
    /**
     * Lists all shards in a Kinesis data stream using manual pagination
     * (the SDK does not have a {@code listShardsPaginator} for the async client).
     *
     * @param streamName the name of the stream
     * @return a {@link CompletableFuture} containing the list of {@link Shard} objects
     */
    public static CompletableFuture<List<Shard>> listShardsAsync(String streamName) {
        List<Shard> allShards = new ArrayList<>();
        return listShardsPage(streamName, null, allShards);
    }

    private static CompletableFuture<List<Shard>> listShardsPage(String streamName,
                                                                  String nextToken,
                                                                  List<Shard> accumulator) {
        ListShardsRequest.Builder requestBuilder = ListShardsRequest.builder();

        if (nextToken != null) {
            // When using nextToken, do NOT set streamName — the token encodes the stream.
            requestBuilder.nextToken(nextToken);
        } else {
            requestBuilder.streamName(streamName);
        }

        return getAsyncClient().listShards(requestBuilder.build())
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ResourceNotFoundException) {
                        System.err.println("Stream '" + streamName + "' not found for listing shards: "
                            + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
            })
            .thenCompose(response -> {
                if (response.shards() != null) {
                    accumulator.addAll(response.shards());
                }
                if (response.nextToken() != null && !response.nextToken().isEmpty()) {
                    return listShardsPage(streamName, response.nextToken(), accumulator);
                }
                return CompletableFuture.completedFuture(accumulator);
            });
    }
    // snippet-end:[kinesis.java2.list_shards.main]

    // snippet-start:[kinesis.java2.get_shard_iterator.main]
    /**
     * Obtains a shard iterator for reading records from a specific shard.
     *
     * @param streamName        the name of the stream
     * @param shardId           the shard identifier
     * @param shardIteratorType the type of shard iterator (e.g., TRIM_HORIZON)
     * @return a {@link CompletableFuture} containing the {@link GetShardIteratorResponse}
     */
    public static CompletableFuture<GetShardIteratorResponse> getShardIteratorAsync(
            String streamName, String shardId, ShardIteratorType shardIteratorType) {
        GetShardIteratorRequest request = GetShardIteratorRequest.builder()
            .streamName(streamName)
            .shardId(shardId)
            .shardIteratorType(shardIteratorType)
            .build();

        return getAsyncClient().getShardIterator(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ResourceNotFoundException) {
                        System.err.println("Stream or shard not found: " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("Obtained shard iterator for shard: " + shardId);
            });
    }
    // snippet-end:[kinesis.java2.get_shard_iterator.main]

    /**
     * Reads data records from a shard using a shard iterator.
     *
     * @param shardIterator the shard iterator
     * @param limit         the maximum number of records to return
     * @return a {@link CompletableFuture} containing the {@link GetRecordsResponse}
     */
    public static CompletableFuture<GetRecordsResponse> getRecordsAsync(String shardIterator, int limit) {
        GetRecordsRequest request = GetRecordsRequest.builder()
            .shardIterator(shardIterator)
            .limit(limit)
            .build();

        return getAsyncClient().getRecords(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ExpiredIteratorException) {
                        System.err.println("Shard iterator expired. Obtain a new one: " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("GetRecords returned " + response.records().size() + " record(s).");
            });
    }

    /**
     * Deletes a Kinesis data stream.
     *
     * @param streamName the name of the stream to delete
     * @return a {@link CompletableFuture} containing the {@link DeleteStreamResponse}
     */
    public static CompletableFuture<DeleteStreamResponse> deleteStreamAsync(String streamName) {
        DeleteStreamRequest request = DeleteStreamRequest.builder()
            .streamName(streamName)
            .enforceConsumerDeletion(true)
            .build();

        return getAsyncClient().deleteStream(request)
            .whenComplete((response, exception) -> {
                if (exception != null) {
                    Throwable cause = exception.getCause() != null ? exception.getCause() : exception;
                    if (cause instanceof ResourceNotFoundException) {
                        System.err.println("Stream '" + streamName
                            + "' not found for deletion (may already be deleted): " + cause.getMessage());
                    }
                    throw new CompletionException(cause);
                }
                System.out.println("DeleteStream request accepted for: " + streamName);
            });
    }
}
// snippet-end:[kinesis.java2.actions.main]
