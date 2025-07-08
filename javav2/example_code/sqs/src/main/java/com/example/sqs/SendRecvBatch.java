// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

// snippet-start:[sqs.java2.sendRecvBatch.main]
// snippet-start:[sqs.java2.sendRecvBatch.import]
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
// snippet-end:[sqs.java2.sendRecvBatch.import]


/**
 * Before running this Java V2 code example, set up your development
 * environment, including your credentials.
 *
 * For more information, see the following documentation topic:
 *
 * https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html
 */

/**
 * This code demonstrates basic message operations in Amazon Simple Queue Service (Amazon SQS).
 */

public class SendRecvBatch {
    private static final Logger logger = Logger.getLogger(SendRecvBatch.class.getName());
    static {
        Logger rootLogger = Logger.getLogger("");
        rootLogger.setLevel(Level.WARNING);
    }
    private static final SqsClient sqsClient = SqsClient.builder()
            .region(Region.US_WEST_2)
            .build();

    // snippet-start:[sqs.java2.sendRecvBatch.sendBatch]
    /**
     * Send a batch of messages in a single request to an SQS queue.
     * This request may return overall success even when some messages were not sent.
     * The caller must inspect the Successful and Failed lists in the response and
     * resend any failed messages.
     *
     * @param queueUrl  The URL of the queue to receive the messages.
     * @param messages  The messages to send to the queue. Each message contains a body and attributes.
     * @return The response from SQS that contains the list of successful and failed messages.
     */
    public static SendMessageBatchResponse sendMessages(
            String queueUrl, List<MessageEntry> messages) {

        try {
            List<SendMessageBatchRequestEntry> entries = new ArrayList<>();

            for (int i = 0; i < messages.size(); i++) {
                MessageEntry message = messages.get(i);
                entries.add(SendMessageBatchRequestEntry.builder()
                        .id(String.valueOf(i))
                        .messageBody(message.getBody())
                        .messageAttributes(message.getAttributes())
                        .build());
            }

            SendMessageBatchRequest sendBatchRequest = SendMessageBatchRequest.builder()
                    .queueUrl(queueUrl)
                    .entries(entries)
                    .build();

            SendMessageBatchResponse response = sqsClient.sendMessageBatch(sendBatchRequest);

            if (!response.successful().isEmpty()) {
                for (SendMessageBatchResultEntry resultEntry : response.successful()) {
                    logger.info("Message sent: " + resultEntry.messageId() + ": " +
                            messages.get(Integer.parseInt(resultEntry.id())).getBody());
                }
            }

            if (!response.failed().isEmpty()) {
                for (BatchResultErrorEntry errorEntry : response.failed()) {
                    logger.warning("Failed to send: " + errorEntry.id() + ": " +
                            messages.get(Integer.parseInt(errorEntry.id())).getBody());
                }
            }

            return response;

        } catch (SqsException e) {
            logger.log(Level.SEVERE, "Send messages failed to queue: " + queueUrl, e);
            throw e;
        }
    }
    // snippet-end:[sqs.java2.sendRecvBatch.sendBatch]

    // snippet-start:[sqs.java2.sendRecvBatch.recvBatch]
    /**
     * Receive a batch of messages in a single request from an SQS queue.
     *
     * @param queueUrl   The URL of the queue from which to receive messages.
     * @param maxNumber  The maximum number of messages to receive. The actual number
     *                   of messages received might be less.
     * @param waitTime   The maximum time to wait (in seconds) before returning. When
     *                   this number is greater than zero, long polling is used. This
     *                   can result in reduced costs and fewer false empty responses.
     * @return The list of Message objects received. These each contain the body
     *         of the message and metadata and custom attributes.
     */
    public static List<Message> receiveMessages(String queueUrl, int maxNumber, int waitTime) {
        try {
            ReceiveMessageRequest receiveRequest = ReceiveMessageRequest.builder()
                    .queueUrl(queueUrl)
                    .maxNumberOfMessages(maxNumber)
                    .waitTimeSeconds(waitTime)
                    .messageAttributeNames("All")
                    .build();

            List<Message> messages = sqsClient.receiveMessage(receiveRequest).messages();

            for (Message message : messages) {
                logger.info("Received message: " + message.messageId() + ": " + message.body());
            }

            return messages;

        } catch (SqsException e) {
            logger.log(Level.SEVERE, "Couldn't receive messages from queue: " + queueUrl, e);
            throw e;
        }
    }
    // snippet-end:[sqs.java2.sendRecvBatch.recvBatch]

    // snippet-start:[sqs.java2.sendRecvBatch.delBatch]
    /**
     * Delete a batch of messages from a queue in a single request.
     *
     * @param queueUrl  The URL of the queue from which to delete the messages.
     * @param messages  The list of messages to delete.
     * @return The response from SQS that contains the list of successful and failed
     *         message deletions.
     */
    public static DeleteMessageBatchResponse deleteMessages(String queueUrl, List<Message> messages) {
        try {
            List<DeleteMessageBatchRequestEntry> entries = new ArrayList<>();

            for (int i = 0; i < messages.size(); i++) {
                entries.add(DeleteMessageBatchRequestEntry.builder()
                        .id(String.valueOf(i))
                        .receiptHandle(messages.get(i).receiptHandle())
                        .build());
            }

            DeleteMessageBatchRequest deleteRequest = DeleteMessageBatchRequest.builder()
                    .queueUrl(queueUrl)
                    .entries(entries)
                    .build();

            DeleteMessageBatchResponse response = sqsClient.deleteMessageBatch(deleteRequest);

            if (!response.successful().isEmpty()) {
                for (DeleteMessageBatchResultEntry resultEntry : response.successful()) {
                    logger.info("Deleted " + messages.get(Integer.parseInt(resultEntry.id())).receiptHandle());
                }
            }

            if (!response.failed().isEmpty()) {
                for (BatchResultErrorEntry errorEntry : response.failed()) {
                    logger.warning("Could not delete " + messages.get(Integer.parseInt(errorEntry.id())).receiptHandle());
                }
            }

            return response;

        } catch (SqsException e) {
            logger.log(Level.SEVERE, "Couldn't delete messages from queue " + queueUrl, e);
            throw e;
        }
    }
    // snippet-end:[sqs.java2.sendRecvBatch.delBatch]

    // snippet-start:[sqs.java2.sendRecvBatch.scenario]
    /**
     * Helper class to represent a message with body and attributes.
     */
    public static class MessageEntry {
        private final String body;
        private final Map<String, MessageAttributeValue> attributes;

        public MessageEntry(String body, Map<String, MessageAttributeValue> attributes) {
            this.body = body;
            this.attributes = attributes != null ? attributes : new HashMap<>();
        }

        public String getBody() {
            return body;
        }

        public Map<String, MessageAttributeValue> getAttributes() {
            return attributes;
        }
    }

    /**
     * Shows how to:
     * * Read the lines from this Java file and send the lines in
     *   batches of 10 as messages to a queue.
     * * Receive the messages in batches until the queue is empty.
     * * Reassemble the lines of the file and verify they match the original file.
     */
    public static void usageDemo() {
        System.out.println("-".repeat(88));
        System.out.println("Welcome to the Amazon Simple Queue Service (Amazon SQS) demo!");
        System.out.println("-".repeat(88));

        // Create a queue for the demo.
        String queueName = "sqs-usage-demo-message-wrapper-"+System.currentTimeMillis();
        CreateQueueRequest createRequest = CreateQueueRequest.builder()
                .queueName(queueName)
                .build();
        String queueUrl = sqsClient.createQueue(createRequest).queueUrl();
        System.out.println("Created queue: " + queueUrl);

        try {
            // Read the lines from this Java file.
            Path projectRoot = Paths.get(System.getProperty("user.dir"));
            Path filePath = projectRoot.resolve("src/main/java/com/example/sqs/SendRecvBatch.java");
            List<String> lines = Files.readAllLines(filePath);


            // Send file lines in batches.
            int batchSize = 10;
            System.out.println("Sending file lines in batches of " + batchSize + " as messages.");

            for (int i = 0; i < lines.size(); i += batchSize) {
                List<MessageEntry> messageBatch = new ArrayList<>();

                for (int j = i; j < Math.min(i + batchSize, lines.size()); j++) {
                    String line = lines.get(j);
                    if (line == null || line.trim().isEmpty()) {
                        continue; // Skip empty lines.
                    }

                    Map<String, MessageAttributeValue> attributes = new HashMap<>();
                    attributes.put("path", MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(filePath.toString())
                            .build());
                    attributes.put("line", MessageAttributeValue.builder()
                            .dataType("String")
                            .stringValue(String.valueOf(j))
                            .build());

                    messageBatch.add(new MessageEntry(lines.get(j), attributes));
                }

                sendMessages(queueUrl, messageBatch);
                System.out.print(".");
                System.out.flush();
            }

            System.out.println("\nDone. Sent " + lines.size() + " messages.");

            // Receive and process messages.
            System.out.println("Receiving, handling, and deleting messages in batches of " + batchSize + ".");
            String[] receivedLines = new String[lines.size()];
            boolean moreMessages = true;

            while (moreMessages) {
                List<Message> receivedMessages = receiveMessages(queueUrl, batchSize, 5);
                System.out.print(".");
                System.out.flush();

                for (Message message : receivedMessages) {
                    int lineNumber = Integer.parseInt(message.messageAttributes().get("line").stringValue());
                    receivedLines[lineNumber] = message.body();
                }

                if (!receivedMessages.isEmpty()) {
                    deleteMessages(queueUrl, receivedMessages);
                } else {
                    moreMessages = false;
                }
            }

            System.out.println("\nDone.");

            // Verify all lines were received correctly.
            boolean allLinesMatch = true;
            for (int i = 0; i < lines.size(); i++) {
                String originalLine = lines.get(i);
                String receivedLine = receivedLines[i] == null ? "" : receivedLines[i];

                if (!originalLine.equals(receivedLine)) {
                    allLinesMatch = false;
                    break;
                }
            }

            if (allLinesMatch) {
                System.out.println("Successfully reassembled all file lines!");
            } else {
                System.out.println("Uh oh, some lines were missed!");
            }

        } catch (IOException e) {
            logger.log(Level.SEVERE, "Error reading file", e);
        } finally {
            // Clean up by deleting the queue.
            DeleteQueueRequest deleteQueueRequest = DeleteQueueRequest.builder()
                    .queueUrl(queueUrl)
                    .build();
            sqsClient.deleteQueue(deleteQueueRequest);
            System.out.println("Deleted queue: " + queueUrl);
        }

        System.out.println("Thanks for watching!");
        System.out.println("-".repeat(88));
    }

    public static void main(String[] args) {
        usageDemo();
    }
}
// snippet-end:[sqs.java2.sendRecvBatch.scenario]
// snippet-end:[sqs.java2.sendRecvBatch.main]