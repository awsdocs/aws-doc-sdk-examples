/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

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
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.connect.ConnectClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class ConnectTest {
    private static ConnectClient connectClient;
    private static String instanceAlias = "";
    private static String instanceId = "" ;
    private static String contactId = "" ;
    private static String existingInstanceId = "" ;
    private static String targetArn = "" ;

    @BeforeAll
    public static void setUp() {
        connectClient = ConnectClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        instanceAlias = values.getInstanceAlias();
        contactId = values.getContactId();
        existingInstanceId = values.getExistingInstanceId();
        targetArn = values.getTargetArn();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = ConnectTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Load a properties file.
            prop.load(input);
            instanceAlias = prop.getProperty("instanceAlias");
            contactId = prop.getProperty("contactId");
            existingInstanceId = prop.getProperty("existingInstanceId");
            targetArn = prop.getProperty("targetArn");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createInstance() {
        instanceId = CreateInstance.createConnectInstance(connectClient, instanceAlias);
        assertFalse(instanceId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void describeInstance() throws InterruptedException {
        assertDoesNotThrow(() ->DescribeInstance.describeSpecificInstance(connectClient, instanceId));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void listInstances() {
        assertDoesNotThrow(() ->ListInstances.listAllInstances(connectClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void deleteInstance() {
        assertDoesNotThrow(() ->DeleteInstance.deleteSpecificInstance(connectClient, instanceId));
        System.out.println("Test 4 passed");
    }

    @Test
    @Order(5)
    public void describeContact() {
        assertDoesNotThrow(() ->DescribeContact.describeSpecificContact(connectClient, existingInstanceId, contactId));
        System.out.println("Test 5 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void describeInstanceAttribute() {
        assertDoesNotThrow(() ->DescribeInstanceAttribute.describeAttribute(connectClient, existingInstanceId));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void getContactAttributes() {
        assertDoesNotThrow(() -> GetContactAttributes.getContactAttrs(connectClient, existingInstanceId, contactId));
        System.out.println("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void listPhoneNumbers() {
        assertDoesNotThrow(() ->ListPhoneNumbers.getPhoneNumbers(connectClient, targetArn));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void listUsers() {
        assertDoesNotThrow(() ->ListUsers.getUsers(connectClient, existingInstanceId));
        System.out.println("Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void searchQueues() {
        assertDoesNotThrow(() ->SearchQueues.searchQueue(connectClient, existingInstanceId));
        System.out.println("Test 10 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

