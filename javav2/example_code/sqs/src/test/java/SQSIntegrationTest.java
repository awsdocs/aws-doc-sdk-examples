// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.sqs.DeadLetterQueues;
import com.example.sqs.LongPolling;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.io.*;
import java.util.*;
import com.example.sqs.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQSIntegrationTest {
    private static final Logger logger = LoggerFactory.getLogger(SQSIntegrationTest.class);
    private static SqsClient sqsClient;
    private static String queueName = "";
    private static String queueUrl = "";
    private static String message = "";
    private static String dlqueueName = "";
    private static List<Message> messages = null;

    @BeforeAll
    public static void setUp() throws IOException {
        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;
        sqsClient = SqsClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        QueueMessage queueMessage = gson.fromJson(json, QueueMessage.class);
        queueName = queueMessage.getQueueName() + randomNum;
        dlqueueName = queueMessage.getDLQueueName();
        message = queueMessage.getMessage();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateSQSQueue() {
        queueUrl = SQSExample.createQueue(sqsClient, queueName);
        assertFalse(queueUrl.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testSendMessage() {
        assertDoesNotThrow(() -> SendMessages.sendMessage(sqsClient, queueName, message));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testGetMessage() {
        messages = SQSExample.receiveMessages(sqsClient, queueUrl);
        assertNotNull(messages);
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetQueueAttributes() {
        assertDoesNotThrow(() -> GetQueueAttributes.getAttributes(sqsClient, queueName));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testDeleteMessages() {
        assertDoesNotThrow(() -> SQSExample.deleteMessages(sqsClient, queueUrl, messages));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testLongPolling() {
        assertDoesNotThrow(() -> LongPolling.setLongPoll(sqsClient));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testDeadLetterQueues() {
        assertDoesNotThrow(() -> DeadLetterQueues.setDeadLetterQueue(sqsClient));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testDeleteQueue() {
        assertDoesNotThrow(() -> DeleteQueue.deleteSQSQueue(sqsClient, queueName));
        logger.info("Test 8 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/sqs";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/sns, an AWS Secrets Manager secret")
    class QueueMessage {
        private String QueueName;
        private String DLQueueName;
        private String Message;

        public String getQueueName() {
            return QueueName;
        }

        public String getDLQueueName() {
            return DLQueueName;
        }

        public String getMessage() {
            return Message;
        }
    }
}
