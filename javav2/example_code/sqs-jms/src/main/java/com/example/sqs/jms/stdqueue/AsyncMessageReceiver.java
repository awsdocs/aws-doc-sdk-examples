// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.stdqueue;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.example.sqs.jms.SqsJmsExampleUtils;
import jakarta.jms.JMSException;
import jakarta.jms.MessageConsumer;
import jakarta.jms.Session;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.UUID;

/**
 * This class demonstrates non-blocking message processing with a standard Amazon SQS queue
 * using the Amazon SQS Java Messaging Library. It shows how to receive and process messages
 * automatically as they arrive using JMS (Java Message Service) event listeners, allowing
 * the application to perform other tasks while waiting for messages.
 */
public class AsyncMessageReceiver {
    private static final Logger LOGGER = LoggerFactory.getLogger(AsyncMessageReceiver.class);
    public static final String QUEUE_NAME = "SQSJMSClientExampleQueue" + UUID.randomUUID();

    public static void main(String[] args){
        try {
            new Thread(SqsJmsExampleUtils.sendAMessageAsync(QUEUE_NAME, 3L, 5, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT)).start();
        } catch (Exception e) {
            LOGGER.error(e.getMessage(), e);
        }
        try {
            doReceiveMessageAsync();
            SqsJmsExampleUtils.cleanUpExample(QUEUE_NAME, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT );
        } catch (JMSException e) {
            LOGGER.error(e.getMessage(), e);
        }
    }

    // snippet-start:[sqs-jms.java2.receive-message-async]
    /**
     * This method sets up automatic message handling for a standard Amazon SQS queue using the
     * Amazon SQS Java Messaging Library. It creates a listener that processes messages as soon
     * as they arrive using JMS (Java Message Service), runs for 5 seconds, then cleans up all
     * messaging resources.
     *
     * @throws JMSException If there is a problem connecting to or receiving messages from the queue
     */
    public static void doReceiveMessageAsync() throws JMSException {
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

            try {
                // Create a consumer for the queue.
                MessageConsumer consumer = session.createConsumer(session.createQueue(QUEUE_NAME));
                // Provide an implementation of the MessageListener interface, which has a single 'onMessage' method.
                // We use a lambda expression for the implementation.
                consumer.setMessageListener(message -> {
                    try {
                        SqsJmsExampleUtils.handleMessage(message);
                        message.acknowledge();
                    } catch (JMSException e) {
                        LOGGER.error("Error processing message: {}", e.getMessage());
                    }
                });
                // Start receiving incoming messages.
                connection.start();
                LOGGER.info("Waiting for messages...");
            } catch (JMSException e) {
                throw new RuntimeException(e);
            }
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                throw new RuntimeException(e);
            }
        }  // The connection closes automatically. This also closes the session.
        LOGGER.info( "Connection closed" );
    }
    // snippet-end:[sqs-jms.java2.receive-message-async]

}
