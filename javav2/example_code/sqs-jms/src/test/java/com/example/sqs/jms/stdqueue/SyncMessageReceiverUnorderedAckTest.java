// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.stdqueue;

import com.example.sqs.jms.SqsJmsExampleUtils;
import jakarta.jms.JMSException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

class SyncMessageReceiverUnorderedAckTest {

    private static final Logger LOGGER = LoggerFactory.getLogger(SyncMessageReceiverUnorderedAckTest.class);

    @AfterEach
    void tearDown() throws JMSException {
        SqsJmsExampleUtils.cleanUpExample(SyncMessageReceiverUnorderedAcknowledge.QUEUE_NAME, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT );
    }

    @Test
    @Tag("IntegrationTest")
    void doReceiveMessagesSyncUnorderedAcknowledgeTest() {
        try {
            SyncMessageReceiverUnorderedAcknowledge.doReceiveMessagesUnorderedAcknowledge();
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }

        // Use plain SQS test to make sure there are no messages in the queue.
        try (SqsClient sqsClient = SqsClient.create()) {
            ReceiveMessageResponse response = sqsClient.receiveMessage(b -> b.queueUrl(sqsClient
                    .getQueueUrl(builder -> builder
                            .queueName(SyncMessageReceiverUnorderedAcknowledge.QUEUE_NAME)).queueUrl()));

            Assertions.assertFalse(response.hasMessages());
        }
    }
}