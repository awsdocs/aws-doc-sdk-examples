// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.s3;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.eventnotifications.s3.model.S3EventNotification;
import software.amazon.awssdk.services.cloudformation.CloudFormationAsyncClient;
import software.amazon.awssdk.services.cloudformation.model.Capability;
import software.amazon.awssdk.services.cloudformation.model.CloudFormationException;
import software.amazon.awssdk.services.cloudformation.waiters.CloudFormationAsyncWaiter;
import software.amazon.awssdk.services.s3.S3AsyncClient;
import software.amazon.awssdk.services.s3.model.Bucket;
import software.amazon.awssdk.services.s3.model.Event;
import software.amazon.awssdk.services.s3.model.ListBucketsResponse;
import software.amazon.awssdk.services.sqs.SqsAsyncClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequest;
import software.amazon.awssdk.services.sqs.model.DeleteMessageBatchRequestEntry;
import software.amazon.awssdk.services.sqs.model.ListQueuesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;
import software.amazon.awssdk.transfer.s3.S3TransferManager;

import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.Duration;
import java.util.HashSet;
import java.util.Optional;
import java.util.concurrent.ExecutionException;

public class ProcessS3EventNotification {
    static final CloudFormationAsyncClient cfClient = CloudFormationAsyncClient.create();
    static final SqsAsyncClient sqsClient = SqsAsyncClient.create();
    static final S3AsyncClient s3Client = S3AsyncClient.create();
    static final S3TransferManager transferManager = S3TransferManager.create();
    static final String STACK_NAME = "direct-target";
    private static final Logger logger = LoggerFactory.getLogger(ProcessS3EventNotification.class);

    public static void main(String[] args) {
        deployCloudFormationStack();
        String queueUrl = getQueueUrl();
        String queueArn = getQueueArn(queueUrl);
        String bucketName = getBucketName();
        processS3Events(bucketName, queueUrl, queueArn);
        destroyCloudFormationStack();
    }

// snippet-start:[s3.java2.process_s3_event_notifications]
    /**
     * This method receives S3 event notifications by using an SqsAsyncClient.
     * After the client receives the messages it deserializes the JSON payload and logs them. It uses
     * the S3EventNotification class (part of the S3 event notification API for Java) to deserialize
     * the JSON payload and access the messages in an object-oriented way.
     *
     * @param queueUrl The URL of the AWS SQS queue that receives the S3 event notifications.
     * @see <a href="https://sdk.amazonaws.com/java/api/latest/software/amazon/awssdk/eventnotifications/s3/model/package-summary.html">S3EventNotification API</a>.
     * <p>
     * To use S3 event notification serialization/deserialization to objects, add the following
     * dependency to your Maven pom.xml file.
     * <dependency>
     * <groupId>software.amazon.awssdk</groupId>
     * <artifactId>s3-event-notifications</artifactId>
     * <version><LATEST></version>
     * </dependency>
     * <p>
     * The S3 event notification API became available with version 2.25.11 of the Java SDK.
     * <p>
     * This example shows the use of the API with AWS SQS, but it can be used to process S3 event notifications
     * in AWS SNS or AWS Lambda as well.
     * <p>
     * Note: The S3EventNotification class does not work with messages routed through AWS EventBridge.
     */
    static void processS3Events(String bucketName, String queueUrl, String queueArn) {
        try {
            // Configure the bucket to send Object Created and Object Tagging notifications to an existing SQS queue.
            s3Client.putBucketNotificationConfiguration(b -> b
                    .notificationConfiguration(ncb -> ncb
                            .queueConfigurations(qcb -> qcb
                                    .events(Event.S3_OBJECT_CREATED, Event.S3_OBJECT_TAGGING)
                                    .queueArn(queueArn)))
                            .bucket(bucketName)
            ).join();

            triggerS3EventNotifications(bucketName);
            // Wait for event notifications to propagate.
            Thread.sleep(Duration.ofSeconds(5).toMillis());

            boolean didReceiveMessages = true;
            while (didReceiveMessages) {
                // Display the number of messages that are available in the queue.
                sqsClient.getQueueAttributes(b -> b
                                .queueUrl(queueUrl)
                                .attributeNames(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)
                        ).thenAccept(attributeResponse ->
                                logger.info("Approximate number of messages in the queue: {}",
                                        attributeResponse.attributes().get(QueueAttributeName.APPROXIMATE_NUMBER_OF_MESSAGES)))
                        .join();

                // Receive the messages.
                ReceiveMessageResponse response = sqsClient.receiveMessage(b -> b
                        .queueUrl(queueUrl)
                ).get();
                logger.info("Count of received messages: {}", response.messages().size());
                didReceiveMessages = !response.messages().isEmpty();

                // Create a collection to hold the received message for deletion
                // after we log the messages.
                HashSet<DeleteMessageBatchRequestEntry> messagesToDelete = new HashSet<>();
                // Process each message.
                response.messages().forEach(message -> {
                    logger.info("Message id: {}", message.messageId());
                    // Deserialize JSON message body to a S3EventNotification object
                    // to access messages in an object-oriented way.
                    S3EventNotification event = S3EventNotification.fromJson(message.body());

                    // Log the S3 event notification record details.
                    if (event.getRecords() != null) {
                        event.getRecords().forEach(record -> {
                            String eventName = record.getEventName();
                            String key = record.getS3().getObject().getKey();
                            logger.info(record.toString());
                            logger.info("Event name is {} and key is {}", eventName, key);
                        });
                    }
                    // Add logged messages to collection for batch deletion.
                    messagesToDelete.add(DeleteMessageBatchRequestEntry.builder()
                            .id(message.messageId())
                            .receiptHandle(message.receiptHandle())
                            .build());
                });
                // Delete messages.
                if (!messagesToDelete.isEmpty()) {
                    sqsClient.deleteMessageBatch(DeleteMessageBatchRequest.builder()
                            .queueUrl(queueUrl)
                            .entries(messagesToDelete)
                            .build()
                    ).join();
                }
            } // End of while block.
        } catch (InterruptedException | ExecutionException e) {
            throw new RuntimeException(e);
        }
    }

    // snippet-end:[s3.java2.process_s3_event_notifications]
    static void triggerS3EventNotifications(String bucketName) {
        Path uploadDir;
        try {
            uploadDir = Paths.get(
                    ProcessS3EventNotification.class.getClassLoader().getResource("uploadDirectory").toURI());
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
        try {
            Thread.sleep(Duration.ofSeconds(1).toMillis());
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }

        try (S3AsyncClient s3Client = S3AsyncClient.create()) {
            s3Client.listObjects(b -> b.bucket(bucketName))
                    .thenAccept(listObjectsResponse ->
                            listObjectsResponse.contents().forEach(s3Object -> {
                                logger.info("Object key is " + s3Object.key());
                                s3Client.putObjectTagging(potr -> potr
                                        .bucket(bucketName)
                                        .key(s3Object.key())
                                        .tagging(tb ->
                                                tb.tagSet(tsb -> tsb.key("akey").value("avalue"))
                                        )
                                ).join();
                            })).join();
        } // End of try-with-resources block.
    }

    static String getQueueUrl() {
        ListQueuesResponse response = sqsClient.listQueues().join();
        Optional<String> queueUrl = response.queueUrls().stream()
                .filter(url -> url.contains(STACK_NAME))
                .findFirst();
        return queueUrl.orElse(null);
    }

    static String getQueueArn(String queueUrl){
        return sqsClient.getQueueAttributes(b -> b
                .queueUrl(queueUrl).attributeNames(QueueAttributeName.QUEUE_ARN)).join()
                .attributes().get(QueueAttributeName.QUEUE_ARN);
    }

    static String getBucketName() {
        ListBucketsResponse listBucketsResponse = s3Client.listBuckets().join();
        for (Bucket bucket : listBucketsResponse.buckets()) {
            if (bucket.name().contains(STACK_NAME)) {
                return bucket.name();
            }
        }
        return null;
    }

    static void deployCloudFormationStack() {
        try {
            URL fileUrl = ProcessS3EventNotification.class.getClassLoader().getResource(STACK_NAME + ".yaml");
            String templateBody;
            try {
                templateBody = Files.readString(Paths.get(fileUrl.toURI()));

            } catch (IOException | URISyntaxException e) {
                throw new RuntimeException(e);
            }

            cfClient.createStack(b -> b.stackName(STACK_NAME)
                            .templateBody(templateBody)
                            .capabilities(Capability.CAPABILITY_IAM))
                    .whenComplete((csr, t) -> {
                        if (csr != null) {
                            logger.info("Stack creation requested, ARN is " + csr.stackId());
                            try (CloudFormationAsyncWaiter waiter = cfClient.waiter()) {
                                waiter.waitUntilStackCreateComplete(request -> request.stackName(STACK_NAME))
                                        .whenComplete((dsr, th) -> {
                                            dsr.matched().response().orElseThrow(() -> new RuntimeException("Failed to deploy"));
                                        }).join();
                            }
                            logger.info("Stack created successfully");
                        } else {
                            logger.error("Error creating stack: " + t.getMessage(), t);
                            throw new RuntimeException(t.getCause().getMessage(), t);
                        }
                    }).join();
        } catch (CloudFormationException ex) {
            throw new RuntimeException("Failed to deploy CloudFormation stack", ex);
        }
    }

    static void destroyCloudFormationStack() {
        String stackName = STACK_NAME;
        cfClient.deleteStack(b -> b.stackName(stackName))
                .whenComplete((dsr, t) -> {
                    if (dsr != null) {
                        logger.info("Delete stack requested ....");
                        try (CloudFormationAsyncWaiter waiter = cfClient.waiter()) {
                            waiter.waitUntilStackDeleteComplete(request -> request.stackName(stackName))
                                    .whenComplete((waiterResponse, throwable) ->
                                            logger.info("Stack deleted successfully."))
                                    .join();
                        }
                    } else {
                        logger.error("Error deleting stack: " + t.getMessage(), t);
                        throw new RuntimeException(t.getCause().getMessage(), t);
                    }
                }).join();
    }
}
