// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.translate.BatchTranslation;
import com.example.translate.DescribeTextTranslationJob;
import com.example.translate.ListTextTranslationJobs;
import com.example.translate.TranslateText;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.translate.TranslateClient;
import java.io.*;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TranslateTest {
    private static final Logger logger = LoggerFactory.getLogger(TranslateTest.class);
    private static TranslateClient translateClient;
    private static Region region;
    private static String s3Uri = "";
    private static String s3UriOut = "";
    private static String jobName = "";
    private static String dataAccessRoleArn = "";
    private static String jobId = "";

    @BeforeAll
    public static void setUp() throws IOException {
        region = Region.US_WEST_2;
        translateClient = TranslateClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        s3Uri = values.getS3Uri();
        s3UriOut = values.getS3UriOut();
        jobName = values.getJobName() + java.util.UUID.randomUUID();
        dataAccessRoleArn = values.getDataAccessRoleArn();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testTranslateText() {
        assertDoesNotThrow(() -> TranslateText.textTranslate(translateClient));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testBatchTranslation() {
        jobId = BatchTranslation.translateDocuments(translateClient, s3Uri, s3UriOut, jobName, dataAccessRoleArn);
        assertFalse(jobId.isEmpty());
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testListTextTranslationJobs() {
        assertDoesNotThrow(() -> ListTextTranslationJobs.getTranslationJobs(translateClient));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testDescribeTextTranslationJob() {
        assertDoesNotThrow(() -> DescribeTextTranslationJob.describeTextTranslationJob(translateClient, jobId));
        logger.info("Test 4 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/translate";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/translate (an AWS Secrets Manager secret)")
    class SecretValues {
        private String s3Uri;
        private String s3UriOut;
        private String jobName;

        private String dataAccessRoleArn;

        public String getS3UriOut() {
            return s3UriOut;
        }

        public String getJobName() {
            return jobName;
        }

        public String getS3Uri() {
            return s3Uri;
        }

        public String getDataAccessRoleArn() {
            return dataAccessRoleArn;
        }
    }
}
