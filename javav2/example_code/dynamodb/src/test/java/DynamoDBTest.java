// Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
// SPDX-License-Identifier: Apache-2.0

import com.example.dynamodb.CreateTable;
import com.example.dynamodb.DeleteItem;
import com.example.dynamodb.DeleteTable;
import com.example.dynamodb.DescribeTable;
import com.example.dynamodb.DynamoDBScanItems;
import com.example.dynamodb.GetItem;
import com.example.dynamodb.ListTables;
import com.example.dynamodb.PutItem;
import com.example.dynamodb.Query;
import com.example.dynamodb.scenario.Scenario;
import com.example.dynamodb.scenario.ScenarioPartiQ;
import com.example.dynamodb.SyncPagination;
import com.example.dynamodb.UpdateItem;
import com.example.dynamodb.UpdateTable;
import com.example.dynamodb.BasicAdaptiveRetryImplementation;
import com.example.dynamodb.MigrationExamples;
import com.google.gson.Gson;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.MethodOrderer;
import org.junit.jupiter.api.Nested;
import org.junit.jupiter.api.Order;
import org.junit.jupiter.api.Tag;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.junit.jupiter.api.TestMethodOrder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.IOException;
import java.util.Random;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynamoDBTest {
    private static final Logger logger = LoggerFactory.getLogger(DynamoDBTest.class);
    private static DynamoDbClient ddb;

    // Define the data members required for the test.
    private static String tableName = "Music";
  //  private static String itemVal = "";
   // private static String updatedVal = "";
    private static String key = "";
    private static String keyVal = "";
    private static String albumTitle = "";
    private static String albumTitleValue = "";
    private static String awards = "";
    private static String awardVal = "";
    private static String tableName2 = "";
    private static String fileName = "";
    private static String songTitle = "";
    private static String songTitleVal = "";

    @BeforeAll
    public static void setUp() throws IOException {
        Region region = Region.US_EAST_1;
        ddb = DynamoDbClient.builder()
                .region(region)
                .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        Random random = new Random();
        int randomValue = random.nextInt(1000) + 1;
        tableName = tableName + randomValue;
        fileName = values.getFileName();
        key = values.getKey();
        keyVal = values.getKeyValue();
        albumTitle = values.getAlbumTitle();
        albumTitleValue = values.getAlbumTitleValue();
        awards = values.getAwards();
        awardVal = values.getAwardVal();
        songTitle = values.getSongTitleVal();
        songTitleVal = values.getSongTitleVal();
        tableName2 = "Movies";
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void testCreateTable() {
        String result = CreateTable.createTable(ddb, tableName, key);
        assertFalse(result.isEmpty());
        logger.info("\n Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void testDescribeTable() {
        assertDoesNotThrow(() -> DescribeTable.describeDymamoDBTable(ddb, tableName));
        logger.info("\n Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void testPutItem() {
        assertDoesNotThrow(() -> PutItem.putItemInTable(ddb,
                tableName,
                key,
                keyVal,
                albumTitle,
                albumTitleValue,
                awards,
                awardVal,
                songTitle,
                songTitleVal));
        logger.info("\n Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void testListTables() {
        assertDoesNotThrow(() -> ListTables.listAllTables(ddb));
        logger.info("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void testQueryTable() {
        int response = Query.queryTable(ddb, tableName, key, keyVal, "#a");
        assertEquals(response, 1);
        logger.info("\n Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void testUpdateItem() {
        assertDoesNotThrow(() -> UpdateItem.updateTableItem(ddb, tableName, key, keyVal, awards, "40"));
        logger.info("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void testGetItem() {
        assertDoesNotThrow(() -> GetItem.getDynamoDBItem(ddb, tableName, key, keyVal));
        logger.info("\n Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void testScanItems() {
        assertDoesNotThrow(() -> DynamoDBScanItems.scanItems(ddb, tableName));
        logger.info("\n Test 8 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(9)
    public void testDeleteItem() {
        assertDoesNotThrow(() -> DeleteItem.deleteDynamoDBItem(ddb, tableName, key, keyVal));
        logger.info("\n Test 9 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(10)
    public void testSycnPagination() {
        assertDoesNotThrow(() -> SyncPagination.manualPagination(ddb));
        assertDoesNotThrow(() -> SyncPagination.autoPagination(ddb));
        assertDoesNotThrow(() -> SyncPagination.autoPaginationWithResume(ddb));
        logger.info("\n Test 10 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(11)
    public void testUpdateTable() {
        assertDoesNotThrow(() -> UpdateTable.updateDynamoDBTable(ddb, tableName));
        logger.info("\n Test 11 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(12)
    public void testDeleteTable() {
        try {
            TimeUnit.SECONDS.sleep(30);
            assertDoesNotThrow(() -> DeleteTable.deleteDynamoDBTable(ddb, tableName));
        } catch (InterruptedException e) {
            System.err.println(e.getMessage());
            System.exit(1);
        }
        logger.info("\n Test 12 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(13)
    public void testTestScenario() throws IOException {
        assertDoesNotThrow(() -> Scenario.createTable(ddb, tableName2));
        assertDoesNotThrow(() -> Scenario.loadData(ddb, tableName2, fileName));
        assertDoesNotThrow(() -> Scenario.getItem(ddb));
        assertDoesNotThrow(() -> Scenario.putRecord(ddb));
        assertDoesNotThrow(() -> Scenario.updateTableItem(ddb, tableName2));
        assertDoesNotThrow(() -> Scenario.scanMovies(ddb, tableName2));
        assertDoesNotThrow(() -> Scenario.queryTable(ddb));
        assertDoesNotThrow(() -> Scenario.deleteDynamoDBTable(ddb, tableName2));
        logger.info("\n Test 13 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(14)
    public void testScenarioPartiQL() throws IOException {
        assertDoesNotThrow(() -> ScenarioPartiQ.createTable(ddb, "MoviesPartiQ"));
        assertDoesNotThrow(() -> ScenarioPartiQ.loadData(ddb, fileName));
        assertDoesNotThrow(() -> ScenarioPartiQ.getItem(ddb));
        assertDoesNotThrow(() -> ScenarioPartiQ.putRecord(ddb));
        assertDoesNotThrow(() -> ScenarioPartiQ.updateTableItem(ddb));
        assertDoesNotThrow(() -> ScenarioPartiQ.queryTable(ddb));
        assertDoesNotThrow(() -> ScenarioPartiQ.deleteDynamoDBTable(ddb, "MoviesPartiQ"));
        logger.info("\n Test 14 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(15)
    public void testBasicAdaptiveRetryImplementation() {
        assertDoesNotThrow(() -> {
            DynamoDbClient client = BasicAdaptiveRetryImplementation.createDynamoDbClientWithAdaptiveRetry();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = BasicAdaptiveRetryImplementation.createDynamoDbClientWithCustomAdaptiveRetry();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = BasicAdaptiveRetryImplementation.createDynamoDbClientWithRetryMode();
            assertNotNull(client);
            client.close();
        });
        logger.info("\n Test 15 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(16)
    public void testMigrationExamples() {
        // Test BEFORE methods
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createClientWithStandardRetry_BEFORE();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createClientWithCustomStandardRetry_BEFORE();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createHighThroughputClient_BEFORE();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createBatchOperationClient_BEFORE();
            assertNotNull(client);
            client.close();
        });
        
        // Test AFTER methods
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createClientWithAdaptiveRetry_AFTER();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createClientWithCustomAdaptiveRetry_AFTER();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createHighThroughputClient_AFTER();
            assertNotNull(client);
            client.close();
        });
        assertDoesNotThrow(() -> {
            DynamoDbClient client = MigrationExamples.createBatchOperationClient_AFTER();
            assertNotNull(client);
            client.close();
        });
        logger.info("\n Test 16 passed");
    }

    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
                .region(Region.US_EAST_1)
                .build();
        String secretName = "test/dynamodb";

        GetSecretValueRequest valueRequest = GetSecretValueRequest.builder()
                .secretId(secretName)
                .build();

        GetSecretValueResponse valueResponse = secretClient.getSecretValue(valueRequest);
        return valueResponse.secretString();
    }

    @Nested
    @DisplayName("A class used to get test values from test/dynamodb (an AWS Secrets Manager secret)")
    class SecretValues {
        private String tableName;
        private String key;
        private String keyValue;

        private String albumTitle;

        private String AlbumTitleValue;

        private String Awards;

        private String AwardVal;

        private String SongTitle;

        private String SongTitleVal;
        private String fileName;

        public String getKeyValue() {
            return keyValue;
        }

        public String getKey() {
            return key;
        }

        public String getTableName() {
            return tableName;
        }

        public String getAlbumTitle() {
            return albumTitle;
        }

        public String getAlbumTitleValue() {
            return AlbumTitleValue;
        }

        public String getAwards() {
            return Awards;
        }

        public String getAwardVal() {
            return SongTitle;
        }

        public String getSongTitleVal() {
            return SongTitleVal;
        }

        public String getFileName() {
            return fileName;
        }
    }
}
