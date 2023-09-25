/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.kms.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.core.SdkBytes;
import software.amazon.awssdk.services.kms.KmsClient;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
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
    private static KmsClient kmsClient;
    private static String keyId = ""; // gets set in test 2
    private static String keyDesc = "";
    private static SdkBytes EncryptData; // set in test 3
    private static String granteePrincipal = "";
    private static String operation = "";
    private static String grantId = "";
    private static String aliasName = "";

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
        keyDesc = values.getKeyDesc();
        operation = values.getOperation();
        aliasName = values.getAliasName();
        granteePrincipal = values.getGranteePrincipal();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AmazonKMSTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            keyDesc = prop.getProperty("keyDesc");
            operation = prop.getProperty("operation");
            aliasName = prop.getProperty("aliasName");
            granteePrincipal = prop.getProperty("granteePrincipal");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateCustomerKey() {
        keyId = CreateCustomerKey.createKey(kmsClient, keyDesc);
        assertFalse(keyId.isEmpty());
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void EncryptDataKey() {
        EncryptData = EncryptDataKey.encryptData(kmsClient, keyId);
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DecryptDataKey() {
        assertDoesNotThrow(() ->EncryptDataKey.decryptData(kmsClient, EncryptData, keyId));
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void DisableCustomerKey() {
        assertDoesNotThrow(() ->DisableCustomerKey.disableKey(kmsClient, keyId));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void EnableCustomerKey() {
        assertDoesNotThrow(() ->EnableCustomerKey.enableKey(kmsClient, keyId));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void CreateGrant() {
        grantId = CreateGrant.createGrant(kmsClient, keyId, granteePrincipal, operation);
        assertFalse(grantId.isEmpty());
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void ViewGrants() {
        assertDoesNotThrow(() ->ListGrants.displayGrantIds(kmsClient, keyId));
        System.out.println("Test 7 passed");
    }

    @Test
    @Order(8)
    public void RevokeGrant() {
        assertDoesNotThrow(() ->RevokeGrant.revokeKeyGrant(kmsClient, keyId, grantId));
        System.out.println("Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void DescribeKey() {
        assertDoesNotThrow(() ->DescribeKey.describeSpecifcKey(kmsClient, keyId));
        System.out.println("Test 9 passed");
    }

    @Test
    @Order(10)
    public void CreateAlias() {
        assertDoesNotThrow(() ->CreateAlias.createCustomAlias(kmsClient, keyId, aliasName));
        System.out.println("Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void ListAliases() {
        assertDoesNotThrow(() ->ListAliases.listAllAliases(kmsClient));
        System.out.println("Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void DeleteAlias() {
        assertDoesNotThrow(() ->DeleteAlias.deleteSpecificAlias(kmsClient, aliasName));
        System.out.println("Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void ListKeys() {
        assertDoesNotThrow(() ->ListKeys.listAllKeys(kmsClient));
        System.out.println("Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void SetKeyPolicy() {
        assertDoesNotThrow(() ->SetKeyPolicy.createPolicy(kmsClient, keyId, "default"));
        System.out.println("Test 14 passed");
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
        private String keyDesc;
        private String operation;

        private String aliasName;

        public String getGranteePrincipal() {
            return granteePrincipal;
        }

        public String getKeyDesc() {
            return keyDesc;
        }

        public String getOperation() {
            return operation;
        }

        public String getAliasName() {
            return aliasName;
        }
    }
}

