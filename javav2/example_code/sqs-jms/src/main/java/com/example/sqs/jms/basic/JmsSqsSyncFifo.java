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
import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.CreateQueueRequest;
import software.amazon.awssdk.services.sqs.model.QueueAttributeName;

import java.util.Map;
import java.util.UUID;

public class JmsSqsSyncFifo {
    private static final Logger LOGGER = org.slf4j.LoggerFactory.getLogger(JmsSqsSyncFifo.class);
    private static final String QUEUE_NAME = "MyQueue-" + UUID.randomUUID() + ".fifo";

    public static void main(String[] args) {
        // Create a new connection factory with all defaults (credentials and region) set automatically.
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                SqsClient.create()
        );
        try {
            // Create the connection.
            SQSConnection connection = connectionFactory.createConnection();
            // snippet-start:[sqs-jms.java2.jms-basics.create-fifo-queue]
            // Get the wrapped client.
            AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
            // Create an Amazon SQS FIFO queue, if it doesn't already exist. FIFO queue names must end in '.fifo'.
            if (!client.queueExists(QUEUE_NAME)) {
                Map<QueueAttributeName, String> attributes2 = Map.of(
                        QueueAttributeName.FIFO_QUEUE, "true",
                        QueueAttributeName.CONTENT_BASED_DEDUPLICATION, "true"
                );
                client.createQueue(CreateQueueRequest.builder()
                        .queueName(QUEUE_NAME)
                        .attributes(attributes2)
                        .build());
            }
            // snippet-end:[sqs-jms.java2.jms-basics.create-fifo-queue]

            // Create a non-transacted session in AUTO_ACKNOWLEDGE mode.
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // Create a queue identity and specify the queue name to the session.
            Queue queue = session.createQueue(QUEUE_NAME);
            // Create a producer for the queue.
            MessageProducer producer = session.createProducer(queue);
            // snippet-start:[sqs-jms.java2.jms-basics.send-text-message-fifo]
            // Create a text message.
            TextMessage message = session.createTextMessage("Hello World!");
            // Set the message group ID.
            message.setStringProperty("JMSXGroupID", "Default");

            // You can also set a custom message deduplication ID. For example,
            // `message.setStringProperty("JMS_SQS_DeduplicationId", "hello");`
            // We don't need it for this example because content-based deduplication is enabled for the queue.

            // Send the message.
            producer.send(message);
            LOGGER.info("JMS Message {}", message.getJMSMessageID());
            LOGGER.info("JMS Message Sequence Number {}", message.getStringProperty("JMS_SQS_SequenceNumber"));
            // snippet-end:[sqs-jms.java2.jms-basics.send-text-message-fifo]

            // Create a consumer for the queue.
            MessageConsumer consumer = session.createConsumer(queue);
            // Start receiving incoming messages.
            connection.start();
            // snippet-start:[sqs-jms.java2.jms-basics.receive-msg-fifo-sync]
            // Receive a message from the queue and wait up to 1 second.
            Message receivedMessage = consumer.receive(1000);
            // Cast the received message as a 'TextMessage' and display the text.
            if (receivedMessage != null) {
                LOGGER.info("Received: {}", ((TextMessage) receivedMessage).getText());
                LOGGER.info("Group id: {}", receivedMessage.getStringProperty("JMSXGroupID"));
                LOGGER.info("Message deduplication id: {}", receivedMessage.getStringProperty("JMS_SQS_DeduplicationId"));
                LOGGER.info("Message sequence number: {}", receivedMessage.getStringProperty("JMS_SQS_SequenceNumber"));
            }
            // snippet-end:[sqs-jms.java2.jms-basics.receive-msg-fifo-sync]
            SqsClient sqsClient = connection.getWrappedAmazonSQSClient().getAmazonSQSClient();
            String queueUrl = connection.getWrappedAmazonSQSClient().getQueueUrl(QUEUE_NAME).queueUrl();
            // Close the connection (and the session).
            connection.close();
            if (queueUrl != null){
                sqsClient.deleteQueue(b -> b.queueUrl(queueUrl));
            }
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }
}
