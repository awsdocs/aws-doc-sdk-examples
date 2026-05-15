// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import software.amazon.awssdk.services.eventbridge.EventBridgeAsyncClient;
import software.amazon.awssdk.services.eventbridge.model.*;
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
class PutBucketS3EventNotificationEventBridgeTest {

    @Mock
    private S3AsyncClient mockS3Client;

    @Mock
    private EventBridgeAsyncClient mockEventBridgeClient;

    @Mock
    private SqsAsyncClient mockSqsClient;

    private static final String BUCKET_NAME = "queue-topic-bucket-12345";
    private static final String QUEUE_ARN = "arn:aws:sqs:us-east-1:123456789012:queue-topic-queue";
    private static final String QUEUE_URL = "https://sqs.us-east-1.amazonaws.com/123456789012/queue-topic-queue";
    private static final String TOPIC_ARN = "arn:aws:sns:us-east-1:123456789012:queue-topic-topic";
    private static final String RULE_ARN = "arn:aws:events:us-east-1:123456789012:rule/s3-object-create-rule";
    private static final String RULE_NAME = "s3-object-create-rule";

    @Test
    void setBucketNotificationToEventBridge_configuresNotificationAndCreatesRule() {
        // Mock S3 putBucketNotificationConfiguration
        when(mockS3Client.putBucketNotificationConfiguration(any(PutBucketNotificationConfigurationRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        PutBucketNotificationConfigurationResponse.builder().build()));

        // Mock EventBridge putRule
        when(mockEventBridgeClient.putRule(any(PutRuleRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        PutRuleResponse.builder()
                                .ruleArn(RULE_ARN)
                                .build()));

        // Mock EventBridge putTargets
        when(mockEventBridgeClient.putTargets(any(PutTargetsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        PutTargetsResponse.builder()
                                .failedEntryCount(0)
                                .build()));

        // Verify S3 notification configuration
        var s3Response = mockS3Client.putBucketNotificationConfiguration(
                PutBucketNotificationConfigurationRequest.builder()
                        .bucket(BUCKET_NAME)
                        .build());
        assertNotNull(s3Response.join());

        // Verify EventBridge rule creation
        var ruleResponse = mockEventBridgeClient.putRule(PutRuleRequest.builder()
                .name(RULE_NAME)
                .eventPattern("{\"source\":[\"aws.s3\"],\"detail-type\":[\"Object Created\"]}")
                .build());
        assertEquals(RULE_ARN, ruleResponse.join().ruleArn());

        // Verify targets are added
        var targetsResponse = mockEventBridgeClient.putTargets(PutTargetsRequest.builder()
                .rule(RULE_NAME)
                .targets(List.of(
                        Target.builder().arn(QUEUE_ARN).id("Queue").build(),
                        Target.builder().arn(TOPIC_ARN).id("Topic").build()))
                .build());
        assertEquals(0, targetsResponse.join().failedEntryCount());
    }

    @Test
    void receiveMessages_afterEventBridgeRouting() {
        // Mock SQS receiveMessage with event notification messages
        String eventBridgeMessage = """
                {"version":"0","id":"abc123","detail-type":"Object Created",
                "source":"aws.s3","detail":{"bucket":{"name":"test-bucket"},"object":{"key":"test.txt"}}}""";

        when(mockSqsClient.receiveMessage(any(java.util.function.Consumer.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        ReceiveMessageResponse.builder()
                                .messages(Message.builder()
                                        .messageId("msg-001")
                                        .body(eventBridgeMessage)
                                        .build())
                                .build()));

        var response = mockSqsClient.receiveMessage(b -> b.queueUrl(QUEUE_URL));
        var messages = response.join().messages();

        assertTrue(response.join().hasMessages());
        assertFalse(messages.isEmpty());
        assertTrue(messages.get(0).body().contains("Object Created"));
    }

    @Test
    void deleteRule_removesTargetsAndRule() {
        // Mock removeTargets
        when(mockEventBridgeClient.removeTargets(any(RemoveTargetsRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        RemoveTargetsResponse.builder()
                                .failedEntryCount(0)
                                .build()));

        // Mock deleteRule
        when(mockEventBridgeClient.deleteRule(any(DeleteRuleRequest.class)))
                .thenReturn(CompletableFuture.completedFuture(
                        DeleteRuleResponse.builder().build()));

        // Verify targets removal
        var removeResponse = mockEventBridgeClient.removeTargets(RemoveTargetsRequest.builder()
                .rule(RULE_NAME)
                .ids("Queue", "Topic")
                .build());
        assertEquals(0, removeResponse.join().failedEntryCount());

        // Verify rule deletion
        var deleteResponse = mockEventBridgeClient.deleteRule(DeleteRuleRequest.builder()
                .name(RULE_NAME)
                .build());
        assertNotNull(deleteResponse.join());
    }
}
