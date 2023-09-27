/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.firehose.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FirehoseTest {
    private static FirehoseClient firehoseClient;
    private static String bucketARN = "";
    private static String roleARN = "";
    private static String newStream = "";
    private static String textValue = "";

    @BeforeAll
    public static void setUp() throws IOException {
        firehoseClient = FirehoseClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        bucketARN = values.getBucketARN();
        roleARN = values.getRoleARN();
        newStream = values.getNewStream() +java.util.UUID.randomUUID();
        textValue = values.getTextValue();
        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = FirehoseTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            bucketARN = prop.getProperty("bucketARN");
            roleARN = prop.getProperty("roleARN");
            newStream = prop.getProperty("newStream")+java.util.UUID.randomUUID();
            textValue = prop.getProperty("textValue");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateDeliveryStream() {
        assertDoesNotThrow(() ->CreateDeliveryStream.createStream(firehoseClient, bucketARN, roleARN, newStream));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void PutRecord() throws InterruptedException {
        //Wait 60 secs for resource to become available
        System.out.println("Wait 10 mins for resource to become available.");
        TimeUnit.MINUTES.sleep(10);
        assertDoesNotThrow(() ->PutRecord.putSingleRecord(firehoseClient, textValue, newStream));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void PutBatchRecords() {
        assertDoesNotThrow(() ->PutBatchRecords.addStockTradeData(firehoseClient, newStream));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListDeliveryStreams() {
        assertDoesNotThrow(() ->ListDeliveryStreams.listStreams(firehoseClient));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void DeleteStream() {
        assertDoesNotThrow(() ->DeleteStream.delStream(firehoseClient, newStream));
        System.out.println("Test 5 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/firehose";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/firehose (an AWS Secrets Manager secret)")
    class SecretValues {
        private String bucketARN;
        private String roleARN;
        private String newStream;

        private String textValue;

        public String getBucketARN() {
            return bucketARN;
        }

        public String getRoleARN() {
            return roleARN;
        }

        public String getNewStream() {
            return newStream;
        }

        public String getTextValue() {
            return textValue;
        }
    }

}

