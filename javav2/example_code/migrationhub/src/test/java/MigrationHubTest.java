/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.migrationhub.*;
import com.google.gson.Gson;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
import org.junit.jupiter.api.*;
import  software.amazon.awssdk.regions.Region;
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
public class MigrationHubTest {
    private static MigrationHubClient migrationClient;
    private static String appId="";
    private static String migrationtask ="";
    private static String progress ="";

    @BeforeAll
    public static void setUp() {
        migrationClient = MigrationHubClient.builder()
            .region(Region.US_WEST_2)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        appId = values.getAppId();
        migrationtask = values.getMigrationtask();
        progress = values.getProgress();

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*

        try (InputStream input = MigrationHubTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            prop.load(input);
            appId = prop.getProperty("appId");
            migrationtask = prop.getProperty("migrationtask");
            progress = prop.getProperty("progress");

        } catch (IOException ex) {
            ex.printStackTrace();
        }
    */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void ImportMigrationTask() {
        assertDoesNotThrow(() -> ImportMigrationTask.importMigrTask(migrationClient, migrationtask, progress));
        System.out.println("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void DescribeAppState() {
        assertDoesNotThrow(() ->DescribeAppState.describeApplicationState(migrationClient, appId));
        System.out.println("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void DescribeMigrationTask() {
        assertDoesNotThrow(() ->DescribeMigrationTask.describeMigTask(migrationClient, migrationtask, progress));
        System.out.println("Test 3 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
   public void ListApplications() {
       assertDoesNotThrow(() ->ListApplications.listApps(migrationClient));
       System.out.println("Test 4 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
   public void ListMigrationTasks() {
       assertDoesNotThrow(() ->ListMigrationTasks.listMigrTasks(migrationClient));
       System.out.println("Test 5 passed");
   }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
   public void DeleteProgressStream() {
       assertDoesNotThrow(() ->DeleteProgressStream.deleteStream(migrationClient, progress));
       System.out.println("Test 6 passed");
   }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();
        String secretName = "test/migrationhub";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
            .secretId(secretName)
            .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/migrationhub (an AWS Secrets Manager secret)")
    class SecretValues {
        private String appId;
        private String migrationtask;
        private String progress;

        public String getAppId() {
            return appId;
        }

        public String getMigrationtask() {
            return migrationtask;
        }

        public String getProgress() {
            return progress;
        }

    }

}

