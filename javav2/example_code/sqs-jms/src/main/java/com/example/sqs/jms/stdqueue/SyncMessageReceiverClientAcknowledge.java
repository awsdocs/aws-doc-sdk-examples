// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.stdqueue;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.example.sqs.jms.SqsJmsExampleUtils;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Duration;
import java.util.UUID;

/**
 * This class demonstrates manual message acknowledgment with a standard Amazon SQS queue
 * using the Amazon SQS Java Messaging Library. It shows how to receive messages synchronously
 * and explicitly confirm their processing using JMS (Java Message Service) client
 * acknowledgment mode, giving the application full control over message completion.
 */
public class SyncMessageReceiverClientAcknowledge {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncMessageReceiverClientAcknowledge.class);
    public static final String QUEUE_NAME = "SQSJMSClientExampleQueue" + UUID.randomUUID();

    // Visibility time-out for the queue.
    private static final Long TIME_OUT_SECONDS = 5L;
    private static final Long TIME_OUT_MILLIS = Duration.ofSeconds(TIME_OUT_SECONDS).toMillis();

    public static void main(String[] args) {
        try {
            doReceiveMessagesSyncClientAcknowledge();
            SqsJmsExampleUtils.cleanUpExample(QUEUE_NAME, TIME_OUT_SECONDS);
        } catch (JMSException e) {
            LOGGER.error("JMS Exception occurred", e);
        }
    }

    // snippet-start:[sqs-jms.java2.receive-message-sync-client-ack]
    /**
     * This method demonstrates how message acknowledgment affects message processing in a standard
     * Amazon SQS queue using the Amazon SQS Java Messaging Library. It sends messages to the queue,
     * then shows how JMS (Java Message Service) client acknowledgment mode handles both explicit
     * and implicit message confirmations, including how acknowledging one message can automatically
     * acknowledge previous messages.
     *
     * @throws JMSException If there is a problem with the messaging operations
     */
    public static void doReceiveMessagesSyncClientAcknowledge() throws JMSException {
        // Create a connection factory.
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                SqsClient.create()
        );

        // Create the connection in a try-with-resources statement so that it's closed automatically.
        try (SQSConnection connection = connectionFactory.createConnection() ) {

            // Create the queue if needed.
            SqsJmsExampleUtils.ensureQueueExists(connection, QUEUE_NAME, TIME_OUT_SECONDS);

            // Create a session with client acknowledge mode.
            Session session = connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);

            // Create a producer and consumer.
            MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));
            MessageConsumer consumer = session.createConsumer(session.createQueue(QUEUE_NAME));

            // Open the connection.
            connection.start();

            // Send two text messages.
            sendMessage(producer, session, "Message 1");
            sendMessage(producer, session, "Message 2");

            // Receive a message and don't acknowledge it.
            receiveMessage(consumer, false);

            // Receive another message and acknowledge it.
            receiveMessage(consumer, true);

            // Wait for the visibility time out, so that unacknowledged messages reappear in the queue,
            LOGGER.info("Waiting for visibility timeout...");
            try {
                Thread.sleep(TIME_OUT_MILLIS);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for visibility timeout", e);
                Thread.currentThread().interrupt();
                throw new RuntimeException("Processing interrupted", e);
            }

            /*  We will attempt to receive another message, but none will be available. This is because in
                CLIENT_ACKNOWLEDGE mode, when we acknowledged the second message, all previous messages were
                automatically acknowledged as well. Therefore, although we never directly acknowledged the first
                message, it was implicitly acknowledged when we confirmed the second one. */
            receiveMessage(consumer, true);
        } // The connection closes automatically. This also closes the session.
        LOGGER.info("Connection closed.");

    }


    /**
     * Sends a text message using the specified JMS MessageProducer and Session.
     *
     * @param producer    The JMS MessageProducer used to send the message
     * @param session     The JMS Session used to create the text message
     * @param messageText The text content to be sent in the message
     * @throws JMSException If there is an error creating or sending the message
     */
    private static void sendMessage(MessageProducer producer, Session session, String messageText) throws JMSException {
        // Create a text message and send it.
        producer.send(session.createTextMessage(messageText));
    }

    /**
     * Receives and processes a message from a JMS queue using the specified consumer.
     * The method waits for a message until the configured timeout period is reached.
     * If a message is received, it is logged and optionally acknowledged based on the
     * acknowledge parameter.
     *
     * @param consumer    The JMS MessageConsumer used to receive messages from the queue
     * @param acknowledge Boolean flag indicating whether to acknowledge the message.
     *                    If true, the message will be acknowledged after processing
     * @throws JMSException If there is an error receiving, processing, or acknowledging the message
     */
    private static void receiveMessage(MessageConsumer consumer, boolean acknowledge) throws JMSException {
        // Receive a message.
        Message message = consumer.receive(TIME_OUT_MILLIS);

        if (message == null) {
            LOGGER.info("Queue is empty!");
        } else {
            // Since this queue has only text messages, cast the message object and print the text.
            LOGGER.info("Received: {}    Acknowledged: {}", ((TextMessage) message).getText(), acknowledge);

            // Acknowledge the message if asked.
            if (acknowledge) message.acknowledge();
        }
    }
    // snippet-end:[sqs-jms.java2.receive-message-sync-client-ack]
}
