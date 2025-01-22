package com.example.sqs;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.ReceiveMessageResponse;

import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Set;

import static org.slf4j.LoggerFactory.getLogger;

class MessageAttributesExampleTest {
    private static final Logger LOGGER = getLogger(MessageAttributesExampleTest.class);
    private String queueUrl = MessageAttributesExample.QUEUE_NAME;
    private SqsClient sqsClient = MessageAttributesExample.SQS_CLIENT;

    @BeforeEach
    void setup() {
        try {
            queueUrl = MessageAttributesExample.createQueue();
        } catch (RuntimeException e) {
            LOGGER.error("Test ending because queue was not created.");
            throw new RuntimeException(e);
        }
    }

    @AfterEach
    void tearDown() {
        try {
            MessageAttributesExample.deleteQueue(queueUrl);
            LOGGER.info("Queue successfully deleted. Program ending.");
        } catch (RuntimeException e) {
            LOGGER.error("Program ending with error deleting queue.");
            throw new RuntimeException(e);
        }
    }

    @Test
    @Tag("IntegrationTest")
    void receiveAndDeleteMessages() {
        Path thumbnailPath = Paths.get(MessageAttributesExample.class.getClassLoader().getResource("thumbnail.jpg").getPath());
        MessageAttributesExample.sendMessageWithAttributes(thumbnailPath, queueUrl);

        ReceiveMessageResponse response = sqsClient.receiveMessage(b -> b.queueUrl(queueUrl).maxNumberOfMessages(1).messageAttributeNames("All"))/*messageAttributeNames("Image", "Age", "Image")*/;
        Assertions.assertEquals(1, response.messages().size());

        response.messages().get(0).messageAttributes().forEach((k, v) -> {
            Assertions.assertTrue((Set.of("Image", "Age", "Name").contains(k)));
        });
    }

}