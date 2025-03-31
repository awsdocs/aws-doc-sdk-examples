// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.mediaconvert.CreateJob;
import com.example.mediaconvert.GetJob;
import com.example.mediaconvert.ListJobs;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.mediaconvert.MediaConvertClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonMediaConvertTest {
    private static final Logger logger = LoggerFactory.getLogger(AmazonMediaConvertTest.class);
    private static MediaConvertClient mc;
    private static Region region;
    private static String mcRoleARN = "";
    private static String fileInput = "";
    private static String jobId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        region = Region.US_WEST_2;
        mc = MediaConvertClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        mcRoleARN = values.getMcRoleARN();
        fileInput = values.getFileInput();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateJob() {
        jobId = CreateJob.createMediaJob(mc, mcRoleARN, fileInput);
        assertFalse(jobId.isEmpty());
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void ListJobs() {
        assertDoesNotThrow(() -> ListJobs.listCompleteJobs(mc));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void GetJob() {
        assertDoesNotThrow(() -> GetJob.getSpecificJob(mc, jobId));
        logger.info("Test 3 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/mediaconvert";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/mediaconvert (an AWS Secrets Manager secret)")
    class SecretValues {
        private String mcRoleARN;
        private String fileInput;
        public String getMcRoleARN() {
            return mcRoleARN;
        }
        public String getFileInput() {
            return fileInput;
        }
    }
}
