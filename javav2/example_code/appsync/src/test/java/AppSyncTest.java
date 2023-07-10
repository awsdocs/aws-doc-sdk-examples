/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.appsync.*;
import com.google.gson.Gson;
import org.junit.jupiter.api.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertFalse;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.appsync.AppSyncClient;
import java.io.*;
import  software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class AppSyncTest {
    private static AppSyncClient appSyncClient;
    private static String apiId="";
    private static String dsName="";
    private static String dsRole="";
    private static String tableName="";
    private static String keyId = "";  // Gets dynamically set in a test.
    private static String dsARN = ""; // Gets dynamically set in a test.
    private static String reg = "";

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_EAST_1;
        reg = region.toString();
        appSyncClient = AppSyncClient.builder()
            .region(region)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        apiId = values.getApiId();
        dsName = values.getDsName();
        dsRole= values.getDsRole();
        tableName= values.getTableName();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = AppSyncTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            apiId = prop.getProperty("apiId");
            dsName = prop.getProperty("dsName");
            dsRole= prop.getProperty("dsRole");
            tableName= prop.getProperty("tableName");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void CreateApiKey() {
        keyId = CreateApiKey.createKey(appSyncClient, apiId);
        assertFalse(keyId.isEmpty());
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void CreateDataSource() {
        dsARN = CreateDataSource.createDS(appSyncClient, dsName, reg, dsRole, apiId, tableName);
        assertFalse(dsARN.isEmpty());
        System.out.println("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void GetDataSource() {
        assertDoesNotThrow(()->GetDataSource.getDS(appSyncClient, apiId, dsName));
        System.out.println("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void ListGraphqlApis() {
        assertDoesNotThrow(()->ListGraphqlApis.getApis(appSyncClient));
        System.out.println("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void ListApiKeys() {
        assertDoesNotThrow(()->ListApiKeys.getKeys(appSyncClient,apiId));
        System.out.println("Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
     public void DeleteDataSource() {
        assertDoesNotThrow(()->DeleteDataSource.deleteDS(appSyncClient, apiId, dsName));
        System.out.println("Test 7 passed");
     }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
     public void DeleteApiKey() {
         assertDoesNotThrow(()->DeleteApiKey.deleteKey(appSyncClient, keyId, apiId)) ;
         System.out.println("Test 8 passed");
     }

    private static String getSecretValues() {
         SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/appsync";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/appsync (an AWS Secrets Manager secret)")
    class SecretValues {
        private String apiId;
        private String dsName;
        private String dsRole;

        private String tableName;

        public String getApiId() {
            return apiId;
        }

        public String getDsName() {
            return dsName;
        }

        public String getDsRole() {
            return dsRole;
        }

        public String getTableName() {
            return tableName;
        }
    }
}
