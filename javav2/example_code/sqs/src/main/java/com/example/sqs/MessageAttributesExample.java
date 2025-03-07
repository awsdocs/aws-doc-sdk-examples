// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

package com.example.sqs;

// snippet-start:[sqs.java2.message_attributes.import]
import org.slf4j.Logger;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.http.apache.ApacheHttpClient;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.DeleteMessageRequest;
import software.amazon.awssdk.services.sqs.model.Message;
import software.amazon.awssdk.services.sqs.model.MessageAttributeValue;
import software.amazon.awssdk.services.sqs.model.QueueDeletedRecentlyException;
import software.amazon.awssdk.services.sqs.model.QueueDoesNotExistException;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageRequest;
import software.amazon.awssdk.services.sqs.model.SendMessageResponse;
import software.amazon.awssdk.services.sqs.model.SqsException;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static org.slf4j.LoggerFactory.getLogger;
// snippet-end:[sqs.java2.message_attributes.import]

/**
 * Before running this Java V2 code example, set up your development environment, including your credentials. For more
 * information, see the <a href="https://docs.aws.amazon.com/sdk-for-java/latest/developer-guide/get-started.html">AWS
 * SDK for Java Developer Guide</a>.
 */
// snippet-start:[sqs.java2.message_attributes.main]
public class MessageAttributesExample {
    static final String QUEUE_NAME = "message-attribute-queue" + UUID.randomUUID();
    static final SqsClient SQS_CLIENT = SqsClient.builder().httpClient(ApacheHttpClient.create()).build();
    private static final Logger LOGGER = getLogger(MessageAttributesExample.class);

    public static void main(String[] args) {
        String queueUrl;
        try {
            queueUrl = createQueue();
        } catch (RuntimeException e) {
            LOGGER.error("Program ending because queue was not created.");
            throw new RuntimeException(e);
        }
        try {
            Path thumbnailPath = Paths.get(MessageAttributesExample.class.getClassLoader().getResource("thumbnail.jpg").getPath());
            sendMessageWithAttributes(thumbnailPath, queueUrl);
            receiveAndDeleteMessages(queueUrl);
        } catch (RuntimeException e) {
            LOGGER.error("Program ending because of error.");
        } finally {
            try {
                deleteQueue(queueUrl);
                LOGGER.info("Queue successfully deleted. Program ending.");
            } catch (RuntimeException e) {
                LOGGER.error("Program ending.");
            } finally {
                SQS_CLIENT.close();
            }
        }
    }

// snippet-start:[sqs.java2.message_attributes.create]
    /**
     * <p>This method demonstrates how to add message attributes to a message.
     * Each attribute must specify a name, value, and data type. You use a Java Map to supply the attributes. The map's
     * key is the attribute name, and you specify the map's entry value using a builder that includes the attribute
     * value and data type.</p>
     *
     * <p>The data type must start with one of "String", "Number" or "Binary". You can optionally
     * define a custom extension by using a "." and your extension.</p>
     *
     * <p>The SQS Developer Guide provides more information on @see <a
     * href="https://docs.aws.amazon.com/AWSSimpleQueueService/latest/SQSDeveloperGuide/sqs-message-metadata.html#sqs-message-attributes">message
     * attributes</a>.</p>
     *
     * @param thumbailPath Filesystem path of the image.
     * @param queueUrl     URL of the SQS queue.
     */
    static void sendMessageWithAttributes(Path thumbailPath, String queueUrl) {
        Map<String, MessageAttributeValue> messageAttributeMap;
        try {
            messageAttributeMap = Map.of(
                    "Name", MessageAttributeValue.builder()
                            .stringValue("Jane Doe")
                            .dataType("String").build(),
                    "Age", MessageAttributeValue.builder()
                            .stringValue("42")
                            .dataType("Number.int").build(),
                    "Image", MessageAttributeValue.builder()
                            .binaryValue(SdkBytes.fromByteArray(Files.readAllBytes(thumbailPath)))
                            .dataType("Binary.jpg").build()
            );
        } catch (IOException e) {
            LOGGER.error("An I/O exception occurred reading thumbnail image: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        SendMessageRequest request = SendMessageRequest.builder()
                .queueUrl(queueUrl)
                .messageBody("Hello SQS")
                .messageAttributes(messageAttributeMap)
                .build();
        try {
            SendMessageResponse sendMessageResponse = SQS_CLIENT.sendMessage(request);
            LOGGER.info("Message ID: {}", sendMessageResponse.messageId());
        } catch (SqsException e) {
            LOGGER.error("Exception occurred sending message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
    // snippet-end:[sqs.java2.message_attributes.create]

    static void receiveAndDeleteMessages(String queueUrl) {
        ReceiveMessageRequest request = ReceiveMessageRequest.builder()
                .queueUrl(queueUrl)
                .maxNumberOfMessages(1) // Retrieve one message at a time.
                .waitTimeSeconds(10) // Enable long polling.
                .messageAttributeNames("All")
                .build();

        List<Message> messages;
        try {
            messages = SQS_CLIENT.receiveMessage(request).messages();
        } catch (SqsException e) {
            LOGGER.error("Error receiving message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }

        for (Message message : messages) {
            LOGGER.info("Received message: {}", message.body());
            message.messageAttributes().entrySet().forEach(entry ->
                    LOGGER.info(entry.toString())
            );

            // Delete message after processing.
            deleteMessage(queueUrl, message.receiptHandle());
        }
    }

    private static void deleteMessage(String queueUrl, String receiptHandle) {
        DeleteMessageRequest request = DeleteMessageRequest.builder()
                .queueUrl(queueUrl)
                .receiptHandle(receiptHandle)
                .build();

        try {
            SQS_CLIENT.deleteMessage(request);
            System.out.println("Message deleted successfully.");
        } catch (SqsException e) {
            LOGGER.error("Error deleting message: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }


    static String createQueue() {
        try {
            return SQS_CLIENT.createQueue(b -> b.queueName(QUEUE_NAME))
                    .queueUrl();
        } catch (QueueDeletedRecentlyException e) {
            LOGGER.error("Queue deleted recently. You must wait at least 60 seconds before creating a queue with the same name.", e);
            throw new RuntimeException(e);
        }
    }

    static void deleteQueue(String queueUrl) {
        try {
            SQS_CLIENT.deleteQueue(b -> b.queueUrl(queueUrl));
        } catch (QueueDoesNotExistException e) {
            LOGGER.error("Queue does not exist: {}", e.getMessage(), e);
            throw new RuntimeException(e);
        }
    }
}
// snippet-end:[sqs.java2.message_attributes.main]
