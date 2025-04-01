// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.firehose.*;
import com.example.firehose.scenario.FirehoseScenario;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.firehose.FirehoseClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class FirehoseTest {
    private static final Logger logger = LoggerFactory.getLogger(FirehoseTest.class);
    private static FirehoseClient firehoseClient;
    private static String bucketARN = "";
    private static String roleARN = "";
    private static String newStream = "";
    private static String textValue = "";

    @BeforeAll
    public static void setUp() throws IOException {
        firehoseClient = FirehoseClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        bucketARN = values.getBucketARN();
        roleARN = values.getRoleARN();
        newStream = values.getNewStream() + java.util.UUID.randomUUID();
        textValue = values.getTextValue();

    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateDeliveryStream() {
        assertDoesNotThrow(() -> {
            CreateDeliveryStream.createStream(firehoseClient, bucketARN, roleARN, newStream);
            CreateDeliveryStream.waitForStreamToBecomeActive(firehoseClient, newStream);
        });
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testPutRecord() throws IOException, InterruptedException {
        String jsonContent = FirehoseScenario.readJsonFile("sample_records.json");
        ObjectMapper objectMapper = new ObjectMapper();
        List<Map<String, Object>> sampleData = objectMapper.readValue(jsonContent, new TypeReference<>() {});

        // Process individual records.
        System.out.println("Processing individual records...");
        sampleData.subList(0, 100).forEach(record -> {
            try {
                FirehoseScenario.putRecord(record, newStream);
            } catch (Exception e) {
                System.err.println("Error processing record: " + e.getMessage());
            }
        });
        logger.info("Test 2 passed");
    }

   @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testListDeliveryStreams() {
        assertDoesNotThrow(() -> ListDeliveryStreams.listStreams(firehoseClient));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testDeleteStream() {
        assertDoesNotThrow(() -> DeleteStream.delStream(firehoseClient, newStream));
        logger.info("Test 4 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
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
