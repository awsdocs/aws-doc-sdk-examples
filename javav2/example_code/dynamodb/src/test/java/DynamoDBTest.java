/*
   Copyright Amazon.com, Inc. or its affiliates. All Rights Reserved.
   SPDX-License-Identifier: Apache-2.0
*/

import com.example.dynamodb.CreateTable;
import com.example.dynamodb.DeleteItem;
import com.example.dynamodb.DeleteTable;
import com.example.dynamodb.DescribeTable;
import com.example.dynamodb.DynamoDBScanItems;
import com.example.dynamodb.GetItem;
import com.example.dynamodb.ListTables;
import com.example.dynamodb.PutItem;
import com.example.dynamodb.Query;
import com.example.dynamodb.Scenario;
import com.example.dynamodb.ScenarioPartiQ;
import com.example.dynamodb.SyncPagination;
import com.example.dynamodb.UpdateItem;
import com.example.dynamodb.UpdateTable;
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
import software.amazon.awssdk.auth.credentials.EnvironmentVariableCredentialsProvider;
import software.amazon.awssdk.regions.Region;
import software.amazon.awssdk.services.dynamodb.DynamoDbClient;
import software.amazon.awssdk.services.secretsmanager.SecretsManagerClient;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueRequest;
import software.amazon.awssdk.services.secretsmanager.model.GetSecretValueResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;
import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;

/**
 * To run these integration tests, you must set the required values
 * in the config.properties file or AWS Secrets Manager.
 */
@TestInstance(TestInstance.Lifecycle.PER_METHOD)
@TestMethodOrder(MethodOrderer.OrderAnnotation.class)
public class DynamoDBTest {

    private static DynamoDbClient ddb;

    // Define the data members required for the test.
    private static String tableName = "";
    private static String itemVal = "";
    private static String updatedVal = "";
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
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
            .build();

        // Get the values to run these tests from AWS Secrets Manager.
        Gson gson = new Gson();
        String json = getSecretValues();
        SecretValues values = gson.fromJson(json, SecretValues.class);
        tableName = values.getTableName();
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

        // Uncomment this code block if you prefer using a config.properties file to retrieve AWS values required for these tests.
       /*
        try (InputStream input = DynamoDBTest.class.getClassLoader().getResourceAsStream("config.properties")) {
            Properties prop = new Properties();
            if (input == null) {
                System.out.println("Sorry, unable to find config.properties");
                return;
            }

            // Populate the data members required for all tests.
            prop.load(input);
            tableName = prop.getProperty("tableName");
            fileName = prop.getProperty("fileName");
            key = prop.getProperty("key");
            keyVal = prop.getProperty("keyValue");
            albumTitle = prop.getProperty("albumTitle");
            albumTitleValue = prop.getProperty("AlbumTitleValue");
            awards = prop.getProperty("Awards");
            awardVal = prop.getProperty("AwardVal");
            songTitle = prop.getProperty("SongTitle");
            songTitleVal = prop.getProperty("SongTitleVal");
            tableName2 = "Movies";

        } catch (IOException ex) {
            ex.printStackTrace();
        }
        */
    }

    @Test
    @Tag("IntegrationTest")
    @Order(1)
    public void createTable() {
        String result = CreateTable.createTable(ddb, tableName, key);
        assertFalse(result.isEmpty());
        System.out.println("\n Test 1 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(2)
    public void describeTable() {
        assertDoesNotThrow(() ->DescribeTable.describeDymamoDBTable(ddb,tableName));
        System.out.println("\n Test 2 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(3)
    public void putItem() {
        assertDoesNotThrow(() ->PutItem.putItemInTable(ddb,
                     tableName,
                     key,
                     keyVal,
                     albumTitle,
                     albumTitleValue,
                     awards,
                     awardVal,
                     songTitle,
                     songTitleVal));
         System.out.println("\n Test 3 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(4)
    public void listTables() {
        assertDoesNotThrow(() ->ListTables.listAllTables(ddb));
        System.out.println("\n Test 4 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(5)
    public void queryTable() {
        int response = Query.queryTable(ddb,tableName, key,keyVal,"#a" );
        assertEquals(response, 1);
        System.out.println("\n Test 5 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(6)
    public void updateItem() {
        assertDoesNotThrow(() ->UpdateItem.updateTableItem(ddb,tableName, key, keyVal, awards, "40"));
        System.out.println("\n Test 6 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(7)
    public void getItem() {
        assertDoesNotThrow(() ->GetItem.getDynamoDBItem(ddb, tableName,key,keyVal));
        System.out.println("\n Test 7 passed");
    }

    @Test
    @Tag("IntegrationTest")
    @Order(8)
    public void scanItems() {
        assertDoesNotThrow(() ->DynamoDBScanItems.scanItems(ddb, tableName));
        System.out.println("\n Test 8 passed");
    }

   @Test
   @Tag("IntegrationTest")
   @Order(9)
   public void deleteItem() {
       assertDoesNotThrow(() ->DeleteItem.deleteItem(ddb,tableName,key,keyVal));
       System.out.println("\n Test 9 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(10)
   public void sycnPagination(){
       assertDoesNotThrow(() -> SyncPagination.manualPagination(ddb));
       assertDoesNotThrow(() ->SyncPagination.autoPagination(ddb));
       assertDoesNotThrow(() ->SyncPagination.autoPaginationWithResume(ddb));
       System.out.println("\n Test 10 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(11)
   public void updateTable(){
       Long readCapacity = Long.parseLong("16");
       Long writeCapacity = Long.parseLong("10");
       assertDoesNotThrow(() ->UpdateTable.updateDynamoDBTable(ddb, tableName, readCapacity, writeCapacity));
       System.out.println("\n Test 11 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(12)
   public void deleteTable() {
       try {
           //Wait 15 secs for table to update based on test 10
           TimeUnit.SECONDS.sleep(15);
           assertDoesNotThrow(() ->DeleteTable.deleteDynamoDBTable(ddb,tableName));
       } catch (InterruptedException e) {
           System.err.println(e.getMessage());
           System.exit(1);
       }
       System.out.println("\n Test 12 passed");
    }

   @Test
   @Tag("IntegrationTest")
   @Order(13)
   public void testScenario() throws IOException {
       assertDoesNotThrow(() ->Scenario.createTable(ddb, tableName2));
       assertDoesNotThrow(() ->Scenario.loadData(ddb, tableName2, fileName));
       assertDoesNotThrow(() ->Scenario.getItem(ddb)) ;
       assertDoesNotThrow(() ->Scenario.putRecord(ddb));
       assertDoesNotThrow(() ->Scenario.updateTableItem(ddb, tableName2));
       assertDoesNotThrow(() ->Scenario.scanMovies(ddb, tableName2));
       assertDoesNotThrow(() ->Scenario.queryTable(ddb));
       assertDoesNotThrow(() ->Scenario.deleteDynamoDBTable(ddb, tableName2));
       System.out.println("\n Test 13 passed");
   }

   @Test
   @Tag("IntegrationTest")
   @Order(14)
   public void testScenarioPartiQL() throws IOException {
       assertDoesNotThrow(() ->ScenarioPartiQ.createTable(ddb, "MoviesPartiQ"));
       assertDoesNotThrow(() ->ScenarioPartiQ.loadData(ddb, fileName));
       assertDoesNotThrow(() ->ScenarioPartiQ.getItem(ddb));
       assertDoesNotThrow(() ->ScenarioPartiQ.putRecord(ddb));
       assertDoesNotThrow(() ->ScenarioPartiQ.updateTableItem(ddb));
       assertDoesNotThrow(() ->ScenarioPartiQ.queryTable(ddb));
       assertDoesNotThrow(() ->ScenarioPartiQ.deleteDynamoDBTable(ddb, "MoviesPartiQ"));
   }
    private static String getSecretValues() {
        SecretsManagerClient secretClient = SecretsManagerClient.builder()
            .region(Region.US_EAST_1)
            .credentialsProvider(EnvironmentVariableCredentialsProvider.create())
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


