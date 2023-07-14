/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.guardduty.GetDetector;
import com.example.guardduty.GetFindings;
import com.example.guardduty.ListDetectors;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.guardduty.GuardDutyClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class GuarddutyTest {
    private static GuardDutyClient guardDutyClient ;
    private static String detectorId = "";
    private static String findingId = "";

    @BeforeAll
    public static void setUp(){
        Region region = Region.US_EAST_1;
        guardDutyClient = GuardDutyClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        detectorId = values.getDetectorId();
        findingId = values.getFindingId();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = GuarddutyTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }
            // Populate the data members required for all tests.
            prop.load(input);
            detectorId = prop.getProperty("detectorId");
            findingId = prop.getProperty("findingId");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void GetDetector() {
        assertDoesNotThrow(() ->GetDetector.getSpecificDetector(guardDutyClient, detectorId));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void GetFindings() {
        assertDoesNotThrow(() ->GetFindings.getSpecificFinding(guardDutyClient, findingId, detectorId));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListDetectors() {
        assertDoesNotThrow(() ->ListDetectors.listAllDetectors(guardDutyClient));
        System.out.println("Test 3 passed");
    }

    private static String getSecretValues() {
        // Get the Amazon RDS creds from Secrets Manager.
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/guarduty";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/guarduty (an AWS Secrets Manager secret)")
    class SecretValues {
        private String detectorId;
        private String findingId;
        public String getDetectorId() {
            return detectorId;
        }

        public String getFindingId() {
            return findingId;
        }

    }
}
