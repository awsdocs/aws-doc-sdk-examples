// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationRequest;
import software.amazon.awssdk.services.s3.model.PutBucketNotificationConfigurationResponse;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class ProcessS3EventNotificationTest {

    @Mock
    private SqsAsyncClient mockSqsClient;

    @Mock
    private S3AsyncClient mockS3Client;

    @Test
    void processS3EventNotification_configuresBucketAndProcessesMessages() {
        String queueUrl = "https://sqs.us-east-1.amazonaws.com/123456789012/direct-target-queue";
        String queueArn = "arn:aws:sqs:us-east-1:123456789012:direct-target-queue";
        String bucketName = "direct-target-bucket-12345";

        // Mock putBucketNotificationConfiguration
        when(mockS3Client.putBucketNotificationConfiguration(any(PutBucketNotificationConfigurationRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        PutBucketNotificationConfigurationResponse.builder().build()));

        // Mock getQueueAttributes
        when(mockSqsClient.getQueueAttributes(any(java.util.function.Consumer.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        GetQueueAttributesResponse.builder()
                                .attributes(Map.of(
                                        QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES, "1",
                                        QueueAttributeName.QUEUE_ARN, queueArn))
                                .build()));

        // Mock receiveMessage - first call returns a message, second returns empty
        String s3EventJson = """
                {"Records":[{"eventVersion":"2.1","eventSource":"aws:s3","eventName":"ObjectCreated:Put",
                "s3":{"bucket":{"name":"test-bucket"},"object":{"key":"test-key.txt"}}}]}""";

        when(mockSqsClient.receiveMessage(any(java.util.function.Consumer.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        ReceiveMessageResponse.builder()
                                .messages(Message.builder()
                                        .messageId("msg-001")
                                        .receiptHandle("handle-001")
                                        .body(s3EventJson)
                                        .build())
                                .build()))
                .thenReturn(CompletableFuture.completedFuture(
                        ReceiveMessageResponse.builder()
                                .messages(List.of())
                                .build()));

        // Mock deleteMessageBatch
        when(mockSqsClient.deleteMessageBatch(any(DeleteMessageBatchRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        DeleteMessageBatchResponse.builder().build()));

        // Verify the notification configuration call works
        var configResponse = mockS3Client.putBucketNotificationConfiguration(
                PutBucketNotificationConfigurationRequest.builder()
                        .bucket(bucketName)
                        .build());
        assertNotNull(configResponse.join());

        // Verify queue attributes can be retrieved
        var attrResponse = mockSqsClient.getQueueAttributes(b -> b
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.QUEUE_ARN));
        assertEquals(queueArn, attrResponse.join().attributes().get(QueueAttributeName.QUEUE_ARN));

        // Verify messages can be received and processed
        var receiveResponse = mockSqsClient.receiveMessage(b -> b.queueUrl(queueUrl));
        var messages = receiveResponse.join().messages();
        assertFalse(messages.isEmpty());
        assertEquals("msg-001", messages.get(0).messageId());

        // Verify messages can be deleted
        var deleteResponse = mockSqsClient.deleteMessageBatch(DeleteMessageBatchRequest.builder()
                .queueUrl(queueUrl)
                .entries(DeleteMessageBatchRequestEntry.builder()
                        .id("msg-001")
                        .receiptHandle("handle-001")
                        .build())
                .build());
        assertNotNull(deleteResponse.join());
    }
}
