// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.stdqueue;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import com.example.sqs.jms.SqsJmsExampleUtils;
import jakarta.jms.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.time.Duration;
import java.util.UUID;

/**
 * This class demonstrates independent message acknowledgment with a standard Amazon SQS queue
 * using the Amazon SQS Java Messaging Library. It shows how to process messages using JMS
 * (Java Message Service) with unordered acknowledgment mode, where each message must be
 * confirmed separately regardless of when it was received, providing fine-grained control
 * over message completion.
 */
public class SyncMessageReceiverUnorderedAcknowledge {
    private static final Logger LOGGER = LoggerFactory.getLogger(SyncMessageReceiverUnorderedAcknowledge.class);
    public static final String QUEUE_NAME = "SQSJMSClientExampleQueue" + UUID.randomUUID();

    // Visibility time-out for the queue.
    private static final Long TIME_OUT_SECONDS = 5L;
    private static final Long TIME_OUT_MILLIS = Duration.ofSeconds(TIME_OUT_SECONDS).toMillis();

    public static void main(String[] args)  {
        try {
            doReceiveMessagesUnorderedAcknowledge();
            SqsJmsExampleUtils.cleanUpExample(QUEUE_NAME, TIME_OUT_SECONDS);
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    // snippet-start:[sqs-jms.java2.receive-message-sync-unordered-ack]
    /**
     * Demonstrates message acknowledgment behavior in UNORDERED_ACKNOWLEDGE mode with Amazon SQS JMS.
     * In this mode, each message must be explicitly acknowledged regardless of receive order.
     * Unacknowledged messages return to the queue after the visibility timeout expires,
     * unlike CLIENT_ACKNOWLEDGE mode where acknowledging one message acknowledges all previous messages.
     *
     * @throws JMSException         If a JMS-related error occurs during message operations
     */
    public static void doReceiveMessagesUnorderedAcknowledge() throws JMSException {
        // Create a connection factory.
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                SqsClient.create()
        );

        // Create the connection in a try-with-resources statement so that it's closed automatically.
        try( SQSConnection connection = connectionFactory.createConnection() ) {

            // Create the queue if needed.
            SqsJmsExampleUtils.ensureQueueExists(connection, QUEUE_NAME, TIME_OUT_SECONDS);

            // Create a session with unordered acknowledge mode.
            Session session = connection.createSession(false, SQSSession.UNORDERED_ACKNOWLEDGE);

            // Create the producer and consumer.
            MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));
            MessageConsumer consumer = session.createConsumer(session.createQueue(QUEUE_NAME));

            // Open a connection.
            connection.start();

            // Send two text messages.
            sendMessage(producer, session, "Message 1");
            sendMessage(producer, session, "Message 2");

            // Receive a message and don't acknowledge it.
            receiveMessage(consumer, false);

            // Receive another message and acknowledge it.
            receiveMessage(consumer, true);

            // Wait for the visibility time out, so that unacknowledged messages reappear in the queue.
            LOGGER.info("Waiting for visibility timeout...");
            try {
                Thread.sleep(TIME_OUT_MILLIS);
            } catch (InterruptedException e) {
                LOGGER.error("Interrupted while waiting for visibility timeout", e);
                Thread.currentThread().interrupt();
                throw new RuntimeException("Processing interrupted", e);
            }

            /*  We will attempt to receive another message, and we'll get the first message again. This occurs
                because in UNORDERED_ACKNOWLEDGE mode, each message requires its own separate acknowledgment.
                Since we only acknowledged the second message, the first message remains in the queue for
                redelivery. */
            receiveMessage(consumer, true);

            LOGGER.info("Connection closed.");
        } // The connection closes automatically. This also closes the session.
    }

    /**
     * Sends a text message to an Amazon SQS queue using JMS.
     *
     * @param producer    The JMS MessageProducer for the queue
     * @param session     The JMS Session for message creation
     * @param messageText The message content
     * @throws JMSException If message creation or sending fails
     */
    private static void sendMessage(MessageProducer producer, Session session, String messageText) throws JMSException {
        // Create a text message and send it.
        producer.send(session.createTextMessage(messageText));
    }
    /**
     * Synchronously receives a message from an Amazon SQS queue using the JMS API
     * with an acknowledgment parameter.
     *
     * @param consumer    The JMS MessageConsumer for the queue
     * @param acknowledge If true, acknowledges the message after receipt
     * @throws JMSException If message reception or acknowledgment fails
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
    // snippet-end:[sqs-jms.java2.receive-message-sync-unordered-ack]
}
