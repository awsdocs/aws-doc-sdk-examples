// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.s3.S3Client;
import software.amazon.awssdk.services.s3.model.HeadObjectRequest;
import software.amazon.awssdk.services.s3.model.HeadObjectResponse;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Request;
import software.amazon.awssdk.services.s3.model.ListObjectsV2Response;
import software.amazon.awssdk.services.s3.model.S3Object;
import software.amazon.awssdk.services.sqs.model.Message;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * Integration test for SqsExtendedClientExample
 * <p>
 * This test verifies that:
 * 1. The S3 bucket is created and configured correctly
 * 2. The SQS queue is created correctly
 * 3. A large message is sent to the queue and stored in S3
 * 4. The message can be received from the queue
 * 5. All resources are cleaned up properly
 * <p>
 * Note: This test requires valid AWS credentials to be configured
 */
public class SqsExtendedClientExampleTest {

    private SqsExtendedClientExample example;
    private S3Client s3Client;
    private final int TEST_MESSAGE_SIZE = 300000; // 300KB, exceeds the SQS limit of 256KB

    @BeforeEach
    public void setUp() {
        s3Client = S3Client.create();
        example = new SqsExtendedClientExample(s3Client, TEST_MESSAGE_SIZE);
    }

    @AfterEach
    public void tearDown() {
        // Ensure all resources are cleaned up even if tests fail
        if (example != null) {
            example.cleanup();
        }
    }

    @Test
    @Tag("IntegrationTest")
    public void testFullWorkflow() {
        // Set up the resources
        example.setup();
        
        // Verify the S3 bucket was created
        String bucketName = example.getS3BucketName();
        assertNotNull(bucketName, "S3 bucket name should not be null");
        
        // Verify the SQS queue was created
        String queueUrl = example.getQueueUrl();
        assertNotNull(queueUrl, "Queue URL should not be null");
        assertTrue(queueUrl.contains(example.getQueueName()), "Queue URL should contain the queue name");
        
        // Send and receive a message
        Message receivedMessage = example.sendAndReceiveMessage();
        
        // Verify the message was received correctly
        assertNotNull(receivedMessage, "Received message should not be null");
        assertNotNull(receivedMessage.messageId(), "Message ID should not be null");
        assertNotNull(receivedMessage.receiptHandle(), "Receipt handle should not be null");
        assertEquals(TEST_MESSAGE_SIZE, receivedMessage.body().length(), "Message body length should match");
        assertEquals("xxxxx", receivedMessage.body().substring(0, 5), "Message content should match");
        
        // Verify the message was stored in S3
        ListObjectsV2Response listObjectsResponse = s3Client.listObjectsV2(
                ListObjectsV2Request.builder().bucket(bucketName).build());
        
        List<S3Object> s3Objects = listObjectsResponse.contents();
        assertFalse(s3Objects.isEmpty(), "S3 bucket should contain objects");
        
        // Verify at least one object in the bucket
        S3Object s3Object = s3Objects.getFirst();
        assertNotNull(s3Object.key(), "S3 object key should not be null");
        
        // Verify the object size is approximately the same as our message
        HeadObjectResponse headObjectResponse = s3Client.headObject(
                HeadObjectRequest.builder()
                        .bucket(bucketName)
                        .key(s3Object.key())
                        .build());
        
        // The S3 object size might be slightly larger due to metadata
        assertTrue(headObjectResponse.contentLength() >= TEST_MESSAGE_SIZE, 
                "S3 object size should be at least the message size");
        
        // Delete the message
        example.deleteMessage(receivedMessage);
        
        // Clean up resources (this is also done in tearDown as a safety measure)
        example.cleanup();
    }
}
