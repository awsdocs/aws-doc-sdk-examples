// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.basic;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.UUID;

class JmsSqsSyncStandardTest {
    private final String QUEUE_NAME = "MyQueueTst" + UUID.randomUUID();
    private SQSConnection connection;

    @BeforeEach
    void setUp() {
        try {
            SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                    new ProviderConfiguration(),
                    SqsClient.create()
            );
            connection = connectionFactory.createConnection();
            AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
            // Create an SQS queue named 'MyQueue', if it doesn't already exist.
            if (!client.queueExists(QUEUE_NAME)) {
                client.createQueue(QUEUE_NAME);
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    @AfterEach
    void tearDown() {
        try {
            SqsClient sqsClient = connection.getWrappedAmazonSQSClient().getAmazonSQSClient();
            String queueUrl = connection.getWrappedAmazonSQSClient().getQueueUrl(QUEUE_NAME).queueUrl();
            connection.close();
            if (queueUrl != null){
                sqsClient.deleteQueue(b -> b.queueUrl(queueUrl));
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    void autoAckAsyncTest() {
        try {
            Session session = JmsSqsSyncStandard.createAutoAcknowledgeSession(connection);
            Queue queue = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("Hello World!");
            producer.send(message);
            JmsSqsSyncStandard.receiveMessageAutoAckAsync(connection, session, queue);
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            Message reReceivedMessage = consumer.receive(1000);
            Assertions.assertNull(reReceivedMessage);
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    void autoAckSyncTest() {
        try {
            Session session = JmsSqsSyncStandard.createAutoAcknowledgeSession(connection);
            Queue queue = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("Hello World!");
            producer.send(message);
            JmsSqsSyncStandard.receiveMessageAutoAckAsync(connection, session, queue);

            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            Message reReceivedMessage = consumer.receive(1000);
            Assertions.assertNull(reReceivedMessage);

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
    @Test
    @Tag("IntegrationTest")
    void clientAckSyncTest() {
        try {
            Session session = JmsSqsSyncStandard.createClientAcknowledgeSession(connection);
            Queue queue = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("Hello World!");
            producer.send(message);
            JmsSqsSyncStandard.receiveMessageExplicitAckSync(connection, session, queue);

            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            Message reReceivedMessage = consumer.receive(1000);
            Assertions.assertNull(reReceivedMessage);

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Tag("IntegrationTest")
    void unorderedAckSyncTest() {
        try {
            Session session = JmsSqsSyncStandard.createUnorderedAcknowledgeSession(connection);
            Queue queue = session.createQueue(QUEUE_NAME);
            MessageProducer producer = session.createProducer(queue);
            TextMessage message = session.createTextMessage("Hello World!");
            producer.send(message);
            JmsSqsSyncStandard.receiveMessageExplicitAckSync(connection, session, queue);

            MessageConsumer consumer = session.createConsumer(queue);
            connection.start();
            Message reReceivedMessage = consumer.receive(1000);
            Assertions.assertNull(reReceivedMessage);

        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    @Test
    @Tag("IntegrationTest")
    void fifoQueueWorks() {
        Assertions.assertDoesNotThrow(() -> JmsSqsSyncFifo.main(null));
    }
}