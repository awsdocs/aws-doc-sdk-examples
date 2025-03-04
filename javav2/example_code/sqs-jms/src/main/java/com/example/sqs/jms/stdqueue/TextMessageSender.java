// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.stdqueue;

import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.example.sqs.jms.SqsJmsExampleUtils;
import jakarta.jms.JMSException;
import jakarta.jms.MessageProducer;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.io.BufferedReader;
import java.io.EOFException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.util.UUID;

/**
 * This class demonstrates how to send text-based messages to a standard Amazon SQS queue
 * using the Amazon SQS Java Messaging Library. It provides an interactive way to compose
 * and send messages through JMS (Java Message Service), allowing users to type messages
 * that are then delivered to the queue.
 */
public class TextMessageSender {
    private static final Logger LOGGER = LoggerFactory.getLogger(TextMessageSender.class);
    private static final String QUEUE_NAME = "SQSJMSClientExampleQueue" + UUID.randomUUID();

    public static void main(String[] args) {
        try {
            doSendTextMessage();
        } catch (JMSException e) {
            LOGGER.error("Failed to send message: {}", e.getMessage(), e);
        } finally {
            try {
                SqsJmsExampleUtils.cleanUpExample(QUEUE_NAME, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT);
            } catch (Exception e) {
                LOGGER.error("Failed to cleanup resources: {}", e.getMessage(), e);
            }
        }
    }

    // snippet-start:[sqs-jms.java2.send-text-message]
    /**
     * This method establishes a connection to a standard Amazon SQS queue using the Amazon SQS
     * Java Messaging Library and sends text messages to it. It uses JMS (Java Message Service) API
     * with automatic acknowledgment mode to ensure reliable message delivery, and automatically
     * manages all messaging resources.
     *
     * @throws JMSException If there is a problem connecting to or sending messages to the queue
     */
    public static void doSendTextMessage() throws JMSException {
        // Create a connection factory.
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                SqsClient.create()
        );

        // Create the connection in a try-with-resources statement so that it's closed automatically.
        try (SQSConnection connection = connectionFactory.createConnection()) {

            // Create the queue if needed.
            SqsJmsExampleUtils.ensureQueueExists(connection, QUEUE_NAME, SqsJmsExampleUtils.QUEUE_VISIBILITY_TIMEOUT);

            // Create a session that uses the JMS auto-acknowledge mode.
            Session session = connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            MessageProducer producer = session.createProducer(session.createQueue(QUEUE_NAME));

            createAndSendMessages(session, producer);
        } // The connection closes automatically. This also closes the session.
        LOGGER.info("Connection closed");
    }

    /**
     * This method reads text input from the keyboard and sends each line as a separate message
     * to a standard Amazon SQS queue using the Amazon SQS Java Messaging Library. It continues
     * to accept input until the user enters an empty line, using JMS (Java Message Service) API to
     * handle the message delivery.
     *
     * @param session The JMS session used to create messages
     * @param producer The JMS message producer used to send messages to the queue
     */
    private static void createAndSendMessages(Session session, MessageProducer producer) {
        BufferedReader inputReader = new BufferedReader(
                new InputStreamReader(System.in, Charset.defaultCharset()));

        try {
            String input;
            while (true) {
                LOGGER.info("Enter message to send (leave empty to exit): ");
                input = inputReader.readLine();
                if (input == null || input.isEmpty()) break;

                TextMessage message = session.createTextMessage(input);
                producer.send(message);
                LOGGER.info("Send message {}", message.getJMSMessageID());
            }
        } catch (EOFException e) {
            // Just return on EOF
        } catch (IOException e) {
            LOGGER.error("Failed reading input: {}", e.getMessage(), e);
        } catch (JMSException e) {
            LOGGER.error("Failed sending message: {}", e.getMessage(), e);
        }
    }
    // snippet-end:[sqs-jms.java2.send-text-message]
}
