// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.migrationhub.*;
import com.google.gson.Gson;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.services.migrationhub.MigrationHubClient;
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
public class MigrationHubTest {
    private static final Logger logger = LoggerFactory.getLogger(MigrationHubTest.class);
    private static MigrationHubClient migrationClient;
    private static String appId = "";
    private static String migrationtask = "";
    private static String progress = "";

    @BeforeAll
    public static void setUp() {
        migrationClient = MigrationHubClient.builder()
                .region(Region.US_WEST_2)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        appId = values.getAppId();
        migrationtask = values.getMigrationtask();
        progress = values.getProgress();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testImportMigrationTask() {
        assertDoesNotThrow(() -> ImportMigrationTask.importMigrTask(migrationClient, migrationtask, progress));
        logger.info("Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeAppState() {
        assertDoesNotThrow(() -> DescribeAppState.describeApplicationState(migrationClient, appId));
        logger.info("Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeMigrationTask() {
        assertDoesNotThrow(() -> DescribeMigrationTask.describeMigTask(migrationClient, migrationtask, progress));
        logger.info("Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListApplications() {
        assertDoesNotThrow(() -> ListApplications.listApps(migrationClient));
        logger.info("Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListMigrationTasks() {
        assertDoesNotThrow(() -> ListMigrationTasks.listMigrTasks(migrationClient));
        logger.info("Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testDeleteProgressStream() {
        assertDoesNotThrow(() -> DeleteProgressStream.deleteStream(migrationClient, progress));
        logger.info("Test 6 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
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
