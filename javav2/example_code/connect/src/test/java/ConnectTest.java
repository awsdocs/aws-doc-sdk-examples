// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.connect.CreateInstance;
import com.example.connect.DeleteInstance;
import com.example.connect.DescribeContact;
import com.example.connect.DescribeInstance;
import com.example.connect.DescribeInstanceAttribute;
import com.example.connect.GetContactAttributes;
import com.example.connect.ListInstances;
import com.example.connect.ListPhoneNumbers;
import com.example.connect.ListUsers;
import com.example.connect.SearchQueues;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import java.util.Random;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConnectTest {
    private static final Logger logger = LoggerFactory.getLogger(ConnectTest.class);
    private static ConnectClient connectClient;
    private static String instanceAlias = "";
    private static String instanceId = "";
    private static String contactId = "";
    private static String existingInstanceId = "";
    private static String targetArn = "";

    @BeforeAll
    public static void setUp() {
        connectClient = ConnectClient.builder()
                .region(Region.US_EAST_1)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        int randomValue = new Random().nextInt(1000) + 1;
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        instanceAlias = values.getInstanceAlias()+randomValue;
        contactId = values.getContactId();
        existingInstanceId = values.getExistingInstanceId();
        targetArn = values.getTargetArn();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateInstance() {
        instanceId = CreateInstance.createConnectInstance(connectClient, instanceAlias);
        assertFalse(instanceId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeInstance() throws InterruptedException {
        assertDoesNotThrow(() -> DescribeInstance.describeSpecificInstance(connectClient, instanceId));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testListInstances() {
        assertDoesNotThrow(() -> ListInstances.listAllInstances(connectClient));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testDeleteInstance() {
        assertDoesNotThrow(() -> DeleteInstance.deleteSpecificInstance(connectClient, instanceId));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListPhoneNumbers() {
        assertDoesNotThrow(() -> ListPhoneNumbers.getPhoneNumbers(connectClient, targetArn));
        logger.info("Test 5 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/connect";
        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/connect (an AWS Secrets Manager secret)")
    class SecretValues {
        private String instanceAlias;
        private String contactId;
        private String existingInstanceId;

        private String targetArn;

        public String getInstanceAlias() {
            return instanceAlias;
        }

        public String getContactId() {
            return contactId;
        }

        public String getExistingInstanceId() {
            return existingInstanceId;
        }

        public String getTargetArn() {
            return targetArn;
        }
    }
}
