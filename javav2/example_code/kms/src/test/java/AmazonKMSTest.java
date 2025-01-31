// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.kms.*;
import com.example.kms.scenario.KMSActions;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AmazonKMSTest {
    private static String granteePrincipal = "";

    private static String accountId = "";

    @BeforeAll
    public static void setUp() {
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        granteePrincipal = values.getGranteePrincipal();
        accountId  = values.getAccountId();

    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void HelloKMS() {
        assertDoesNotThrow(HelloKMS::listAllKeys);
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testKMSActionsIntegration() {
        KMSActions kmsActions = new KMSActions();
        String targetKeyId = assertDoesNotThrow(() -> kmsActions.createKeyAsync("Test Key Description").join());

        try {
            boolean isEnabled = assertDoesNotThrow(() -> kmsActions.isKeyEnabledAsync(targetKeyId).join());
            if (!isEnabled) {
                assertDoesNotThrow(() -> kmsActions.enableKeyAsync(targetKeyId).join());
            }
            assertTrue(assertDoesNotThrow(() -> kmsActions.isKeyEnabledAsync(targetKeyId).join()));

            String plaintext = "Hello, AWS KMS!";
            SdkBytes encryptedData = assertDoesNotThrow(() -> kmsActions.encryptDataAsync(targetKeyId, plaintext).join());
            assertNotNull(encryptedData);

            String currentTimestamp = String.valueOf(System.currentTimeMillis());
            String fullAliasName = "alias/dev-encryption-key" + currentTimestamp;;
            assertDoesNotThrow(() -> kmsActions.createCustomAliasAsync(targetKeyId, fullAliasName).join());
            assertDoesNotThrow(kmsActions::listAllAliasesAsync).join();
            assertDoesNotThrow(() -> kmsActions.enableKeyRotationAsync(targetKeyId).join());

            String grantId = assertDoesNotThrow(() -> kmsActions.grantKeyAsync(targetKeyId, "granteePrincipal").join());
            assertNotNull(grantId);
            assertDoesNotThrow(() -> kmsActions.displayGrantIdsAsync(targetKeyId).join());

            assertDoesNotThrow(() -> kmsActions.revokeKeyGrantAsync(targetKeyId, grantId).join());
            SdkBytes decryptedData = SdkBytes.fromUtf8String(assertDoesNotThrow(() -> kmsActions.decryptDataAsync(encryptedData, targetKeyId).join()));
            assertEquals(plaintext, decryptedData.asUtf8String());

            assertDoesNotThrow(kmsActions::signVerifyDataAsync);
            assertDoesNotThrow(() -> kmsActions.tagKMSKeyAsync(targetKeyId).join());
            assertDoesNotThrow(() -> kmsActions.disableKeyAsync(targetKeyId).join());
        } finally {
            assertDoesNotThrow(() -> kmsActions.deleteKeyAsync(targetKeyId).join());
        }

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

        private String accountId;

        public String getGranteePrincipal() {
            return granteePrincipal;
        }

        public String getAccountId() {
            return accountId;
        }
    }
}










