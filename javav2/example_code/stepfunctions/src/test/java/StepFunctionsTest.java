// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.stepfunctions.*;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ObjectNode;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.iam.IamClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sfn.SfnClient;
import java.io.*;
import java.util.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class StepFunctionsTest {
    private static SfnClient sfnClient;
    private static String roleNameSC = "";
    private static String activityNameSC = "";
    private static String stateMachineNameSC = "";

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_EAST_1;
        sfnClient = SfnClient.builder()
                .region(region)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        roleNameSC = values.getRoleNameSC() + java.util.UUID.randomUUID();
        ;
        activityNameSC = values.getActivityNameSC() + java.util.UUID.randomUUID();
        ;
        stateMachineNameSC = values.getStateMachineNameSC() + java.util.UUID.randomUUID();
        ;

        // Uncomment this code block if you prefer using a config.properties file to
        // retrieve AWS values required for these tests.
        /*
         * 
         * try (InputStream input =
         * StepFunctionsTest.class.getClassLoader().getResourceAsStream(
         * "config.properties")) {
         * Properties prop = new Properties();
         * if (input == null) {
         * System.out.println("Sorry, unable to find config.properties");
         * return;
         * }
         * prop.load(input);
         * roleNameSC = prop.getProperty("roleNameSC")+ java.util.UUID.randomUUID();;
         * activityNameSC = prop.getProperty("activityNameSC")+
         * java.util.UUID.randomUUID();;
         * stateMachineNameSC = prop.getProperty("stateMachineNameSC")+
         * java.util.UUID.randomUUID();;
         * 
         * } catch (IOException ex) {
         * ex.printStackTrace();
         * }
         */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void ListActivities() {
        assertDoesNotThrow(() -> ListActivities.listAllActivites(sfnClient));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void TestHello() {
        assertDoesNotThrow(() -> ListStateMachines.listMachines(sfnClient));
        System.out.println("Test 2 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String secretName = "test/stepfunctions";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/stepfunctions (an AWS Secrets Manager secret)")
    class SecretValues {
        private String roleNameSC;
        private String activityNameSC;
        private String stateMachineNameSC;

        public String getRoleNameSC() {
            return roleNameSC;
        }

        public String getActivityNameSC() {
            return activityNameSC;
        }

        public String getStateMachineNameSC() {
            return stateMachineNameSC;
        }
    }
}
