/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.sqs.DeadLetterQueues;
import com.example.sqs.LongPolling;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.sqs.SqsClient;
import software.amazon.awssdk.services.sqs.model.*;
import java.io.*;
import java.util.*;
import com.example.sqs.*;

@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class SQSIntegrationTest {

    private static SqsClient sqsClient;
    private static String queueName ="";
    private static String queueUrl ="" ; // set dynamically in the test
    private static String message ="";
    private static String dlqueueName ="";
    private static List<Message> messages = null; // set dynamically in the test

    @BeforeAll
    public static void setUp() {
        sqsClient = SqsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        try (InputStream input = SQSIntegrationTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            queueName = prop.getProperty("QueueName");
            message = prop.getProperty("Message");
            dlqueueName=prop.getProperty("DLQueueName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateSQSQueue() {
        queueUrl = SQSExample.createQueue(sqsClient, queueName);
        assertFalse(queueUrl.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void SendMessage() {
        SendMessages.sendMessage(sqsClient,queueName, message);
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void GetMessage() {
        messages = SQSExample.receiveMessages(sqsClient, queueUrl);
        assertNotNull(messages);
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void GetQueueAttributes() {
        GetQueueAttributes.getAttributes(sqsClient, queueName);
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void DeleteMessages() {
        SQSExample.deleteMessages(sqsClient, queueUrl,messages );
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void LongPolling() {
        LongPolling.setLongPoll(sqsClient);
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void DeadLetterQueues() {
        DeadLetterQueues.setDeadLetterQueue(sqsClient);
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void DeleteQueue() {
        DeleteQueue.deleteSQSQueue(sqsClient, queueName);
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void HelloSQS() {
        HelloSQS.listQueues(sqsClient);
        System.out.println("Test 9 passed");
    }

}
