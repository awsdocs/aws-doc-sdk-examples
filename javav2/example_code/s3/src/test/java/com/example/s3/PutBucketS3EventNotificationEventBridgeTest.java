// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.net.URISyntaxException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PutBucketS3EventNotificationEventBridgeTest {
    static String bucketName;
    static String topicArn;
    static String directToQueueUrl;
    static String directToQueueArn;
    static String subscriberQueueUrl;
    static String subscriberQueueArn;
    static SqsAsyncClient sqsClient = PutBucketS3EventNotificationEventBridge.sqsClient;
    private static final Logger logger = LoggerFactory.getLogger(PutBucketS3EventNotificationEventBridgeTest.class);
    static S3TransferManager transferManager = S3TransferManager.create();

    @BeforeAll
    static void setUp() {
        PutBucketS3EventNotificationEventBridge.deployCloudFormationStack();
        bucketName = PutBucketS3EventNotificationEventBridge.getBucketName();
        topicArn = PutBucketS3EventNotificationEventBridge.getTopicArn();
        directToQueueUrl = PutBucketS3EventNotificationEventBridge.getQueueUrl(false);
        directToQueueArn = PutBucketS3EventNotificationEventBridge.getQueueArn(directToQueueUrl);
        subscriberQueueUrl = PutBucketS3EventNotificationEventBridge.getQueueUrl(true);
        subscriberQueueArn = PutBucketS3EventNotificationEventBridge.getQueueArn(subscriberQueueUrl);
    }

    @AfterAll
    static void tearDown() {
        PutBucketS3EventNotificationEventBridge.deleteRule();
        PutBucketS3EventNotificationEventBridge.destroyCloudFormationStack();
    }

    @Test
    @Tag("IntegrationTest")
    void setBucketNotificationToEventBridge() {

        String ruleArn = PutBucketS3EventNotificationEventBridge.setBucketNotificationToEventBridge(bucketName, topicArn, directToQueueArn);
        PutBucketS3EventNotificationEventBridge.addPermissions(directToQueueArn, directToQueueUrl,
                subscriberQueueArn, subscriberQueueUrl, topicArn, ruleArn);
        triggerS3EventMessages();
        try {
            Thread.sleep(Duration.ofSeconds(30).toMillis()); // Wait for messages to route through.
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        List<String> urls = getQueueUrls();
        urls.forEach(url -> {
                    ReceiveMessageResponse response = sqsClient.receiveMessage(b -> b
                            .queueUrl(url)).join();
                    logger.info("Messages received at queue {}: {}", url, response);
                    assertTrue(response.hasMessages());
                }
        );
    }

    static void triggerS3EventMessages() {
        Path uploadDir;
        try {
            uploadDir = Paths.get(
                    PutBucketS3EventNotificationEventBridge.class.getClassLoader().getResource("uploadDirectory").toURI());
        } catch (URISyntaxException e) {
            throw new RuntimeException(e);
        }

        transferManager.uploadDirectory(b -> b
                        .bucket(bucketName)
                        .source(uploadDir)
                        .build()).completionFuture()
                .whenComplete((completedUpload, t) -> {
                    if (t != null) {
                        logger.error("Failed to upload directory", t);
                        return;
                    }
                    completedUpload.failedTransfers().forEach(failedUpload ->
                            logger.error("Object {} failed to upload with exception {}",
                                    failedUpload.request().putObjectRequest().key(),
                                    failedUpload.exception().getMessage())
                    );
                }).join();
    }

    static List<String> getQueueUrls() {
        return sqsClient.listQueues()
                .thenApply(r -> new ArrayList<>(r.queueUrls())).join();
    }
}