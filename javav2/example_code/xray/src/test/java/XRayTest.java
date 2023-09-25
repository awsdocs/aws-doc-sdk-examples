/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.xray.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.xray.XRayClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class XRayTest {
    private static XRayClient xRayClient;
    private static String groupName ="";
    private static String newGroupName ="";
    private static String ruleName ="";

    @BeforeAll
    public static void setUp() throws IOException {

        Region region = Region.US_EAST_1;
        xRayClient = XRayClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        Random random = new Random();
        int randomNum = random.nextInt((10000 - 1) + 1) + 1;

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        groupName = values.getGroupName();
        newGroupName = values.getNewGroupName()+randomNum;
        ruleName= values.getRuleName()+ randomNum;

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = XRayTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            Random rand = new Random();
            int randomNum = rand.nextInt((10000 - 1) + 1) + 1;
            prop.load(input);
            groupName = prop.getProperty("groupName");
            newGroupName = prop.getProperty("newGroupName")+randomNum;
            ruleName= prop.getProperty("ruleName")+ randomNum;

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateGroup() {
        assertDoesNotThrow(() ->CreateGroup.createNewGroup(xRayClient,newGroupName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateSamplingRule() {
        assertDoesNotThrow(() ->CreateSamplingRule.createRule(xRayClient, ruleName));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void GetGroups() {
        assertDoesNotThrow(() ->GetGroups.getAllGroups(xRayClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DeleteSamplingRule() {
        assertDoesNotThrow(() ->DeleteSamplingRule.deleteRule(xRayClient, ruleName));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void DeleteGroup() {
        assertDoesNotThrow(() ->DeleteGroup.deleteSpecificGroup(xRayClient, newGroupName));
        System.out.println("Test 5 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/xray";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/xray (an AWS Secrets Manager secret)")
    class SecretValues {
        private String groupName;
        private String newGroupName;
        private String ruleName;

        public String getGroupName() {
            return groupName;
        }

        public String getNewGroupName() {
            return newGroupName;
        }

        public String getRuleName() {
            return ruleName;
        }
    }
}

