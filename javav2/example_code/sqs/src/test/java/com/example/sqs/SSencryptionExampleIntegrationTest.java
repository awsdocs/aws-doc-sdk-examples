// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesRequest;
import software.amazon.awssdk.services.sqs.model.GetQueueAttributesResponse;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class SSEncryptionExampleIntegrationTest {
    private static SqsClient sqsClient;
    private static String queueName;
    private static String kmsMasterKeyAlias;

    @BeforeAll
    static void setUp() {
        CloudFormationHelper.deployCloudFormationStack(
                SSEncryptionExample.STACK_NAME, SSEncryptionExample.CFN_TEMPLATE_FILE_NAME);

        sqsClient = SqsClient.create();
        final Map<String, String> stackOutputs = CloudFormationHelper.getStackOutputs(SSEncryptionExample.STACK_NAME);
        queueName = stackOutputs.get("QueueName");
        kmsMasterKeyAlias = stackOutputs.get("KeyAlias");
    }

    @AfterAll
    static void tearDown() {
        sqsClient.close();
        CloudFormationHelper.destroyCloudFormationStack(SSEncryptionExample.STACK_NAME);
    }

    @Test
    @DisplayName("Test SQS Queue Encryption Configuration")
    void testAddEncryption() {
        // Call the method under test
        SSEncryptionExample.addEncryption(queueName, kmsMasterKeyAlias);

        String queueUrl = sqsClient.getQueueUrl(b -> b.queueName(queueName).build()).queueUrl();


        // Verify the encryption settings
        GetQueueAttributesRequest attributesRequest = GetQueueAttributesRequest.builder()
                .queueUrl(queueUrl)
                .attributeNames(QueueAttributeName.KMS_MASTER_KEY_ID)
                .build();

        GetQueueAttributesResponse attributesResponse = sqsClient.getQueueAttributes(attributesRequest);

        // Assert that the KMS key is properly set
        assertTrue(attributesResponse.hasAttributes());
        assertEquals(kmsMasterKeyAlias,
                attributesResponse.attributes().get(QueueAttributeName.KMS_MASTER_KEY_ID));
    }
}
