// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs.jms.basic;

import com.amazon.sqs.javamessaging.AmazonSQSMessagingClientWrapper;
import com.amazon.sqs.javamessaging.ProviderConfiguration;
import com.amazon.sqs.javamessaging.SQSConnection;
import com.amazon.sqs.javamessaging.SQSConnectionFactory;
import com.amazon.sqs.javamessaging.SQSSession;
import jakarta.jms.JMSException;
import jakarta.jms.Message;
import jakarta.jms.MessageConsumer;
import jakarta.jms.MessageProducer;
import jakarta.jms.Queue;
import jakarta.jms.Session;
import jakarta.jms.TextMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.services.sqs.SqsClient;

import java.util.UUID;

public class JmsSqsSyncStandard {
    private static final Logger LOGGER = LoggerFactory.getLogger(JmsSqsSyncStandard.class);
    private static final String QUEUE_NAME = "MyQueue" + UUID.randomUUID();
    private static int ACKNOWLEDGE_MODE;
    private static String RECEIVE_MODE;


    /** This code example helps you get started using the SQS JMS client. Learn how to use the Java Message Service (JMS)
     * with Amazon SQS. Create connections, send and receive messages, and implement asynchronous message handling.
     *
     * @param args Provide'-acknowledge-mode [ack-mode]' and '-receive-mode [receive-mode]' as program arguments.
     *             Valid values for [ack-mode] are 'auto', 'client' and 'unordered'. Values for [receive-mode] are 'sync' and 'async'.
     *             Example: -acknowledge-mode unordered -receive-mode sync
     *             Example: -acknowledge-mode client -receive-mode async
     *             If you don't provide program arguments, the defaults are `-acknowledge-mode unordered -receive-mode sync`.
     */
    public static void main(String[] args) {
        parseProgramArgs(args);
        // snippet-start:[sqs-jms.java2.jms-basics.create-conn-factory]
        // Create a new connection factory with all defaults (credentials and region) set automatically.
        SQSConnectionFactory connectionFactory = new SQSConnectionFactory(
                new ProviderConfiguration(),
                SqsClient.create()
        );
        // snippet-end:[sqs-jms.java2.jms-basics.create-conn-factory]
        try {
            // snippet-start:[sqs-jms.java2.jms-basics.create-conn]
            // Create the connection.
            SQSConnection connection = connectionFactory.createConnection();
            // snippet-end:[sqs-jms.java2.jms-basics.create-conn]

            // snippet-start:[sqs-jms.java2.jms-basics.create-std-queue]
            // Get the wrapped client.
            AmazonSQSMessagingClientWrapper client = connection.getWrappedAmazonSQSClient();
            // Create an SQS queue named 'MyQueue', if it doesn't already exist.
                if (!client.queueExists(QUEUE_NAME)) {
                    client.createQueue(QUEUE_NAME);
                }
            // snippet-end:[sqs-jms.java2.jms-basics.create-std-queue]


            Session session;
            if (RECEIVE_MODE.equals("sync")) {
                session = switch (ACKNOWLEDGE_MODE) {
                    case Session.AUTO_ACKNOWLEDGE ->  createAutoAcknowledgeSession(connection);
                    case Session.CLIENT_ACKNOWLEDGE -> createClientAcknowledgeSession(connection);
                    case SQSSession.UNORDERED_ACKNOWLEDGE -> createUnorderedAcknowledgeSession(connection);
                    default -> throw new IllegalStateException("Unexpected value: " + ACKNOWLEDGE_MODE);
                };
            } else {
                session = createAutoAcknowledgeSession(connection);
            }
            // snippet-start:[sqs-jms.java2.jms-basics.create-send-setup]
            // Create a queue identity and specify the queue name to the session.
            Queue queue = session.createQueue(QUEUE_NAME);
            // Create a producer for queue.
            MessageProducer producer = session.createProducer(queue);
            // snippet-end:[sqs-jms.java2.jms-basics.create-send-setup]

            // snippet-start:[sqs-jms.java2.jms-basics.send-text-message-std]
            // Create the text message.
            TextMessage message = session.createTextMessage("Hello World!");
            // Send the message.
            producer.send(message);
            LOGGER.info("JMS Message {}", message.getJMSMessageID());
            // snippet-end:[sqs-jms.java2.jms-basics.send-text-message-std]

            switch (RECEIVE_MODE) {
                case "sync" -> {
                    switch (ACKNOWLEDGE_MODE) {
                        case Session.AUTO_ACKNOWLEDGE -> receiveMessageAutoAckSync(connection, session, queue);
                        case Session.CLIENT_ACKNOWLEDGE -> receiveMessageExplicitAckSync(connection, session, queue);
                        case SQSSession.UNORDERED_ACKNOWLEDGE -> receiveMessageExplicitAckSync(connection, session, queue);
                    }
                }
                case "async" -> receiveMessageAutoAckAsync(connection, session, queue);
            }
            SqsClient sqsClient = connection.getWrappedAmazonSQSClient().getAmazonSQSClient();
            String queueUrl = connection.getWrappedAmazonSQSClient().getQueueUrl(QUEUE_NAME).queueUrl();
            if (queueUrl != null){
                sqsClient.deleteQueue(b -> b.queueUrl(queueUrl));
            }
            // snippet-start:[sqs-jms.java2.jms-basics.close-conn]
            // Close the connection (and the session).
            connection.close();
            // snippet-end:[sqs-jms.java2.jms-basics.close-conn]
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    static Session createAutoAcknowledgeSession(SQSConnection connection) {
        try {
            // snippet-start:[sqs-jms.java2.jms-basics.create-session-auto-ack]
            // Create the non-transacted session with AUTO_ACKNOWLEDGE mode.
            return connection.createSession(false, Session.AUTO_ACKNOWLEDGE);
            // snippet-end:[sqs-jms.java2.jms-basics.create-session-auto-ack]
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    static Session createClientAcknowledgeSession(SQSConnection connection) {
        try {
            // snippet-start:[sqs-jms.java2.jms-basics.create-session-client-ack]
            // Create the non-transacted session with CLIENT_ACKNOWLEDGE mode.
            return connection.createSession(false, Session.CLIENT_ACKNOWLEDGE);
            // snippet-end:[sqs-jms.java2.jms-basics.create-session-client-ack]
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    static Session createUnorderedAcknowledgeSession(SQSConnection connection) {
        try {
            // snippet-start:[sqs-jms.java2.jms-basics.create-session-unordered-ack]
            // Create the non-transacted session with UNORDERED_ACKNOWLEDGE mode.
            return connection.createSession(false, SQSSession.UNORDERED_ACKNOWLEDGE);
            // snippet-end:[sqs-jms.java2.jms-basics.create-session-unordered-ack]
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    static void receiveMessageAutoAckSync(SQSConnection connection, Session session, Queue queue) {
        try {
            // snippet-start:[sqs-jms.java2.jms-basics.receive-setup-std-sync]
            // Create a consumer for the queue.
            MessageConsumer consumer = session.createConsumer(queue);
            // Start receiving incoming messages.
            connection.start();
            // snippet-end:[sqs-jms.java2.jms-basics.receive-setup-std-sync]
            // snippet-start:[sqs-jms.java2.jms-basics.receive-msg-std-sync]
            // Receive a message from the queue and wait up to 1 second.
            Message receivedMessage = consumer.receive(1000);

            // Cast the received message as a 'TextMessage' and display the text.
            if (receivedMessage != null) {
                LOGGER.info("Received: {}", ((TextMessage) receivedMessage).getText());
            }
            // snippet-end:[sqs-jms.java2.jms-basics.receive-msg-std-sync]
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    static void receiveMessageExplicitAckSync(SQSConnection connection, Session session, Queue queue) {
        try {
            // Create a consumer for the queue.
            MessageConsumer consumer = session.createConsumer(queue);
            // Start receiving incoming messages.
            connection.start();
            // Receive a message from the queue and wait up to 1 second.
            Message receivedMessage = consumer.receive(1000);
            // snippet-start:[sqs-jms.java2.jms-basics.receive-msg-client-ack]
            // Cast the received message as a 'TextMessage' and display the text.
            if (receivedMessage != null) {
                LOGGER.info("Received: {}", ((TextMessage) receivedMessage).getText());
                receivedMessage.acknowledge();
                LOGGER.info("Acknowledged: {}", receivedMessage.getJMSMessageID());
            }
            // snippet-end:[sqs-jms.java2.jms-basics.receive-msg-client-ack]
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
    }

    static void receiveMessageAutoAckAsync(SQSConnection connection, Session session, Queue queue) {
        // snippet-start:[sqs-jms.java2.jms-basics.receive-async]
        try {
            // Create a consumer for the queue.
            MessageConsumer consumer = session.createConsumer(queue);
            // Provide an implementation of the MessageListener interface, which has a single 'onMessage' method.
            // We use a lambda expression for the implementation.
            consumer.setMessageListener(message -> {
                try {
                    // Cast the received message as TextMessage and print the text to screen.
                    LOGGER.info("Received: {}", ((TextMessage) message).getText());
                } catch (JMSException e) {
                    LOGGER.error("Error processing message: {}", e.getMessage());
                }
            });
            // Start receiving incoming messages.
            connection.start();
        } catch (JMSException e) {
            throw new RuntimeException(e);
        }
        try {
            Thread.sleep(1000);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
        // snippet-end:[sqs-jms.java2.jms-basics.receive-async]
    }

    static void parseProgramArgs(String[] args) {
        for (int i = 0; i < args.length; i++) {
            switch (args[i]) {
                case "-acknowledge-mode":
                    if (i + 1 < args.length) {
                        switch (args[i + 1]) {
                            case "auto" -> ACKNOWLEDGE_MODE = Session.AUTO_ACKNOWLEDGE;
                            case "client" -> ACKNOWLEDGE_MODE = Session.CLIENT_ACKNOWLEDGE;
                            case "unordered" -> ACKNOWLEDGE_MODE = SQSSession.UNORDERED_ACKNOWLEDGE;
                        }
                        i++;
                    }
                    break;
                case "-receive-mode":
                    if (i + 1 < args.length) {
                        switch (args[i + 1]){
                            case "sync" -> RECEIVE_MODE = "sync";
                            case "async" -> RECEIVE_MODE = "async";
                        }
                        i++;
                    }
                    break;
            }
        }
        if (ACKNOWLEDGE_MODE == 0) {
            ACKNOWLEDGE_MODE = Session.AUTO_ACKNOWLEDGE;
        }
        if (RECEIVE_MODE == null) {
            RECEIVE_MODE = "sync";
        }
    }
}
