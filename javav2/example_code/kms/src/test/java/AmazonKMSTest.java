// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.kms.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import com.example.kms.scenario.KMSScenario;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonKMSTest {
    private static KmsClient kmsClient;
    private static String keyDesc = "";
    private static String granteePrincipal = "";

    @BeforeAll
    public static void setUp() {
        kmsClient = KmsClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        granteePrincipal = values.getGranteePrincipal();

        // Uncomment this code block if you prefer using a config.properties file to
        // retrieve AWS values required for these tests.
        /*
         * try (InputStream input =
         * AmazonKMSTest.class.getClassLoader().getResourceAsStream("config.properties")
         * ) {
         * Properties prop = new Properties();
         * if (input == null) {
         * System.out.println("Sorry, unable to find config.properties");
         * return;
         * }
         *
         * // Populate the data members required for all tests.
         * prop.load(input);
         * keyDesc = prop.getProperty("keyDesc");
         * operation = prop.getProperty("operation");
         * aliasName = prop.getProperty("aliasName");
         * granteePrincipal = prop.getProperty("granteePrincipal");
         *
         * } catch (IOException ex) {
         * ex.printStackTrace();
         * }
         */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void HelloKSM() {
        assertDoesNotThrow(() -> HelloKMS.listAllKeys(kmsClient));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void TestScenario() {
        String targetKeyId = assertDoesNotThrow(() -> KMSScenario.createKey(kmsClient, keyDesc));
        boolean isEnabled = assertDoesNotThrow(() -> KMSScenario.isKeyEnabled(kmsClient, targetKeyId));
        if (!isEnabled)
            assertDoesNotThrow(() -> KMSScenario.enableKey(kmsClient, targetKeyId));

        String plaintext = "Hello, AWS KMS!";
        SdkBytes ciphertext = assertDoesNotThrow(() -> KMSScenario.encryptData(kmsClient, targetKeyId, plaintext));

        String fullAliasName = "alias/dev-encryption-key";
        assertDoesNotThrow(() -> KMSScenario.createCustomAlias(kmsClient, targetKeyId, fullAliasName));
        assertDoesNotThrow(() -> KMSScenario.listAllAliases(kmsClient));
        assertDoesNotThrow(() -> KMSScenario.enableKeyRotation(kmsClient, targetKeyId));
        String grantId = assertDoesNotThrow(() -> KMSScenario.grantKey(kmsClient, targetKeyId, granteePrincipal));
        assertDoesNotThrow(() -> KMSScenario.displayGrantIds(kmsClient, targetKeyId));
        assertDoesNotThrow(() -> KMSScenario.revokeKeyGrant(kmsClient,  targetKeyId, grantId));
        assertDoesNotThrow(() -> KMSScenario.decryptData(kmsClient, ciphertext, targetKeyId));
        String policyName = "testPolicy1";
        assertDoesNotThrow(() -> KMSScenario.replacePolicy(kmsClient,targetKeyId, policyName));
        assertDoesNotThrow(() -> KMSScenario.signVerifyData(kmsClient));
        assertDoesNotThrow(() -> KMSScenario.tagKMSKey(kmsClient, targetKeyId));
        assertDoesNotThrow(() -> KMSScenario.deleteSpecificAlias(kmsClient, fullAliasName));
        assertDoesNotThrow(() -> KMSScenario.disableKey(kmsClient, targetKeyId));
        assertDoesNotThrow(() -> KMSScenario.deleteKey(kmsClient, targetKeyId));
        System.out.println("Test 2 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/kms";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/kms (an AWS Secrets Manager secret)")
    class SecretValues {
        private String granteePrincipal;

        public String getGranteePrincipal() {
            return granteePrincipal;
        }
    }
}










