// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.google.gson.Gson;
import com.timestream.write.*;
import org.junit.jupiter.api.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import software.amazon.awssdk.services.timestreamquery.TimestreamQueryClient;
import software.amazon.awssdk.services.timestreamwrite.TimestreamWriteClient;
import java.io.*;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class TimestreamTest {
    private static final Logger logger = LoggerFactory.getLogger(TimestreamTest.class);
    private static TimestreamWriteClient timestreamWriteClient;
    private static TimestreamQueryClient queryClient;
    private static String dbName = "";
    private static String newTable = "";

    // TODO Change database name in this string.
    private static String queryString = """
    SELECT
        truck_id,
        fleet,
        fuel_capacity,
        model,
        load_capacity,
        make,
        measure_name
    FROM "ScottTimeDB".IoTMulti
    """;

    @BeforeAll
    public static void setUp() throws IOException {
        timestreamWriteClient = TimestreamWriteClient.builder()
                .region(Region.US_EAST_1)
                .build();

        queryClient = TimestreamQueryClient.builder()
                .region(Region.US_EAST_1)
                .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        dbName = values.getDbName() + java.util.UUID.randomUUID();
        newTable = values.getNewTable() + java.util.UUID.randomUUID();
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateDatabase() {
        assertDoesNotThrow(() -> CreateDatabase.createNewDatabase(timestreamWriteClient, dbName));
        logger.info("\nTest 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testCreateTable() {
        assertDoesNotThrow(() -> CreateTable.createNewTable(timestreamWriteClient, dbName, newTable));
        logger.info("\nTest 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testDescribeDatabase() {
        assertDoesNotThrow(() -> DescribeDatabase.DescribeSingleDatabases(timestreamWriteClient, dbName));
        logger.info("\nTest 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testDescribeTable() {
        assertDoesNotThrow(() -> DescribeTable.describeSingleTable(timestreamWriteClient, dbName, newTable));
        logger.info("\nTest 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testListDatabases() {
        assertDoesNotThrow(() -> ListDatabases.listAllDatabases(timestreamWriteClient));
        logger.info("\nTest 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testListTables() {
        assertDoesNotThrow(() -> ListTables.listAllTables(timestreamWriteClient, dbName));
        logger.info("\nTest 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testUpdateTable() {
        assertDoesNotThrow(() -> UpdateTable.updateTable(timestreamWriteClient, dbName, newTable));
        logger.info("\nTest 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testWriteData() {
        assertDoesNotThrow(() -> WriteData.writeRecords(timestreamWriteClient, dbName, newTable));
        logger.info("\nTest 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDeleteTable() {
        assertDoesNotThrow(() -> DeleteTable.deleteSpecificTable(timestreamWriteClient, dbName, newTable));
        logger.info("\nTest 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testDeleteDatabase() {
        assertDoesNotThrow(() -> DeleteDatabase.delDatabase(timestreamWriteClient, dbName));
        logger.info("\nTest 10 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/timestream";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/timestream (an AWS Secrets Manager secret)")
    class SecretValues {
        private String dbName;
        private String newTable;

        public String getDbName() {
            return dbName;
        }

        public String getNewTable() {
            return newTable;
        }
    }
}
