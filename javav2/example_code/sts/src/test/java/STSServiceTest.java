// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.sts.AssumeRole;
import com.example.sts.GetAccessKeyInfo;
import com.example.sts.GetCallerIdentity;
import com.example.sts.GetSessionToken;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.sts.StsClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class STSServiceTest {
    private static final Logger logger = LoggerFactory.getLogger(STSServiceTest.class);
    private static StsClient stsClient;
    private static String roleArn = "";
    private static String accessKeyId = "";
    private static String roleSessionName = "";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        stsClient = StsClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        roleArn = values.getRoleArn();
        accessKeyId = values.getAccessKeyId();
        roleSessionName = values.getRoleSessionName();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testAssumeRole() {
        assertDoesNotThrow(() -> AssumeRole.assumeGivenRole(stsClient, roleArn, roleSessionName));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testGetSessionToken() {
        assertDoesNotThrow(() -> GetSessionToken.getToken(stsClient));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testGetCallerIdentity() {
        assertDoesNotThrow(() -> GetCallerIdentity.getCallerId(stsClient));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testGetAccessKeyInfo() {
        assertDoesNotThrow(() -> GetAccessKeyInfo.getKeyInfo(stsClient, accessKeyId));
        logger.info("Test 4 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/sts";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/sts (an AWS Secrets Manager secret)")
    class SecretValues {
        private String roleArn;
        private String accessKeyId;
        private String roleSessionName;

        public String getRoleArn() {
            return roleArn;
        }

        public String getAccessKeyId() {
            return accessKeyId;
        }

        public String getRoleSessionName() {
            return roleSessionName;
        }
    }
}
