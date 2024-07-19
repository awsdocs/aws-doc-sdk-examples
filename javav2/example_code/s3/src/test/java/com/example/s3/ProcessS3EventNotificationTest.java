// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import static org.junit.jupiter.api.Assertions.*;

class ProcessS3EventNotificationTest {
    static SqsAsyncClient sqsClient = ProcessS3EventNotification.sqsClient;

    @BeforeAll
    static void setUp() {
        ProcessS3EventNotification.deployCloudFormationStack();
    }

    @AfterAll
    static void tearDown() {
        ProcessS3EventNotification.destroyCloudFormationStack();
    }

    @Test
    @Tag("IntegrationTest")
    void processS3EventsReadsProcessesAndDeletes() {
        String queueUrl = ProcessS3EventNotification.getQueueUrl();
        String queueArn = ProcessS3EventNotification.getQueueArn(queueUrl);
        String bucketName = ProcessS3EventNotification.getBucketName();

        ProcessS3EventNotification.processS3Events(bucketName, queueUrl, queueArn);

        sqsClient.receiveMessage(r -> r
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1)
        ).thenAccept(receiveMessageResponse ->
                assertEquals(0, receiveMessageResponse.messages().size())
        ).join();
    }
}