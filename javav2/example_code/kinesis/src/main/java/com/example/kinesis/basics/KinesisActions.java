// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis.basics;

import software.amazon.awssdk.core.client.config.ClientOverrideConfiguration;
import software.amazon.awssdk.core.retry.RetryMode;
import software.amazon.awssdk.core.waiters.WaiterResponse;
import software.amazon.awssdk.http.async.SdkAsyncHttpClient;
import software.amazon.awssdk.http.nio.netty.NettyNioAsyncHttpClient;
import software.amazon.awssdk.services.kinesis.KinesisAsyncClient;
import software.amazon.awssdk.services.kinesis.model.AddTagsToStreamRequest;
import software.amazon.awssdk.services.kinesis.model.AddTagsToStreamResponse;
import software.amazon.awssdk.services.kinesis.model.Consumer;
import software.amazon.awssdk.services.kinesis.model.ConsumerDescription;
import software.amazon.awssdk.services.kinesis.model.CreateStreamRequest;
import software.amazon.awssdk.services.kinesis.model.CreateStreamResponse;
import software.amazon.awssdk.services.kinesis.model.DeleteStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DeleteStreamResponse;
import software.amazon.awssdk.services.kinesis.model.DeregisterStreamConsumerRequest;
import software.amazon.awssdk.services.kinesis.model.DeregisterStreamConsumerResponse;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamConsumerRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamRequest;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamResponse;
import software.amazon.awssdk.services.kinesis.model.DescribeStreamSummaryRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.GetRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.GetShardIteratorRequest;
import software.amazon.awssdk.services.kinesis.model.InvalidArgumentException;
import software.amazon.awssdk.services.kinesis.model.LimitExceededException;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListStreamsRequest;
import software.amazon.awssdk.services.kinesis.model.ListTagsForStreamRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.RegisterStreamConsumerRequest;
import software.amazon.awssdk.services.kinesis.model.RemoveTagsFromStreamRequest;
import software.amazon.awssdk.services.kinesis.model.RemoveTagsFromStreamResponse;
import software.amazon.awssdk.services.kinesis.model.ResourceInUseException;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamDescriptionSummary;
import software.amazon.awssdk.services.kinesis.model.StreamSummary;
import software.amazon.awssdk.services.kinesis.model.Tag;

import java.time.Duration;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

// snippet-start:[kinesis.java2.actions.main]
/**
 * Wrapper class for Amazon Kinesis Data Streams operations that uses the
 * asynchronous {@link KinesisAsyncClient}.
 * <p>
 * Each method returns a {@link CompletableFuture}. Failures are handled in
 * {@code exceptionally()}: the most relevant service exception is rethrown as a
 * {@link CompletionException} with an actionable message, and the original
 * service exception is kept as the cause.
 * </p>
 */
public class KinesisActions {

    private static KinesisAsyncClient kinesisAsyncClient;

    /**
     * Retrieves an asynchronous Kinesis client, creating it on first use.
     *
     * @return the shared {@link KinesisAsyncClient}
     */
    private static synchronized KinesisAsyncClient getAsyncClient() {
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
     * Creates a provisioned Kinesis data stream.
     *
     * @param streamName the name of the stream to create
     * @param shardCount the number of shards for the stream
     * @return a {@link CompletableFuture} that contains the {@link CreateStreamResponse}
     */
    public CompletableFuture<CreateStreamResponse> createStreamAsync(String streamName, int shardCount) {
        CreateStreamRequest request = CreateStreamRequest.builder()
            .streamName(streamName)
            .shardCount(shardCount)
            .build();

        return getAsyncClient().createStream(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceInUseException) {
                    throw new CompletionException(
                        "Stream '" + streamName + "' already exists or is being created.", cause);
                }
                throw new CompletionException("Failed to create stream: " + cause.getMessage(), cause);
            });
    }

    /**
     * Waits until a stream is ACTIVE by using the SDK waiter.
     *
     * @param streamName the name of the stream
     * @return a {@link CompletableFuture} that completes when the stream is ACTIVE
     */
    public CompletableFuture<WaiterResponse<DescribeStreamResponse>> waitForStreamActiveAsync(String streamName) {
        DescribeStreamRequest request = DescribeStreamRequest.builder()
            .streamName(streamName)
            .build();

        return getAsyncClient().waiter().waitUntilStreamExists(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                throw new CompletionException(
                    "Stream '" + streamName + "' did not become ACTIVE: " + cause.getMessage(), cause);
            });
    }

    // snippet-start:[kinesis.java2.list_streams.main]
    /**
     * Lists all streams in the account and Region by using the async paginator.
     *
     * @return a {@link CompletableFuture} that contains the list of {@link StreamSummary} objects
     */
    public CompletableFuture<List<StreamSummary>> listStreamsAsync() {
        ListStreamsRequest request = ListStreamsRequest.builder()
            .limit(100)
            .build();

        List<StreamSummary> streams = new ArrayList<>();
        return getAsyncClient().listStreamsPaginator(request)
            .streamSummaries()
            .subscribe(streams::add)
            .handle((ignored, exception) -> {
                if (exception != null) {
                    Throwable cause = unwrap(exception);
                    if (cause instanceof LimitExceededException) {
                        throw new CompletionException(
                            "The request rate is too high. Wait briefly and then retry.", cause);
                    }
                    throw new CompletionException("Failed to list streams: " + cause.getMessage(), cause);
                }
                return streams;
            });
    }
    // snippet-end:[kinesis.java2.list_streams.main]

    // snippet-start:[kinesis.java2.describe_stream.main]
    /**
     * Gets a summary description of a stream without listing its shards.
     *
     * @param streamName the name of the stream to describe
     * @return a {@link CompletableFuture} that contains the {@link StreamDescriptionSummary}
     */
    public CompletableFuture<StreamDescriptionSummary> describeStreamSummaryAsync(String streamName) {
        DescribeStreamSummaryRequest request = DescribeStreamSummaryRequest.builder()
            .streamName(streamName)
            .build();

        return getAsyncClient().describeStreamSummary(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "Stream '" + streamName + "' was not found. Check the stream name.", cause);
                }
                throw new CompletionException("Failed to describe stream: " + cause.getMessage(), cause);
            })
            .thenApply(response -> response.streamDescriptionSummary());
    }
    // snippet-end:[kinesis.java2.describe_stream.main]

    // snippet-start:[kinesis.java2.list_shards.main]
    /**
     * Lists all shards in a stream. The async client has no paginator for
     * ListShards, so this method follows {@code nextToken} manually.
     *
     * @param streamName the name of the stream
     * @return a {@link CompletableFuture} that contains the list of {@link Shard} objects
     */
    public CompletableFuture<List<Shard>> listShardsAsync(String streamName) {
        return listShardsPage(streamName, null, new ArrayList<>());
    }

    private CompletableFuture<List<Shard>> listShardsPage(String streamName, String nextToken,
                                                          List<Shard> shards) {
        // A request that includes nextToken must not also include the stream name.
        ListShardsRequest request = nextToken == null
            ? ListShardsRequest.builder().streamName(streamName).build()
            : ListShardsRequest.builder().nextToken(nextToken).build();

        return getAsyncClient().listShards(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "Stream '" + streamName + "' was not found. Verify that it is ACTIVE.", cause);
                }
                throw new CompletionException("Failed to list shards: " + cause.getMessage(), cause);
            })
            .thenCompose(response -> {
                shards.addAll(response.shards());
                if (response.nextToken() != null) {
                    return listShardsPage(streamName, response.nextToken(), shards);
                }
                return CompletableFuture.completedFuture(shards);
            });
    }
    // snippet-end:[kinesis.java2.list_shards.main]

    // snippet-start:[kinesis.java2.put_records.main]
    /**
     * Writes a batch of records to a stream in a single call.
     *
     * @param streamName the name of the stream
     * @param records    the records to write
     * @return a {@link CompletableFuture} that contains the {@link PutRecordsResponse}
     */
    public CompletableFuture<PutRecordsResponse> putRecordsAsync(String streamName,
                                                                 List<PutRecordsRequestEntry> records) {
        PutRecordsRequest request = PutRecordsRequest.builder()
            .streamName(streamName)
            .records(records)
            .build();

        return getAsyncClient().putRecords(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof InvalidArgumentException) {
                    throw new CompletionException(
                        "A parameter is invalid. Each record must be 1 MiB or less, "
                            + "and each request 5 MiB or less.", cause);
                }
                throw new CompletionException("Failed to put records: " + cause.getMessage(), cause);
            });
    }
    // snippet-end:[kinesis.java2.put_records.main]

    // snippet-start:[kinesis.java2.get_shard_iterator.main]
    /**
     * Gets a shard iterator for reading records from a shard.
     *
     * @param streamName        the name of the stream
     * @param shardId           the ID of the shard
     * @param shardIteratorType where in the shard to start reading
     * @return a {@link CompletableFuture} that contains the shard iterator
     */
    public CompletableFuture<String> getShardIteratorAsync(String streamName, String shardId,
                                                           ShardIteratorType shardIteratorType) {
        GetShardIteratorRequest request = GetShardIteratorRequest.builder()
            .streamName(streamName)
            .shardId(shardId)
            .shardIteratorType(shardIteratorType)
            .build();

        return getAsyncClient().getShardIterator(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "Stream or shard '" + shardId + "' was not found. Verify the shard ID.", cause);
                }
                throw new CompletionException("Failed to get shard iterator: " + cause.getMessage(), cause);
            })
            .thenApply(response -> response.shardIterator());
    }
    // snippet-end:[kinesis.java2.get_shard_iterator.main]

    /**
     * Reads records from a shard by using a shard iterator.
     *
     * @param shardIterator the shard iterator
     * @param limit         the maximum number of records to return
     * @return a {@link CompletableFuture} that contains the {@link GetRecordsResponse}
     */
    public CompletableFuture<GetRecordsResponse> getRecordsAsync(String shardIterator, int limit) {
        GetRecordsRequest request = GetRecordsRequest.builder()
            .shardIterator(shardIterator)
            .limit(limit)
            .build();

        return getAsyncClient().getRecords(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                throw new CompletionException("Failed to get records: " + cause.getMessage(), cause);
            });
    }

    // snippet-start:[kinesis.java2.add_tags.main]
    /**
     * Adds or updates tags on a stream.
     *
     * @param streamName the name of the stream
     * @param tags       the tag keys and values to add
     * @return a {@link CompletableFuture} that contains the {@link AddTagsToStreamResponse}
     */
    public CompletableFuture<AddTagsToStreamResponse> addTagsToStreamAsync(String streamName,
                                                                           Map<String, String> tags) {
        AddTagsToStreamRequest request = AddTagsToStreamRequest.builder()
            .streamName(streamName)
            .tags(tags)
            .build();

        return getAsyncClient().addTagsToStream(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "Stream '" + streamName + "' does not exist for tagging.", cause);
                }
                throw new CompletionException("Failed to add tags: " + cause.getMessage(), cause);
            });
    }
    // snippet-end:[kinesis.java2.add_tags.main]

    // snippet-start:[kinesis.java2.list_tags.main]
    /**
     * Lists all tags on a stream. ListTagsForStream has no paginator, so this
     * method requests the next page while {@code hasMoreTags} is true.
     *
     * @param streamName the name of the stream
     * @return a {@link CompletableFuture} that contains the list of {@link Tag} objects
     */
    public CompletableFuture<List<Tag>> listTagsForStreamAsync(String streamName) {
        return listTagsPage(streamName, null, new ArrayList<>());
    }

    private CompletableFuture<List<Tag>> listTagsPage(String streamName, String exclusiveStartTagKey,
                                                      List<Tag> tags) {
        ListTagsForStreamRequest request = ListTagsForStreamRequest.builder()
            .streamName(streamName)
            .exclusiveStartTagKey(exclusiveStartTagKey)
            .build();

        return getAsyncClient().listTagsForStream(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "Stream '" + streamName + "' was not found when listing tags.", cause);
                }
                throw new CompletionException("Failed to list tags: " + cause.getMessage(), cause);
            })
            .thenCompose(response -> {
                tags.addAll(response.tags());
                if (Boolean.TRUE.equals(response.hasMoreTags()) && !response.tags().isEmpty()) {
                    String lastKey = response.tags().get(response.tags().size() - 1).key();
                    return listTagsPage(streamName, lastKey, tags);
                }
                return CompletableFuture.completedFuture(tags);
            });
    }
    // snippet-end:[kinesis.java2.list_tags.main]

    // snippet-start:[kinesis.java2.remove_tags.main]
    /**
     * Removes tags from a stream.
     *
     * @param streamName the name of the stream
     * @param tagKeys    the keys of the tags to remove
     * @return a {@link CompletableFuture} that contains the {@link RemoveTagsFromStreamResponse}
     */
    public CompletableFuture<RemoveTagsFromStreamResponse> removeTagsFromStreamAsync(String streamName,
                                                                                     List<String> tagKeys) {
        RemoveTagsFromStreamRequest request = RemoveTagsFromStreamRequest.builder()
            .streamName(streamName)
            .tagKeys(tagKeys)
            .build();

        return getAsyncClient().removeTagsFromStream(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "Stream '" + streamName + "' does not exist for tag removal.", cause);
                }
                throw new CompletionException("Failed to remove tags: " + cause.getMessage(), cause);
            });
    }
    // snippet-end:[kinesis.java2.remove_tags.main]

    // snippet-start:[kinesis.java2.register_consumer.main]
    /**
     * Registers an enhanced fan-out consumer with a stream.
     *
     * @param streamArn    the ARN of the stream
     * @param consumerName the name of the consumer
     * @return a {@link CompletableFuture} that contains the registered {@link Consumer}
     */
    public CompletableFuture<Consumer> registerStreamConsumerAsync(String streamArn, String consumerName) {
        RegisterStreamConsumerRequest request = RegisterStreamConsumerRequest.builder()
            .streamARN(streamArn)
            .consumerName(consumerName)
            .build();

        return getAsyncClient().registerStreamConsumer(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceInUseException) {
                    throw new CompletionException(
                        "Consumer '" + consumerName + "' already exists or the stream is not available.",
                        cause);
                }
                throw new CompletionException("Failed to register consumer: " + cause.getMessage(), cause);
            })
            .thenApply(response -> response.consumer());
    }
    // snippet-end:[kinesis.java2.register_consumer.main]

    /**
     * Describes a registered stream consumer.
     *
     * @param consumerArn the ARN of the consumer
     * @return a {@link CompletableFuture} that contains the {@link ConsumerDescription}
     */
    public CompletableFuture<ConsumerDescription> describeStreamConsumerAsync(String consumerArn) {
        DescribeStreamConsumerRequest request = DescribeStreamConsumerRequest.builder()
            .consumerARN(consumerArn)
            .build();

        return getAsyncClient().describeStreamConsumer(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                throw new CompletionException("Failed to describe consumer: " + cause.getMessage(), cause);
            })
            .thenApply(response -> response.consumerDescription());
    }

    // snippet-start:[kinesis.java2.deregister_consumer.main]
    /**
     * Deregisters an enhanced fan-out consumer from a stream.
     *
     * @param consumerArn the ARN of the consumer
     * @return a {@link CompletableFuture} that contains the {@link DeregisterStreamConsumerResponse}
     */
    public CompletableFuture<DeregisterStreamConsumerResponse> deregisterStreamConsumerAsync(String consumerArn) {
        DeregisterStreamConsumerRequest request = DeregisterStreamConsumerRequest.builder()
            .consumerARN(consumerArn)
            .build();

        return getAsyncClient().deregisterStreamConsumer(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "The consumer was already deregistered or does not exist.", cause);
                }
                throw new CompletionException("Failed to deregister consumer: " + cause.getMessage(), cause);
            });
    }
    // snippet-end:[kinesis.java2.deregister_consumer.main]

    /**
     * Deletes a stream, including any registered consumers.
     *
     * @param streamName the name of the stream to delete
     * @return a {@link CompletableFuture} that contains the {@link DeleteStreamResponse}
     */
    public CompletableFuture<DeleteStreamResponse> deleteStreamAsync(String streamName) {
        DeleteStreamRequest request = DeleteStreamRequest.builder()
            .streamName(streamName)
            .enforceConsumerDeletion(true)
            .build();

        return getAsyncClient().deleteStream(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                if (cause instanceof ResourceNotFoundException) {
                    throw new CompletionException(
                        "Stream '" + streamName + "' was not found. It may already be deleted.", cause);
                }
                throw new CompletionException("Failed to delete stream: " + cause.getMessage(), cause);
            });
    }

    /**
     * Waits until a stream is deleted by using the SDK waiter.
     *
     * @param streamName the name of the stream
     * @return a {@link CompletableFuture} that completes when the stream no longer exists
     */
    public CompletableFuture<WaiterResponse<DescribeStreamResponse>> waitForStreamDeletedAsync(String streamName) {
        DescribeStreamRequest request = DescribeStreamRequest.builder()
            .streamName(streamName)
            .build();

        return getAsyncClient().waiter().waitUntilStreamNotExists(request)
            .exceptionally(exception -> {
                Throwable cause = unwrap(exception);
                throw new CompletionException(
                    "Stream '" + streamName + "' was not deleted: " + cause.getMessage(), cause);
            });
    }

    /**
     * Returns the underlying cause of an async failure, which the SDK wraps
     * in a {@link CompletionException}.
     */
    private static Throwable unwrap(Throwable exception) {
        if (exception instanceof CompletionException && exception.getCause() != null) {
            return exception.getCause();
        }
        return exception;
    }
}
// snippet-end:[kinesis.java2.actions.main]
