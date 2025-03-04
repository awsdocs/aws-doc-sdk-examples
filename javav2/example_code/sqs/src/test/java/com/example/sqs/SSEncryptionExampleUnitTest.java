// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueUrlResponse;
import software.amazon.awssdk.services.sqs.model.SetQueueAttributesRequest;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class SSEncryptionExampleUnitTest {
    /**
     * Test case for addEncryption method
     * Verifies that the method successfully adds encryption attributes to the SQS queue
     */
    @Test
    public void testAddEncryptionSuccessful() {
        // Arrange
        String queueName = "TestQueue";
        String kmsMasterKeyAlias = "test-kms-key";
        String queueUrl = "anyqueueUrl";

        SqsClient mockSqsClient = mock(SqsClient.class);
        GetQueueUrlResponse mockGetQueueUrlResponse = mock(GetQueueUrlResponse.class);

        try (MockedStatic<SqsClient> mockedStaticSqsClient = Mockito.mockStatic(SqsClient.class)) {
            mockedStaticSqsClient.when(SqsClient::create).thenReturn(mockSqsClient);

            when(mockSqsClient.getQueueUrl(any(GetQueueUrlRequest.class))).thenReturn(mockGetQueueUrlResponse);
            when(mockGetQueueUrlResponse.queueUrl()).thenReturn(queueUrl);

            // Act
            SSEncryptionExample.addEncryption(queueName, kmsMasterKeyAlias);

            // Assert
            verify(mockSqsClient).getQueueUrl(any(GetQueueUrlRequest.class));
            verify(mockSqsClient).setQueueAttributes(any(SetQueueAttributesRequest.class));
            verify(mockSqsClient).close();
        }
    }
}