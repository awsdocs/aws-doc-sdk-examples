// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.util.*;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Order;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SendRecvBatchTest {
    private static final Logger logger = LoggerFactory.getLogger(SendRecvBatchTest.class);
    private static final SqsClient sqsClient = SqsClient.create();
    private String queueUrl = "";

    @BeforeEach
    void setUp() {
        String queueName = "SendRecvBatch-queue-" + UUID.randomUUID().toString().replace("-", "").substring(0, 20);
        queueUrl = sqsClient.createQueue(b -> b.queueName(queueName)).queueUrl();
        logger.info("Created test queue: {}", queueUrl);
    }

    @AfterEach
    void tearDown() {
        sqsClient.deleteQueue(b -> b.queueUrl(queueUrl));
        logger.info("Deleted test queue: {}", queueUrl);
    }

    private static Stream<Arguments> sendMessageBatchTestData() {
        return Stream.of(
                Arguments.of(List.of(
                        new SendRecvBatch.MessageEntry("Message 1", Collections.emptyMap()),
                        new SendRecvBatch.MessageEntry("Message 2", Collections.emptyMap())
                )),
                Arguments.of(List.of(
                        new SendRecvBatch.MessageEntry("Message with attributes", Map.of(
                                "type", MessageAttributeValue.builder()
                                        .stringValue("test")
                                        .dataType("String")
                                        .build()
                        ))
                ))
        );
    }

    @ParameterizedTest
    @MethodSource("sendMessageBatchTestData")
    @Order(1)
    void testSendMessages(List<SendRecvBatch.MessageEntry> messages) {
        logger.info("Testing send messages with {} messages", messages.size());
        SendMessageBatchResponse response = SendRecvBatch.sendMessages(queueUrl, messages);
        assertEquals(messages.size(), response.successful().size());
        logger.info("Successfully sent {} messages", response.successful().size());
    }

    @Test
    @Order(2)
    void testReceiveMessages() {
        logger.info("Testing receive messages");
        // First send some messages
        List<SendRecvBatch.MessageEntry> messages = List.of(
                new SendRecvBatch.MessageEntry("Test message 1", Collections.emptyMap()),
                new SendRecvBatch.MessageEntry("Test message 2", Collections.emptyMap())
        );
        SendRecvBatch.sendMessages(queueUrl, messages);
        logger.info("Sent {} messages for receive test", messages.size());

        List<Message> receivedMessages = SendRecvBatch.receiveMessages(queueUrl, 10, 5);
        assertFalse(receivedMessages.isEmpty());
        logger.info("Received {} messages", receivedMessages.size());
    }

    @Test
    @Order(3)
    void testDeleteMessages() {
        logger.info("Testing delete messages");
        // First send and receive messages
        List<SendRecvBatch.MessageEntry> messages = List.of(
                new SendRecvBatch.MessageEntry("Test message", Collections.emptyMap())
        );
        SendRecvBatch.sendMessages(queueUrl, messages);
        logger.info("Sent {} messages for delete test", messages.size());

        List<Message> receivedMessages = SendRecvBatch.receiveMessages(queueUrl, 10, 5);
        assertFalse(receivedMessages.isEmpty());
        logger.info("Received {} messages to delete", receivedMessages.size());

        DeleteMessageBatchResponse response = SendRecvBatch.deleteMessages(queueUrl, receivedMessages);
        assertEquals(receivedMessages.size(), response.successful().size());
        logger.info("Successfully deleted {} messages", response.successful().size());
    }

    @Test
    @Order(4)
    void testMessageEntry() {
        logger.info("Testing MessageEntry with attributes");
        Map<String, MessageAttributeValue> attributes = Map.of(
                "test", MessageAttributeValue.builder()
                        .stringValue("value")
                        .dataType("String")
                        .build()
        );

        SendRecvBatch.MessageEntry entry = new SendRecvBatch.MessageEntry("Test body", attributes);

        assertEquals("Test body", entry.getBody());
        assertEquals(attributes, entry.getAttributes());
        logger.info("MessageEntry test passed with body: {}", entry.getBody());
    }

    @Test
    @Order(5)
    void testMessageEntryWithNullAttributes() {
        logger.info("Testing MessageEntry with null attributes");
        SendRecvBatch.MessageEntry entry = new SendRecvBatch.MessageEntry("Test body", null);

        assertEquals("Test body", entry.getBody());
        assertNotNull(entry.getAttributes());
        assertTrue(entry.getAttributes().isEmpty());
        logger.info("MessageEntry null attributes test passed");
    }
}
