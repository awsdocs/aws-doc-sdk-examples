/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.sts.AssumeRole;
import com.example.sts.GetAccessKeyInfo;
import com.example.sts.GetCallerIdentity;
import com.example.sts.GetSessionToken;
import com.google.gson.Gson;
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
    private static StsClient stsClient;
    private static String roleArn = "";
    private static String accessKeyId = "";
    private static String roleSessionName = "";

    @BeforeAll
    public static void setUp() {
        Region region = Region.US_EAST_1;
        stsClient = StsClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        roleArn = values.getRoleArn();
        accessKeyId = values.getAccessKeyId();
        roleSessionName = values.getRoleSessionName();


        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = STSServiceTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            roleArn = prop.getProperty("roleArn");
            accessKeyId = prop.getProperty("accessKeyId");
            roleSessionName = prop.getProperty("roleSessionName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void AssumeRole() {
        assertDoesNotThrow(() ->AssumeRole.assumeGivenRole(stsClient, roleArn, roleSessionName));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void GetSessionToken() {
        assertDoesNotThrow(() ->GetSessionToken.getToken(stsClient));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void GetCallerIdentity() {
        assertDoesNotThrow(() ->GetCallerIdentity.getCallerId(stsClient));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void GetAccessKeyInfo() {
        assertDoesNotThrow(() ->GetAccessKeyInfo.getKeyInfo(stsClient, accessKeyId));
        System.out.println("Test 4 passed");
    }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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

