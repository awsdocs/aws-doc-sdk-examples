// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.kinesis.basics;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.core.SdkBytes;
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
import software.amazon.awssdk.services.kinesis.model.HashKeyRange;
import software.amazon.awssdk.services.kinesis.model.LimitExceededException;
import software.amazon.awssdk.services.kinesis.model.ListShardsRequest;
import software.amazon.awssdk.services.kinesis.model.ListShardsResponse;
import software.amazon.awssdk.services.kinesis.model.ListStreamsRequest;
import software.amazon.awssdk.services.kinesis.model.ListStreamsResponse;
import software.amazon.awssdk.services.kinesis.model.ListTagsForStreamRequest;
import software.amazon.awssdk.services.kinesis.model.ListTagsForStreamResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequest;
import software.amazon.awssdk.services.kinesis.model.PutRecordsRequestEntry;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResponse;
import software.amazon.awssdk.services.kinesis.model.PutRecordsResultEntry;
import software.amazon.awssdk.services.kinesis.model.Record;
import software.amazon.awssdk.services.kinesis.model.ResourceInUseException;
import software.amazon.awssdk.services.kinesis.model.ResourceNotFoundException;
import software.amazon.awssdk.services.kinesis.model.Shard;
import software.amazon.awssdk.services.kinesis.model.ShardIteratorType;
import software.amazon.awssdk.services.kinesis.model.StreamDescriptionSummary;
import software.amazon.awssdk.services.kinesis.model.StreamMode;
import software.amazon.awssdk.services.kinesis.model.StreamModeDetails;
import software.amazon.awssdk.services.kinesis.model.StreamStatus;
import software.amazon.awssdk.services.kinesis.model.Tag;
import software.amazon.awssdk.services.kinesis.paginators.ListStreamsPublisher;

import java.time.Instant;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.CompletionException;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Unit tests for {@link KinesisActions}. Uses Mockito to mock the
 * {@link KinesisAsyncClient}. No real AWS calls are made.
 */
@ExtendWith(MockitoExtension.class)
class KinesisTest {

    @Mock
    private KinesisAsyncClient mockClient;

    @BeforeEach
    void setUp() {
        KinesisActions.setAsyncClient(mockClient);
    }

    @AfterEach
    void tearDown() {
        KinesisActions.setAsyncClient(null);
    }

    // -----------------------------------------------------------------------
    // 1. CreateStream — success
    // -----------------------------------------------------------------------
    @Test
    void testCreateStreamAsync() {
        CreateStreamResponse response = CreateStreamResponse.builder().build();
        when(mockClient.createStream(any(CreateStreamRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        CreateStreamResponse result = KinesisActions.createStreamAsync("test-stream").join();

        assertNotNull(result);
        verify(mockClient).createStream(any(CreateStreamRequest.class));
    }

    // -----------------------------------------------------------------------
    // 2. CreateStream — ResourceInUseException
    // -----------------------------------------------------------------------
    @Test
    void testCreateStreamAsync_ResourceInUseException() {
        ResourceInUseException ex = ResourceInUseException.builder()
            .message("Stream already exists").build();
        when(mockClient.createStream(any(CreateStreamRequest.class)))
            .thenReturn(CompletableFuture.failedFuture(ex));

        CompletionException thrown = assertThrows(CompletionException.class,
            () -> KinesisActions.createStreamAsync("test-stream").join());

        assertTrue(thrown.getCause() instanceof ResourceInUseException);
    }

    // -----------------------------------------------------------------------
    // 3. DescribeStreamSummary — success
    // -----------------------------------------------------------------------
    @Test
    void testDescribeStreamSummaryAsync() {
        StreamDescriptionSummary summary = StreamDescriptionSummary.builder()
            .streamName("test-stream")
            .streamARN("arn:aws:kinesis:us-east-1:123456789012:stream/test-stream")
            .streamStatus(StreamStatus.ACTIVE)
            .streamModeDetails(StreamModeDetails.builder().streamMode(StreamMode.ON_DEMAND).build())
            .retentionPeriodHours(24)
            .openShardCount(1)
            .build();

        DescribeStreamSummaryResponse response = DescribeStreamSummaryResponse.builder()
            .streamDescriptionSummary(summary)
            .build();

        when(mockClient.describeStreamSummary(any(DescribeStreamSummaryRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        DescribeStreamSummaryResponse result =
            KinesisActions.describeStreamSummaryAsync("test-stream").join();

        assertNotNull(result);
        assertEquals("test-stream",
            result.streamDescriptionSummary().streamName());
        verify(mockClient).describeStreamSummary(any(DescribeStreamSummaryRequest.class));
    }

    // -----------------------------------------------------------------------
    // 4. AddTagsToStream — success
    // -----------------------------------------------------------------------
    @Test
    void testAddTagsToStreamAsync() {
        AddTagsToStreamResponse response = AddTagsToStreamResponse.builder().build();
        when(mockClient.addTagsToStream(any(AddTagsToStreamRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        Map<String, String> tags = new HashMap<>();
        tags.put("Key1", "Value1");

        AddTagsToStreamResponse result =
            KinesisActions.addTagsToStreamAsync("test-stream", tags).join();

        assertNotNull(result);
        verify(mockClient).addTagsToStream(any(AddTagsToStreamRequest.class));
    }

    // -----------------------------------------------------------------------
    // 5. ListTagsForStream — paginated (2 pages)
    // -----------------------------------------------------------------------
    @Test
    void testListTagsForStreamAsync_Paginated() {
        Tag tag1 = Tag.builder().key("Key1").value("Value1").build();
        Tag tag2 = Tag.builder().key("Key2").value("Value2").build();
        Tag tag3 = Tag.builder().key("Key3").value("Value3").build();

        // First page: 2 tags, hasMoreTags = true
        ListTagsForStreamResponse page1 = ListTagsForStreamResponse.builder()
            .tags(tag1, tag2)
            .hasMoreTags(true)
            .build();

        // Second page: 1 tag, hasMoreTags = false
        ListTagsForStreamResponse page2 = ListTagsForStreamResponse.builder()
            .tags(tag3)
            .hasMoreTags(false)
            .build();

        // First call (no exclusiveStartTagKey) returns page 1
        when(mockClient.listTagsForStream(any(ListTagsForStreamRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(page1))
            .thenReturn(CompletableFuture.completedFuture(page2));

        List<Tag> result = KinesisActions.listTagsForStreamAsync("test-stream").join();

        assertEquals(3, result.size());
        assertEquals("Key1", result.get(0).key());
        assertEquals("Key3", result.get(2).key());
    }

    // -----------------------------------------------------------------------
    // 6. PutRecord — success
    // -----------------------------------------------------------------------
    @Test
    void testPutRecordAsync() {
        PutRecordResponse response = PutRecordResponse.builder()
            .shardId("shardId-000000000000")
            .sequenceNumber("12345")
            .build();

        when(mockClient.putRecord(any(PutRecordRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        PutRecordResponse result = KinesisActions.putRecordAsync(
            "test-stream", "pk-1", SdkBytes.fromUtf8String("test data")).join();

        assertEquals("shardId-000000000000", result.shardId());
        assertEquals("12345", result.sequenceNumber());
        verify(mockClient).putRecord(any(PutRecordRequest.class));
    }

    // -----------------------------------------------------------------------
    // 7. PutRecords — success
    // -----------------------------------------------------------------------
    @Test
    void testPutRecordsAsync() {
        PutRecordsResultEntry entry = PutRecordsResultEntry.builder()
            .shardId("shardId-000000000000")
            .sequenceNumber("12345")
            .build();

        PutRecordsResponse response = PutRecordsResponse.builder()
            .records(entry)
            .failedRecordCount(0)
            .build();

        when(mockClient.putRecords(any(PutRecordsRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        List<PutRecordsRequestEntry> entries = new ArrayList<>();
        entries.add(PutRecordsRequestEntry.builder()
            .data(SdkBytes.fromUtf8String("data"))
            .partitionKey("pk-1")
            .build());

        PutRecordsResponse result =
            KinesisActions.putRecordsAsync("test-stream", entries).join();

        assertEquals(0, result.failedRecordCount());
        verify(mockClient).putRecords(any(PutRecordsRequest.class));
    }

    // -----------------------------------------------------------------------
    // 8. ListShards — paginated (2 pages, manual)
    // -----------------------------------------------------------------------
    @Test
    void testListShardsAsync_Paginated() {
        Shard shard1 = Shard.builder().shardId("shard-001")
            .hashKeyRange(HashKeyRange.builder()
                .startingHashKey("0").endingHashKey("100").build())
            .build();
        Shard shard2 = Shard.builder().shardId("shard-002")
            .hashKeyRange(HashKeyRange.builder()
                .startingHashKey("101").endingHashKey("200").build())
            .build();
        Shard shard3 = Shard.builder().shardId("shard-003")
            .hashKeyRange(HashKeyRange.builder()
                .startingHashKey("201").endingHashKey("300").build())
            .build();

        // First page: 2 shards + nextToken
        ListShardsResponse page1 = ListShardsResponse.builder()
            .shards(shard1, shard2)
            .nextToken("token-page2")
            .build();

        // Second page: 1 shard, no nextToken
        ListShardsResponse page2 = ListShardsResponse.builder()
            .shards(shard3)
            .build();

        when(mockClient.listShards(any(ListShardsRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(page1))
            .thenReturn(CompletableFuture.completedFuture(page2));

        List<Shard> result = KinesisActions.listShardsAsync("test-stream").join();

        assertEquals(3, result.size());
        assertEquals("shard-001", result.get(0).shardId());
        assertEquals("shard-003", result.get(2).shardId());
    }

    // -----------------------------------------------------------------------
    // 9. GetShardIterator — success
    // -----------------------------------------------------------------------
    @Test
    void testGetShardIteratorAsync() {
        GetShardIteratorResponse response = GetShardIteratorResponse.builder()
            .shardIterator("AAAAAA-iterator-value")
            .build();

        when(mockClient.getShardIterator(any(GetShardIteratorRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        GetShardIteratorResponse result = KinesisActions.getShardIteratorAsync(
            "test-stream", "shard-001", ShardIteratorType.TRIM_HORIZON).join();

        assertEquals("AAAAAA-iterator-value", result.shardIterator());
        verify(mockClient).getShardIterator(any(GetShardIteratorRequest.class));
    }

    // -----------------------------------------------------------------------
    // 10. GetRecords — success
    // -----------------------------------------------------------------------
    @Test
    void testGetRecordsAsync() {
        Record record = Record.builder()
            .partitionKey("pk-1")
            .data(SdkBytes.fromUtf8String("hello"))
            .sequenceNumber("seq-001")
            .approximateArrivalTimestamp(Instant.now())
            .build();

        GetRecordsResponse response = GetRecordsResponse.builder()
            .records(record)
            .millisBehindLatest(0L)
            .nextShardIterator("next-iterator")
            .build();

        when(mockClient.getRecords(any(GetRecordsRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        GetRecordsResponse result = KinesisActions.getRecordsAsync("iterator-value", 25).join();

        assertEquals(1, result.records().size());
        assertEquals("pk-1", result.records().get(0).partitionKey());
        verify(mockClient).getRecords(any(GetRecordsRequest.class));
    }

    // -----------------------------------------------------------------------
    // 11. DeleteStream — success
    // -----------------------------------------------------------------------
    @Test
    void testDeleteStreamAsync() {
        DeleteStreamResponse response = DeleteStreamResponse.builder().build();
        when(mockClient.deleteStream(any(DeleteStreamRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(response));

        DeleteStreamResponse result = KinesisActions.deleteStreamAsync("test-stream").join();

        assertNotNull(result);
        verify(mockClient).deleteStream(any(DeleteStreamRequest.class));
    }

    // -----------------------------------------------------------------------
    // 12. GetRecords — ExpiredIteratorException
    // -----------------------------------------------------------------------
    @Test
    void testGetRecordsAsync_ExpiredIteratorException() {
        ExpiredIteratorException ex = ExpiredIteratorException.builder()
            .message("Iterator expired").build();
        when(mockClient.getRecords(any(GetRecordsRequest.class)))
            .thenReturn(CompletableFuture.failedFuture(ex));

        CompletionException thrown = assertThrows(CompletionException.class,
            () -> KinesisActions.getRecordsAsync("expired-iterator", 25).join());

        assertTrue(thrown.getCause() instanceof ExpiredIteratorException);
    }

    // -----------------------------------------------------------------------
    // 13. ListStreams (Hello) — paginated via listStreamsPaginator
    // -----------------------------------------------------------------------
    @Test
    void testListStreams_Hello() {
        // For the Hello test, we test the paginator by stubbing the underlying
        // listStreams call and creating a real ListStreamsPublisher.
        ListStreamsResponse page1 = ListStreamsResponse.builder()
            .streamNames("stream-1", "stream-2")
            .hasMoreStreams(true)
            .nextToken("token-2")
            .build();

        ListStreamsResponse page2 = ListStreamsResponse.builder()
            .streamNames("stream-3")
            .hasMoreStreams(false)
            .build();

        when(mockClient.listStreams(any(ListStreamsRequest.class)))
            .thenReturn(CompletableFuture.completedFuture(page1))
            .thenReturn(CompletableFuture.completedFuture(page2));

        ListStreamsPublisher publisher = new ListStreamsPublisher(mockClient,
            ListStreamsRequest.builder().build());

        List<String> names = Collections.synchronizedList(new ArrayList<>());
        CompletableFuture<Void> future = publisher.subscribe(response -> {
            if (response.streamNames() != null) {
                names.addAll(response.streamNames());
            }
        });

        future.join();

        assertEquals(3, names.size());
        assertTrue(names.contains("stream-1"));
        assertTrue(names.contains("stream-3"));
    }
}
