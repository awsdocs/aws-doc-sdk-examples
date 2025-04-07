// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.stdqueue;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.example.sqs.jms.SqsJmsExampleUtils;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Duration;
import java.util.UUID;

/**
 * This class demonstrates synchronous message processing with a standard Amazon SQS queue
 * using the Amazon SQS Java Messaging Library. It shows how to receive messages from the queue
 * using standard JMS (Java Message Service) interfaces, processing each message completely
 * before moving to the next one.
 */
public class SyncMessageReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncMessageReceiver.class);
    public static final String QUEUE_NAME = "SQSJMSClientExampleQueue" + UUID.randomUUID();

    public static void main(String[] args) {
        try {
            new Thread(SqsJmsExampleUtils.sendAMessageAsync(QUEUE_NAME, 10L, 1, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT)).start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            doReceiveMessageSync();
            SqsJmsExampleUtils.cleanUpExample(QUEUE_NAME, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT);
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    // snippet-start:[sqs-jms.java2.receive-message-sync]
    /**
     * This method receives messages from a standard Amazon SQS queue using the Amazon SQS Java
     * Messaging Library. It creates a connection to the queue using JMS (Java Message Service),
     * waits for messages to arrive, and processes them one at a time. The method handles all
     * necessary setup and cleanup of messaging resources.
     *
     * @throws JMSException If there is a problem connecting to or receiving messages from the queue
     */
    public static void doReceiveMessageSync() throws JMSException {
        // Create a connection factory.
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                SqsClient.create()
        );

        // Create a connection.
        try (SQSConnection connection = connectionFactory.createConnection() ) {

            // Create the queue if needed.
            SqsJmsExampleUtils.ensureQueueExists(connection, QUEUE_NAME, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT);

            // Create a session.
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            MessageConsumer consumer = session.createConsumer(session.createQueue(QUEUE_NAME));

            connection.start();

            receiveMessages(consumer);
        }  // The connection closes automatically. This also closes the session.
        LOGGER.info("Connection closed");
    }

    /**
     * This method continuously checks for new messages from a standard Amazon SQS queue using
     * the Amazon SQS Java Messaging Library. It waits up to 20 seconds for each message, processes
     * it using JMS (Java Message Service), and confirms receipt. The method stops checking for
     * messages after 20 seconds of no activity.
     *
     * @param consumer The JMS message consumer that receives messages from the queue
     */
    private static void receiveMessages(MessageConsumer consumer) {
        try {
            while (true) {
                LOGGER.info("Waiting for messages...");
                // Wait 1 minute for a message
                Message message = consumer.receive(Duration.ofSeconds(20).toMillis());
                if (message == null) {
                    LOGGER.info("Shutting down after 20 seconds of silence.");
                    break;
                }
                SqsJmsExampleUtils.handleMessage(message);
                message.acknowledge();
                LOGGER.info("Acknowledged message {}", message.getJMSMessageID());
            }
        } catch (JMSException e) {
            LOGGER.error("Error receiving from SQS: {}", e.getMessage(), e);
        }
    }
    // snippet-end:[sqs-jms.java2.receive-message-sync]

}
