// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;
// snippet-start:[sqs.java2.sqs-extended-client.main]
import com.amazon.sqs.javamessaging.AmazonSQSExtendedClient;
import com.amazon.sqs.javamessaging.ExtendedClientConfiguration;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.joda.time.DateTime;
import org.joda.time.format.DateTimeFormat;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.BucketLifecycleConfiguration;
import software.amazon.awssdk.services.s3.model.CreateBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteBucketRequest;
import software.amazon.awssdk.services.s3.model.DeleteObjectRequest;
import software.amazon.awssdk.services.s3.model.ExpirationStatus;
import software.amazon.awssdk.services.s3.model.LifecycleExpiration;
import software.amazon.awssdk.services.s3.model.LifecycleRule;
import software.amazon.awssdk.services.s3.model.LifecycleRuleFilter;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsRequest;
import software.amazon.awssdk.services.s3.model.ListObjectVersionsResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.PutBucketLifecycleConfigurationRequest;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.CreateQueueResponse;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.DeleteQueueRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;

import java.util.Arrays;
import java.util.List;
import java.util.UUID;

/**
 * Example of using Amazon SQS Extended Client Library for Java 2.x.
 */
public class SqsExtendedClientExample {
    private static final Logger logger = LoggerFactory.getLogger(SqsExtendedClientExample.class);
    
    private String s3BucketName;
    private String queueUrl;
    private final String queueName;
    private final S3Client s3Client;
    private final SqsClient sqsExtendedClient;
    private final int messageSize;

    /**
     * Constructor with default clients and message size.
     */
    public SqsExtendedClientExample() {
        this(S3Client.create(), 300000);
    }

    /**
     * Constructor with custom S3 client and message size.
     *
     * @param s3Client The S3 client to use
     * @param messageSize The size of the test message to create
     */
    public SqsExtendedClientExample(S3Client s3Client, int messageSize) {
        this.s3Client = s3Client;
        this.messageSize = messageSize;

        // Generate a unique bucket name.
        this.s3BucketName = UUID.randomUUID() + "-" +
                DateTimeFormat.forPattern("yyMMdd-hhmmss").print(new DateTime());

        // Generate a unique queue name.
        this.queueName = "MyQueue-" + UUID.randomUUID();

        // Configure the SQS extended client.
        final ExtendedClientConfiguration extendedClientConfig = new ExtendedClientConfiguration()
                .withPayloadSupportEnabled(s3Client, s3BucketName);

        this.sqsExtendedClient = new AmazonSQSExtendedClient(SqsClient.builder().build(), extendedClientConfig);
    }

    public static void main(String[] args) {
        SqsExtendedClientExample example = new SqsExtendedClientExample();
        try {
            example.setup();
            example.sendAndReceiveMessage();
        } finally {
            example.cleanup();
        }
    }

    /**
     * Send a large message and receive it back.
     *
     * @return The received message
     */
    public Message sendAndReceiveMessage() {
        try {
            // Create a large message.
            char[] chars = new char[messageSize];
            Arrays.fill(chars, 'x');
            String largeMessage = new String(chars);

            // Send the message.
            final SendMessageRequest sendMessageRequest = SendMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .messageBody(largeMessage)
                    .build();

            sqsExtendedClient.sendMessage(sendMessageRequest);
            logger.info("Sent message of size: {}", largeMessage.length());

            // Receive and return the message.
            final ReceiveMessageResponse receiveMessageResponse = sqsExtendedClient.receiveMessage(
                    ReceiveMessageRequest.builder().queueUrl(queueUrl).build());

            List<Message> messages = receiveMessageResponse.messages();
            if (messages.isEmpty()) {
                throw new RuntimeException("No messages received");
            }

            Message message = messages.getFirst();
            logger.info("\nMessage received.");
            logger.info("  ID: {}", message.messageId());
            logger.info("  Receipt handle: {}", message.receiptHandle());
            logger.info("  Message body size: {}", message.body().length());
            logger.info("  Message body (first 5 characters): {}", message.body().substring(0, 5));

            return message;
        } catch (RuntimeException e) {
            logger.error("Error during message processing: {}", e.getMessage(), e);
            throw e;
        }
    }
// snippet-end:[sqs.java2.sqs-extended-client.main]
    /**
     * Set up the S3 bucket and SQS queue.
     */
    public void setup() {
        try {
            // Create and configure the S3 bucket.
            createAndConfigureS3Bucket();

            // Create the SQS queue.
            createSqsQueue();
        } catch (RuntimeException e) {
            logger.error("Error during setup: {}", e.getMessage(), e);
            cleanup(); // Clean up any resources that were created before the error
            throw e;
        }
    }

    /**
     * Clean up all AWS resources
     */
    public void cleanup() {
        try {
            // Delete the queue if it was created
            if (queueUrl != null) {
                sqsExtendedClient.deleteQueue(DeleteQueueRequest.builder().queueUrl(queueUrl).build());
                logger.info("Deleted the queue: {}", queueUrl);
                queueUrl = null;
            }

            // Delete the S3 bucket and its contents if it was created
            if (s3BucketName != null) {
                deleteBucketAndAllContents();
                logger.info("Deleted the bucket: {}", s3BucketName);
                s3BucketName = null;
            }
        } catch (RuntimeException e) {
            logger.error("Error during cleanup: {}", e.getMessage(), e);
        }
    }

    /**
     * Create and configure the S3 bucket with lifecycle rules
     */
    private void createAndConfigureS3Bucket() {
        final LifecycleRule lifeCycleRule = LifecycleRule.builder()
                .expiration(LifecycleExpiration.builder().days(14).build())
                .filter(LifecycleRuleFilter.builder().prefix("").build())
                .status(ExpirationStatus.ENABLED)
                .build();

        final BucketLifecycleConfiguration lifecycleConfig = BucketLifecycleConfiguration.builder()
                .rules(lifeCycleRule)
                .build();

        s3Client.createBucket(CreateBucketRequest.builder().bucket(s3BucketName).build());
        s3Client.putBucketLifecycleConfiguration(PutBucketLifecycleConfigurationRequest.builder()
                .bucket(s3BucketName)
                .lifecycleConfiguration(lifecycleConfig)
                .build());

        logger.info("Bucket created and configured: {}", s3BucketName);
    }

    /**
     * Create the SQS queue
     */
    private void createSqsQueue() {
        final CreateQueueResponse createQueueResponse = sqsExtendedClient.createQueue(
                CreateQueueRequest.builder().queueName(queueName).build());
        queueUrl = createQueueResponse.queueUrl();
        logger.info("Queue created: {}", queueUrl);
    }

    /**
     * Delete the message from the SQS queue
     *
     * @param message The message to delete
     */
    public void deleteMessage(Message message) {
        sqsExtendedClient.deleteMessage(
                DeleteMessageRequest.builder()
                        .queueUrl(queueUrl)
                        .receiptHandle(message.receiptHandle())
                        .build());

        logger.info("Deleted the message: {}", message.messageId());
    }

    /**
     * Delete the S3 bucket and all its contents
     */
    private void deleteBucketAndAllContents() {
        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(
                ListObjectsV2Request.builder().bucket(s3BucketName).build());

        listObjectsResponse.contents().forEach(object -> {
            s3Client.deleteObject(DeleteObjectRequest.builder()
                    .bucket(s3BucketName)
                    .key(object.key())
                    .build());
            logger.info("Deleted S3 object: {}", object.key());
        });

        ListObjectVersionsResponse listVersionsResponse = s3Client.listObjectVersions(
                ListObjectVersionsRequest.builder().bucket(s3BucketName).build());

        listVersionsResponse.versions().forEach(version -> s3Client.deleteObject(DeleteObjectRequest.builder()
                .bucket(s3BucketName)
                .key(version.key())
                .versionId(version.versionId())
                .build()));

        s3Client.deleteBucket(DeleteBucketRequest.builder().bucket(s3BucketName).build());
    }

    /**
     * Get the S3 bucket name
     *
     * @return The S3 bucket name
     */
    public String getS3BucketName() {
        return s3BucketName;
    }

    /**
     * Get the SQS queue URL
     *
     * @return The SQS queue URL
     */
    public String getQueueUrl() {
        return queueUrl;
    }

    /**
     * Get the SQS queue name
     *
     * @return The SQS queue name
     */
    public String getQueueName() {
        return queueName;
    }

}
