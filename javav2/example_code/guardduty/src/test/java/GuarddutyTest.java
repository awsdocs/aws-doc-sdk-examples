// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.guardduty.GetDetector;
import com.example.guardduty.GetFindings;
import com.example.guardduty.ListDetectors;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
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
    private static final Logger logger = LoggerFactory.getLogger(GuarddutyTest.class);
    private static GuardDutyClient guardDutyClient;
    private static String detectorId = "";
    private static String findingId = "";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        guardDutyClient = GuardDutyClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        detectorId = values.getDetectorId();
        findingId = values.getFindingId();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void GetDetector() {
        assertDoesNotThrow(() -> GetDetector.getSpecificDetector(guardDutyClient, detectorId));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void GetFindings() {
        assertDoesNotThrow(() -> GetFindings.getSpecificFinding(guardDutyClient, findingId, detectorId));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void ListDetectors() {
        assertDoesNotThrow(() -> ListDetectors.listAllDetectors(guardDutyClient));
        logger.info("Test 3 passed");
    }

    private static String getSecretValues() {
        // Get the Amazon RDS creds from Secrets Manager.
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
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
