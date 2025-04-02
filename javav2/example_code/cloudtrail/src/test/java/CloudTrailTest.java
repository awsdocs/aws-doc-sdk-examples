// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.cloudtrail.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.cloudtrail.CloudTrailClient;
import org.junit.jupiter.api.*;
import software.amazon.awssdk.regions.Region;
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
public class CloudTrailTest {
    private static final Logger logger = LoggerFactory.getLogger(CloudTrailTest.class);
    private static CloudTrailClient cloudTrailClient;
    private static String trailName = "";
    private static String s3BucketName = "";

    @BeforeAll
    public static void setUp() {
        cloudTrailClient = CloudTrailClient.builder()
                .region(Region.US_EAST_1)
                 .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        trailName = values.getTrailName();
        s3BucketName = values.getS3BucketName();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateTrail() {
        assertDoesNotThrow(() -> CreateTrail.createNewTrail(cloudTrailClient, trailName, s3BucketName));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testPutEventSelectors() {
        assertDoesNotThrow(() -> PutEventSelectors.setSelector(cloudTrailClient, trailName));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testGetEventSelectors() {
        assertDoesNotThrow(() -> GetEventSelectors.getSelectors(cloudTrailClient, trailName));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testLookupEvents() {
        assertDoesNotThrow(() -> LookupEvents.lookupAllEvents(cloudTrailClient));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testDescribeTrails() {
        assertDoesNotThrow(() -> DescribeTrails.describeSpecificTrails(cloudTrailClient, trailName));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testGetTrailLoggingTime() {
        assertDoesNotThrow(() -> GetTrailLoggingTime.getLogTime(cloudTrailClient, trailName));
        logger.info("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testStartLogging() {
        assertDoesNotThrow(() -> StartLogging.startLog(cloudTrailClient, trailName));
        logger.info("Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testStopLogging() {
        assertDoesNotThrow(() -> StartLogging.stopLog(cloudTrailClient, trailName));
        logger.info("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDeleteTrail() {
        assertDoesNotThrow(() -> DeleteTrail.deleteSpecificTrail(cloudTrailClient, trailName));
        logger.info("Test 9 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                //.credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();
        String secretName = "test/cloudtrail";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/cloudtrail (an AWS Secrets Manager secret)")
    class SecretValues {
        private String trailName;
        private String s3BucketName;

        public String getTrailName() {
            return trailName;
        }

        public String getS3BucketName() {
            return s3BucketName;
        }
    }
}
